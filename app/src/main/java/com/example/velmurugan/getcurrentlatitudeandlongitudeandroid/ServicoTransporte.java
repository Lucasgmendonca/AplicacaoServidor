package com.example.velmurugan.getcurrentlatitudeandlongitudeandroid;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um serviço de transporte que possui um número de identificação,
 * data e hora de início e data e hora de fim, bem como listas de cargas e motoristas associados.
 */
public class ServicoTransporte {
    private String numeroIdentificacao;
    private String dataHoraInicio;
    private String dataHoraFim;
    private List<Carga> cargas;
    private List<Motorista> motoristas;

    /**
     * Construtor da classe ServicoTransporte.
     *
     * @param numeroIdentificacao O número de identificação do serviço de transporte.
     * @param dataHoraInicio A data e hora de início do serviço de transporte.
     * @param dataHoraFim A data e hora de fim do serviço de transporte.
     */
    public ServicoTransporte(String numeroIdentificacao, String dataHoraInicio, String dataHoraFim) {
        this.numeroIdentificacao = numeroIdentificacao;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.cargas = new ArrayList<>();
        this.motoristas = new ArrayList<>();
    }

    /**
     * Obtém o número de identificação do serviço de transporte.
     *
     * @return O número de identificação do serviço de transporte.
     */
    public String getNumeroIdentificacao() {
        return numeroIdentificacao;
    }

    /**
     * Obtém a data e hora de início do serviço de transporte.
     *
     * @return A data e hora de início do serviço de transporte.
     */
    public String getDataHoraInicio() {
        return dataHoraInicio;
    }

    /**
     * Obtém a data e hora de fim do serviço de transporte.
     *
     * @return A data e hora de fim do serviço de transporte.
     */
    public String getDataHoraFim() {
        return dataHoraFim;
    }

    /**
     * Obtém a lista de cargas associadas ao serviço de transporte.
     *
     * @return A lista de cargas associadas ao serviço de transporte.
     */
    public List<Carga> getCargas() {
        return cargas;
    }

    /**
     * Obtém a lista de motoristas associados ao serviço de transporte.
     *
     * @return A lista de motoristas associados ao serviço de transporte.
     */
    public List<Motorista> getMotoristas() {
        return motoristas;
    }
}