package com.example.velmurugan.getcurrentlatitudeandlongitudeandroid;

/**
 * Classe Motorista representa um objeto de motorista com um nome.
 */
public class Motorista {
    private String nome;

    /**
     * Construtor da classe Motorista.
     *
     * @param nome O nome do motorista.
     */
    public Motorista(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém a o nome do motorista.
     *
     * @return O nome do motorista.
     */
    public String getNome() {
        return nome;
    }
}