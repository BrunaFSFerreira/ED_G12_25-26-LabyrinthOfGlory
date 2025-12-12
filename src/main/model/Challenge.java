package main.model;

import main.data.impl.list.ArrayUnorderedList;
import main.data.impl.list.LinkedList;
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
        Random random = new Random();
        ArrayUnorderedList<String> options = new ArrayUnorderedList<>();

        if (enigma == null) {
            player.addActionToHistory("Unable to retrieve enigma.");
            return true;
        }

        System.out.println(player.getName() + ", solve the enigma to unlock " + roomToUnlock.getName() + ":");
        System.out.println("\nPERGUNTA: " + enigma.getQuestion());

        String correctAnswer = enigma.getAnswer();
        if (!correctAnswer.isEmpty()) {
            options.addToRear(correctAnswer);
        }

        String[] allWrongAnswer = enigma.getWrongAnswers();
        if (allWrongAnswer != null) {
            for (String wrongAnswers : allWrongAnswer) {
                options.addToRear(wrongAnswers);
            }
        }

        int size = options.size();
        String[] optionsArray = new String[size];
        int index = 0;
        for (String option : options) {
            optionsArray[index++] = option;
        }

        for (int i = size - 1; i > 0; i--) {
            int randomIndex = random.nextInt(size);
            String temp = optionsArray[i];
            optionsArray[i] = optionsArray[randomIndex];
            optionsArray[randomIndex] = temp;
        }

        int correctIndex = -1;
        for (int i = 0; i < size; i++) {
            if (optionsArray[i].equals(correctAnswer)) {
                correctIndex = i + 1;
                break;
            }
        }

        System.out.println("\n--- Options ---");
        for (int i = 0; i < size; i++) {
            System.out.println((i + 1) + ". " + optionsArray[i]);
        }

        System.out.println("Your answer: ");
        int playerChoice;
        try {
            playerChoice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid option.");
            player.addActionToHistory("Failed ENIGMA challenge in room " + roomToUnlock.getName() + ". Invalid input.");
            return false;
        }

        if (playerChoice < 1 || playerChoice > options.size()) {
            System.out.println("Invalid option. Choose a number between 1 and " + options.size() + ".");
            player.addActionToHistory("Failed ENIGMA challenge in room " + roomToUnlock.getName() + ". Invalid choice outside 1-" + options.size() + ".");
            return false;
        }

        String selectedAnswer = optionsArray[playerChoice - 1];

        boolean isCorrect = (playerChoice == correctIndex);

        if(isCorrect){
            player.addActionToHistory("Successfully challenged enigma in room " + roomToUnlock.getName() + ". Correct answer: " + selectedAnswer + ". Halls unlocked.");
            System.out.println("Enigma resolved! Access to " + roomToUnlock.getName() + " unlocked.");
            return true;
        } else {
            EventType eventType = possibleEvents[random.nextInt(possibleEvents.length)];
            RandomEvent event = new RandomEvent(eventType);
            event.activate(player, game);

            player.addActionToHistory("Failed ENIGMA challenge in room " + roomToUnlock.getName() + ". Wrong answer: " + selectedAnswer + ". Event triggered: " + eventType.toString());
            System.out.println("Wrong answer! Access to " + roomToUnlock.getName() + " is blocked this turn.");
            return false;
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
