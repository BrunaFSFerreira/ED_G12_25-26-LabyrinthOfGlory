package main.data.impl.list;

/**
 * DoubleNode represents a node in a doubly linked list.
 * @param <E>
 */
public class DoubleNode<E> {
    private DoubleNode<E> next;
    private E element;
    private DoubleNode<E> previous;

    /**
     * Creates an empty node.
     */
    public DoubleNode() {
        next = null;
        element = null;
        previous = null;
    }

    /**
     * Creates a node storing the specified element.
     * @param elem the element to be stored into the new node
     */
    public DoubleNode(E elem) {
        next = null;
        element = elem;
        previous = null;
    }


    public void setNext(DoubleNode<E> next) {
        this.next = next;
    }

    /**
     * Returns the node that follows this one.
     * @return the node that follows the current one
     */
    public DoubleNode<E> getNext() {
        return next;
    }

    /**
     * Sets the node that precedes this onde.
     * @param dnode the node to be set as the one to precede the current node
     */
    public void setPrevious (DoubleNode<E> dnode) {
        previous = dnode;
    }

    public DoubleNode<E> getPrevious() {
        return previous;
    }

    /**
     * Returns the element stored in this node.
     * @return the element stored in this node
     */
    public E getElement() {
        return element;
    }

    /**
     * Sets the element stored in this node.
     * @param elem the element to be stored in this node.
     */
    public void setElement(E elem) {
        element = elem;
    }
}
