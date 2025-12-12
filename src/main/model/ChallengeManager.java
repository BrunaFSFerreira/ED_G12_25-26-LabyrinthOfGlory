package main.model;

import main.data.impl.list.LinkedList;
import main.data.impl.list.LinkedOrderedList;
import main.data.impl.list.LinkedUnorderedList;
import main.data.impl.queue.LinkedQueue;

import java.util.Random;

public class ChallengeManager {

    private LinkedQueue<EnigmaData> poll = new LinkedQueue<>();
    private LinkedQueue<EnigmaData> available = new LinkedQueue<>();
    private final Random random = new Random();

    public ChallengeManager(LinkedList<EnigmaData> listEnigmas) {
        initializeQueues(listEnigmas);
    }

    private void initializeQueues(LinkedList<EnigmaData> listEnigmas) {
        for (EnigmaData enigma : listEnigmas) {
            available.enqueue(enigma);
        }
    }

    public EnigmaData getNextEnigma() {
        if (available.isEmpty()){
            recicleEnigmas();
        }
        if (available.isEmpty()){
            return null;
        }

        EnigmaData enigma = available.dequeue();
        poll.enqueue(enigma);

        return enigma;
    }

    private void recicleEnigmas() {
        while (!poll.isEmpty()) {
            available.enqueue(poll.dequeue());
        }
    }
}
