package main.io;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import main.data.impl.list.LinkedList;
import main.model.Enigma;

public class JSONReader {

    private final String enigmaFilePath;

    public JSONReader() {
        this("resource-files/enigmas.json");
    }

    public JSONReader(String enigmaFilePath) {
        this.enigmaFilePath = enigmaFilePath;
    }

    public LinkedList<Enigma> lerEnigmas() {
        LinkedList<Enigma> enigmas = new LinkedList<>();

        try (FileReader reader = new FileReader(enigmaFilePath)) {
            Gson gson = new Gson();
            Enigma[] arrayTemp = gson.fromJson(reader, Enigma[].class);

            for (Enigma e : arrayTemp) {
                if (e.getIdEnigma() == null || e.getIdEnigma().isEmpty() || e.getPergunta() == null || e.getPergunta().isEmpty() || e.getResposta() == null || e.getResposta().isEmpty()) {
                    System.out.println("Enigma inválido encontrado: " + e);
                    continue;
                } else {
                    enigmas.add(e);
                }
            }

        } catch (JsonSyntaxException e) {
            System.out.println("JSON mal formado: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return enigmas;
    }
}
