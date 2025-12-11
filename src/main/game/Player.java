package main.game;

import main.data.impl.list.DoubleLinkedUnorderedList;
import main.model.Room;

public abstract class Player {
    private final String name;
    private Room currentPosition;
    private int blockedShifts;
    private final DoubleLinkedUnorderedList<String> historicalActions;
    private Room initialPosition;

    public Player(String name, Room startingPosition) {
        this.name = name;
        this.currentPosition = startingPosition;
        this.blockedShifts = 0;
        this.historicalActions = new DoubleLinkedUnorderedList<>();
    }

    public String getName() { return name; }
    public Room getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Room newPosition) { this.currentPosition = newPosition; }
    public int getBlockedShifts() { return blockedShifts; }
    public void setBlockedShifts(int shifts) { this.blockedShifts = shifts; }
    public DoubleLinkedUnorderedList<String> getHistoricalActions() { return historicalActions; }    public void setInitialPosition(Room initialPosition) {
        this.initialPosition = initialPosition;
    }

    public Room getInitialPosition() {
        return initialPosition;
    }

    public void addActionToHistory(String action) {
        this.historicalActions.addToRear(action);
    }

    public Room chooseMovement(Game game) {
        // Implementação base para o modo Manual (deve ser refinado para UI/Input)
        return null;
    }
}
