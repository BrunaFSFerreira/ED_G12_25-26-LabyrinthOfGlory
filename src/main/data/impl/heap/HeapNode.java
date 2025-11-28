package main.data.impl.heap;

import main.data.impl.tree.BinaryTreeNode;

public class HeapNode<T> extends BinaryTreeNode<T> {

    public HeapNode<T> left;
    public HeapNode<T> right;
    public T element;
    protected HeapNode<T> parent;

    /**
     * Creates a heap node with the specified data.
     * @param obj the data to be contained within the new heap nodes
     */
    HeapNode(T obj) {
        super(obj);
        parent = null;
    }
}
