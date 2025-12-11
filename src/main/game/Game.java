package main.game;

import main.data.impl.list.ArrayUnorderedList;
import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.list.LinkedList;
import main.data.impl.queue.LinkedQueue;
import main.model.*;

import java.util.Random;
import java.util.Scanner;
import java.util.Iterator;

public class Game {

    private final Maze maze;
    private final LinkedQueue<Player> queueShifts;
    private int currentShift;
    private Player winner;
    private final Random random;
    private final DoubleLinkedUnorderedList<Player> allPlayers;
    private final LinkedList<Enigma> enigmas;
    private final Enigma enigmaManager;

    public Game(Maze maze, DoubleLinkedUnorderedList<Player> players, LinkedList<Enigma> enigmas) {
        this.maze = maze;
        this.queueShifts = new LinkedQueue<>();
        this.allPlayers = players;
        this.currentShift = 1;
        this.random = new Random();
        this.enigmas = enigmas;

        this.enigmaManager = new Enigma();
        if (this.enigmas != null && !this.enigmas.isEmpty()) {
            this.enigmaManager.initializeQueues(this.enigmas);
        }

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
        System.out.println("--- Game started: Labyrinth of Glory ---\n");

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
                (winner.getCurrentPosition() != null ? winner.getCurrentPosition().getName() : "<unknown>"));
        System.out.println("GAME OVER. Winner: " + winner.getName());
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

        if (hall == null) {
            if (current instanceof LeverRoom) {
                ((LeverRoom) current).attemptSolve(0, active);
                active.addActionToHistory("Attempted to solve LeverRoom " + current.getName() + ".");
            } else if (current instanceof EnigmaRoom) {
                active.addActionToHistory("Attempted to solve EnigmaRoom " + current.getName() + ".");
            }
            return;
        }

        boolean canEnter = hall.activateEvent(active, this);
        if (!canEnter) {
            active.addActionToHistory("Movement blocked to " + next.getName());
            System.out.println("-> " + active.getName() + " could not enter " + next.getName());
            return;
        }

        active.setCurrentPosition(next);
        active.addActionToHistory("Movement: " + current.getId() + " -> " + next.getName());

        // Se já houve um evento que concedeu EXTRA_MOVE, tenta o movimento extra (também verifica evento na passagem)
        if (active.getHistoricalActions().last().contains("EXTRA_MOVE")) {
            Room extraNext = active.chooseMovement(this);
            if (extraNext != null) {
                Hall extraHall = getHallToDestination(next, extraNext);
                if (extraHall != null) {
                    boolean canEnterExtra = extraHall.activateEvent(active, this);
                    if (canEnterExtra) {
                        active.setCurrentPosition(extraNext);
                        active.addActionToHistory("Extra Movement: " + next.getId() + " -> " + extraNext.getName());
                        System.out.println("-> " + active.getName() + " made an extra move to " + extraNext.getName());
                    } else {
                        active.addActionToHistory("Extra movement blocked to " + extraNext.getName());
                    }
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
        ArrayUnorderedList<Room> entriesList = new ArrayUnorderedList<>();
        for (Room r : entries) entriesList.addToRear(r);

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
                int target = roundIndex % entriesList.size();
                int counter = 0;
                for (Room r : entriesList) {
                    if (counter == target) {
                        start = r;
                        break;
                    }
                    counter++;
                }
                roundIndex++;
            } else {
                System.out.println("\n--- Entrance Room ---");
                int i = 1;
                for (Room r : entriesList) {
                    System.out.println(i + ") " + r.getName() + " (ID: " + r.getId() + ")");
                    i++;
                }
                System.out.println("\n Player " + p.getName() + ", choose your entrance:");
                String line = scanner.nextLine().trim();

                try {
                    int choice = Integer.parseInt(line);
                    if (choice >= 1 && choice <= entriesList.size()) {
                        int counter = 1;
                        for (Room r : entriesList) {
                            if (counter == choice) {
                                start = r;
                                break;
                            }
                            counter++;
                        }
                    } else {
                        System.out.println("Invalid option. Using default.");
                        int target = roundIndex % entriesList.size();
                        int counter = 0;
                        for (Room r : entriesList) {
                            if (counter == target) {
                                start = r;
                                break;
                            }
                            counter++;
                        }
                        roundIndex++;
                    }
                } catch (NumberFormatException e) {
                    Room matched = null;
                    for (Room r : entriesList) {
                        if (r.getName().equalsIgnoreCase(line)) {
                            matched = r;
                            break;
                        }
                    }
                    if (matched == null) {
                        start = matched;
                    } else {
                        System.out.println("Invalid option. Using default.");
                        int target = roundIndex % entriesList.size();
                        int counter = 0;
                        for (Room r : entriesList) {
                            if (counter == target) {
                                start = r;
                                break;
                            }
                            counter++;
                        }
                        roundIndex++;
                    }
                }
            }
            p.setCurrentPosition(start);
            p.setBlockedShifts(0);
            allPlayers.addToRear(p);
            queueShifts.enqueue(p);


            System.out.println("Added player: " + p.getName() + " starting at " +
                    (start != null ? start.getName() : "<none>"));
        }
    }

    private Enigma findEnigmaById(String id) {
        if (id == null || enigmas == null) return null;
        for (Enigma e : enigmas) {
            if (id.equals(e.getIdEnigma())) {
                return e;
            }
        }
        return null;
    }

    public boolean attemptEnterEnigmaRoom(Player player, EnigmaRoom room) {
        Scanner scanner = new Scanner(System.in);

        Enigma enigma = null;
        if (enigma == null) {
            enigma = enigmaManager.getNextEnigma();
        }

        // se ainda não há enigma disponível, mantém comportamento anterior e permite entrada
        if (enigma == null) {
            return true;
        }

        System.out.println(player.getName() + ", resolva o enigma: ");
        System.out.println(enigma.getQuestion());
        System.out.print("Resposta: ");
        String answer = scanner.nextLine().trim();

        boolean solved = room.attemptSolve(answer, enigma, player);

        if (!solved) {
            player.addActionToHistory("Blocked entering " + room.getName() + " due to wrong enigma answer.");
        }

        return solved;
    }

}



