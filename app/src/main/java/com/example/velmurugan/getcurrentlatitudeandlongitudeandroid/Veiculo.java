package com.example.velmurugan.getcurrentlatitudeandlongitudeandroid;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;


/**
 * Classe que representa um veículo.
 */
public class Veiculo {
    private static final double LATITUDE_INICIAL = -20.46614333333333;
    private static final double LONGITUDE_INICIAL = -45.813025;
    private static final double LATITUDE_FINAL = -20.45683;
    private static final double LONGITUDE_FINAL = -45.83586333333333;
    private static final long TEMPO_PARA_DESTINO_FINAL = 100;
    private static final double DESLOCAMENTO_PARCIAL = 0.5052034858527461;

    private boolean verificaTrocaLocalizacao;
    private double velocidadeMediaParcial;
    private double distanciaPercorrida;
    private double consumoCombustivelTotal;
    private double velocidadeMediaTotal;
    private double velocidadeRecomendada;
    private long tempoDeslocamento;
    private long tempoParaDestinoFinal;
    private int intervaloLocalizacoes;
    private int respectivoIntervalo;

    private GpsTracker gpsTracker;
    private ServicoTransporte servicoTransporte;

    /**
     * Construtor da classe Veiculo.
     *
     * @param servicoTransporte O objeto ServicoTransporte responsável pelo serviço de transporte associado ao veículo.
     * @param gpsTracker O objeto GpsTracker responsável por rastrear a localização do veículo.
     */
    public Veiculo(ServicoTransporte servicoTransporte, GpsTracker gpsTracker) {
        this.servicoTransporte = servicoTransporte;
        this.gpsTracker = gpsTracker;
        this.verificaTrocaLocalizacao = false;
        this.velocidadeMediaParcial = 0;
        this.distanciaPercorrida = 0;
        this.consumoCombustivelTotal = 0;
        this.velocidadeMediaTotal = 0;
        this.velocidadeRecomendada = (DESLOCAMENTO_PARCIAL * 1000 / 20 ) * 3.6;
        this.tempoDeslocamento = 0;
        this.tempoParaDestinoFinal = TEMPO_PARA_DESTINO_FINAL;
        this.intervaloLocalizacoes = 1;
        this.respectivoIntervalo = 0;
    }

    /**
     * Obtém o valor da variável que indica se houve troca de localização.
     *
     * @return true se houve troca de localização, false caso contrário.
     */
    public boolean getVerificaTrocaLocalizacao() {
        return verificaTrocaLocalizacao;
    }

    /**
     * Obtém a velocidade média parcial do veículo.
     *
     * @return a velocidade média parcial.
     */
    public double getVelocidadeMediaParcial() {
        return velocidadeMediaParcial;
    }

    /**
     * Obtém a distância percorrida pelo veículo.
     *
     * @return a distância percorrida.
     */
    public double getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    /**
     * Obtém o consumo total de combustível do veículo.
     *
     * @return o consumo total de combustível.
     */
    public double getConsumoCombustivelTotal() {
        return consumoCombustivelTotal;
    }

    /**
     * Obtém a velocidade média total do veículo.
     *
     * @return a velocidade média total.
     */
    public double getVelocidadeMediaTotal() {
        return velocidadeMediaTotal;
    }

    /**
     * Obtém a velocidade recomendada para o veículo.
     *
     * @return a velocidade recomendada.
     */
    public double getVelocidadeRecomendada() {
        return velocidadeRecomendada;
    }

    /**
     * Obtém o tempo de deslocamento do veículo.
     *
     * @return o tempo de deslocamento.
     */
    public long getTempoDeslocamento() {
        return tempoDeslocamento;
    }

    /**
     * Obtém o tempo restante para o destino final do veículo.
     *
     * @return o tempo restante para o destino final.
     */
    public long getTempoParaDestinoFinal() {
        return tempoParaDestinoFinal;
    }

    /**
     * Obtém o intervalo de tempo entre as 2 últimas localizações do veículo.
     *
     * @return o intervalo de tempo entre as 2 últimas localizações do veículo.
     */
    public int getIntervaloTempoLocalizacoes() {
        return intervaloLocalizacoes;
    }

    /**
     * Obtém o respectivo intervalo de localizações do veículo.
     *
     * @return o respectivo intervalo de localizações.
     */
    public int getRespectivoIntervalo() {
        return respectivoIntervalo;
    }

