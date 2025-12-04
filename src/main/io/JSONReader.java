package main.io;

import com.google.gson.*;

import java.io.FileReader;

import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.list.LinkedList;
import main.model.Enigma;

public class JSONReader {

    private final String enigmaFilePath;
    private final String mapFilePath;

    public JSONReader() {
        this("resource-files/puzzle.json", "resource-files/maps.json");
    }

    public JSONReader(String enigmaFilePath, String mapFilePath) {
        this.enigmaFilePath = enigmaFilePath;
        this.mapFilePath = mapFilePath;
    }

    //TODO: Aletarar para LinkedOrderedList
    public LinkedList<Enigma> readEnigmas() {
        LinkedList<Enigma> enigmas = new LinkedList<>();

        try (FileReader reader = new FileReader(enigmaFilePath)) {
            Gson gson = new Gson();
            Enigma[] arrayTemp = gson.fromJson(reader, Enigma[].class);

            for (Enigma e : arrayTemp) {
                if (e.getIdEnigma() == null || e.getIdEnigma().isEmpty() || e.getQuestion() == null || e.getQuestion().isEmpty() || e.getAnswer() == null || e.getAnswer().isEmpty()) {
                    System.out.println("Enigma Invalid found: " + e);
                    continue;
                } else {
                    enigmas.add(e);
                }
            }

        } catch (JsonSyntaxException e) {
            System.out.println("Poorly structured JSON: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return enigmas;
    }

    public static class MapDTO {
        public DoubleLinkedUnorderedList<RoomDTO> rooms = new DoubleLinkedUnorderedList<>();
        public DoubleLinkedUnorderedList<HallDTO> halls = new DoubleLinkedUnorderedList<>();
    }

    public static class RoomDTO {
        public String id;
        public String name;
        public boolean hasTreasure;
    }

    public static class HallDTO {
        public String origin;
        public String destination;
        public int size;
    }

    public DoubleLinkedUnorderedList<MapDTO> writeMapa() {
        DoubleLinkedUnorderedList<MapDTO> maps = new DoubleLinkedUnorderedList<>();

        try {
            JsonArray rootArray = JsonParser.parseReader(new FileReader(mapFilePath)).getAsJsonArray();
            if (rootArray.size() == 0) {
                throw new IllegalStateException("No maps found.");
            }

            for (JsonElement mapaElement : rootArray) {
                JsonObject root = mapaElement.getAsJsonObject();
                MapDTO mapDTO = new MapDTO();

                //Validar Divisões
                JsonArray roomsJson = root.getAsJsonArray("rooms");
                if (roomsJson == null || roomsJson.isEmpty()) {
                    throw new IllegalStateException("No rooms found.");
                }

                for (JsonElement room : roomsJson) {
                    JsonObject roomObj = room.getAsJsonObject();

                    if (!roomObj.has("id") || !roomObj.has("name")) {
                        System.out.println("Found Invalid Room: " + roomObj);
                        continue;
                    }

                    RoomDTO roomDTO = new RoomDTO();
                    roomDTO.id = roomObj.get("id").getAsString();
                    roomDTO.name = roomObj.get("name").getAsString();
                    if (roomObj.has("hasTreasure")) {
                        roomDTO.hasTreasure = roomObj.get("hasTreasure").getAsBoolean();
                    } else {
                        roomDTO.hasTreasure = false;
                    }
                    mapDTO.rooms.addToRear(roomDTO);
                }

                //Validar Corredores
                JsonArray hallJson = root.getAsJsonArray("halls");
                if (hallJson == null || hallJson.isEmpty()) {
                    throw new IllegalStateException("No halls found.");
                }
                for (JsonElement hall : hallJson) {
                    JsonObject hallObj = hall.getAsJsonObject();

                    if (!hallObj.has("origin") || !hallObj.has("destination") || !hallObj.has("size")) {
                        System.out.println("Invalid Hall found: " + hallObj);
                        continue;
                    }

                    HallDTO hallDTO = new HallDTO();
                    hallDTO.origin = hallObj.get("origin").getAsString();
                    hallDTO.destination = hallObj.get("destination").getAsString();
                    hallDTO.size = hallObj.get("size").getAsInt();
                    mapDTO.halls.addToRear(hallDTO);
                }

                maps.addToRear(mapDTO);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error reading the map: " + e.getMessage(), e);
        }

        return maps;
    }
}
