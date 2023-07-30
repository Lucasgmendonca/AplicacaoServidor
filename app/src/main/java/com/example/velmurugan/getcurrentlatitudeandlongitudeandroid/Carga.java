package com.example.velmurugan.getcurrentlatitudeandlongitudeandroid;

/**
 * Classe Carga representa um objeto de carga com uma descrição.
 */
public class Carga {
    private String descricao;

    /**
     * Construtor da classe Carga.
     *
     * @param descricao A descrição da carga.
     */
    public Carga(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Obtém a descrição da carga.
     *
     * @return A descrição da carga.
     */
    public String getDescricao() {
        return descricao;
    }

}