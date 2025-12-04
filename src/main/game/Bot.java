package main.game;

import main.data.adt.GraphADT;
import main.data.impl.graph.WeightedGraph.AdjListGraph;
import main.data.impl.list.LinkedList;
import main.model.Corredor;
import main.model.Divisao;
import main.model.Labirinto;

import java.util.Iterator;

public class Bot extends Jogador {
    public Bot(String nome, Divisao posicaoInicial) {
        super(nome, posicaoInicial);
    }

    @Override
    public Divisao escolherMovimento(Jogo jogo) {
        Labirinto lab = jogo.getLabirinto();
        AdjListGraph<Divisao> grafo = lab.getDivs();
        LinkedList<Divisao> tesouro = jogo.getLabirinto().getTesouros();
        Divisao atual = getPosicaoAtual();

        if (tesouro != null) {
            Iterator<Divisao> caminho = grafo.interatorShortestPath(atual, tesouro);

            if(caminho.hasNext()){
                caminho.next();
                if(caminho.hasNext()){
                    return caminho.next();
                }
            }
        }

        Iterator<Corredor> vizinhos = atual.getVizinhos().iterator();
        if (vizinhos.hasNext()) {
            return vizinhos.next().getDestino();
        }
        return null;
    }


}
