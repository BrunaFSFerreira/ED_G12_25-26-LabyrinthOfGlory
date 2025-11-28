package main.data.impl.list;

import main.data.adt.OrderedListADT;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class ArrayOrderedList<T> extends ArrayList<T> implements OrderedListADT<T> {

    public ArrayOrderedList() {
        super();
    }

    public ArrayOrderedList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public void add(T element) {
        if(size() == list.length) {
            extandCapacity();
        }
        int i = 0;
        // Find the correct position to insert the element
        while (i < rear && ((Comparable<T>) list[i]).compareTo(element) < 0) {
            i++;
        }

        // Shift elements to the right to make space
        for (int j = rear; j > i; j--) {
            list[j] = list[j - 1];
        }

        list[i] = element;
        ++rear;
        ++modCount;
    }

    @Override
    public Iterator<T> iterator() {
        return new BasicIterator();
    }

    private class BasicIterator implements Iterator<T> {
        private int current;
        private int expectedModCount;
        private boolean okToRemove = false;

        public BasicIterator() {
            current = 0;
            expectedModCount = modCount;
        }

        public boolean hasNext() {
            return current < rear;
        }

        public T next() throws IllegalStateException {
            if (current >= rear) {
                throw new IllegalStateException("No more elements in the list");
            }

            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException("The list has been modified since the iterator was created");
            }

            okToRemove = true;
            expectedModCount = modCount;
            return list[current++];
        }

        public void remove() {
            if (!okToRemove) {
                throw new IllegalStateException("Cannot remove element before calling next()");
            }

            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException("The list has been modified since the iterator was created");
            }

            ArrayOrderedList.this.remove(list[--current]);
            expectedModCount = modCount;
            okToRemove = false;
        }
    }
}
