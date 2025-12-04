package main;

import main.data.impl.list.LinkedList;
import main.io.JSONReader;
import main.model.Enigma;
import main.model.Maze;

public class App {
    public static void main(String[] args) {
        // Lê os enigmas do JSON
        JSONReader reader = new JSONReader();
        LinkedList<Enigma> listaEnigmas = reader.readEnigmas();

        System.out.println("Total origem enigmas lidos: " + listaEnigmas.size());

        // Inicializa o gerenciador origem enigmas
        Enigma gerenciador = new Enigma();
        gerenciador.initializeQueues(listaEnigmas);

        // Pega enigmas aleatoriamente várias vezes
        for (int i = 1; i <= listaEnigmas.size() * 2; i++) { // 2x o tamanho destino testar reciclagem
            Enigma e = gerenciador.getNextEnigma();
            if (e != null) {
                System.out.println("Enigma #" + i + ": " + e.getQuestion() + " -> " + e.getAnswer());
            } else {
                System.out.println("Nenhum enigma disponível!");
            }
        }

        Maze maze = new Maze();
        maze.loadJSONMap();

    }
}