    /**
     * Atualiza os dados do veículo com a nova localização e escreva os dados no JSON criptografado usando a classe JSONEscritor
     *
     * @param latitude  a nova latitude.
     * @param longitude a nova longitude.
     * @param timestamp o timestamp da nova localização.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void atualizarDados(double latitude, double longitude, long timestamp) {
        List<LocationData> locationDataList = gpsTracker.getSortedLocationData();
        int size = locationDataList.size();

        if (size >= 1) {
            LocationData lastLocationData = locationDataList.get(size - 1);

            // Verifica se houve troca de localização
            if (lastLocationData.getLatitude() != latitude || lastLocationData.getLongitude() != longitude) {
                lastLocationData.setLatitude(latitude);
                lastLocationData.setLongitude(longitude);
                lastLocationData.setTimestamp(timestamp);
                verificaTrocaLocalizacao = true;
            }

            if (size >= 2) {

                // Calcula a distância percorrida desde o ponto inicial
                distanciaPercorrida = calculoDistancia(
                        LATITUDE_INICIAL, LONGITUDE_INICIAL,
                        latitude, longitude
                );

                LocationData secondLastLocationData = locationDataList.get(size - 2);

                if (latitude < LATITUDE_FINAL && longitude > LONGITUDE_FINAL) {
                    tempoDeslocamento = (System.currentTimeMillis() - secondLastLocationData.getTimestamp()) / 1000;
                    tempoParaDestinoFinal--;
                }

                if (getVerificaTrocaLocalizacao()) {
                    respectivoIntervalo++;
                    // Calcula a velocidade média parcial
                    velocidadeMediaParcial = (DESLOCAMENTO_PARCIAL * 1000 / getIntervaloTempoLocalizacoes()) * 3.6;
                    // Calcula o consumo de combustível total
                    consumoCombustivelTotal += calculoConsumoCombustivel();
                    // Calcula a velocidade média total
                    velocidadeMediaTotal = (DESLOCAMENTO_PARCIAL * getRespectivoIntervalo() * 1000 / getTempoDeslocamento()) * 3.6;
                    // Calcula a velocidade recomendada
                    velocidadeRecomendada = calculoVelocidadeReconciliacao();

                    try {
                        // Gere um par de chaves RSA
                        KeyPair keyPair = gerarParDeChavesRSA();
                        PublicKey publicKey = keyPair.getPublic();
                        PrivateKey privateKey = keyPair.getPrivate();

                        // Escreva os dados no JSON criptografado usando a classe JSONEscritor
                        JSONEscritor writerThread = new JSONEscritor(velocidadeMediaParcial, distanciaPercorrida,
                                tempoParaDestinoFinal, servicoTransporte.getNumeroIdentificacao(), servicoTransporte.getDataHoraInicio(),
                                servicoTransporte.getDataHoraFim(), servicoTransporte.getCargas().get(0).getDescricao(),
                                servicoTransporte.getMotoristas().get(0).getNome(), respectivoIntervalo, intervaloLocalizacoes, publicKey, privateKey);

                        writerThread.start();
                        try {
                            writerThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intervaloLocalizacoes = 1;
                }
                intervaloLocalizacoes++;
                verificaTrocaLocalizacao = false;
            }
        }
    }

    /**
     * Calcula a distância entre duas coordenadas geográficas usando a fórmula de Haversine.
     *
     * @param lat1 a latitude da primeira coordenada.
     * @param lon1 a longitude da primeira coordenada.
     * @param lat2 a latitude da segunda coordenada.
     * @param lon2 a longitude da segunda coordenada.
     * @return a distância entre as coordenadas em quilômetros.
     */
    private double calculoDistancia(double lat1, double lon1, double lat2, double lon2) {
        double raioTerra = 6371; // Raio da Terra em quilômetros

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return raioTerra * c;
    }

    // Variáveis de Reconciliação para cálculo da velocidade recomendada com base no Tempo para chegar ao Destino Final
    private double P1 = 20.0;
    private double P2 = 20.0;
    private double P3 = 20.0;
    private double P4 = 20.0;
    private double P5 = 20.0;
    private double variacaoTempoParaDestinoFinal = 0.0;
    private double V1 = 1.0;
    private double V2 = 1.0;
    private double V3 = 1.0;
    private double V4 = 1.0;
    private double V5 = 1.0;

    /**
     * Calcula a velocidade de reconciliação com base no tempo para o chegar ao Destino Final.
     *
     * @return a velocidade de reconciliação em km/h.
     */
    private double calculoVelocidadeReconciliacao() {

        // Atualiza as variáveis P e V com base no intervalo atual
        if (getRespectivoIntervalo() == 1) {
            P1 = getIntervaloTempoLocalizacoes();
            V1 = 0.0;
        } else if (getRespectivoIntervalo() == 2) {
            P2 = getIntervaloTempoLocalizacoes();
            V2 = 0.0;
        } else if (getRespectivoIntervalo() == 3) {
            P3 = getIntervaloTempoLocalizacoes();
            V3 = 0.0;
        } else if (getRespectivoIntervalo() == 4) {
            P4 = getIntervaloTempoLocalizacoes();
            V4 = 0.0;
        } else if (getRespectivoIntervalo() == 5) {
            P5 = getIntervaloTempoLocalizacoes();
            V5 = 0.0;
            variacaoTempoParaDestinoFinal = 1.0;
        }

        double[] y = new double[]{TEMPO_PARA_DESTINO_FINAL, P1, P2, P3, P4, P5};
        double[] v = new double[]{variacaoTempoParaDestinoFinal, V1, V2, V3, V4, V5};
        double[][] A = new double[][]{{1.0, -1.0, -1.0, -1.0, -1.0, -1.0}};
        Reconciliation rec = new Reconciliation(y, v, A);

        // System.out.println("Y_hat:");
        // rec.printMatrix(rec.getReconciledFlow());

        double[] reconciledFlow = rec.getReconciledFlow();

        if (getRespectivoIntervalo() >= 5) {
            return 0;
        }

        return (DESLOCAMENTO_PARCIAL * 1000 / reconciledFlow[getRespectivoIntervalo()+1])*3.6;
    }

    /**
     * Calcula o consumo de combustível com base na velocidade média parcial.
     *
     * @return o consumo de combustível.
     */
    private double calculoConsumoCombustivel() {
        double consumoCombustivelPorKm;

        if (getVelocidadeMediaParcial() >= 0 && getVelocidadeMediaParcial() <= 80) {
            consumoCombustivelPorKm = 0.047;
        } else if (getVelocidadeMediaParcial() > 80 && getVelocidadeMediaParcial() <= 120) {
            consumoCombustivelPorKm = 0.0641;
        } else {
            consumoCombustivelPorKm = 0.0962;
        }

        return DESLOCAMENTO_PARCIAL * consumoCombustivelPorKm;
    }

    /**
     * Método para gerar um par de chaves RSA.
     *
     * @return Um par de chaves RSA (pública e privada).
     */
    public static KeyPair gerarParDeChavesRSA() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Tamanho da chave (2048 bits)
        return keyPairGenerator.generateKeyPair();
    }
}