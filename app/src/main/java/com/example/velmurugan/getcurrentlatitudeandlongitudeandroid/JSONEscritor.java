package com.example.velmurugan.getcurrentlatitudeandlongitudeandroid;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Essa classe representa um escritor de dados JSON criptografados que será enviado para o Firebase Realtime Database.
 * Ela é responsável por receber os dados relevantes, criptografá-los usando AES e RSA, e enviá-los para o banco de dados.
 */
public class JSONEscritor extends Thread {

    private double velocidadeMediaParcial;
    private double distanciaPercorrida;
    private long tempoParaDestinoFinal;
    private int numeroIdentificacao;
    private String dataHoraInicio;
    private String dataHoraFim;
    private String descricaoCarga;
    private String nomeMotorista;
    private int respectivoIntervalo;
    private int intervaloTempoLocalizacoes;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    /**
     * Construtor da classe JSONEscritor.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONEscritor(double velocidadeMediaParcial, double distanciaPercorrida,
                      long tempoParaDestinoFinal, int numeroIdentificacao, String dataHoraInicio,
                      String dataHoraFim, String descricaoCarga, String nomeMotorista,
                      int respectivoIntervalo, int intervaloTempoLocalizacoes,
                      PublicKey publicKey, PrivateKey privateKey) {
        this.velocidadeMediaParcial = velocidadeMediaParcial;
        this.distanciaPercorrida = distanciaPercorrida;
        this.tempoParaDestinoFinal = tempoParaDestinoFinal;
        this.numeroIdentificacao = numeroIdentificacao;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.descricaoCarga = descricaoCarga;
        this.nomeMotorista = nomeMotorista;
        this.respectivoIntervalo = respectivoIntervalo;
        this.intervaloTempoLocalizacoes = intervaloTempoLocalizacoes;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Esse método será executado quando a thread for iniciada.
     * Ele é responsável por criar um objeto JSON, criptografar os dados com AES e RSA,
     * e enviar os dados criptografados para o Firebase Realtime Database.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        try {
            // Criação do objeto JSON com os dados recebidos no construtor.
            JSONObject dados = new JSONObject();
            dados.put("velocidadeMediaParcial", velocidadeMediaParcial);
            dados.put("distanciaPercorrida", distanciaPercorrida);
            dados.put("tempoParaDestinoFinal", tempoParaDestinoFinal);
            dados.put("numeroIdentificacao", numeroIdentificacao);
            dados.put("dataHoraInicio", dataHoraInicio);
            dados.put("dataHoraFim", dataHoraFim);
            dados.put("descricaoCarga", descricaoCarga);
            dados.put("nomeMotorista", nomeMotorista);
            dados.put("respectivoIntervalo", respectivoIntervalo);
            dados.put("intervaloTempoLocalizacoes", intervaloTempoLocalizacoes);

            // Converte os dados JSON para String
            String dadosJSON = dados.toString();

            // Gera uma chave AES
            SecretKey chaveAES = KeyGenerator.getInstance("AES").generateKey();

            // Criptografa os dados JSON usando AES
            byte[] dadosCriptografados = criptografiaComAES(dadosJSON.getBytes(StandardCharsets.UTF_8), chaveAES);

            // Criptografa a chave AES usando a chave pública RSA
            byte[] chaveAesCriptografada = criptografiaComRSA(chaveAES.getEncoded(), publicKey);

            // Converte os arrays de bytes para strings em formato Base64
            String dadosCriptografadosString = Base64.getEncoder().encodeToString(dadosCriptografados);
            String chaveAesCriptografadaString = Base64.getEncoder().encodeToString(chaveAesCriptografada);
            String chaveRsaString = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            // Envia os dados criptografados e a chave AES criptografada para o Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dataReference = databaseReference.child("dadosCriptografados");
            dataReference.child("dados").setValue(dadosCriptografadosString);
            dataReference.child("chaveAES").setValue(chaveAesCriptografadaString);
            dataReference.child("chaveRSA").setValue(chaveRsaString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Realiza criptografia simétrica dos dados fornecidos usando a chave AES.
     *
     * @param data       Dados a serem criptografados.
     * @param secretKey  Chave secreta AES usada para criptografar os dados.
     * @return Os dados criptografados em formato de array de bytes.
     * @throws Exception Se ocorrer algum erro durante a criptografia.
     */
    private static byte[] criptografiaComAES(byte[] data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * Realiza a criptografia assimétrica dos dados usando RSA (Rivest-Shamir-Adleman).
     *
     * @param data       Chave AES que será criptografada.
     * @param publicKey  A chave pública RSA usada para criptografia.
     * @return A chave AES criptografada em formato de array de bytes.
     * @throws Exception Se ocorrer algum erro durante a criptografia.
     */
    private static byte[] criptografiaComRSA(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }
}