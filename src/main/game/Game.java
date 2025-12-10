package main.game;

import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.queue.LinkedQueue;
import main.model.*;
import main.utils.EventType;

import java.util.List;
import java.util.Random;

public class Game {

    private final Maze maze;
    private final LinkedQueue<Player> queueShifts;
    private int currentShift;
    private Player winner;
    private final Random random;
    private final DoubleLinkedUnorderedList<Player> allPlayers;

    public Game(Maze maze, DoubleLinkedUnorderedList<Player> players) {
        this.maze = maze;
        this.queueShifts = new LinkedQueue<>();
        this.allPlayers = players;
        this.currentShift = 1;
        this.random = new Random();

        for (Player player : players) {
            queueShifts.enqueue(player);
        }
    }

    public DoubleLinkedUnorderedList<Player> getAllPlayers() {
        return allPlayers;
    }

    public int getCurrentShift() {
        return currentShift;
    }

    public void start() {
        System.out.println("--- Game started: Labyrinth of Glory\n ---");

        while (queueShifts.size() > 0 && winner == null) {
            Player active = queueShifts.dequeue();

            System.out.println("\n== Sift " + currentShift + " - Player: " + active.getName() + " ==");

            if (processBlock(active)) {
                queueShifts.enqueue(active);
                continue;
            }

            executePlay(active);

            if (checkVictory(active)) {
                winner = active;
                break;
            }

            queueShifts.enqueue(active);
            currentShift++;
        }

        if (winner != null) {
            System.out.println("GAME OVER. Winner: " + winner.getName());
        }
    }

    private boolean processBlock(Player active) {
        if (active.getBlockedShifts() > 0) {
            active.setBlockedShifts(active.getBlockedShifts() - 1);
            active.addActionToHistory("Blocked. Turn skipped. Remaining: " + active.getBlockedShifts());
            System.out.println("-> " + active.getName() + " is blocked. Skip the turn. Remainder: " + active.getBlockedShifts());
            return true;
        }
        return false;
    }

    private void executePlay(Player active) {
        Room current = active.getCurrentPosition();

        Room next = active.chooseMovement(this);

        if (next == null) {
            active.addActionToHistory("Decision: No valid move found/selected.");
            return;
        }

        Hall hall = getHallToDestination(current, next);

        if (hall == null || hall.isBlock()) {
            if (current instanceof LeverRoom) {
                ((LeverRoom) current).attemptSolve(0, active);
                active.addActionToHistory("Attempted to solve LeverRoom " + current.getId() + ".");
            } else if (current instanceof EnigmaRoom) {
                active.addActionToHistory("Attempted to solve EnigmaRoom " + current.getId() + ".");
            }
            return; // Não houve movimento.
        }
        active.setCurrentPosition(next);
        active.addActionToHistory("Movement: " + current.getId() + " -> " + next.getId());

        if (hall.getEvent() != null) { // Hall.getEvent() deve retornar a classe RandomEvent
            hall.getEvent().activate(active, this);
        }

        if (active.getHistoricalActions().last().contains("EXTRA_MOVE")) {
            Room extraNext = active.chooseMovement(this);
            if (extraNext != null) {
                Hall extraHall = getHallToDestination(next, extraNext);
                if (extraHall != null && !extraHall.isBlock()) {
                    active.setCurrentPosition(extraNext);
                    active.addActionToHistory("Extra Movement: " + next.getId() + " -> " + extraNext.getId());
                    System.out.println("-> " + active.getName() + " made an extra move to " + extraNext.getName());
                }
            }
        }

        System.out.println("-> " + active.getName() + " moved to " + next.getName());
    }

    public void swapAllPlayerPositions() {
        if (allPlayers.size() <= 1) return;

        // 1. Coletar todas as posições atuais na ordem de inserção original dos jogadores
        Room[] tempArray = new Room[allPlayers.size()];
        int index = 0;
        for (Player player : allPlayers) {
            tempArray[index++] = player.getCurrentPosition();
        }

        // 2. Embaralhar o array de posições (Algoritmo Fisher-Yates)
        for (int i = tempArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Room temp = tempArray[i];
            tempArray[i] = tempArray[j];
            tempArray[j] = temp;
        }

        // 3. Atribuir as novas posições embaralhadas de volta aos jogadores
        index = 0;
        for (Player player : allPlayers) {
            Room newPos = tempArray[index++];
            player.setCurrentPosition(newPos);
            player.addActionToHistory("Position changed by GENERAL_SWAP event to " + newPos.getName());
        }
    }

    public Player chooseRandomPlayer(Player exclusion) {
        return null;
    }

    private Hall getHallToDestination(Room origin, Room destination) {
        for (Hall hall : origin.getNeighbors()) {
            if (hall.getDestination().equals(destination)) {
                return hall;
            }
        }
        return null;
    }

    private boolean checkVictory(Player active) {
        return active.getCurrentPosition().isHasTreasure();
    }

    public Maze getMaze() {
        return maze;
    }


    public void addPlayers(List<Player> players) {

        if (players == null || players.isEmpty()) return;

        DoubleLinkedUnorderedList<Room> entries = maze.getEntries();
        java.util.Iterator<Room> itEntries = entries.iterator();
        java.util.Iterator<Room> roomsIt = maze.getRooms().iterator();
        Room fallback = roomsIt.hasNext() ? roomsIt.next() : null;

        for (Player p : players) {
            if (p == null) continue;

            // assign starting position from entries (round-robin) or fallback
            Room start = itEntries.hasNext() ? itEntries.next() : fallback;
            p.setCurrentPosition(start);
            p.setBlockedShifts(0);

            // add to collections and queue
            allPlayers.addToRear(p);
            queueShifts.enqueue(p);

            System.out.println("Added player: " + p.getName() + " starting at " +
                    (start != null ? start.getName() : "<none>"));
        }
    }
}

