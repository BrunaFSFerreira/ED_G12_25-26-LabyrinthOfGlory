package main.data.impl.graph.WeightedGraph;

import main.data.adt.GraphADT;
import main.data.impl.list.ArrayUnorderedList;
import main.data.impl.queue.LinkedQueue;
import main.data.impl.stack.LinkedStack;

import java.util.Iterator;

/**
 * Graph represents an adjacency matrix implementation of a graph.
 * @param <T>
 */
public class MatrixGraph<T> implements GraphADT<T> {
    protected final int DEFAULT_CAPACITY = 10;
    protected int numVertices; // number of vertices in the graph
    protected double[][] weightMatrix; // adjacency matrix
    protected T[] vertices; // values of vertices
    protected final double NO_EDGE = Double.POSITIVE_INFINITY;

    /**
     * Creates an empty graph
     */
    public MatrixGraph() {
        numVertices = 0;
        this.weightMatrix = new double[DEFAULT_CAPACITY][DEFAULT_CAPACITY];
        this.vertices = (T[]) new Object[DEFAULT_CAPACITY];
        initWeightMatrix(weightMatrix, DEFAULT_CAPACITY);
    }

    private void initWeightMatrix(double[][] weightMatrix, int defaultCapacity) {
        for (int i = 0; i < defaultCapacity; i++) {
            for (int j = 0; j < defaultCapacity; j++) {
                weightMatrix[i][j] = NO_EDGE;
            }
        }
    }

    /**
     * Adds a vertex to the graph, expanding the capacity of the gaph if necessary
     * It also associates an object with the vertex
     * @param vertex the vertex to add to the graph
     */
    @Override
    public void addVertex(T vertex) {
        if (numVertices == vertices.length) {
            expandCapacity();
        }
        vertices[numVertices] = vertex;
        for (int i = 0; i <= numVertices; i++) {
            weightMatrix[numVertices][i] = NO_EDGE;
            weightMatrix[i][numVertices] = NO_EDGE;
        }
        numVertices++;
        
    }

    private void expandCapacity() {
        int newCapacity = vertices.length * 2;
        T[] newVertices = (T[]) new Object[newCapacity];
        for (int i = 0; i < vertices.length; i++) {
            newVertices[i] = vertices[i];
        }
        double[][] newAdj = new double[newCapacity][newCapacity];
        for (int i = 0; i < weightMatrix.length; i++) {
            for (int j = 0; j < weightMatrix.length; j++) {
                newAdj[i][j] = weightMatrix[i][j];
            }
        }
        vertices = newVertices;
        weightMatrix = newAdj;
    }

    @Override
    public void removeVertex(T vertex) {
        int idx = getIndex(vertex);
        if (!indexIsValid(idx)) {
            return;
        }

        // shift vertices left
        for (int i = idx; i < numVertices - 1; i++) {
            vertices[i] = vertices[i + 1];
        }
        vertices[numVertices - 1] = null;

        // shift rows up
        for (int i = idx; i < numVertices - 1; i++) {
            for (int j = 0; j < numVertices; j++) {
                weightMatrix[i][j] = weightMatrix[i + 1][j];
            }
        }

        // shift columns left
        for (int j = idx; j < numVertices - 1; j++) {
            for (int i = 0; i < numVertices - 1; i++) {
                weightMatrix[i][j] = weightMatrix[i][j + 1];
            }
        }

        // clear last row/column
        for (int i = 0; i < numVertices; i++) {
            weightMatrix[numVertices - 1][i] = NO_EDGE;
            weightMatrix[i][numVertices - 1] = NO_EDGE;
        }

        numVertices--;
    }

    /**
     * Inserts an edge between two vertices of the graph
     * @param vertex1 the first vertex
     * @param vertex2 the second vertex
    */
    @Override
    public void addEdge(T vertex1, T vertex2, double weight) {
        addEdge(getIndex(vertex1), getIndex(vertex2), weight);
    }

