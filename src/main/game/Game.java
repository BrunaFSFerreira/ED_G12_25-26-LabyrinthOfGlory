package main.game;

import main.data.impl.list.ArrayUnorderedList;
import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.list.LinkedList;
import main.data.impl.queue.LinkedQueue;
import main.model.*;
import main.utils.EventType;

import java.util.Random;
import java.util.Scanner;
import java.util.Iterator;

public class Game {

    private final Maze maze;
    private final LinkedQueue<Player> queueShifts;
    private int currentShift;
    public Player winner;
    private final Random random;
    private final DoubleLinkedUnorderedList<Player> allPlayers;
    private final Scanner scanner;

    private final ChallengeManager challengeManager;

    public Game(Maze maze, DoubleLinkedUnorderedList<Player> players, LinkedList<EnigmaData> enigmas, Scanner scanner) {
        this.maze = maze;
        this.queueShifts = new LinkedQueue<>();
        this.allPlayers = players;
        this.currentShift = 1;
        this.random = new Random();
        this.scanner = scanner;

        // Inicializa o manager com a lista de enigmas lida
        this.challengeManager = new ChallengeManager(enigmas);

        for (Player player : players) {
            queueShifts.enqueue(player);
        }
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    public Scanner getScanner() {
        return scanner;
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

            // CHAMA A NOVA FUNÇÃO DE EXIBIÇÃO DE ESTADO
            displayGameStateNarrative(active);

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

        if (next.getChallenge() != null && !next.isChallengeResolved()) {
            System.out.println("\nChallenge: " + next.getChallenge().getType() + " in " + next.getName());
            boolean solved = next.getChallenge().attemptChallenge(active, this, next, this.scanner);
            if (!solved) {
                System.out.println(active.getName() + " challenge failed.");
                return;
            }
        }

        Hall hall = getHallToDestination(current, next);

        if (hall == null) {
            active.addActionToHistory("Attempted move to " + next.getName() + " but no valid hall found.");
            return;
        }

        boolean canEnter = hall.activateEvent(active, this);
        if (!canEnter) {
            active.addActionToHistory("Movement blocked to " + next.getName());
            System.out.println("-> " + active.getName() + " could not enter " + next.getName());
            return;
        }

        active.setCurrentPosition(next);
        active.addActionToHistory("Movement -> " + next.getName());

        if (!active.getHistoricalActions().isEmpty() && active.getHistoricalActions().last().contains("EXTRA_MOVE")) {
            Room extraNext = active.chooseMovement(this);
            if (extraNext != null) {

                if (extraNext.getChallenge() != null && !extraNext.isChallengeResolved()) {
                    boolean extraSolved = extraNext.getChallenge().attemptChallenge(active, this, extraNext, this.scanner);

                    if (!extraSolved) {
                        System.out.println("-> " + active.getName() + " falhou o desafio extra e permanece em " + next.getName());
                        return;
                    }
                }

                Hall extraHall = getHallToDestination(next, extraNext);
                if (extraHall != null) {
                    boolean canEnterExtra = extraHall.activateEvent(active, this);
                    if (canEnterExtra) {
                        active.setCurrentPosition(extraNext);
                        active.addActionToHistory("Extra Movement: " + next.getName() + " -> " + extraNext.getName());
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

        Room[] tempArray = new Room[allPlayers.size()];
        int index = 0;
        for (Player player : allPlayers) {
            tempArray[index++] = player.getCurrentPosition();
        }

        for (int i = tempArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Room temp = tempArray[i];
            tempArray[i] = tempArray[j];
            tempArray[j] = temp;
        }

        index = 0;
        for (Player player : allPlayers) {
            Room newPos = tempArray[index++];
            player.setCurrentPosition(newPos);
            player.addActionToHistory("Position changed by GENERAL_SWAP event to " + newPos.getName());
        }
    }

    public Player chooseRandomPlayer(Player exclusion) {
        if (allPlayers.size() <= 1) return null;

        ArrayUnorderedList<Player> targetablePlayers = new ArrayUnorderedList<>();
        for(Player p : allPlayers) {
            if (!p.equals(exclusion)) {
                targetablePlayers.addToRear(p);
            }
        }

        if (targetablePlayers.isEmpty()) return null;

        int randomIndex = random.nextInt(targetablePlayers.size());
        int count = 0;
        for (Player p : targetablePlayers) {
            if (count == randomIndex) return p;
            count++;
        }
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
                    if (matched != null) {
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
            // CRITICAL ADDITION: Record the initial position
            p.setInitialPosition(start);
            p.setCurrentPosition(start);
            p.setBlockedShifts(0);
            allPlayers.addToRear(p);
            queueShifts.enqueue(p);


            System.out.println("Added player: " + p.getName() + " starting at " +
                    (start != null ? start.getName() : "<none>"));
        }
    }

    private void displayGameStateNarrative(Player activePlayer) {
        System.out.println("\n==================================================");
        System.out.println("== TURN " + currentShift + " | ACTIVE PLAYER: " + activePlayer.getName() + " ==");
        System.out.println("==================================================");

        System.out.println("\n>>> EXPLORERS' STATUS <<<");
        for (Player p : allPlayers) {
            String currentEffects = "None";
            Room pos = p.getCurrentPosition();

            String lastAction = p.getHistoricalActions().size() > 0 ? p.getHistoricalActions().last() : "Start: " + (p.getCurrentPosition() != null ? p.getCurrentPosition().getName() : "Unknown");

            if (p.getBlockedShifts() > 0) {
                currentEffects = "BLOCKED (" + p.getBlockedShifts() + " turns left)";
            } else if (lastAction.contains("Gained an extra move")) {
                currentEffects = "Extra Move Pending";
            } else if (pos != null && pos.getChallenge() != null && !pos.isChallengeResolved()) {
                currentEffects = "Pending LEVER/ENIGMA Challenge";
            }

            System.out.println("-------------------------");
            System.out.println("Player: " + p.getName() + (p.equals(activePlayer) ? " (ACTIVE)" : ""));

            if (p.getHistoricalActions().size() >= 2) {
                System.out.println("  Start Position: " + (p.getInitialPosition() != null ? p.getInitialPosition().getName() : "N/A"));
            }

            System.out.println("  History of Actions (All Traversed Halls/Overcome Obstacles):");
            // Usar Iterator explícito para garantir a exibição de todos os elementos
            Iterator<String> historyIterator = p.getHistoricalActions().iterator();
            while (historyIterator.hasNext()) {
                System.out.println("    - " + historyIterator.next());
            }

            System.out.println("  Current Position: " + (pos != null ? pos.getName() : "N/A"));
            System.out.println("  Active Effects: " + currentEffects);
        }

        System.out.println("\n--------------------------------------------------");
    }
}