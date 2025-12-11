package main.model;

import main.data.impl.graph.WeightedGraph.AdjListGraph;
import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.list.LinkedUnorderedList;
import main.io.JSONReader;
import main.utils.RoomType;

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
        destination.getNeighbors().addToRear(new Hall(origin, c.getSize()));
        rooms.addEdge(origin, destination, c.getSize());
        return true;
    }

    public DoubleLinkedUnorderedList<Room> getEntries() {
        DoubleLinkedUnorderedList<Room> entries = new DoubleLinkedUnorderedList<>();
        for (Room room : rooms) {
            if (room.getType() == RoomType.ENTRANCE) {
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
        LinkedUnorderedList<JSONReader.MapDTO> maps = new JSONReader().writeMap();

        if (maps.isEmpty()) {
            System.out.println("No maps found in JSON.");
            return;
        }

        JSONReader.MapDTO map = maps.first();

        for (JSONReader.RoomDTO roomDTO: map.rooms) {
            Room room;
            RoomType type;
            try {
                type = RoomType.valueOf(roomDTO.type.name());
            } catch (Exception e) {
                type = RoomType.NORMAL;
            }

            int x = roomDTO.x;
            int y = roomDTO.y;

            switch (type) {
                case PUZZLE:
                    // Cria EnigmaRoom
                    room = new EnigmaRoom(roomDTO.id, roomDTO.name, roomDTO.hasTreasure, roomDTO.enigmaId);
                    break;
                case LEVER:
                    // Cria LeverRoom
                    int correctLeverId = roomDTO.correctLeverId != null ? roomDTO.correctLeverId : 1;
                    room = new LeverRoom(roomDTO.id, roomDTO.name, roomDTO.hasTreasure, correctLeverId);
                    break;
                default:
                    // Cria Room genérica
                    // Note: Room is abstract, so it's instantiated as an anonymous subclass here.
                    room = new Room(roomDTO.id, roomDTO.name, roomDTO.hasTreasure, roomDTO.type ) {};
                    break;
            }

            room.setX(x);
            room.setY(y);

            if(!addRoom(room)) {
                System.out.println("Failed to add Room: " + roomDTO.id);
            }
        }

        for (JSONReader.HallDTO hallDTO : map.halls) {
            Room origin = getRoomById(hallDTO.origin);
            Room destination = getRoomById(hallDTO.destination);


            if (origin == null || destination == null) {
                System.out.println("Failed to add Hall: " + hallDTO.origin + " -> " + hallDTO.destination);
                continue;
            }

            Hall hall = new Hall(destination, hallDTO.size);

            if (origin instanceof LeverRoom) {
                ((LeverRoom) origin).addHallToUnlock(hall);
            }

            if (!addHall(origin, destination, hall)) {
                System.out.println("Failed to add Hall: " + hallDTO.origin + " -> " + hallDTO.destination);
            }
        }

        printMaze();
        //debugMaze();
    }

    public void printMaze() {

        if (rooms.isEmpty()) {
            System.out.println("Maze is empty.");
            return;
        }

        // Dimensões fixas para cada sala
        final int W = 15;   // largura da caixa
        final int H = 7;    // altura da caixa
        final int GAP_X = 4; // espaço horizontal entre salas
        final int GAP_Y = 2; // espaço vertical entre salas

        // Encontrar limites do labirinto
        int maxX = 0, maxY = 0;
        for (Room r : rooms) {
            maxX = Math.max(maxX, r.getX());
            maxY = Math.max(maxY, r.getY());
        }

        // Criar grelha segura
        int width = (maxX + 1) * (W + GAP_X);
        int height = (maxY + 1) * (H + GAP_Y);

        String[][] grid = new String[height][width];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                grid[y][x] = " ";

        // Desenhar sala + símbolo
        for (Room room : rooms) {

            int ox = room.getX() * (W + GAP_X);
            int oy = room.getY() * (H + GAP_Y);

            // borda superior
            grid[oy][ox] = "╔";
            for (int i = 1; i < W - 1; i++) grid[oy][ox + i] = "─";
            grid[oy][ox + W - 1] = "╗";

            // laterais
            for (int j = 1; j < H - 1; j++) {
                grid[oy + j][ox] = "│";
                grid[oy + j][ox + W - 1] = "│";
            }

            // borda inferior
            grid[oy + H - 1][ox] = "╚";
            for (int i = 1; i < W - 1; i++) grid[oy + H - 1][ox + i] = "─";
            grid[oy + H - 1][ox + W - 1] = "╝";

            // símbolo
            String symbol =
                    room.isHasTreasure() ? "✦" :
                            room instanceof EnigmaRoom ? "⧈" :
                                    room instanceof LeverRoom ? "⥂" : " ";

            int cx = ox + W / 2;
            int cy = oy + H / 2;

            grid[cy][cx] = symbol;
        }

        // Desenhar ligações sem invadir salas
        for (Room room : rooms) {

            int ox = room.getX() * (W + GAP_X);
            int oy = room.getY() * (H + GAP_Y);

            int midX = ox + W / 2;
            int midY = oy + H / 2;

            for (Hall hall : room.getNeighbors()) {

                Room dst = hall.getDestination();

                int dx = dst.getX() * (W + GAP_X);
                int dy = dst.getY() * (H + GAP_Y);

                int midDX = dx + W / 2;
                int midDY = dy + H / 2;

                // Horizontal
                if (oy == dy) {
                    int start = Math.min(midX, midDX) + 2;
                    int end = Math.max(midX, midDX) - 2;
                    for (int x = start; x <= end; x++)
                        grid[midY][x] = "─";
                }
                // Vertical
                else if (ox == dx) {
                    int start = Math.min(midY, midDY) + 1;
                    int end = Math.max(midY, midDY) - 1;
                    for (int y = start; y <= end; y++)
                        grid[y][midX] = "│";
                }
            }
        }

        // Print final
        System.out.println("======  LABIRINTO  ======");
        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++)
                sb.append(grid[y][x]);
            System.out.println(sb);
        }
        System.out.println("==========================");
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

