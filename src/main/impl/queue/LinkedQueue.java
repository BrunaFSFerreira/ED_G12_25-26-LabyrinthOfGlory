package main.impl.queue;


import main.adt.QueueADT;
import main.impl.list.LinearNode;

public class LinkedQueue<T> implements QueueADT<T> {
    private LinearNode<T> front;
    private LinearNode<T> rear;
    private int size;

    public LinkedQueue() {
        front = rear = null;
        size = 0;
    }

    @Override
    public void enqueue(T element) {
        LinearNode<T> node = new LinearNode<>(element);
        if (isEmpty()) {
            front = rear = node;
        } else {
            rear.setNext(node);
            rear = node;
        }
        size++;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T elem = front.getElement();
        front = front.getNext();
        size--;
        if (front == null) {
            rear = null;
        }
        return elem;
    }

    @Override
    public T first() {
        return isEmpty() ? null : front.getElement();
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        String result = "";

        LinearNode<T> current = front;
        while (current != null) {
            result += current.getElement().toString() + " ";
            current = current.getNext();
        }
        return result;
    }
}
