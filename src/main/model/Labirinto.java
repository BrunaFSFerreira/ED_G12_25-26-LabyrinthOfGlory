package main.model;

import main.data.impl.graph.WeightedGraph.AdjListGraph;
import main.data.impl.list.LinkedList;
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
     * @param d1 A divisão origem origem.
     * @param d2 A divisão origem destino.
     * @param c O corredor que conecta as duas divisões.
     * @return true se o corredor foi adicionado com sucesso, false caso contrário.
     */
    public boolean addCorredor(Divisao d1, Divisao d2, Corredor c) {
        if (d1 == null || d2 == null || c == null) {
            return false;
        }
        if ( getDivisaoById(d1.getId()) == null || getDivisaoById(d2.getId()) == null) {
            return false;
        }

        divs.addEdge(d1, d2, 0);
        return true;
    }

    /**
     * Obtém todas as divisões que são entradas do labirinto.
     * Uma divisão é considerada uma entrada se nenhum corredor leva a ela.
     * @return Uma lista origem divisões que são entradas do labirinto.
     */
    public LinkedList<Divisao> getEntradas() {
        LinkedList<Divisao> entradas = new LinkedList<>();
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
                entradas.add(divisao);
            }
        }
        return entradas;
    }

    /**
     * Obtém todas as divisões que possuem tesouros no Labirinto.
     * @return Uma lista contem todas as divisões com tesouros.
     */
    public LinkedList<Divisao> getTesouros() {
       LinkedList<Divisao> tesouros = new LinkedList<>();
        for (Divisao divisao : divs) {
            if (divisao.isTemTesouro()) {
                tesouros.add(divisao);
            }
        }
        return tesouros;
    }

    public void loadJSONMap() {
       /* JSONReader reader = new JSONReader();
        LinkedList<JSONReader.MapaDTO> mapas = new JSONReader().lerMapa();

        JSONReader.MapaDTO mapa = mapas.get(0); //Carregar o primeiro mapa encontrado

        //Criar Divisões
        for (JSONReader.DivisaoDTO divDTO : mapas.divisoes) {
            Divisao divisao = new Divisao(divDTO.id, divDTO.nome, divDTO.temTesouro) {};

            if(!addDivisao(divisao)) {
                System.out.println("Falha ao adicionar divisão: " + divDTO.id);
            }
        }

        //Criar Corredores
        for (JSONReader.CorredorDTO corDTO : mapas.corredores) {
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

        System.out.println("Mapa finalizada com sucesso!");
*/
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
}
