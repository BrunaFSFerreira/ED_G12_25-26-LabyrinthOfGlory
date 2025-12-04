package main.game;

import main.data.impl.graph.WeightedGraph.AdjListGraph;
import main.data.impl.list.DoubleLinkedUnorderedList;
import main.model.Hall;
import main.model.Room;
import main.model.Maze;

import java.util.Iterator;

public class Bot extends Player {
    public Bot(String name, Room startingPosition) {
        super(name, startingPosition);
    }

    @Override
    public Room chooseMovement(Game game) {
        Maze lab = game.getMaze();
        AdjListGraph<Room> graph = lab.getRooms();
        DoubleLinkedUnorderedList<Room> treasure = game.getMaze().getTreasures();
        Room current = getCurrentPosition();

        if (treasure != null) {
            Iterator<Room> itTreasury = treasure.iterator();
            if (itTreasury.hasNext()) {
                Room destination = itTreasury.next();
                Iterator<Room> path = graph.interatorShortestPath(current, destination);

                if (path != null && path.hasNext()) {
                    path.next();
                    if (path.hasNext()) {
                        return path.next();
                    }
                }
            }
        }

        Iterator<Hall> neighbors = current.getNeighbors().iterator();
        if (neighbors.hasNext()) {
            return neighbors.next().getDestination();
        }
        return null;
    }
}
