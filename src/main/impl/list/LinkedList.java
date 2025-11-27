package main.impl.list;

import main.execption.ElementNotFoundExecption;
import main.execption.EmptyCollectionExecption;

public class LinkedList<T> {

    private int count;
    private LinearNode<T> head, tail;

    public LinkedList() {
        count = 0;
        head = tail = null;
    }

    public void add (T element) {
        LinearNode<T> newNode = new LinearNode<T>(element);

        if (head == null) {
            head = tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;

        }

        count++;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    /**
     * Removes and returns the specified element from this list and returns a reference to it.
     * //@param targetElement
     * @return T the element that was removed
     * @throws EmptyCollectionExecption if the list is empty
     * @throws ElementNotFoundExecption if the specified element is not found in the list
     */
    /*
    public T remove (T targetElement) throws EmptyCollectionExecption, ElementNotFoundExecption {
        if(isEmpty()) {
            throw new EmptyCollectionExecption("Linked List");
        }

        boolean found = false;
        LinearNode<T> previous = null;
        LinearNode<T> current = head;

        while (current != null && !found) {
            if (targetElement.equals(current.getElement())) {
                found = true;
            } else {
                previous = current;
                current = current.getNext();
            }
        }

        if (!found) {
            throw new ElementNotFoundExecption("List");
        }

        if (size() == 1){
            head = tail = null;
        } else if (current.equals(head)) {
            head = head.getNext();
        } else if (current.equals(tail)) {
            tail = previous;
            tail.setNext(null);
        } else {
            previous.setNext(current.getNext());
        }

        count--;
        return current.getElement();
    }
    */

    @Override
    public String toString() {
        String result = "";

        LinearNode<T> current = head;
        while (current != null) {
            result += current.getElement().toString() + " ";
            current = current.getNext();
        }
        return result;
    }

}
