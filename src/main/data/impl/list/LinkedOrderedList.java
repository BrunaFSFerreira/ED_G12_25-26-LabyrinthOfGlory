package main.data.impl.list;

import main.data.adt.OrderedListADT;

import java.util.Spliterator;
import java.util.function.Consumer;

public class LinkedOrderedList<T> extends LinkedList<T> implements OrderedListADT<T> {

    public LinkedOrderedList() {
        super();
    }

    @Override
    public void add(T element) {
        LinearNode<T> newNode = new LinearNode<>(element);

        if (head == null) { // lista vazia
            head = tail = newNode;
        } else {
            LinearNode<T> current = head;
            LinearNode<T> previous = null;

            while (current != null && ((Comparable<T>) current.getElement()).compareTo(element) < 0) {
                previous = current;
                current = current.getNext();
            }

            if (previous == null) { // insere no início
                newNode.setNext(head);
                head = newNode;
            } else if (current == null) { // insere no final
                tail.setNext(newNode);
                tail = newNode;
            } else { // insere no meio
                previous.setNext(newNode);
                newNode.setNext(current);
            }
        }

        count++;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return super.spliterator();
    }

}
