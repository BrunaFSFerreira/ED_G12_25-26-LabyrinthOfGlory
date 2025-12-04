package main.data.impl.graph.WeightedGraph;


import main.data.adt.GraphADT;
import main.data.impl.list.ArrayUnorderedList;
import main.data.impl.queue.LinkedQueue;
import main.data.impl.stack.LinkedStack;

import java.util.Iterator;

public class AdjListGraph<T> implements GraphADT<T>, Iterable<T> {
    protected final int DEFAULT_CAPACITY = 10;
    protected int numVertices;
    protected ArrayUnorderedList<Edge>[] adjList;
    protected T[] vertices;

    public AdjListGraph() {
        numVertices = 0;
        this.vertices = (T[]) new Object[DEFAULT_CAPACITY];
        this.adjList = (ArrayUnorderedList<Edge>[]) new ArrayUnorderedList[DEFAULT_CAPACITY];
        for (int i = 0; i < this.adjList.length; i++) {
            this.adjList[i] = new ArrayUnorderedList<>();
        }
    }

    private static class Edge {
        int index;
        double weight;
        Edge(int index, double weight) { this.index = index; this.weight = weight; }
    }

    @Override
    public void addVertex(T vertex) {
        if (numVertices == vertices.length) expandCapacity();
        vertices[numVertices] = vertex;
        adjList[numVertices] = new ArrayUnorderedList<>();
        numVertices++;
    }


    private void expandCapacity() {
        int newCapacity = vertices.length * 2;
        T[] newVertices = (T[]) new Object[newCapacity];
        for (int i = 0; i < vertices.length; i++) newVertices[i] = vertices[i];
        vertices = newVertices;

        ArrayUnorderedList<Edge>[] newAdj = (ArrayUnorderedList<Edge>[]) new ArrayUnorderedList[newCapacity];
        for (int i = 0; i < newCapacity; i++) {
            if (i < adjList.length && adjList[i] != null) newAdj[i] = adjList[i];
            else newAdj[i] = new ArrayUnorderedList<>();
        }
        adjList = newAdj;
    }

    @Override
    public void removeVertex(T vertex) {
        int idx = getIndex(vertex);
        if (!indexIsValid(idx)) return;

        // Remove edges to idx and adjust indices > idx
        for (int i = 0; i < numVertices; i++) {
            if (i == idx) continue;
            ArrayUnorderedList<Edge> newList = new ArrayUnorderedList<>();
            Iterator<Edge> it = adjList[i].iterator();
            while (it.hasNext()) {
                Edge e = it.next();
                if (e.index == idx) continue; //Remove edge to vertex removed
                int newIndex = e.index > idx ? e.index - 1 : e.index;
                newList.addToRear(new Edge(newIndex, e.weight));
            }
            adjList[i] = newList;
        }

        //Shift vertices left
        for (int i = idx; i < numVertices - 1; i++) vertices[i] = vertices[i + 1];
        vertices[numVertices - 1] = null;

        //Shift adjList left
        for (int i = idx; i < numVertices - 1; i++) adjList[i] = adjList[i + 1];
        adjList[numVertices - 1] = new ArrayUnorderedList<>();

        numVertices--;
    }

    @Override
    public void addEdge(T vertex1, T vertex2, double weight) {
        addEdge(getIndex(vertex1), getIndex(vertex2), weight);
    }

    private int getIndex(T vertex) {
        for (int i = 0; i < numVertices; i++) {
            if (vertices[i] == null) {
                if (vertex == null) return i;
            } else if (vertices[i].equals(vertex)) return i;
        }
        return -1;
    }

    public void addEdge(int index1, int index2, double weight) {
        if (indexIsValid(index1) && indexIsValid(index2)) {
            adjList[index1].addToRear(new Edge(index2, weight));
            adjList[index2].addToRear(new Edge(index1, weight));
        }
    }

    private boolean indexIsValid(int index) {
        return index >= 0 && index < numVertices;
    }

    @Override
    public void removeEdge(T vertex1, T vertex2) {
        int i1 = getIndex(vertex1);
        int i2 = getIndex(vertex2);
        if (!indexIsValid(i1) || !indexIsValid(i2)) return;

        ArrayUnorderedList<Edge> newList1 = new ArrayUnorderedList<>();
        Iterator<Edge> it1 = adjList[i1].iterator();
        while (it1.hasNext()) {
            Edge e = it1.next();
            if (e.index != i2) newList1.addToRear(new Edge(e.index, e.weight));
        }
        adjList[i1] = newList1;

        ArrayUnorderedList<Edge> newList2 = new ArrayUnorderedList<>();
        Iterator<Edge> it2 = adjList[i2].iterator();
        while (it2.hasNext()) {
            Edge e = it2.next();
            if (e.index != i1) newList2.addToRear(new Edge(e.index, e.weight));
        }
        adjList[i2] = newList2;
    }

