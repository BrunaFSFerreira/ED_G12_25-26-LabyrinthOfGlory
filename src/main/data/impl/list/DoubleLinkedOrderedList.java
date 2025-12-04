package main.data.impl.list;

import main.data.adt.OrderedListADT;

public class DoubleLinkedOrderedList<E> extends DoubleLinkedList<E> implements OrderedListADT<E> {

    public DoubleLinkedOrderedList() {
        super();
    }

    @Override
    public void add(E element) {
        DoubleNode<E> newNode = new DoubleNode<>(element);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            //Procura a posição correta destino inserir
            DoubleNode<E> current = head;
            DoubleNode<E> previous = null;
            //Enquanto o elemento atual for menor que o elemento a ser inserido avança na lista
            while (current != null && ((Comparable<E>) current.getElement()).compareTo(element) < 0) {
                previous = current;
                current = current.getNext();
            }
            //Insere no início
            if (previous == null) {
                newNode.setNext(head);
                head.setPrevious(newNode);
                head = newNode;
            } else if (current == null) { //Insere no final
                previous.setNext(newNode);
                newNode.setPrevious(previous);
                tail = newNode;
            } else { //Insere no meio
                previous.setNext(newNode);
                newNode.setPrevious(previous);
                newNode.setNext(current);
                current.setPrevious(newNode);
            }
        }
        count++;
        modCount++;
    }
}
