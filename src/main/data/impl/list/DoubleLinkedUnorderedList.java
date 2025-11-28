package main.data.impl.list;

import main.data.adt.UnorderedListADT;

public class DoubleLinkedUnorderedList<E> extends DoubleLinkedList<E> implements UnorderedListADT<E> {

    public DoubleLinkedUnorderedList() {
        super();
    }


    @Override
    public void addToFront(E element) {
        DoubleNode<E> newNode = new DoubleNode<>(element);
        if (isEmpty()) {
            // Se a lista estiver vazia, o novo nó é tanto a cabeça quanto a cauda
            head = tail = newNode;
        } else {
            // Caso contrário, insere o novo nó no início da lista
            newNode.setNext(head);
            head.setPrevious(newNode);
            head = newNode;
        }
        count++;
        modCount++;
    }

    @Override
    public void addToRear(E element) {
        // Cria um novo nó com o elemento fornecido
        DoubleNode<E> newNode = new DoubleNode<>(element);
        if (isEmpty()) {
            // Se a lista estiver vazia, o novo nó é tanto a cabeça quanto a cauda
            head = tail = newNode;
        } else {
            // Caso contrário, insere o novo nó no final da lista
            tail.setNext(newNode);
            newNode.setPrevious(tail);
            tail = newNode;
        }
        count++;
        modCount++;
    }

    @Override
    public void addAfter(E element, E target) {
        DoubleNode<E> newNode = new DoubleNode<>(element);
        DoubleNode<E> current = head;

        // Percorre a lista para encontrar o nó com o elemento alvo
        while (current != null) {
            E el = current.getElement();
            if ((target == null && el == null) || (target != null && target.equals(el))) {
                // Insere o novo nó após o nó encontrado
                newNode.setNext(current.getNext());
                newNode.setPrevious(current);
                if (current.getNext() != null) {
                    current.getNext().setPrevious(newNode);
                } else {
                    tail = newNode; // Atualiza a cauda se estiver inserindo no final
                }
                current.setNext(newNode);
                count++;
                modCount++;
                return;
            }
            current = current.getNext();
        }
        throw new IllegalArgumentException("Elemento alvo não encontrado na lista.");

    }
}
