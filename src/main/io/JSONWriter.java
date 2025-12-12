// Conteúdo atualizado de src/main/io/JSONWriter.java

package main.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.list.ArrayUnorderedList;
import main.game.Game;
import main.game.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JSONWriter {

    private final String outputFilePath;

    public JSONWriter() {
        this("resource-files/game_report_" + System.currentTimeMillis() + ".json");
    }

    public JSONWriter(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    // CLASSE PARA O RELATÓRIO ESTATÍSTICO DE CADA JOGADOR
    private class PlayerReport {
        final String playerName;
        final String finalPosition;
        final String initialPosition;
        final boolean isWinner;
        final int blockedTurnsLeft;

        // O PERCURSO COMPLETO (Percurso, Obstáculos e Efeitos Aplicados)
        final String[] pathAndEvents;

        PlayerReport(Player p, Player gameWinner) {
            this.playerName = p.getName();
            // CORREÇÃO: Remove o ID da posição (apenas nome)
            this.finalPosition = p.getCurrentPosition() != null ? p.getCurrentPosition().getName() : "Unknown";
            this.initialPosition = p.getInitialPosition() != null ? p.getInitialPosition().getName() : "N/A";
            this.isWinner = p.equals(gameWinner);
            this.blockedTurnsLeft = p.getBlockedShifts();

            // CONVERSÃO CRÍTICA: Copiar elementos da lista customizada para um array simples
            DoubleLinkedUnorderedList<String> historyList = p.getHistoricalActions();
            String[] actionsArray = new String[historyList.size()];
            Iterator<String> it = historyList.iterator();
            int index = 0;
            while (it.hasNext()) {
                // Ação está limpa de ID de sala se Game.executePlay estiver corrigido
                actionsArray[index++] = it.next();
            }
            this.pathAndEvents = actionsArray;
        }
    }

    // CLASSE RAIZ DO DOCUMENTO JSON
    private class GameReport {
        final String game_id;
        final String end_time;
        final String winner;
        final PlayerReport[] players;

        GameReport(String game_id, String end_time, String winner, PlayerReport[] players) {
            this.game_id = game_id;
            this.end_time = end_time;
            this.winner = winner;
            this.players = players;
        }
    }

    public void writeGameReport(Game game) {

        // GERAÇÃO DE METADADOS
        String gameId = String.valueOf(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        String endTime = sdf.format(new Date());

        DoubleLinkedUnorderedList<Player> allPlayers = game.getAllPlayers();
        Player gameWinner = game.winner;

        // 1. CONSTRUIR O SUMÁRIO DE CADA JOGADOR e popular o ARRAY nativo
        PlayerReport[] playersArray = new PlayerReport[allPlayers.size()];

        int index = 0;
        for (Player p : allPlayers) {
            playersArray[index++] = new PlayerReport(p, gameWinner);
        }

        // 2. MONTAR O OBJETO FINAL DO RELATÓRIO
        String winnerName = gameWinner != null ? gameWinner.getName() : "None";
        GameReport finalReportObject = new GameReport(gameId, endTime, winnerName, playersArray);


        // ALTERAÇÃO CRÍTICA: Usa disableHtmlEscaping() para prevenir \u003e
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();

        try (FileWriter writer = new FileWriter(outputFilePath)) {
            gson.toJson(finalReportObject, writer);
            System.out.println("Relatório do jogo escrito com sucesso em " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Erro ao escrever o relatório JSON: " + e.getMessage());
        }
    }
}