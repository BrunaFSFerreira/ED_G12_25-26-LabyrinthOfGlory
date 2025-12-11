package main;

import main.data.impl.list.ArrayUnorderedList;
import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.list.LinkedList;
import main.game.Bot;
import main.game.Game;
import main.game.HumanPlayer;
import main.game.Player;
import main.io.JSONReader;
import main.io.JSONWriter;
import main.model.Enigma;
import main.model.Maze;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        JSONReader reader = new JSONReader();
        LinkedList<Enigma> listaEnigmas = reader.readEnigmas();

        Enigma menager = new Enigma();
        menager.initializeQueues(listaEnigmas);

        for (int i = 1; i <= listaEnigmas.size() * 2; i++) {
            Enigma e = menager.getNextEnigma();
            if (e != null) {
            } else {
            }
        }

        Maze maze = new Maze();
        maze.loadJSONMap();

        Scanner scanner = new Scanner(System.in);
        int numPlayers = 1;
        while (true) {
            System.out.print("Number of players (1-5): ");
            String line = scanner.nextLine().trim();
            try {
                numPlayers = Integer.parseInt(line);
                if (numPlayers >= 1 && numPlayers <= 5) {
                    break;
                } else {
                    System.out.println("Please, insert a number between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid entry insert a number between 1 and 5");
            }
        }

        ArrayUnorderedList<Player> players = new ArrayUnorderedList<>();
        for (int p = 1; p <= numPlayers; p++) {
            System.out.println("--- Player " + p + " ---");
            System.out.print("Name: ");
            String name = scanner.nextLine().trim();
            String type = "";
            while (true) {
                System.out.print("Type (H = Human, B = Bot): ");
                type = scanner.nextLine().trim().toUpperCase();
                if (type.equals("H") || type.equals("B")) break;
                System.out.println("Invalid type. Enter H or B.");
            }

            // TODO: replace `null` with your maze's actual starting Room, e.g. maze.getStartRoom()
            if (type.equals("B")) {
                players.addToRear(new Bot(name, null));
            } else {
                players.addToRear(new HumanPlayer(name, null));
            }
        }

        System.out.println("Players created:");
        for (Player pl : players) {
            System.out.println(" - " + pl.getName() + " (" + pl.getClass().getSimpleName() + ")");
        }

        ArrayUnorderedList<Player> playersForGame = new ArrayUnorderedList<>();
        for (Player pl : players) {
            playersForGame.addToRear(pl);
        }

        DoubleLinkedUnorderedList<Player> initialList = new DoubleLinkedUnorderedList<>();
        Game game = new Game(maze, initialList, listaEnigmas);

        game.addPlayers(playersForGame);

        game.start();
        
        JSONWriter writer = new JSONWriter();
        writer.writeGameReport(game);

        scanner.close();

    }
}