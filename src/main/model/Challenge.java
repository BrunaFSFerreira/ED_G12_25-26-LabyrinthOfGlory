package main.model;

import main.game.Game;
import main.game.Player;
import main.game.RandomEvent;
import main.utils.ChallengeType;
import main.utils.EventType;

import java.util.Random;
import java.util.Scanner;

public class Challenge {

    private final ChallengeType type;
    private final int correctLeverId;
    private final Random random = new Random();
    private final EventType[] possibleEvents = EventType.values();

    public Challenge(ChallengeType type) {
        this.type = ChallengeType.ENIGMA;
        this.correctLeverId = -1;
    }

    public Challenge(ChallengeType type, int correctLeverId) {
        this.type = ChallengeType.LEVER;
        this.correctLeverId = correctLeverId;
    }

    public ChallengeType getType() {
        return type;
    }

    public boolean attemptChallenge(Player player, Game game, Room roomToUnlock, Scanner scanner) {
        if (roomToUnlock.isChallengeResolved()) {
            return true;
        }

        if (type == ChallengeType.ENIGMA) {
            return handleEnigma(player, game, roomToUnlock, scanner);
        } else if (type == ChallengeType.LEVER) {
            return handleLever(player, game, roomToUnlock, scanner);
        }

        return true;

    }

    private boolean handleEnigma(Player player, Game game, Room roomToUnlock, Scanner scanner) {
        ChallengeManager manager = game.getChallengeManager();
        EnigmaData enigma = manager.getNextEnigma();

        if (enigma == null) {
            player.addActionToHistory("Unable to retrieve enigma.");
            return true;
        }

        System.out.println(player.getName() + ", solve the enigma to unlock " + roomToUnlock.getName() + ":");
        System.out.println(enigma.getQuestion());

        String correctAnswer = enigma.getAnswer();
        String wrongAnswer = generateWrongAnswer(correctAnswer);

        boolean correctIsFirst = random.nextBoolean();
        String op1 = correctIsFirst ? correctAnswer : wrongAnswer;
        String op2 = correctIsFirst ? wrongAnswer : correctAnswer;

        System.out.println("1. " + op1);
        System.out.println("2. " + op2);

        System.out.print("Your answer (1 or 2): ");
        String answer = scanner.nextLine().trim();

        if (enigma.checkAnswer(answer)) {
            roomToUnlock.setChallengeResolved(true);
            player.addActionToHistory("Solved ENIGMA challenge in room " + roomToUnlock.getName() + ". Correct answer: " + answer);
            System.out.println("-> Desafio ENIGMA resolvido! A porta para " + roomToUnlock.getName() + " abre.");
            return true;
        } else {
            player.addActionToHistory("Failed ENIGMA challenge in room " + roomToUnlock.getName() + ". Incorrect answer: " + answer);
            System.out.println("-> Resposta incorreta! O acesso a " + roomToUnlock.getName() + " está bloqueado neste turno.");
            return false;
        }
    }

    private String generateWrongAnswer(String correctAnswer) {
        try {
            int num = Integer.parseInt(correctAnswer.trim());
            if (num != 42) return String.valueOf(num + 1);
            return "43";
        } catch (NumberFormatException e) {
            return "NAO " + correctAnswer.toUpperCase();
        }
    }

    private boolean handleLever(Player player, Game game, Room roomToUnlock, Scanner scanner) {
        System.out.println(player.getName() + ", escolha uma alavanca (1, 2 ou 3) para entrar em " + roomToUnlock.getName() + ":");

        System.out.print("Sua escolha: ");
        int playerChoice = -1;
        try {
            String line = scanner.nextLine().trim();
            playerChoice = Integer.parseInt(line);
        } catch (Exception e) {
            System.out.println("Opção inválida.");
            player.addActionToHistory("Failed LEVER challenge in room " + roomToUnlock.getName() + ". Invalid input.");
            return false;
        }

        if (playerChoice < 1 || playerChoice > 3) {
            System.out.println("Opção inválida. Escolha 1, 2 ou 3.");
            player.addActionToHistory("Failed LEVER challenge in room " + roomToUnlock.getName() + ". Invalid choice outside 1-3.");
            return false;
        }
        if (playerChoice == correctLeverId) {
            roomToUnlock.setChallengeResolved(true);

            for (Hall hall : roomToUnlock.getHallsToUnlock()) {
                hall.setBlock(false);
            }
            player.addActionToHistory("Solved LEVER challenge in room " + roomToUnlock.getName() + ". Correct lever pulled (" + correctLeverId + "). Halls unlocked.");
            System.out.println("-> Desafio LEVER resolvido! Os caminhos a partir de " + roomToUnlock.getName() + " foram desbloqueados.");
            return true;
        } else {
            EventType eventType = possibleEvents[random.nextInt(possibleEvents.length)];
            RandomEvent event = new RandomEvent(eventType);
            event.activate(player, game);

            player.addActionToHistory("Failed LEVER challenge in room " + roomToUnlock.getName() + ". Wrong lever pulled (" + playerChoice + "). Event triggered: " + eventType.toString());
            System.out.println("-> Alavanca incorreta! O acesso a " + roomToUnlock.getName() + " está bloqueado neste turno.");
            return false;
        }


    }
}
