package main.data.impl.list;

public class LinkedListSentinela<T> {
    private LinearNode<T> sentinel;

    public LinkedListSentinela() {
        sentinel = new LinearNode<T>(null);
    }

    public void add(T element) {
        LinearNode<T> newNode = new LinearNode<T>(element);
        LinearNode<T> current = sentinel;

        while (current.getNext() != null) {
            current = current.getNext();
        }
        current.setNext(newNode);

    }

    public T remove(T element) throws Exception {
        System.out.print("[EX2] Antes do remove: ");
        printPointers();

        LinearNode<T> previous = sentinel;
        LinearNode<T> current = sentinel.getNext();

        while (current != null) {
            if (element.equals(current.getElement())) {
                previous.setNext(current.getNext());
                System.out.print("[EX2] Depois do remove: ");
                printPointers();
                return current.getElement();
            }
            previous = current;
            current = current.getNext();
        }
        throw new Exception("Elemento nao encontrado");

    }

    private void printPointers() {
        LinearNode<T> current = sentinel;
        System.out.print("sentinela -> ");
        while (current.getNext() != null) {
            current = current.getNext();
            System.out.print(current.getElement() + " -> ");
        }
        System.out.println("null");
    }

    @Override
    public String toString() {
        String result = "";
        LinearNode<T> current = sentinel.getNext();
        while (current != null) {
            result += current.getElement().toString() + " ";
            current = current.getNext();
        }
        return result.trim();
    }
}
