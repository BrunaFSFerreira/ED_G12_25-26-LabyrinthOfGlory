package main.execption;

public class EmptyCollectionExecption extends Exception {
    public EmptyCollectionExecption() {
        super("The collection is empty.");
    }

    public EmptyCollectionExecption(String message) {
        super(message);
    }
}