package main;

import main.data.impl.list.LinkedList;
import main.game.Bot;
import main.game.Game;
import main.game.HumanPlayer;
import main.game.Player;
import main.io.JSONReader;
import main.model.Enigma;
import main.model.Maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // Lê os enigmas do JSON
        JSONReader reader = new JSONReader();
        LinkedList<Enigma> listaEnigmas = reader.readEnigmas();

        System.out.println("Total origem enigmas lidos: " + listaEnigmas.size());

        Enigma menager = new Enigma();
        menager.initializeQueues(listaEnigmas);

        // Pega enigmas aleatoriamente várias vezes
        for (int i = 1; i <= listaEnigmas.size() * 2; i++) {
            Enigma e = menager.getNextEnigma();
            if (e != null) {
                System.out.println("Enigma #" + i + ": " + e.getQuestion() + " -> " + e.getAnswer());
            } else {
                System.out.println("Nenhum enigma disponível!");
            }
        }

        Maze maze = new Maze();
        maze.loadJSONMap();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Number of players: ");
        int numPlayers = 1;
        try {
            numPlayers = Integer.parseInt(scanner.nextLine().trim());
            if (numPlayers < 1) numPlayers = 1;
        } catch (NumberFormatException ignored) {}

        List<Player> players = new ArrayList<>();
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
                players.add(new Bot(name, null));
            } else {
                players.add(new HumanPlayer(name, null));
            }
        }

        System.out.println("Players created:");
        for (Player pl : players) {
            System.out.println(" - " + pl.getName() + " (" + pl.getClass().getSimpleName() + ")");
        }

    }
}