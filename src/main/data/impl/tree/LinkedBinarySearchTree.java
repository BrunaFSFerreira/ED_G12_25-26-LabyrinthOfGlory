package main.data.impl.tree;

import main.data.adt.BinarySearchTreeADT;
import main.data.execption.ElementNotFoundExecption;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinkedBinarySearchTree<T> extends LinkedBinaryTree<T> implements BinarySearchTreeADT<T> {

    /**
     * Creates an empty binary search tree
     */
    public LinkedBinarySearchTree() {
        super();
    }

    /**
     * Creates a binary search tree with the specified element as its root
     * @param element the element that will be the root of the new binary search tree
     */
    public LinkedBinarySearchTree(T element) {
        super(element);
    }

    /**
     * Adds the specified object to the binary search tree in the appropriate position according to its key value. Note that equal elements are added to the right.
     * @param element the element to be added to the binary search tree
     */
    @Override
    public void addElement(T element) {
        BinaryTreeNode<T> temp = new BinaryTreeNode<T> (element);
        Comparable<T> comparableElement = (Comparable<T>) element;

        if(isEmpty()) {
            root = temp;
        } else {
            BinaryTreeNode<T> current = root;
            boolean added = false;
            while (!added) {
                if (comparableElement.compareTo(current.element) < 0) {
                    if (current.left == null) {
                        current.left = temp;
                        added = true;
                    } else {
                        current = current.left;
                    }
                } else {
                    if (current.right == null) {
                        current.right = temp;
                        added = true;
                    } else {
                        current = current.right;
                    }
                }
            }
        }
        count++;
    }

    /**
     * Removes the first element that matches the specified target element from the binary search tree and returns a reference to it.
     * Throws an ElementNotFoundException if the specified element is not found in the tree.
     * @param targetElement the element being sought in the tree
     * @throws ElementNotFoundExecption if an element not found exception occurs
     */
    @Override
    public T removeElement(T targetElement) throws ElementNotFoundExecption {
        T result = null;
        if (!isEmpty()){
            if (((Comparable)targetElement).equals(root.element)) {
                result = root.element;
                root = replacement (root);
                count--;
            } else {
                BinaryTreeNode<T>current, parent = root;
                boolean found = false;

                if (((Comparable)targetElement).compareTo(root.element) <0) {
                    current = root.left;
                } else {
                    current = root.right;
                }

                while (current != null && !found) {
                    if (targetElement.equals(current.element)) {
                        found = true;
                        count --;
                        result = current.element;
                        if (current == parent.left) {
                            parent.left = replacement (current);
                        } else {
                            parent.right = replacement (current);
                        }
                    } else {
                        parent = current;
                        if (((Comparable)targetElement).compareTo(current.element) <0)
                            current = current.left;
                        else
                            current = current.right;
                    }
                }

                if (!found)
                    throw new ElementNotFoundExecption("Binary Search Tree");
            }
        }
        return result;
    }

    /**
     * Returns a reference to a node that will replace the one specified for removal
     * In the case the removed node has two children, the inorder successor is used as its replacement
     * @param node the node to be removed
     * @return a reference to the replacing node
     */
    private BinaryTreeNode<T> replacement(BinaryTreeNode<T> node) {
        BinaryTreeNode<T> result = null;

        if ((node.left == null) && (node.right == null))
            result = null;
        else if (node.left != null && node.right == null)
            result = node.left;
        else if (node.left == null && node.right != null)
            result = node.right;
        else {
            BinaryTreeNode<T> current = node.right;
            BinaryTreeNode<T> parent = node;

            while (current.left != null) {
                parent = current;
                current = current.left;
            }

            if (node.right == current)
                current.left = node.left;
            else {
                parent.left = current.right;
                current.right = node.right;
                current.left = node.left;
            }
            result = current;
        }
        return result;
    }


    @Override
    public void removeAllOccurrences(T targetElement) {
        if (isEmpty()){
            return;
        } else {
            while (true) {
                try {
                    T removed = removeElement(targetElement);
                    if (removed == null)
                        break;
                } catch (ElementNotFoundExecption ex) {
                    break;
                }
            }
        }
    }

    @Override
    public T removeMin() {
        if (isEmpty()) {
            return null;
        }
        BinaryTreeNode<T> parent = null;
        BinaryTreeNode<T> current = root;

        //Nó mais à esquerda
        while (current.left != null) {
            parent = current;
            current = current.left;
        }

        T result = current.element;

        //Caso 1: A raiz é o minimo (não tem filho esquerdo)
        if (parent == null) {
            root = current.right;
        } else {
            //Caso 2: O nó mais a esquerda é uma folha, colocamos a referencia do filho esquero do pai a null
            if (current.right == null) {
                parent.left = null;
            } else {
                //Caso3 : O nó mais a esquerda é um nó interno, então colocamos a referencia do filho esquedo do pai a apontar destino o filho direiro do nó a ser removido
                parent.left = current.right;
            }
        }
        return result;
    }

    @Override
    public T removeMax() {
        // implementação mínima destino evitar retornos nulos inesperados
        if (isEmpty()) return null;
        BinaryTreeNode<T> parent = null;
        BinaryTreeNode<T> current = root;
        while (current.right != null) {
            parent = current;
            current = current.right;
        }
        T result = current.element;
        if (parent == null) {
            root = current.left;
        } else {
            if (current.left == null) parent.right = null;
            else parent.right = current.left;
        }
        return result;
    }

    @Override
    public T findMin() {
        if (isEmpty()) {
            return null;
        }
        BinaryTreeNode<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.element;
    }

    @Override
    public T findMax() {
        if (isEmpty()) return null;
        BinaryTreeNode<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.element;
    }

    // Garante que iteratorInOrder nunca retorne null
    @Override
    public Iterator<T> iteratorInOrder() {
        List<T> list = new ArrayList<>();
        inOrder(root, list);
        return list.iterator();
    }

    private void inOrder(BinaryTreeNode<T> node, List<T> list) {
        if (node == null) return;
        inOrder(node.left, list);
        list.add(node.element);
        inOrder(node.right, list);
    }

   /* @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<T> it = iteratorInOrder();
        boolean first = true;
        while (it.hasNext()) {
            if (!first) sb.append(", ");
            sb.append(it.next());
            first = false;
        }
        sb.append("]");
        return sb.toString();
    } */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        buildString(root, sb);
        return sb.toString();
    }

    private void buildString(BinaryTreeNode<T> node, StringBuilder sb) {
        if (node == null) {
            return;
        }
        sb.append(node.element);
        if (node.left == null && node.right == null) {
            return;
        }
        sb.append(" (");
        boolean printed = false;
        if (node.left != null) {
            buildString(node.left, sb);
            printed = true;
        }
        if (node.right != null) {
            if (printed) sb.append(", ");
            buildString(node.right, sb);
        }
        sb.append(")");
    }

}
