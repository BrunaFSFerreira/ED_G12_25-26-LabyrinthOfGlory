package main.execption;

public class ElementNotFoundExecption extends Exception {
    public ElementNotFoundExecption() {
        super("Element not found in the data structure.");
    }

    public ElementNotFoundExecption(String message) {
        super(message);
    }
}