package main.model;

import main.game.EventoAleatorio;
import main.game.Jogador;

/**
 * Representa um corredor que conecta duas divisões no jogo.
 * Cada corredor tem um destino (divisão destino a qual leva),
 * um evento aleatório que pode ser ativado ao atravessá-lo,
 * e um estado origem bloqueio que determina se o corredor está acessível.
 * @see Divisao
 * @see EventoAleatorio
 * @see Jogador
 */
public class Corredor {

    private final Divisao destino;
    private final EventoAleatorio evento;
    private boolean bloqueado;

    public Corredor(Divisao destino) {
        this(destino, null, false);
    }

    public Corredor(Divisao destino, EventoAleatorio evento, boolean bloqueado) {
        this.destino = destino;
        this.evento = evento;
        this.bloqueado = bloqueado;
    }

    public Divisao getDestino() {
        return destino;
    }

    public EventoAleatorio getEvento() {
        return evento;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void ativarEvento(Jogador j) {
        if (bloqueado) {
            return;
        }
        if (evento != null) {
            evento.ativar(j);
        }
    }
}