    @Override
    public Iterator<T> interatorBFS(T startVertex) {
        LinkedQueue<Integer> traversalQueue = new LinkedQueue<>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<>();
        int startIndex = getIndex(startVertex);
        if (!indexIsValid(startIndex)) return resultList.iterator();
        boolean[] visited = new boolean[numVertices];

        traversalQueue.enqueue(startIndex);
        visited[startIndex] = true;
        while (!traversalQueue.isEmpty()) {
            int x = traversalQueue.dequeue();
            resultList.addToRear(vertices[x]);
            Iterator<Edge> it = adjList[x].iterator();
            while (it.hasNext()) {
                Edge e = it.next();
                int neighborIndex = e.index;
                if (!visited[neighborIndex]) {
                    traversalQueue.enqueue(neighborIndex);
                    visited[neighborIndex] = true;
                }
            }
        }
        return resultList.iterator();
    }

    @Override
    public Iterator<T> interatorDFS(T startVertex) {
        LinkedStack<Integer> traversalStack = new LinkedStack<>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<>();
        int startIndex = getIndex(startVertex);
        if (!indexIsValid(startIndex)) return resultList.iterator();
        boolean[] visited = new boolean[numVertices];

        traversalStack.push(startIndex);
        resultList.addToRear(vertices[startIndex]);
        visited[startIndex] = true;

        while (!traversalStack.isEmpty()) {
            int x = traversalStack.peek();
            boolean found = false;
            Iterator<Edge> it = adjList[x].iterator();
            while (it.hasNext() && !found) {
                Edge e = it.next();
                int neighborIndex = e.index;
                if (!visited[neighborIndex]) {
                    traversalStack.push(neighborIndex);
                    resultList.addToRear(vertices[neighborIndex]);
                    visited[neighborIndex] = true;
                    found = true;
                }
            }
            if (!found && !traversalStack.isEmpty()) traversalStack.pop();
        }
        return resultList.iterator();
    }

    @Override
    public Iterator<T> interatorShortestPath(T startVertex, T targetVertex) {
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<>();
        int startIndex = getIndex(startVertex);
        int targetIndex = getIndex(targetVertex);
        if (!indexIsValid(startIndex) || !indexIsValid(targetIndex)) {
            return resultList.iterator();
        }

        double[] dist = new double[numVertices];
        int[] pred = new int[numVertices];
        boolean[] visited = new boolean[numVertices];

        for (int i = 0; i < numVertices; i++) {
            dist[i] = Double.POSITIVE_INFINITY;
            pred[i] = -1;
            visited[i] = false;
        }
        dist[startIndex] = 0.0;

        for (int count = 0; count < numVertices; count++) {
            int u = -1;
            double min = Double.POSITIVE_INFINITY;
            for (int i = 0; i < numVertices; i++) {
                if (!visited[i] && dist[i] < min) {
                    min = dist[i];
                    u = i;
                }
            }
            if (u == -1) break;
            visited[u] = true;
            if (u == targetIndex) break;

            Iterator<Edge> it = adjList[u].iterator();
            while (it.hasNext()) {
                Edge e = it.next();
                int v = e.index;
                double w = e.weight;
                if (!visited[v] && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pred[v] = u;
                }
            }
        }

        if (pred[targetIndex] == -1 && startIndex != targetIndex) {
            return resultList.iterator();
        }

        ArrayUnorderedList<T> reverse = new ArrayUnorderedList<>();
        int crawl = targetIndex;
        while (crawl != -1) {
            reverse.addToRear(vertices[crawl]);
            crawl = pred[crawl];
        }

        for (T vertex : reverse) {
            resultList.addToFront(vertex);
        }

        return resultList.iterator();
    }


    @Override
    public boolean isEmpty() { return numVertices == 0; }

    @Override
    public boolean isConnected() { return false; }

    @Override
    public int size() { return numVertices; }

    @Override
    public Iterator<T> iterator() {
        ArrayUnorderedList<T> vertexList = new ArrayUnorderedList<>();
        for (int i = 0; i < numVertices; i++) {
            vertexList.addToRear(vertices[i]);
        }
        return vertexList.iterator();
    }
}