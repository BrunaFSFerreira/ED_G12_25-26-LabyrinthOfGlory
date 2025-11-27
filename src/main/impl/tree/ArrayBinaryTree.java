package main.impl.tree;

import main.adt.BinaryTreeADT;
import main.adt.QueueADT;
import main.execption.ElementNotFoundExecption;
import main.impl.list.ArrayUnorderedList;
import main.impl.queue.CircularArrayQueue;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayBinaryTree<T> implements BinaryTreeADT<T> {
    protected int count;
    protected T[] tree;
    protected final int CAPACITY = 50;

    /**
     * Creates an empty binary tree
     */
    public ArrayBinaryTree() {
        count = 0;
        tree = (T[]) new Object[CAPACITY];
    }

    /**
     * Creates a binary tree with the specified element as its root
     * @param element the element that will become the root of the new tree
     */
    public ArrayBinaryTree(T element) {
        count = 1;
        tree = (T[]) new Object[CAPACITY];
        tree[0] = element;
    }

    @Override
    public T getRoot() {
        if (tree[0] == null) {
            throw new IllegalStateException("binary main.java.impl.tree");
        }

        return tree[0];
    }

    @Override
    public boolean isEmpty() {
        return count ==0;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean contains(T targetElement) {
        boolean found = false;

        for (int ct = 0; ct < count && !found; ct++) {
            if (targetElement.equals(tree[ct])) {
                found = true;
            }
        }
        return found;
    }

    /**
     * Returnd a reference to the specified element if it is found in this binary tree.
     * Throws a NoSuchElementException if the specified target element is not found in the binary tree.
     * @param targetElement the element being sought in this tree
     * @return true if the element is in the tree
     * @throws ElementNotFoundExecption if an element not found exception occurs
     */
    @Override
    public T find(T targetElement) throws ElementNotFoundExecption {
        T temp = null;
        boolean found = false;

        for (int ct = 0; ct < count && !found; ct++) {
            if (targetElement.equals(tree[ct])) {
                found = true;
                temp = tree[ct];
            }
        } if (!found) {
            throw new ElementNotFoundExecption("Binary Tree");
        }
        return temp;
    }

    /**
     * Performs an inorder traversal on this binary tree by calling an overloaded, recursive inorder method that starts with the root
     * @return an iterator over the binary tree
     */
    @Override
    public Iterator<T> iteratorInOrder() {
        ArrayUnorderedList<T> temp = new ArrayUnorderedList<>();
        inOrder(0, temp);
        return temp.iterator();
    }

    /**
     * Performs a recursive inorder traversal
     * @param node the node used in the traversal
     * @param templist the temporary list used in the traversal
     */
    private void inOrder(int node, ArrayUnorderedList<T> templist) {
        if (node < tree.length) {
            if (tree[node] != null) {
                inOrder(node*2 + 1, templist);
                templist.addToRear(tree[node]);
                inOrder((node+1)*2, templist);
            }
        }
    }

    @Override
    public Iterator<T> iteratorPreOrder() {
        ArrayUnorderedList<T> temp = new ArrayUnorderedList<>();
        preorder(0, temp);
        return temp.iterator();
    }

    private void preorder(int node, ArrayUnorderedList<T> templist) {
        if (node < tree.length) {
            if (tree[node] != null) {
                templist.addToRear(tree[node]);
                preorder(node*2 + 1, templist);
                preorder((node+1)*2, templist);
            }
        }
    }

    @Override
    public Iterator<T> iteratorPostOrder() {
        ArrayUnorderedList<T> temp = new ArrayUnorderedList<>();
        postorder(0, temp);
        return temp.iterator();
    }

    private void postorder(int node, ArrayUnorderedList<T> templist) {
        if (node < tree.length) {
            if (tree[node] != null) {
                postorder(node*2 + 1, templist);
                postorder((node+1)*2, templist);
                templist.addToRear(tree[node]);
            }
        }
    }

    @Override
    public Iterator<T> iteratorLevelOrder() {
        ArrayUnorderedList<T> temp = new ArrayUnorderedList<>();
        levelorder(0, temp);
        return temp.iterator();
    }

    private void levelorder(int node, ArrayUnorderedList<T> templist) {
        QueueADT<Integer> queue = new CircularArrayQueue<Integer>();

        if (tree[0] != null) {
            queue.enqueue(0);
        }
        while (!queue.isEmpty()) {
            int currentNode = queue.dequeue();
            templist.addToRear(tree[currentNode]);

            int leftChild = currentNode * 2 + 1;
            int rightChild = (currentNode + 1) * 2;

            if (leftChild < tree.length && tree[leftChild] != null) {
                queue.enqueue(leftChild);
            }
            if (rightChild < tree.length && tree[rightChild] != null) {
                queue.enqueue(rightChild);
            }
        }


    }
}
