package main.model;

import main.data.impl.list.LinkedList;
import main.data.impl.stack.LinkedStack;

import java.util.Iterator;
import java.util.Random;

public class ChallengeManager {

    private LinkedStack<EnigmaData> poll = new LinkedStack<>();
    private LinkedStack<EnigmaData> available = new LinkedStack<>();
    private final Random random = new Random();

    public ChallengeManager(LinkedList<EnigmaData> listEnigmas) {
        initializeStacks(listEnigmas);
    }

    private void initializeStacks(LinkedList<EnigmaData> listEnigmas) {
        int size = listEnigmas.size();
        EnigmaData[] enigmasArray = (EnigmaData[]) new EnigmaData[size];
        int i = 0;
        Iterator<EnigmaData> it = listEnigmas.iterator();
        while (it.hasNext()) {
            enigmasArray[i++] = it.next();
        }

        for (int j = size - 1; j > 0; j--) {
            int randomIndex = random.nextInt(j + 1);
            EnigmaData temp = enigmasArray[j];
            enigmasArray[j] = enigmasArray[randomIndex];
            enigmasArray[randomIndex] = temp;
        }

        for (EnigmaData enigma : enigmasArray) {
            available.push(enigma);
        }
    }
    public EnigmaData getNextEnigma() {
        if (available.isEmpty()){
            recycleEnigmas();
        }
        if (available.isEmpty()){
            return null;
        }

        EnigmaData enigma = available.pop();

        poll.push(enigma);

        return enigma;
    }

    private void recycleEnigmas() {
        while (!poll.isEmpty()) {
            available.push(poll.pop());
        }
    }
}
