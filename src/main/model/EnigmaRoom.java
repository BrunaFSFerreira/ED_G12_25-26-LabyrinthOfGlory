package main.model;

import main.game.Player;

public class EnigmaRoom extends Room {

    private final String enigmaId;

    public EnigmaRoom(String id, String name, boolean hasTreasure, String enigmaId) {
        super(id, name, hasTreasure);
        this.enigmaId = enigmaId;
        setResolved(false);
    }

    public String getEnigmaId() {
        return enigmaId;
    }

    public boolean attemptSolve(String playerAnswer, Enigma enigma, Player player) {

        if (isResolved()) {
            return true;
        }

        if (enigma.checkAnswer(playerAnswer)) {
            setResolved(true);
            player.addActionToHistory("Solved EnigmaRoom: " + getName() + ". Enigma answered correctly.");
            return true;
        }
        player.addActionToHistory("Failed attempt to solve EnigmaRoom: " + getName() + ". Incorrect answer provided.");
        return false;
    }

}
