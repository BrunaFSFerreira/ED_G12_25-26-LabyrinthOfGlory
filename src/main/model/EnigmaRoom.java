package main.model;

import main.game.Player;
import main.utils.RoomType;

public class EnigmaRoom extends Room {


    public EnigmaRoom(String id, String name, boolean hasTreasure) {
        super(id, name, hasTreasure, RoomType.PUZZLE);
        setResolved(false);
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
