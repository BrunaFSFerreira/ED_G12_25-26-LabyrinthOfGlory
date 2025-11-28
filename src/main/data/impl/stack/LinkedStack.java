package main.data.impl.stack;

import main.data.impl.list.LinearNode;

public class LinkedStack<T> {
    private LinearNode<T> top;
    private int size;

    /**
     * Creates an empty stack
     */
    public LinkedStack() {
        top = null;
        size = 0;
    }

    /**
     * Adds the specified element to the top of this stack
     * @param element
     */
    public void push(T element) {
        LinearNode<T> newNode = new LinearNode<>(element);
        newNode.setNext(top);
        top = newNode;
        size++;
    }

    /**
     * Removes the element at the top of this stack and returns a reference to it.
     * @return
     */
    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("main.java.impl.Stack empty");
        }
        T result = top.getElement();
        top = top.getNext();
        size--;
        return result;
    }

    /**
     * Returns a reference to the element at the top of this stack.
     * The element is not removed from the stack.
     * @return
     */
    public T peek() {
        if (isEmpty()) {
            System.out.println("The stack is empty");
            return null;
        }
        return top.getElement();
    }


    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        String result = "";

        LinearNode<T> current = top;
        while (current != null) {
            result += current.getElement().toString() + " ";
            current = current.getNext();
        }
        return result;


    }
}
