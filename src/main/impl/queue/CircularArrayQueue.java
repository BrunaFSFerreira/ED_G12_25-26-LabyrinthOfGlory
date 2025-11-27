package main.impl.queue;

import main.adt.QueueADT;

public class CircularArrayQueue<T> implements QueueADT<T> {

    private static final int DEFAULT_CAPACITY = 10;
    private int front, rear, count;
    private T[] queue;

    public CircularArrayQueue(int capacity) {
        front = rear = 0;
        count = 0;
        queue = (T[]) new Object[capacity];
    }

    public CircularArrayQueue() {
        this(DEFAULT_CAPACITY);
    }

    public void enqueue(T element) {
        if (count == queue.length) {
            expandCapacity();
        }
        queue[rear] = element;
        rear = (rear + 1) % queue.length;
        count++;
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Fila está vazia");
        }
        T result = queue[front];
        queue[front] = null;
        front = (front + 1) % queue.length;
        count--;
        return result;
    }

    public T first() {
        if (isEmpty()) {
            throw new IllegalStateException("Fila está vazia");
        }
        return queue[front];
    }

    private void expandCapacity() {
        int newCapacity = queue.length * 2;
        T[] newQueue = (T[]) new Object[newCapacity];
        for (int i = 0; i < count; i++) {
            newQueue[i] = queue[(front + i) % queue.length];
        }
        queue = newQueue;
        front = 0;
        rear = count;
    }


    public boolean isEmpty() {
        return count == 0;
    }


    public int size() {
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(", ");
            sb.append(queue[(front + i) % queue.length]);
        }
        sb.append("]");
        return sb.toString();
    }
}
