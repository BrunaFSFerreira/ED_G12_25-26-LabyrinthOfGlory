package main.io;

import com.google.gson.*;

import java.io.FileReader;

import main.data.impl.list.LinkedUnorderedList;
import main.model.EnigmaData;

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

    public LinkedUnorderedList<EnigmaData> readEnigmas() {
        LinkedUnorderedList<EnigmaData> enigmas = new LinkedUnorderedList<>();

        try (FileReader reader = new FileReader(enigmaFilePath)) {
            Gson gson = new Gson();
            EnigmaData[] arrayTemp = gson.fromJson(reader, EnigmaData[].class);

            for (EnigmaData e : arrayTemp) {
                if ( e.getQuestion() == null || e.getQuestion().isEmpty() || e.getAnswer() == null || e.getAnswer().isEmpty()) {
                    System.err.println("Enigma Invalid found (skipped): " + e);
                    continue;
                } else {
                    enigmas.addToRear(e);
                }
            }

        } catch (JsonSyntaxException e) {
            System.err.println("Poorly structured JSON: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return enigmas;
    }

    public static class MapDTO {
        public LinkedUnorderedList<RoomDTO> rooms = new LinkedUnorderedList<>();
        public LinkedUnorderedList<HallDTO> halls = new LinkedUnorderedList<>();
    }

    public static class RoomDTO {
        public String id;
        public String name;
        public boolean hasTreasure;
        public int x;
        public int y;
        public Integer correctLeverId;

        public String getChallengeType() {
            if (correctLeverId != null) return "LEVER";
            if (id.equals("r1") || id.equals("r10") || id.equals("r22")) return "ENTRANCE";
            if (hasTreasure) return "TREASURE";
            return "NORMAL";
        }
    }

    public static class HallDTO {
        public String origin;
        public String destination;
        public int size;
    }

    public LinkedUnorderedList<MapDTO> writeMap() {
        LinkedUnorderedList<MapDTO> maps = new LinkedUnorderedList<>();

        try {
            JsonArray rootArray = JsonParser.parseReader(new FileReader(mapFilePath)).getAsJsonArray();
            if (rootArray.size() == 0) {
                throw new IllegalStateException("No maps found.");
            }

            for (JsonElement mapElement : rootArray) {
                JsonObject root = mapElement.getAsJsonObject();
                MapDTO mapDTO = new MapDTO();

                JsonArray roomsJson = root.getAsJsonArray("rooms");
                if (roomsJson == null || roomsJson.isEmpty()) {
                    throw new IllegalStateException("No rooms found.");
                }

                for (JsonElement room : roomsJson) {
                    JsonObject roomObj = room.getAsJsonObject();

                    if (!roomObj.has("id") || !roomObj.has("name")) {
                        System.err.println("Found Invalid Room (skipped): " + roomObj);
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

                    if (roomObj.has("x")) roomDTO.x = roomObj.get("x").getAsInt();
                    if (roomObj.has("y")) roomDTO.y = roomObj.get("y").getAsInt();

                    if (roomObj.has("correctLeverId") && roomObj.get("correctLeverId").isJsonPrimitive()) {
                        roomDTO.correctLeverId = roomObj.get("correctLeverId").getAsInt();
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
                        System.err.println("Invalid Hall found (skipped): " + hallObj);
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
