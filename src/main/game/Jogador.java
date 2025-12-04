package main.game;

import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.list.LinkedList;
import main.model.Divisao;

public class Jogador {
    private final String nome;
    private Divisao posicaoAtual;
    private int turnosBloqueado;
    private final DoubleLinkedUnorderedList<String> historicoAcoes;

    public Jogador(String nome, Divisao posicaoInicial) {
        this.nome = nome;
        this.posicaoAtual = posicaoInicial;
        this.turnosBloqueado = 0;
        this.historicoAcoes = new DoubleLinkedUnorderedList<>();
    }

    public String getNome() { return nome; }
    public Divisao getPosicaoAtual() { return posicaoAtual; }
    public void setPosicaoAtual(Divisao novaPosicao) { this.posicaoAtual = novaPosicao; }
    public int getTurnosBloqueado() { return turnosBloqueado; }
    public void setTurnosBloqueado(int turnos) { this.turnosBloqueado = turnos; }
    public DoubleLinkedUnorderedList<String> getHistoricoAcoes() { return historicoAcoes; }

    public void adicionarAcaoAoHistorico(String acao) {
        this.historicoAcoes.addToRear(acao);
    }

    public Divisao escolherMovimento(Jogo jogo) {
        // Implementação base para o modo Manual (deve ser refinado para UI/Input)
        return null;
    }

}
