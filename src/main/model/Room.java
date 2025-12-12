package main.model;

import main.data.impl.list.LinkedUnorderedList;

public abstract class Room {

    private String id;
    private String name;
    private final LinkedUnorderedList<Hall> neighbors = new LinkedUnorderedList<>();
    private boolean hasTreasure;
    private boolean isEntrance;
    private boolean isChallengeResolved;
    private Challenge challenge;
    private final LinkedUnorderedList<Hall> hallsToUnlock = new LinkedUnorderedList<>();

    private int x;
    private int y;

    public Room() {}

    public Room(String id, String name, boolean hasTreasure) {
        this.id = id;
        this.name = name;
        this.hasTreasure = hasTreasure;
        this.isChallengeResolved = false;
        this.challenge = null;
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

    public boolean isEntrance() {
        return isEntrance;
    }

    public void setEntrance(boolean entrance) {
        isEntrance = entrance;
    }

    public boolean isChallengeResolved() {
        return isChallengeResolved;
    }

    public void setChallengeResolved(boolean resolved) {
        this.isChallengeResolved = resolved;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public LinkedUnorderedList<Hall> getHallsToUnlock() {
        return hallsToUnlock;
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