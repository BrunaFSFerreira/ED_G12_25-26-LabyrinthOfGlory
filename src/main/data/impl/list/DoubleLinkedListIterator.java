package main.data.impl.list;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.function.Supplier;

public class DoubleLinkedListIterator<E> implements Iterator<E> {

    private DoubleNode<E> current;
    private final int expextedModCount;
    private final Supplier<Integer> modCountSupplier;

    public DoubleLinkedListIterator(int expextedModCount, Supplier<Integer> modCountSupplier, DoubleNode<E> current) {
        this.expextedModCount = expextedModCount;
        this.modCountSupplier = modCountSupplier;
        this.current = current;
    }

    @Override
    public boolean hasNext() {
        // Verifica se a lista foi modificada durante a iteração
        if (expextedModCount != modCountSupplier.get()) {
            throw new ConcurrentModificationException();
        }
        // Verifica se há um próximo elemento
        return current != null;
    }

    @Override
    public E next() {
        if (expextedModCount != modCountSupplier.get()) {
            throw new ConcurrentModificationException();
        }
        // Retorna o elemento atual e avança para o próximo
        if (current == null) {
            throw new IllegalStateException("No more elements in the list");
        }
        // Retorna o elemento atual e avança para o próximo
        E elem = current.getElement();
        // Avança para o próximo nó
        current = current.getNext();
        return elem;
    }
}
