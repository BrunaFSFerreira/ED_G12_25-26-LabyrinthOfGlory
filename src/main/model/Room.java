package main.model;

import main.data.impl.list.LinkedUnorderedList;
import main.utils.RoomType;

public abstract class Room {

    private String id;
    private String name;
    private final LinkedUnorderedList<Hall> neighbors = new LinkedUnorderedList<>();
    private boolean hasTreasure;
    private boolean resolved;
    private RoomType type;

    private int x;
    private int y;

    public Room() {}

    public Room(String id, String name, boolean hasTreasure, RoomType type) {
        this.id = id;
        this.name = name;
        this.hasTreasure = hasTreasure;
        this.type = type;
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

    public LinkedUnorderedList<Hall> getNeighbors() {
        return neighbors;
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

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
