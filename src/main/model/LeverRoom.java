package main.model;

import main.data.impl.list.LinkedUnorderedList;
import main.game.Player;

public class LeverRoom extends Room {

    private final int correctLeverId;
    private final LinkedUnorderedList<Hall> hallsToUnlock;

    public LeverRoom(String id, String name, boolean hasTreasure, int correctLeverId) {
        super(id, name, hasTreasure);
        this.correctLeverId = correctLeverId;
        this.hallsToUnlock = new LinkedUnorderedList<>();
    }

    public boolean attemptSolve(int playerChoice, Player player) {

        if (isResolved()) {
            return true;
        }

        if (playerChoice == correctLeverId) {
            setResolved(true);
            unlockHalls();
            player.addActionToHistory("Solved LeverRoom: " + getName() + ". Passages unlocked.");
            return true;
        }
        player.addActionToHistory("Failed attempt to solve LeverRoom: " + getName() + ". Incorrect lever pulled.");
        return false;
    }

    public void unlockHalls(){
        for (Hall hall : hallsToUnlock) {
            hall.setBlock(false);
        }
    }

    public void addHallToUnlock(Hall hall) {
        hallsToUnlock.addToRear(hall);
    }


}
