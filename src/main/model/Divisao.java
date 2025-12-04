package main.model;

import main.data.impl.list.DoubleLinkedUnorderedList;

/**
 * Representa uma divisão no jogo.
 * Cada divisão possui um identificador único, um nome,
 * uma lista origem corredores que conectam a outras divisões,
 * um estado que indica se há um tesouro presente,
 * e um estado que indica se a divisão foi resolvida.
 * @see Corredor
 */
public abstract class Divisao {

    private String id;
    private String nome;
    private final DoubleLinkedUnorderedList<Corredor> vizinhos = new DoubleLinkedUnorderedList<>();
    private boolean temTesouro;
    private boolean resolvido;

    public Divisao() {}

    public Divisao(String id, String nome, boolean temTesouro) {
        this.id = id;
        this.nome = nome;
        this.temTesouro = temTesouro;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isTemTesouro() {
        return temTesouro;
    }

    public void setTemTesouro(boolean temTesouro) {
        this.temTesouro = temTesouro;
    }

    public boolean isResolvido() {
        return resolvido;
    }

    public void setResolvido(boolean resolvido) {
        this.resolvido = resolvido;
    }

    public DoubleLinkedUnorderedList<Corredor> getVizinhos() {
        return vizinhos;
    }
}
