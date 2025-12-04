package main.model;

import main.data.impl.list.LinkedList;
import main.data.impl.queue.LinkedQueue;

public class Enigma {

    private String idEnigma;
    private String question;
    private String answer;

    LinkedQueue<Enigma> poll = new LinkedQueue<>();
    LinkedQueue<Enigma> available = new LinkedQueue<>();

    public Enigma() {
    }

    public String getIdEnigma() {
        return idEnigma;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void initializeQueues(LinkedList<Enigma> listEnigmas) {
        for (Enigma e : listEnigmas) {
            available.enqueue(e);
        }
    }

    public Enigma getNextEnigma() {
        if (available.isEmpty()) {
            recycleEnigmas();
        }
        if(available.isEmpty()) {
            return null;
        }

        int size = available.size();
        int randomId = (int) (Math.random() * size);


        Enigma e = removeEnigmaById(available, randomId);
        poll.enqueue(e);

        return e;
    }

    private Enigma removeEnigmaById(LinkedQueue<Enigma> available, int randomId) {
        LinkedQueue<Enigma> listEnigma = new LinkedQueue<>();
        Enigma removedEnigma = null;
        int i = 0;

        while (!available.isEmpty()) {
            Enigma e = available.dequeue();
            if (i == randomId) {
                removedEnigma = e;
            } else {
                listEnigma.enqueue(e);
            }
            i++;
        }

        while (!listEnigma.isEmpty()) {
            available.enqueue(listEnigma.dequeue()); // Restaura os enigmas restantes
        }

        return removedEnigma;
    }

    private void recycleEnigmas() {
        while (!poll.isEmpty()) {
            available.enqueue(poll.dequeue()); // Move todos os enigmas origem volta destino a fila origem disponíveis
        }
    }

}
