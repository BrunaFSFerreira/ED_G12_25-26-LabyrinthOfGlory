package main.game;

import main.data.impl.list.ArrayUnorderedList;
import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.queue.LinkedQueue;
import main.model.*;

import java.util.*;

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

    public void endGame(Player winner) {
        if (winner == null) return;
        this.winner = winner;
        winner.addActionToHistory("WON: entered treasure room " +
                (winner.getCurrentPosition() != null ? winner.getCurrentPosition().getId() : "<unknown>"));
        System.out.println("GAME OVER. Winner: " + winner.getName());
        // Esvazia a fila de turnos para garantir que o loop termine
        while (queueShifts.size() > 0) {
            queueShifts.dequeue();
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

    public void addPlayers(ArrayUnorderedList<Player> players) {

        if (players == null || players.isEmpty()) return;

        DoubleLinkedUnorderedList<Room> entries = maze.getEntries();
        List<Room> entriesList = new ArrayList<>();
        for (Room r : entries) entriesList.add(r);

        Iterator<Room> roomsIt = maze.getRooms().iterator();
        Room fallback = roomsIt.hasNext() ? roomsIt.next() : null;

        Scanner scanner = new Scanner(System.in);

        int roundIndex = 0;

        for (Player p : players) {
            if (p == null) continue;

            Room start = null;

            if (entriesList.isEmpty()) {
                start = fallback;
            } else if (p instanceof Bot) {
                start = entriesList.get(roundIndex % entriesList.size());
                roundIndex++;
            } else {
                System.out.println("\nEntrances: ");
                for (int i = 0; i < entriesList.size(); i++) {
                    Room r = entriesList.get(i);
                    System.out.println((i + 1) + ") " + r.getName() + " (" + r.getId() + ")");
                }
                System.out.println("Select an entrance: ");
                String line = scanner.nextLine().trim();


                try {
                    int choice = Integer.parseInt(line);
                    if (choice >= 1 && choice <= entriesList.size()) {
                        start = entriesList.get(choice - 1);
                    } else {
                        System.out.println("Invalid option. Using default.");
                        start = entriesList.get(roundIndex % entriesList.size());
                        roundIndex++;
                    }
                } catch (NumberFormatException ex) {
                    Room matched = null;
                    for (Room r : entriesList) {
                        if (r.getId().equalsIgnoreCase(line)) {
                            matched = r;
                            break;
                        }
                    }
                    if (matched != null) {
                        start = matched;
                    } else {
                        System.out.println("Invalid option. Using default.");
                        start = entriesList.get(roundIndex % entriesList.size());
                        roundIndex++;
                    }
                }
            }


            p.setCurrentPosition(start);
            p.setInitialPosition(start);
            p.setBlockedShifts(0);

            allPlayers.addToRear(p);
            queueShifts.enqueue(p);

            System.out.println("Added player: " + p.getName() + " starting at " +
                    (start != null ? start.getName() : "<none>"));
        }
    }

}

