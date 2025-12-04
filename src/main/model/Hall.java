package main.model;

import main.game.RandomEvent;
import main.game.Player;

public class Hall {

    private final Room destination;
    private final RandomEvent event;
    private boolean block;

    public Hall(Room destination) {
        this(destination, null, false);
    }

    public Hall(Room destination, RandomEvent event, boolean block) {
        this.destination = destination;
        this.event = event;
        this.block = block;
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

    public void activateEvent(Player j) {
        if (block) {
            return;
        }
        if (event != null) {
            event.activate(j);
        }
    }
}
