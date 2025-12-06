package main.model;

import main.data.impl.graph.WeightedGraph.AdjListGraph;
import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.list.LinkedUnorderedList;
import main.io.JSONReader;

public class Maze {

    private final AdjListGraph<Room> rooms = new AdjListGraph<>();

    public boolean addRoom(Room d) {
        if (d == null || d.getId() == null || getRoomById(d.getId()) != null) {
            return false;
        }

        rooms.addVertex(d);
        return true;
    }


    public boolean addHall (Room origin, Room destination, Hall c) {
        if (origin == null || destination == null || c == null) {
            return false;
        }

        if (getRoomById(origin.getId()) == null || getRoomById(destination.getId()) == null) {
            return false;
        }

        for (Hall existente : origin.getNeighbors()) {
            if (existente.getDestination().equals(destination)) {
                return false;
            }
        }

        origin.getNeighbors().addToRear(c);
        destination.getNeighbors().addToRear(new Hall(origin));
        return true;
    }

    public DoubleLinkedUnorderedList<Room> getEntries() {
        DoubleLinkedUnorderedList<Room> entries = new DoubleLinkedUnorderedList<>();
        for (Room room : rooms) {
            boolean isEntry = true;
            for (Room neighbor : rooms) {
                for (Hall hall : neighbor.getNeighbors()) {
                    if (hall.getDestination().equals(room)) {
                        isEntry = false;
                        break;
                    }
                }
                if (!isEntry) break;
            }
            if (isEntry) {
                entries.addToRear(room);
            }
        }
        return entries;
    }

    public DoubleLinkedUnorderedList<Room> getTreasures() {
        DoubleLinkedUnorderedList<Room> treasures = new DoubleLinkedUnorderedList<>();
        for (Room room : rooms) {
            if (room.isHasTreasure()) {
                treasures.addToRear(room);
            }
        }
        return treasures;
    }

    public void loadJSONMap() {
        JSONReader reader = new JSONReader();
        LinkedUnorderedList<JSONReader.MapDTO> maps = new JSONReader().writeMapa();

        JSONReader.MapDTO map = maps.first();

        //Criar Divisões
        for (JSONReader.RoomDTO roomDTO: map.rooms) {
            Room room = new Room(roomDTO.id, roomDTO.name, roomDTO.hasTreasure) {};

            if(!addRoom(room)) {
                System.out.println("Failed to add Room: " + roomDTO.id);
            }
        }

        //Criar Corredores
        for (JSONReader.HallDTO hallDTO : map.halls) {
            Room origem = getRoomById(hallDTO.origin);
            Room destino = getRoomById(hallDTO.destination);


            if (origem == null || destino == null) {
                System.out.println("Failed to add Hall: " + hallDTO.origin + " -> " + hallDTO.destination);
                continue;
            }

            Hall hall = new Hall(destino);
            if (!addHall(origem, destino, hall)) {
                System.out.println("Failed to add Hall: " + hallDTO.origin + " -> " + hallDTO.destination);
            }
        }
        //TODO: Remover
        debugMaze();
    }

    public Room getRoomById(String id) {
        for (Room room : rooms) {
            if (room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }

    public AdjListGraph<Room> getRooms() {
        return rooms;
    }

    //TODO: Remover
    public void debugMaze() {
        System.out.println("=== LABIRINTO CARREGADO ===");

        for (Room room : rooms) {
            System.out.println("Divisão: " + room.getId() +
                    " (" + room.getName() + ") " +
                    (room.isHasTreasure() ? "[TESOURO]" : ""));

            if (room.getNeighbors().isEmpty()) {
                System.out.println("  -> Sem corredores");
            } else {
                for (Hall c : room.getNeighbors()) {
                    System.out.println("  -> Conecta a: " + c.getDestination().getId());
                }
            }
        }

        System.out.println("============================\n");
    }

}
