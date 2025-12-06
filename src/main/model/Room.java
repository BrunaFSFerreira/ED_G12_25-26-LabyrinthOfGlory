package main.model;

import main.data.impl.list.LinkedUnorderedList;

public abstract class Room {

    private String id;
    private String name;
    private final LinkedUnorderedList<Hall> neighbors = new LinkedUnorderedList<>();
    private boolean hasTreasure;
    private boolean resolved;

    public Room() {}

    public Room(String id, String name, boolean hasTreasure) {
        this.id = id;
        this.name = name;
        this.hasTreasure = hasTreasure;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasTreasure() {
        return hasTreasure;
    }

    public void setHasTreasure(boolean hasTreasure) {
        this.hasTreasure = hasTreasure;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public LinkedUnorderedList<Hall> getNeighbors() {
        return neighbors;
    }
}
