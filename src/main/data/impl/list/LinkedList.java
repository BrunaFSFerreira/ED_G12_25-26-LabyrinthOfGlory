package main.data.impl.list;

import main.data.adt.ListADT;
import main.data.execption.ElementNotFoundExecption;
import main.data.execption.EmptyCollectionExecption;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<T> implements ListADT<T> {
    protected int count;
    protected LinearNode<T> head, tail;

    public LinkedList() {
        count = 0;
        head = tail = null;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Lista vazia");
        }
        T result = head.getElement();
        head = head.getNext();
        if (head == null) {
            tail = null;
        }
        count--;
        return result;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Lista vazia");
        }
        if (head == tail) {
            T result = head.getElement();
            head = tail = null;
            count = 0;
            return result;
        }
        LinearNode<T> current = head;
        while (current.getNext() != tail) {
            current = current.getNext();
        }
        T result = tail.getElement();
        tail = current;
        tail.setNext(null);
        count--;
        return result;
    }

    /**
     * Removes and returns the specified element from this list and returns a reference to it.
     * //@param targetElement
     * @return T the element that was removed
     * @throws EmptyCollectionExecption if the list is empty
     * @throws ElementNotFoundExecption if the specified element is not found in the list
     */
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

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException("Lista vazia");
        }
        return head.getElement();
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException("Lista vazia");
        }
        return tail.getElement();
    }

    @Override
    public boolean contains(T target) {
        LinearNode<T> current = head;
        while (current != null) {
            T el = current.getElement();
            if ((target == null && el == null) || (target != null && target.equals(el))) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private LinearNode<T> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (current == null) throw new NoSuchElementException();
            T elem = current.getElement();
            current = current.getNext();
            return elem;
        }
    }

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