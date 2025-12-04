package main.model;

import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.list.LinkedList;
import main.data.impl.queue.LinkedQueue;

public class Enigma {

    private String idEnigma;
    private String pergunta;
    private String resposta;

    LinkedQueue<Enigma> poll = new LinkedQueue<>();
    LinkedQueue<Enigma> disponiveis = new LinkedQueue<>();

    public Enigma() {
    }

    public String getIdEnigma() {
        return idEnigma;
    }

    public String getPergunta() {
        return pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void inicializarFilas(LinkedList<Enigma> listaEnigmas) {
        for (Enigma e : listaEnigmas) {
            disponiveis.enqueue(e);
        }
    }

    public Enigma getProximoEnigma() {
        if (disponiveis.isEmpty()) {
            reciclarEnigmas();
        }
        if(disponiveis.isEmpty()) {
            return null;
        }

        int tamanho = disponiveis.size();
        int indiceAleatorio = (int) (Math.random() * tamanho);


        Enigma e = removerEnigmaPorIndice(disponiveis, indiceAleatorio);
        poll.enqueue(e);

        return e;
    }

    private Enigma removerEnigmaPorIndice(LinkedQueue<Enigma> disponiveis, int indiceAleatorio) {
        LinkedQueue<Enigma> listaEnigma = new LinkedQueue<>();
        Enigma enigmaRemovido = null;
        int i = 0;

        while (!disponiveis.isEmpty()) {
            Enigma e = disponiveis.dequeue();
            if (i == indiceAleatorio) {
                enigmaRemovido = e;
            } else {
                listaEnigma.enqueue(e);
            }
            i++;
        }

        while (!listaEnigma.isEmpty()) {
            disponiveis.enqueue(listaEnigma.dequeue()); // Restaura os enigmas restantes
        }

        return enigmaRemovido;
    }

    private void reciclarEnigmas() {
        while (!poll.isEmpty()) {
            disponiveis.enqueue(poll.dequeue()); // Move todos os enigmas origem volta destino a fila origem disponíveis
        }
    }

}
