package main.impl.tree;

import main.adt.BinarySearchTreeADT;

/**
 * ArrayBinarySearchTree implements a binary search tree using an arrar.
 */
public class ArrayBinarySearchTree<T> extends ArrayBinaryTree<T> implements BinarySearchTreeADT<T> {
   protected int height;
   protected int maxIndex;

    /**
     * Creates an empty binary search tree
     */
    public ArrayBinarySearchTree() {
        super();
        height = 0;
        maxIndex = -1;
    }

    /**
     * Creates a binary search tree with the specified element as its root
     * @param element the element that will be the root of the new tree
     */
    public ArrayBinarySearchTree(T element) {
        super(element);
        height = 1;
        maxIndex = 0;
    }

    /**
     * Adds the specified object to this binary search tree in the appropriate position according to its key value.
     * Note that equal elements are added to the right. Also note that the index of the left child of the current index can be found by doubling the current index and adding 1.
     * Finding the index of the right child can be calculated by doubling the current index and adding 2.
     * @param element the element to be added to the search tree
     */
    @Override
    public void addElement(T element) {
        if (tree.length < maxIndex * 2 + 3){
            expandCapacity();
        }
        Comparable<T> tempElement = (Comparable<T>) element;

        if (isEmpty()) {
            tree[0] = element;
            maxIndex = 0;
        } else {
            boolean added = false;
            int currentIndex = 0;

            while (!added) {
                if (tempElement.compareTo(tree[currentIndex]) < 0) {
                    /*go left*/
                    if (tree[currentIndex * 2 + 1] == null) {
                        tree[currentIndex * 2 + 1] = element;
                        added = true;
                        if (currentIndex * 2 + 1 > maxIndex) {
                            maxIndex = currentIndex * 2 + 1;
                        }
                    } else {
                        currentIndex = currentIndex * 2 + 1;
                    }
                } else {
                    /*go right*/
                    if (tree[currentIndex * 2 + 2] == null) {
                        tree[currentIndex * 2 + 2] = element;
                        added = true;
                        if (currentIndex * 2 + 2 > maxIndex) {
                            maxIndex = currentIndex * 2 + 2;
                        }
                    } else {
                        currentIndex = currentIndex * 2 + 2;
                    }
                }
            }
        }
        height = (int) (Math.log(maxIndex + 1) / Math.log(2)) + 1;
        count++;
    }

    private void expandCapacity() {
        T[] newTree = (T[]) new Object[tree.length * 2];
        for (int i = 0; i < tree.length; i++) {
            newTree[i] = tree[i];
        }
        tree = newTree;

    }

    @Override
    public T removeElement(T element) {
        if (isEmpty()) throw new IllegalArgumentException("Tree is empty");
        Comparable<T> key = (Comparable<T>) element;

        int current = 0;
        int parent = -1;
        boolean found = false;

        //Search for the element index.
        while (current < tree.length && tree[current] != null){
            int compareResult = key.compareTo(tree[current]);
            if (compareResult == 0) {
                found = true;
                break;
            }
            parent = current;
            current = (compareResult < 0) ? (current * 2 + 1) : (current * 2 + 2);
        }
        if (!found) throw new IllegalArgumentException("Element not found");

        T result = tree[current];
        int left = current * 2 + 1;
        int right = current * 2 + 2;
        boolean hasLeft = left < tree.length && tree[left] != null;
        boolean hasRight = right < tree.length && tree[right] != null;

        if(!hasLeft && !hasRight){
            //Remove leaf
            tree[current] = null;
        } else if (hasLeft && !hasRight){
            //Left child: move left subtree to current
            shiftSubtree(right, current);
        } else {
            //Two children: find the in-order successor (leftmost of right subtree)
            int succ = right;
            while (true) {
                int l = succ * 2 + 1;
                if (l < tree.length && tree[l] != null) succ = l;
                else break;
            }
            //Copy the successor to current
            tree[current] = tree[succ];
            //Removes the successor (he has no left child)
            int succRight = succ * 2 + 2;
            if (succRight < tree.length && tree[succRight] != null) {
                shiftSubtree(succRight, succ);
            } else {
                tree[succ] = null;
            }
        }
        count--;

        //Recaulculate maxIndex
        maxIndex = -1;
        for (int i = tree.length - 1; i >= 0; i--) {
            if (tree[i] != null) {
                maxIndex = i;
                break;
            }
        }
        height = (maxIndex == -1) ? 0 : (int) (Math.log(maxIndex + 1) / Math.log(2)) + 1;

        return result;
    }

    private void ensureCapacityForIndex(int index) {
        while (index >= tree.length) {
            expandCapacity();
        }
    }

