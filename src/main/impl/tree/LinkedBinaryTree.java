package main.impl.tree;

import main.adt.BinaryTreeADT;
import main.adt.QueueADT;
import main.execption.ElementNotFoundExecption;
import main.impl.list.ArrayUnorderedList;
import main.impl.queue.CircularArrayQueue;

import java.util.Iterator;

/**
 * LinkedBinaryTree implements the BinaryTreeADT interface
 * @param <T>
 */
public class LinkedBinaryTree<T> implements BinaryTreeADT<T> {

    protected int count;
    protected BinaryTreeNode<T> root;

    /**
     * Creates an empty binary tree
     */
    public LinkedBinaryTree(){
        count = 0;
        root = null;
    }

    /**
     * Creates a binary tree with the specified element as its root
     * @param element the element that will become the root of the new binary tree
     */
    public LinkedBinaryTree (T element){
        count = 1;
        root = new BinaryTreeNode<T>(element);
    }

    @Override
    public T getRoot() {
        if (root != null)
            return root.element;
        return null;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean contains(T targetElement) {
        return false;
    }

    /**
     * Returns a reference to the specified element if it is found in this binary tree.
     * Throws a NoSuchElementException if the specified target element is not found in the binary tree
     * @param targetElement the element being sought in this tree
     * @return a reference to the specified element
     * @throws ElementNotFoundExecption if an element not found exception occurs
     */
    @Override
    public T find(T targetElement) throws ElementNotFoundExecption {
        BinaryTreeNode<T> current = findAgain(targetElement, root);
        if (current == null){
            throw new ElementNotFoundExecption("Binary Tree");
        }
        return current.element;
    }

    /**
     * Returns a reference to the specified element if it is found in this binary tree
     * @param targetElement the element being sought in this tree
     * @param next the element to begin searching from
     */
    private BinaryTreeNode<T> findAgain(T targetElement, BinaryTreeNode<T> next) {
        if (next == null)
            return null;

        if (next.element.equals(targetElement))
            return next;

        BinaryTreeNode<T> temp = findAgain(targetElement, next.left);
        if (temp == null)
            temp = findAgain(targetElement, next.right);

        return temp;
    }

    private String toString(BinaryTreeNode<T> node) {
        String result = "";
        if (node != null) {
            result += node.element + " ";
            result += toString(node.left);
            result += toString(node.right);
        }
        return result;
    }

    /**
     * Performs an inorder traversal on this binary tree by calling an overloaded, recursive inorder method that starts with the root
     * @return an in order iterator over this binary tree
     */
    @Override
    public Iterator<T> iteratorInOrder() {
        ArrayUnorderedList<T> tempList = new ArrayUnorderedList<T>();
        inOrder(root, tempList);
        return tempList.iterator();
    }

    /**
     * Performs a recursive inorder traversal
     * @param node the node to be used as the root for this traversal
     * @param tempList the temporary list for the traversal
     */
    private void inOrder(BinaryTreeNode<T> node, ArrayUnorderedList<T> tempList) {
        if (node != null) {
            inOrder(node.left, tempList);
            tempList.addToRear(node.element);
            inOrder(node.right, tempList);
        }
    }

    @Override
    public Iterator<T> iteratorPreOrder() {
        ArrayUnorderedList<T> tempList = new ArrayUnorderedList<T>();
        preOrder(root, tempList);
        return tempList.iterator();
    }

    private void preOrder(BinaryTreeNode<T> node, ArrayUnorderedList<T> tempList) {
        if (node != null) {
            tempList.addToRear(node.element);
            preOrder(node.left, tempList);
            preOrder(node.right, tempList);
        }
    }

    @Override
    public Iterator<T> iteratorPostOrder() {
        ArrayUnorderedList<T> tempList = new ArrayUnorderedList<T>();
        postOrder(root, tempList);
        return tempList.iterator();
    }

    private void postOrder(BinaryTreeNode<T> root, ArrayUnorderedList<T> tempList) {
        if (root != null) {
            postOrder(root.left, tempList);
            postOrder(root.right, tempList);
            tempList.addToRear(root.element);
        }
    }

    @Override
    public Iterator<T> iteratorLevelOrder() {
        ArrayUnorderedList<T> tempList = new ArrayUnorderedList<T>();
        levelOrder(root, tempList);
        return tempList.iterator();
    }

    private void levelOrder(BinaryTreeNode<T> root, ArrayUnorderedList<T> tempList) {
        QueueADT<BinaryTreeNode<T>> queue = new CircularArrayQueue<BinaryTreeNode<T>>();
        if (root != null)
            queue.enqueue(root);
        while (!queue.isEmpty()) {
            BinaryTreeNode<T> node = queue.dequeue();
            tempList.addToRear(node.element);
            if (node.left != null)
                queue.enqueue(node.left);
            if (node.right != null)
                queue.enqueue(node.right);
        }
    }
}
