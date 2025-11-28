package main.data.impl.tree;

/**
 * BinaryTreeNode represents a node in a binary tree with a left and right child.
 * @param <T>
 */
public class BinaryTreeNode<T> {
    protected T element;
    protected BinaryTreeNode<T> left, right;

    /**
     * Creates a binary tree node with the specified data.
     * @param obj the element that will become a part of the new tree node
     */
    protected BinaryTreeNode(T obj) {
        element = obj;
        left = right = null;
    }

    /**
     * Returns the number of nom-null children of this node.
     * This method may be able to be written more efficiently.
     * @return the integer number of non-null children of this node
     */
    public int numChildren() {
        int children = 0;

        if (left != null)
            children = 1 + left.numChildren();
        if (right != null)
            children = children + 1 + right.numChildren();

        return children;
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }
}
