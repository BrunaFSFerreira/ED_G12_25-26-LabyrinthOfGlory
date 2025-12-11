package main.model;

import main.game.Game;
import main.game.RandomEvent;
import main.game.Player;

public class Hall {

    private final Room destination;
    private final RandomEvent event;
    private boolean block;
    private final int size;

    public Hall(Room destination) {
        this(destination, null, false, 1);
    }

    public Hall(Room destination, int size) {
        this(destination, null, false, size);
    }

    public Hall(Room destination, RandomEvent event, boolean block, int size) {
        this.destination = destination;
        this.event = event;
        this.block = block;
        this.size = size;
    }

    public Room getDestination() {
        return destination;
    }

    public RandomEvent getEvent() {
        return event;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public int getSize() {
        return size;
    }

    public boolean activateEvent(Player player, Game game) {
        if (block) {
            return false;
        }

        if (event != null) {
            event.activate(player, game);
        }

        return true;
    }
}