package main.impl.stack;

import main.adt.StackADT;
import main.execption.EmptyCollectionExecption;

public class ArrayStack<T> implements StackADT<T> {

    /**
     * constant to represent the default capacity of the array
     */
    private final int DEFAULT_CAPACITY = 10;

    /**
     * int that represents both the number of elements and the nest
     * available position in the array
     */
    private int top;

    /**
     * array of generic elements to represent the stack
     */
    private T[] stack;

    /**
     * Creates an empty stack using the default capacity
     */
    public ArrayStack() {
        top = 0;
        stack = (T[]) (new Object[DEFAULT_CAPACITY]);
    }

    /**
     * Creates an empty stack using the specified capacity.
     * @param initialCapacity represents the specified capacity
     */
    public ArrayStack(int initialCapacity) {
        top = 0;
        stack = (T[]) (new Object[initialCapacity]);
    }

    /**
     * Adds the specified element to the top od thid stack,
     * expanding the capacity of the stack array if necessary
     * @param element generic element to be pushed onto stack
     */
    public void push(T element) {
        if (size() == stack.length)
            expandCapacity();

        stack[top] = element;
        top++;
    }

    private void expandCapacity() {
        T[] newStack = (T[]) (new Object[stack.length * 2]);
        for (int i = 0; i < stack.length; i++) {
            newStack[i] = stack[i];
        }
        stack = newStack;
    }

    /**
     * Removes the element at the top of this stack and returns a reference to it.
     * Throws an EmptyCollectionException if the stack is empty.
     * @return T element removed from the top of the stack
     * @throws EmptyCollectionExecption if a pop is attempted on an empty stack
     */
    public T pop() throws EmptyCollectionExecption {
        if (isEmpty())
            throw new EmptyCollectionExecption("Stack");

        top--;
        T result = stack[top];
        stack[top] = null;

        return result;
    }

    /**
     * Returns a reference to the element at the top of this stack.
     * The element is not removed from the stack.
     * Throws an EmptyCollectionException if the stack is empty.
     * @return T element on top of the stack
     * @throws EmptyCollectionExecption if a peek is attempted on an empty stack
     */
    public T peek() throws EmptyCollectionExecption {
        if (isEmpty())
            throw new EmptyCollectionExecption("Stack");

        return stack[top - 1];
    }

    public boolean isEmpty() {
        return top == 0;
    }

    public int size() {
        return top;
    }
}
