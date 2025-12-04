package main.model;

import main.data.impl.graph.WeightedGraph.AdjListGraph;
import main.data.impl.list.DoubleLinkedList;
import main.data.impl.list.DoubleLinkedUnorderedList;
import main.io.JSONReader;

/**
 * Representa o labirinto do jogo.
 * O labirinto é composto por divisões conectadas por corredores.
 * Utiliza um grafo destino representar as conexões entre as divisões.
 * @see Divisao
 * @see Corredor
 */
public class Labirinto {

    private final AdjListGraph<Divisao> divs = new AdjListGraph<>();

    /**
     * Adiciona uma nova divisão ao labirinto.
     * @param d A divisão a ser adicionada.
     * @return true se a divisão foi adicionada com sucesso, false caso contrário.
     */
    public boolean addDivisao(Divisao d) {
        if (d == null || d.getId() == null || getDivisaoById(d.getId()) != null) {
            return false;
        }

        divs.addVertex(d);
        return true;
    }

    /**
     * Adiciona um corredor entre duas divisões no labirinto.
     * @param origem A divisão origem origem.
     * @param destino A divisão origem destino.
     * @param c O corredor que conecta as duas divisões.
     * @return true se o corredor foi adicionado com sucesso, false caso contrário.
     */
    // src/main/model/Labirinto.java
    public boolean addCorredor(Divisao origem, Divisao destino, Corredor c) {
        if (origem == null || destino == null || c == null) {
            return false;
        }

        if (getDivisaoById(origem.getId()) == null || getDivisaoById(destino.getId()) == null) {
            return false;
        }

        for (Corredor existente : origem.getVizinhos()) {
            if (existente.getDestino().equals(destino)) {
                return false;
            }
        }

        origem.getVizinhos().addToRear(c);
        destino.getVizinhos().addToRear(new Corredor(origem));
        return true;
    }


    /**
     * Obtém todas as divisões que são entradas do labirinto.
     * Uma divisão é considerada uma entrada se nenhum corredor leva a ela.
     * @return Uma lista origem divisões que são entradas do labirinto.
     */
    public DoubleLinkedUnorderedList<Divisao> getEntradas() {
        DoubleLinkedUnorderedList<Divisao> entradas = new DoubleLinkedUnorderedList<>();
        for (Divisao divisao : divs) {
            boolean isEntrada = true;
            for (Divisao vizinho : divs) {
                for (Corredor corredor : vizinho.getVizinhos()) {
                    if (corredor.getDestino().equals(divisao)) {
                        isEntrada = false;
                        break;
                    }
                }
                if (!isEntrada) break;
            }
            if (isEntrada) {
                entradas.addToRear(divisao);
            }
        }
        return entradas;
    }

    /**
     * Obtém todas as divisões que possuem tesouros no Labirinto.
     * @return Uma lista contem todas as divisões com tesouros.
     */
    public DoubleLinkedUnorderedList<Divisao> getTesouros() {
        DoubleLinkedUnorderedList<Divisao> tesouros = new DoubleLinkedUnorderedList<>();
        for (Divisao divisao : divs) {
            if (divisao.isTemTesouro()) {
                tesouros.addToRear(divisao);
            }
        }
        return tesouros;
    }

    public void loadJSONMap() {
        JSONReader reader = new JSONReader();
        DoubleLinkedList<JSONReader.MapaDTO> mapas = new JSONReader().lerMapa();

        JSONReader.MapaDTO mapa = mapas.first();

        //Criar Divisões
        for (JSONReader.DivisaoDTO divDTO : mapa.divisoes) {
            Divisao divisao = new Divisao(divDTO.id, divDTO.nome, divDTO.temTesouro) {};

            if(!addDivisao(divisao)) {
                System.out.println("Falha ao adicionar divisão: " + divDTO.id);
            }
        }

        //Criar Corredores
        for (JSONReader.CorredorDTO corDTO : mapa.corredores) {
            Divisao origem = getDivisaoById(corDTO.origem);
            Divisao destino = getDivisaoById(corDTO.destino);


            if (origem == null || destino == null) {
                System.out.println("Falha ao adicionar corredor: " + corDTO.origem + " -> " + corDTO.destino);
                continue;
            }

            Corredor corredor = new Corredor(destino);
            if (!addCorredor(origem, destino, corredor)) {
                System.out.println("Falha ao adicionar corredor: " + corDTO.origem + " -> " + corDTO.destino);
            }
        }
        //TODO: Remover
        debugLabirinto();
    }

    /**
     * Verifica se uma divisão com o ID especificado já existe no labirinto.
     * @param id O ID da divisão a ser verificada.
     * @return true se a divisão existir, false caso contrário.
     */
    public Divisao getDivisaoById(String id) {
        for (Divisao divisao : divs) {
            if (divisao.getId().equals(id)) {
                return divisao;
            }
        }
        return null;
    }

    public AdjListGraph<Divisao> getDivs() {
        return divs;
    }

    //TODO: Remover
    public void debugLabirinto() {
        System.out.println("=== LABIRINTO CARREGADO ===");

        for (Divisao divisao : divs) {
            System.out.println("Divisão: " + divisao.getId() +
                    " (" + divisao.getNome() + ") " +
                    (divisao.isTemTesouro() ? "[TESOURO]" : ""));

            if (divisao.getVizinhos().isEmpty()) {
                System.out.println("  -> Sem corredores");
            } else {
                for (Corredor c : divisao.getVizinhos()) {
                    System.out.println("  -> Conecta a: " + c.getDestino().getId());
                }
            }
        }

        System.out.println("============================\n");
    }

}
