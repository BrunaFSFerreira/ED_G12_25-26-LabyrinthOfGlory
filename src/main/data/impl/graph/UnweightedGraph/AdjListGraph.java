package main.data.impl.graph.UnweightedGraph;

import main.data.adt.GraphADT;
import main.data.impl.list.ArrayUnorderedList;
import main.data.impl.queue.LinkedQueue;
import main.data.impl.stack.LinkedStack;

import java.util.Iterator;

public class AdjListGraph<T> implements GraphADT<T> {
    protected final int DEFAULT_CAPACITY = 10;
    protected int numVertices;
    protected ArrayUnorderedList<T>[] adjList;
    protected T[] vertices;

    public AdjListGraph() {
        numVertices = 0;
        this.vertices = (T[]) new Object[DEFAULT_CAPACITY];
        this.adjList = (ArrayUnorderedList<T>[]) new ArrayUnorderedList[DEFAULT_CAPACITY];
        for (int i = 0; i < this.adjList.length; i++) {
            this.adjList[i] = new ArrayUnorderedList<>();
        }
    }

    @Override
    public void addVertex(T vertex) {
        if (numVertices == vertices.length) {
            expandCapacity();
        }
        vertices[numVertices] = vertex;
        //Incialized the adjacency list for the new vertex
        adjList[numVertices] = new ArrayUnorderedList<>();
        numVertices++;
    }

    private void expandCapacity() {
        int newCapacity = vertices.length * 2;
        T[] newVertices = (T[]) new Object[newCapacity];
        for (int i = 0; i < vertices.length; i++) {
            newVertices[i] = vertices[i];
        }
        vertices = newVertices;

        ArrayUnorderedList<T>[] newAdj = (ArrayUnorderedList<T>[]) new ArrayUnorderedList[newCapacity];
        for (int i = 0; i < newCapacity; i++) {
            if (i < adjList.length && adjList[i] != null) {
                newAdj[i] = adjList[i];
            } else {
                newAdj[i] = new ArrayUnorderedList<>();
            }
        }
        adjList = newAdj;
    }

    @Override
    public void removeVertex(T vertex) {
        int idx = getIndex(vertex);
        if (!indexIsValid(idx)) {
            return;
        }

        //Remove all edges to this vertex
        for (int i = 0; i < numVertices; i++) {
            if (i == idx) continue;
            ArrayUnorderedList<T> newList = new ArrayUnorderedList<>();
            Iterator<T> it = adjList[i].iterator();
            while (it.hasNext()) {
                T neighbor = it.next();
                if (neighbor == null) continue;
                if (!neighbor.equals(vertex)) {
                    newList.addToRear(neighbor);
                }
            }
            adjList[i] = newList;
        }

        //Shift vertices left
        for (int i = idx; i < numVertices - 1; i++) {
            vertices[i] = vertices[i + 1];
        }
        vertices[numVertices - 1] = null;

        //Ahift adjList left (removing the vertex's adjacency list)
        for (int i = idx; i < numVertices - 1; i++) {
            adjList[i] = adjList[i + 1];
        }
        adjList[numVertices - 1] = new ArrayUnorderedList<>();

        numVertices--;
    }


    @Override
    public void addEdge(T vertex1, T vertex2, double weight) {
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        addEdge(index1, index2);
    }

    private int getIndex(T vertex) {
        for (int i = 0; i < numVertices; i++) {
            if (vertices[i] != null && vertices[i].equals(vertex)) {
                return i;
            }
        }
        return -1;
    }

    public void addEdge(int index1, int index2) {
        if (indexIsValid(index1) && indexIsValid(index2)) {
            //Add the edge in both directions for an undirected graph
            adjList[index1].addToRear(vertices[index2]);
            adjList[index2].addToRear(vertices[index1]);
        }
    }

    private boolean indexIsValid(int index1) {
        return index1 >= 0 && index1 < numVertices;
    }

    @Override
    public void removeEdge(T vertex1, T vertex2) {
        int i1 = getIndex(vertex1);
        int i2 = getIndex(vertex2);
        if (!indexIsValid(i1) || !indexIsValid(i2)) return;

        // Remove vertex2 from vertex1's adjacency list
        ArrayUnorderedList<T> newList1 = new ArrayUnorderedList<>();
        Iterator<T> interator1 = adjList[i1].iterator();
        while (interator1.hasNext()) {
            T neighbor = interator1.next();
            if (!neighbor.equals(vertex2)) {
                newList1.addToRear(neighbor);
            }
        }
        adjList[i1] = newList1;

        // Remove vertex1 from vertex2's adjacency list
        ArrayUnorderedList<T> newList2 = new ArrayUnorderedList<>();
        Iterator<T> interator2 = adjList[i2].iterator();
        while (interator2.hasNext()) {
            T neighbor = interator2.next();
            if (!neighbor.equals(vertex1)) {
                newList2.addToRear(neighbor);
            }
        }
        adjList[i2] = newList2;
    }

