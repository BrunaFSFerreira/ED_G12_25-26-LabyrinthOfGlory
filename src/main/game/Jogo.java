package main.game;

import main.data.impl.list.DoubleLinkedUnorderedList;
import main.data.impl.queue.LinkedQueue;
import main.model.Corredor;
import main.model.Divisao;
import main.model.Labirinto;
import main.utils.TipoEvento;

import java.util.Random;

public class Jogo {

    private final Labirinto labirinto;
    private final LinkedQueue<Jogador> filaTurnos;
    private final DoubleLinkedUnorderedList<Jogador> todosJogadores;
    private int turnoAtual;
    private final Random random;
    private Jogador vencedor;

    public Jogo(Labirinto labirinto, DoubleLinkedUnorderedList<Jogador> jogadores) {
        this.labirinto = labirinto;
        this.filaTurnos = new LinkedQueue<>();
        this.todosJogadores = jogadores;
        this.turnoAtual = 1;
        this.random = new Random();

        for (Jogador jogador : jogadores) {
            filaTurnos.enqueue(jogador);
        }
    }

    public void iniciar() {
        System.out.println("--- Jogo Iniciado: Labirinto da Glória ---");

        while (filaTurnos.size() > 0 && vencedor == null) {
            Jogador ativo = filaTurnos.dequeue();

            System.out.println("\n== Turno " + turnoAtual + " - Jogador: " + ativo.getNome() + " ==");

            if (processarBloqueio(ativo)) {
                filaTurnos.enqueue(ativo);
                continue;
            }

            executarJogada(ativo);

            if (verificarVitoria(ativo)) {
                vencedor = ativo;
                break;
            }

            filaTurnos.enqueue(ativo);
            turnoAtual++;
        }

        if (vencedor != null) {
            System.out.println("JOGO TERMINADO. Vencedor: " + vencedor.getNome());
        }
    }

    private boolean processarBloqueio(Jogador ativo) {
        if (ativo.getTurnosBloqueado() > 0) {
            ativo.setTurnosBloqueado(ativo.getTurnosBloqueado() - 1);
            ativo.adicionarAcaoAoHistorico("Bloqueado. Turno pulado. Restante: " + ativo.getTurnosBloqueado());
            System.out.println("-> " + ativo.getNome() + " está bloqueado. Pula a vez. Restante: " + ativo.getTurnosBloqueado());
            return true;
        }
        return false;
    }

    private void executarJogada(Jogador ativo) {
        Divisao atual = ativo.getPosicaoAtual();

        Divisao proxima = ativo.escolherMovimento(this); // Usa o método polimórfico de Jogador/Bot

        if (proxima == null) {
            ativo.adicionarAcaoAoHistorico("Decisão: Nenhum movimento válido encontrado/escolhido.");
            return;
        }

        Corredor corredor = getCorredorParaDestino(atual, proxima);

        if (corredor == null || corredor.isBloqueado()) {
            // Regra: Se o corredor está bloqueado, a Divisão atual deve ser resolvida
            // A Divisão atual tem de ser Alavanca ou Enigma.
            // ... [Lógica de ativação de Alavanca/Enigma deve ser implementada aqui ou na Divisao]
            return;
        }

        // 3. Movimento
        ativo.setPosicaoAtual(proxima);
        ativo.adicionarAcaoAoHistorico("Movimento: " + atual.getId() + " -> " + proxima.getId());


        // 5. Atualização do estado do jogo (visualização)
        System.out.println("-> " + ativo.getNome() + " moveu para " + proxima.getNome());
    }

    private void aplicarEvento(Jogador ativo, TipoEvento evento) {
        ativo.adicionarAcaoAoHistorico("Evento: Ativou evento " + evento.toString());

        switch (evento) {
            case JOGADA_EXTRA:
                // Regra: Coloca o jogador de volta na frente da fila para jogar de novo
                // A implementação da fila encadeada é mais simples se for uma fila circular.
                // Aqui, apenas re-enfilamos *antes* dos jogadores que já estavam na fila (se implementarmos a fila de prioridade).
                // Para uma LinkedQueue simples:
                System.out.println("Evento: " + ativo.getNome() + " ganhou uma jogada extra!");
                // O jogador não é enfileirado novamente no final do loop, mas sim inserido no início.
                break;
            case TROCA_POSICAO:
                Jogador alvo = escolherJogadorAleatorio(ativo);
                if (alvo != null) {
                    Divisao temp = ativo.getPosicaoAtual();
                    ativo.setPosicaoAtual(alvo.getPosicaoAtual());
                    alvo.setPosicaoAtual(temp);
                    ativo.adicionarAcaoAoHistorico("Troca: Trocada posição com " + alvo.getNome());
                    alvo.adicionarAcaoAoHistorico("Troca: Posição trocada por " + ativo.getNome());
                }
                break;
            case BLOQUEIO_TURNOS:
                int turnos = random.nextInt(3) + 1;
                ativo.setTurnosBloqueado(turnos);
                ativo.adicionarAcaoAoHistorico("Bloqueado por " + turnos + " turnos.");
                break;
            case TROCA_GERAL:
                // [Implementação: trocar todos os jogadores de posições de forma aleatória]
                break;
            case RECUA_CASAS:
                // [Implementação: Recuar para uma posição anterior]
                break;
            // ...
        }
    }

    private Jogador escolherJogadorAleatorio(Jogador exclusao) {
        // [Lógica para escolher um jogador da lista 'todosJogadores' que não seja o 'exclusao']
        return null;
    }

    private Corredor getCorredorParaDestino(Divisao origem, Divisao destino) {
        // Usa a lista de vizinhos da Divisão (do seu código)
        for (Corredor corredor : origem.getVizinhos()) {
            if (corredor.getDestino().equals(destino)) {
                return corredor;
            }
        }
        return null;
    }

    private boolean verificarVitoria(Jogador ativo) {
        // Condição de Vitória: O vencedor é o primeiro jogador a alcançar a sala central.
        return ativo.getPosicaoAtual().isTemTesouro(); // Assumindo isTemTesouro na Divisao base
    }

    // Getters para a lógica do Bot
    public Labirinto getLabirinto() {
        return labirinto;
    }
}

