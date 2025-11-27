package main.impl.list;

import main.adt.UnorderedListADT;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayUnorderedList<T>  extends ArrayList<T> implements UnorderedListADT<T> {
    public ArrayUnorderedList() {
        super();
    }

    public ArrayUnorderedList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public void addToFront(T element) {
        if(size() == list.length) {
            extandCapacity();
        }
        // Shift elements to the right to make space at the front
        for (int i = rear; i > 0; i--) {
            list[i] = list[i - 1];
        }
        list[0] = element;
        ++rear;
        ++modCount;
    }

    @Override
    public void addToRear(T element) {
        if(size() == list.length) {
            extandCapacity();
        }
        list[rear++] = element;
        ++modCount;
    }

    @Override
    public void addAfter(T element, T target) {
        if(size() == list.length) {
            extandCapacity();
        }
        int index = find(target);
        if (index == -1) {
            throw new NoSuchElementException("Elemento alvo não encontrado");
        }
        // Shift elements to the right to make space after the target
        for (int i = rear; i > index + 1; i--) {
            list[i] = list[i - 1];
        }
        list[index + 1] = element;
        ++rear;
        ++modCount;

    }
    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<T> {
        private int current = 0;
        private int expectedModCount = modCount;
        private int lastRet = -1;

        @Override
        public boolean hasNext() {
            return current < rear;
        }

        @Override
        public T next() {
            if (expectedModCount != modCount) {
                throw new java.util.ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastRet = current;
            @SuppressWarnings("unchecked")
            T result = (T) list[current++];
            return result;
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            if (expectedModCount != modCount) {
                throw new java.util.ConcurrentModificationException();
            }
            // shift left from lastRet+1 .. rear-1
            for (int i = lastRet + 1; i < rear; i++) {
                list[i - 1] = list[i];
            }
            list[--rear] = null;
            ++modCount;
            expectedModCount = modCount;
            current = lastRet;
            lastRet = -1;
        }
    }
}