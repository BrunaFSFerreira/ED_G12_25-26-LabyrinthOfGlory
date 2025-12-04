package main.io;

import com.google.gson.*;

import java.io.FileReader;

import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.list.LinkedList;
import main.model.Enigma;

public class JSONReader {

    private final String enigmaFilePath;
    private final String mapaFilePath;

    public JSONReader() {
        this("resource-files/enigmas.json", "resource-files/mapas.json");
    }

    public JSONReader(String enigmaFilePath, String mapaFilePath) {
        this.enigmaFilePath = enigmaFilePath;
        this.mapaFilePath = mapaFilePath;
    }

    //TODO: Aletarar para LinkedOrderedList
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

    public static class MapaDTO {
        public DoubleLinkedUnorderedList<DivisaoDTO> divisoes = new DoubleLinkedUnorderedList<>();
        public DoubleLinkedUnorderedList<CorredorDTO> corredores = new DoubleLinkedUnorderedList<>();
    }

    public static class DivisaoDTO {
        public String id;
        public String nome;
        public boolean temTesouro;
    }

    public static class CorredorDTO {
        public String origem;
        public String destino;
        public int tamanho;
    }

    public DoubleLinkedUnorderedList<MapaDTO> lerMapa() {
        DoubleLinkedUnorderedList<MapaDTO> mapas = new DoubleLinkedUnorderedList<>();

        try {
            JsonArray rootArray = JsonParser.parseReader(new FileReader(mapaFilePath)).getAsJsonArray();
            if (rootArray.size() == 0) {
                throw new IllegalStateException("JSON origem mapa inválido: Nenhum mapa encontrado.");
            }

            for (JsonElement mapaElement : rootArray) {
                JsonObject root = mapaElement.getAsJsonObject();
                MapaDTO mapaDTO = new MapaDTO();

                //Validar Divisões
                JsonArray divsJson = root.getAsJsonArray("divisoes");
                if (divsJson == null || divsJson.isEmpty()) {
                    throw new IllegalStateException("JSON origem mapa inválido: Nenhuma divisão encontrada.");
                }

                for (JsonElement div : divsJson) {
                    JsonObject divObj = div.getAsJsonObject();

                    if (!divObj.has("id") || !divObj.has("nome")) {
                        System.out.println("Divisão inválida encontrada: " + divObj);
                        continue;
                    }

                    DivisaoDTO divisaoDTO = new DivisaoDTO();
                    divisaoDTO.id = divObj.get("id").getAsString();
                    divisaoDTO.nome = divObj.get("nome").getAsString();
                    if (divObj.has("temTesouro")) {
                        divisaoDTO.temTesouro = divObj.get("temTesouro").getAsBoolean();
                    } else {
                        divisaoDTO.temTesouro = false;
                    }
                    mapaDTO.divisoes.addToRear(divisaoDTO);
                }

                //Validar Corredores
                JsonArray corsJson = root.getAsJsonArray("corredores");
                if (corsJson == null || corsJson.isEmpty()) {
                    throw new IllegalStateException("JSON origem mapa inválido: Nenhum corredor encontrado.");
                }
                for (JsonElement cor : corsJson) {
                    JsonObject corObj = cor.getAsJsonObject();

                    if (!corObj.has("origem") || !corObj.has("destino") || !corObj.has("tamanho")) {
                        System.out.println("Corredor inválido encontrado: " + corObj);
                        continue;
                    }

                    CorredorDTO corredorDTO = new CorredorDTO();
                    corredorDTO.origem = corObj.get("origem").getAsString();
                    corredorDTO.destino = corObj.get("destino").getAsString();
                    corredorDTO.tamanho = corObj.get("tamanho").getAsInt();
                    mapaDTO.corredores.addToRear(corredorDTO);
                }

                mapas.addToRear(mapaDTO);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler o arquivo origem mapa: " + e.getMessage(), e);
        }

        return mapas;
    }
}