    private void shiftSubtree(int src, int dest) {
        if (src >= tree.length || tree[src] == null) return;
        //Ensures capacity for the destination and its descendants as needed        ensureCapacityForIndex(dest);
        tree[dest] = tree[src];

        int leftSrc = src * 2 + 1;
        int rightSrc = src * 2 + 2;
        int leftDest = dest * 2 + 1;
        int rightDest = dest * 2 + 2;

        if (leftSrc < tree.length && tree[leftSrc] != null) {
            shiftSubtree(leftSrc, leftDest);
        }
        if (rightSrc < tree.length && tree[rightSrc] != null) {
            shiftSubtree(rightSrc, rightDest);
        }

        // limpa a posição original após copiar filhos
        tree[src] = null;
    }

    @Override
    public void removeAllOccurrences(T element) {
        if (isEmpty()){
            return;
        } else {
            while (true) {
                try {
                    T removed = removeElement(element);
                    if (removed == null)
                        break;
                } catch (IllegalArgumentException ex) {
                    break;
                }
            }
        }
    }

    @Override
    public T removeMin() {
        if (isEmpty()) return null;

        int parent = -1;
        int current = 0;

        // Goes down to the left until the minimum
        while (true) {
            int left = current * 2 + 1;
            if (left < tree.length && tree[left] != null) {
                parent = current;
                current = left;
            } else {
                break;
            }
        }

        T result = tree[current];
        int right = current * 2 + 2;
        boolean hasRight = right < tree.length && tree[right] != null;

        if (parent == -1) {
            //Minimum is the root
            if (hasRight) {
                shiftSubtree(right, 0);
            } else {
                tree[0] = null;
            }
        } else {
            //Minimum is not the root
            if (hasRight) {
                //Replace minimum with its right subtree
                shiftSubtree(right, current);
            } else {
                //Just remove the minimum
                tree[current] = null;
            }
        }

        count--;

        //Recaulculate maxIndex
        maxIndex = -1;
        for (int i = tree.length - 1; i >= 0; i--) {
            if (tree[i] != null) {
                maxIndex = i;
                break;
            }
        }
        height = (maxIndex == -1) ? 0 : (int) (Math.log(maxIndex + 1) / Math.log(2)) + 1;

        return result;
    }


    @Override
    public T removeMax() {
        if( isEmpty()) return null;
        int parent = -1;
        int current = 0;

        //Goes down to the right until the maximum
        while (true) {
            int right = current * 2 + 2;
            if (right < tree.length && tree[right] != null) {
                parent = current;
                current = right;
            } else {
                break;
            }
        }
        T result = tree[current];
        int left = current * 2 + 1;
        boolean hasLeft = left < tree.length && tree[left] != null;
        if (parent == -1) {
            //Maximum is the root
            if (hasLeft) {
                shiftSubtree(left, 0);
            } else {
                tree[0] = null;
            }
        } else {
            //Maximum is not the root
            if (hasLeft) {
                //Replace maximum with its left subtree
                shiftSubtree(left, current);
            } else {
                //Just remove the maximum
                tree[current] = null;
            }
        }
        count--;
        //Recaulculate maxIndex
        maxIndex = -1;
        for (int i = tree.length - 1; i >= 0; i--) {
            if (tree[i] != null) {
                maxIndex = i;
                break;
            }
        }
        height = (maxIndex == -1) ? 0 : (int) (Math.log(maxIndex + 1) / Math.log(2)) + 1;
        return result;
    }

    @Override
    public T findMin() {
        if (isEmpty()) return null;
        int current = 0;
        while (true) {
            int left = current * 2 + 1;
            if (left < tree.length && tree[left] != null) {
                current = left;
            } else {
                break;
            }
        }
        return tree[current];
    }

    @Override
    public T findMax() {
        if (isEmpty()) return null;
        int current = 0;
        while (true) {
            int right = current * 2 + 2;
            if (right < tree.length && tree[right] != null) {
                current = right;
            } else {
                break;
            }
        }
        return tree[current];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isEmpty()) return "";
        buildString(0, sb);
        return sb.toString();
    }

    private void buildString(int index, StringBuilder sb) {
        if (index > maxIndex || tree[index] == null) {
            return;
        }
        sb.append(tree[index]);
        int left = index * 2 + 1;
        int right = index * 2 + 2;
        boolean hasLeft = left <= maxIndex && tree[left] != null;
        boolean hasRight = right <= maxIndex && tree[right] != null;
        if (!hasLeft && !hasRight) {
            return;
        }
        sb.append(" (");
        boolean printed = false;
        if (hasLeft) {
            buildString(left, sb);
            printed = true;
        }
        if (hasRight) {
            if (printed) sb.append(", ");
            buildString(right, sb);
        }
        sb.append(")");
    }
}