    @Override
    public Iterator<T> interatorBFS(T startVertex) {
        LinkedQueue<Integer> traversalQueue = new LinkedQueue<Integer>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<T>();

        int startIndex = getIndex(startVertex);
        if (!indexIsValid(startIndex)) {
            return resultList.iterator();
        }
        boolean[] visited = new boolean[numVertices];

        for (int i = 0; i < numVertices; i++) {
            visited[i] = false;
        }

        traversalQueue.enqueue(startIndex);
        visited[startIndex] = true;
        while (!traversalQueue.isEmpty()) {
            Integer x = traversalQueue.dequeue();
            resultList.addToRear(vertices[x]);

            // Iterate about the neighbors of vertex x in the adjacency list
            Iterator<T> it = adjList[x].iterator();
            while (it.hasNext()) {
                T neighbor = it.next();
                int neighborIndex = getIndex(neighbor);
                if (neighborIndex != -1 && !visited[neighborIndex]) {
                    traversalQueue.enqueue(neighborIndex);
                    visited[neighborIndex] = true;
                }
            }
        }
        return resultList.iterator();
    }

    @Override
    public Iterator<T> interatorDFS(T startVertex) {
        Integer x;
        boolean found;
        LinkedStack<Integer> traversalStack = new LinkedStack<Integer>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<T>();
        boolean[] visited = new boolean[numVertices];

        int startIndex = getIndex(startVertex);
        if (!indexIsValid(startIndex)) {
            return resultList.iterator();
        }

        for (int i = 0; i < numVertices; i++) {
            visited[i] = false;
        }

        traversalStack.push(startIndex);
        resultList.addToRear(vertices[startIndex]);
        visited[startIndex] = true;

        while (!traversalStack.isEmpty()) {
            x = traversalStack.peek();
            found = false;

            Iterator<T> it = adjList[x].iterator();
            while (it.hasNext() && !found) {
                T neighbor = it.next();
                int neighborIndex = getIndex(neighbor);
                if (neighborIndex != -1 && !visited[neighborIndex]) {
                    traversalStack.push(neighborIndex);
                    resultList.addToRear(vertices[neighborIndex]);
                    visited[neighborIndex] = true;
                    found = true;
                }
            }

            if (!found && !traversalStack.isEmpty()) {
                traversalStack.pop();
            }
        }

        return resultList.iterator();
    }

    @Override
    public Iterator<T> interatorShortestPath(T startVertex, T targetVertex) {
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<T>();
        int startIndex = getIndex(startVertex);
        int targetIndex = getIndex(targetVertex);
        if (!indexIsValid(startIndex) || !indexIsValid(targetIndex)) {
            return resultList.iterator();
        }

        LinkedQueue<Integer> q = new LinkedQueue<>();
        boolean[] visited = new boolean[numVertices];
        int[] pred = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            visited[i] = false;
            pred[i] = -1;
        }

        visited[startIndex] = true;
        q.enqueue(startIndex);
        boolean found = false;

        while (!q.isEmpty() && !found) {
            int u = q.dequeue();
            Iterator<T> it = adjList[u].iterator();
            while (it.hasNext() && !found) {
                T neighbor = it.next();
                int v = getIndex(neighbor);
                if (v != -1 && !visited[v]) {
                    visited[v] = true;
                    pred[v] = u;
                    q.enqueue(v);
                    if (v == targetIndex) {
                        found = true;
                        break;
                    }
                }
            }
        }

        if (!visited[targetIndex]) {
            return resultList.iterator();
        }

        int crawl = targetIndex;
        ArrayUnorderedList<T> reverse = new ArrayUnorderedList<>();
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
    public boolean isEmpty() {
        return numVertices == 0;
    }

    @Override
    public boolean isConnected() {
        if (isEmpty()) {
            return true;
        }
        Iterator<T> it = interatorBFS(vertices[0]);
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count == numVertices;
    }

    @Override
    public int size() {
        return numVertices;
    }


}
