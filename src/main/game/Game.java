package main.game;

import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.queue.LinkedQueue;
import main.model.Hall;
import main.model.Room;
import main.model.Maze;
import main.utils.EventType;

import java.util.Random;

public class Game {

    private final Maze maze;
    private final LinkedQueue<Player> queueShifts;
    private final DoubleLinkedUnorderedList<Player> allPlayers;
    private int currentShift;
    private final Random random;
    private Player winner;

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

        Room next = active.chooseMovement(this); // Usa o método polimórfico de Jogador/Bot

        if (next == null) {
            active.addActionToHistory("Decision: No valid move found/selected.");
            return;
        }

        Hall hall = getHallToDestination(current, next);

        if (hall == null || hall.isBlock()) {
            // Regra: Se o corredor está bloqueado, a Divisão current deve ser resolvida
            // A Divisão current tem de ser Alavanca ou Enigma.
            // ... [Lógica de ativação de Alavanca/Enigma deve ser implementada aqui ou na Divisao]
            return;
        }

        // 3. Movimento
        active.setCurrentPosition(next);
        active.addActionToHistory("Movement: " + current.getId() + " -> " + next.getId());


        // 5. Atualização do estado do jogo (visualização)
        System.out.println("-> " + active.getName() + " moved to " + next.getName());
    }

    private void applyEvent(Player active, EventType event) {
        active.addActionToHistory("Event: activated event\n " + event.toString());

        switch (event) {
            case EXTRA_MOVE:
                // Regra: Coloca o jogador de volta na frente da fila para jogar de novo
                // A implementação da fila encadeada é mais simples se for uma fila circular.
                // Aqui, apenas re-enfilamos *antes* dos jogadores que já estavam na fila (se implementarmos a fila de prioridade).
                // Para uma LinkedQueue simples:
                System.out.println("Event: " + active.getName() + " won an extra play!\n");
                // O jogador não é enfileirado novamente no final do loop, mas sim inserido no início.
                break;
            case POSITION_SWAP:
                Player target = chooseRandomPlayer(active);
                if (target != null) {
                    Room temp = active.getCurrentPosition();
                    active.setCurrentPosition(target.getCurrentPosition());
                    target.setCurrentPosition(temp);
                    active.addActionToHistory("Exchange: Position swapped with " + target.getName());
                    target.addActionToHistory("Swap: Position swapped by " + active.getName());
                }
                break;
            case TURN_BLOCK:
                int shifts = random.nextInt(3) + 1;
                active.setBlockedShifts(shifts);
                active.addActionToHistory("Bloqueado por " + shifts + " shifts.");
                break;
            case GENERAL_SWAP:
                // [Implementação: trocar todos os jogadores de posições de forma aleatória]
                break;
            case MOVE_BACK:
                // [Implementação: Recuar para uma posição anterior]
                break;
        }
    }

    private Player chooseRandomPlayer(Player exclusion) {
        // [Lógica para escolher um jogador da lista 'todosJogadores' que não seja o 'exclusion']
        return null;
    }

    private Hall getHallToDestination(Room origin, Room destination) {
        // Usa a lista de vizinhos da Divisão (do seu código)
        for (Hall hall : origin.getNeighbors()) {
            if (hall.getDestination().equals(destination)) {
                return hall;
            }
        }
        return null;
    }

    private boolean checkVictory(Player active) {
        // Condição de Vitória: O vencedor é o primeiro jogador a alcançar a sala central.
        return active.getCurrentPosition().isHasTreasure(); // Assumindo isTemTesouro na Divisao base
    }

    // Getters para a lógica do Bot
    public Maze getMaze() {
        return maze;
    }
}