    private int getIndex(T vertex1) {
        for (int i = 0; i < numVertices; i++) {
            if (vertices[i] == null) {
                if (vertex1 == null) {
                    return i;
                }
            } else if (vertices[i].equals(vertex1)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Inserts an edge between two vertices of the graph
     * @param index1 the first vertex
     * @param index2 the second vertex
     */
    public void addEdge(int index1, int index2, double weight) {
        if (indexIsValid(index1) && indexIsValid(index2)) {
            weightMatrix[index1][index2] = weight;
            weightMatrix[index2][index1] = weight;
        }
    }

    private boolean indexIsValid(int index1) {
        return index1 >= 0 && index1 < numVertices;
    }

    @Override
    public void removeEdge(T vertex1, T vertex2) {
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        if (indexIsValid(index1) && indexIsValid(index2)) {
            weightMatrix[index1][index2] = NO_EDGE;
            weightMatrix[index2][index1] = NO_EDGE;
        }
    }

    private boolean hasEdge(int i, int j) {
        return !Double.isInfinite(weightMatrix[i][j]);
    }

    /**
     * Returns an iterator that performs a breadth first search traversal starting at the given vertex
     * @param startVertex the index to begin the search from
     * @return an iterator that performs a breadth first search traversal
     */
    @Override
    public Iterator<T> interatorBFS(T startVertex) {
        Integer x;
        LinkedQueue<Integer> traversalQueue = new LinkedQueue<Integer>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<T>();

        int startIndex = getIndex(startVertex);
        if (!indexIsValid(startIndex)) {
            return resultList.iterator();
        }
        boolean[] visited = new boolean[numVertices];
        
        traversalQueue.enqueue(startIndex);
        visited[startIndex] = true;
        
        while (!traversalQueue.isEmpty()) {
            x = traversalQueue.dequeue();
            resultList.addToRear(vertices[x]);
            /**
             * Find all vertices adjacent to x that have not been visited and enqueue them up
             */
            for (int i = 0; i < numVertices; i++) {
                if (hasEdge(x, i) && !visited[i]) {
                    traversalQueue.enqueue(i);
                    visited[i] = true;
                }
            }
        }
        return resultList.iterator();
    }

    /**
     * Returns an iterator that performs a depth first search traversal starting at the given vertex
     * @param startVertex the index to begin the search transversal from
     * @return an iterator that performs a depth first search traversal
     */
    @Override
    public Iterator<T> interatorDFS(T startVertex) {
        Integer x;
        boolean found;
        LinkedStack<Integer> traversalStack = new LinkedStack<Integer>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<T>();
        boolean[] visited = new boolean[numVertices];

        int startIndex = getIndex(startVertex);
        if(!indexIsValid(startIndex)) {
            return resultList.iterator();
        }
        
        traversalStack.push(startIndex);
        resultList.addToRear(vertices[startIndex]);
        visited[startIndex] = true;
        
        while (!traversalStack.isEmpty()) {
            x = traversalStack.peek();
            found = false;
            /**
             * Find a vertex adjacent to x that has not been visited and push it on the stack
             */
            for (int i = 0; i < numVertices && !found; i++) {
                if (hasEdge(x, i) && !visited[i]) {
                    traversalStack.push(i);
                    resultList.addToRear(vertices[i]);
                    visited[i] = true;
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

        double[] dist = new double[numVertices];
        boolean[] visited = new boolean[numVertices];
        int[] pred = new int[numVertices];

        for (int i = 0; i < numVertices; i++) {
            dist[i] = NO_EDGE;
            visited[i] = false;
            pred[i] = -1;
        }

        // distância do início para si mesmo é 0
        dist[startIndex] = 0.0;

        // itera numVertices vezes (ou até esgotar)
        for (int count = 0; count < numVertices; count++) {
            // escolhe o vértice não visitado com menor dist
            double min = NO_EDGE;
            int u = -1;
            for (int i = 0; i < numVertices; i++) {
                if (!visited[i] && !Double.isInfinite(dist[i]) && (u == -1 || dist[i] < min)) {
                    min = dist[i];
                    u = i;
                }
            }

            // se não encontrou vértice acessível restante, encerra
            if (u == -1) break;

            // marca u como visitado (dist finalizada)
            visited[u] = true;

            // otimização: se já alcançamos o alvo, podemos parar cedo
            if (u == targetIndex) break;

            // relaxa as arestas (u,v)
            for (int v = 0; v < numVertices; v++) {
                if (hasEdge(u, v) && !visited[v]) {
                    double alt = dist[u] + weightMatrix[u][v];
                    // se caminho via u for melhor, atualiza distância e predecessor
                    if (Double.isInfinite(dist[v]) || alt < dist[v]) {
                        dist[v] = alt;
                        pred[v] = u;
                    }
                }
            }
        }

        // se alvo inalcançável, retorna iterador vazio
        if (Double.isInfinite(dist[targetIndex])) {
            return resultList.iterator();
        }

        // reconstrói o caminho do target até o start usando pred[]
        ArrayUnorderedList<T> reverse = new ArrayUnorderedList<T>();
        int crawl = targetIndex;
        while (crawl != -1) {
            reverse.addToRear(vertices[crawl]);
            crawl = pred[crawl];
        }

        // inverte a ordem para obter start -> ... -> target
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
        return false;
    }

    @Override
    public int size() {
        return numVertices;
    }
}
