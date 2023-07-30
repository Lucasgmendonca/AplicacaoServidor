package com.example.velmurugan.getcurrentlatitudeandlongitudeandroid;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Essa classe representa um leitor de dados JSON criptografados que serão lidos do Firebase Realtime Database.
 * Ela é responsável por obter os dados criptografados do banco de dados, descriptografá-los usando AES e RSA,
 * e fornecer o resultado descriptografado.
 */
public class JSONLeitor {
    private JSONLeitorCallback callback;

    /**
     * Interface para definir os métodos de retorno de chamada do JSONLeitor.
     */
    public interface JSONLeitorCallback {
        void onResult(JSONObject result);
        void onError(Exception e);
    }

    /**
     * Construtor da classe JSONLeitor.
     *
     * @param callback  O objeto que implementa a interface JSONLeitorCallback para receber os resultados da leitura.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONLeitor(JSONLeitorCallback callback) {
        this.callback = callback;
    }

    /**
     * Método para ler os dados criptografados do Firebase Realtime Database e decifrá-los.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void lerDados() {
        try {
            // Obtem uma referência ao Firebase Realtime Database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference dataReference = databaseReference.child("dadosCriptografados");

            // Lê os dados do Firebase Realtime Database usando um Listener
            dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        // Obtem os dados criptografados do Firebase
                        String dadosCriptografadosString = dataSnapshot.child("dados").getValue(String.class);
                        String chaveAesCriptografadaString = dataSnapshot.child("chaveAES").getValue(String.class);
                        String chaveRsaString = dataSnapshot.child("chaveRSA").getValue(String.class);

                        // Decodifica os dados Base64 para arrays de bytes
                        byte[] dadosCriptografados = Base64.getDecoder().decode(dadosCriptografadosString);
                        byte[] chaveAesCriptografada = Base64.getDecoder().decode(chaveAesCriptografadaString);
                        byte[] chaveRsa = Base64.getDecoder().decode(chaveRsaString);

                        // Converte a chave privada RSA para o formato PrivateKey
                        PrivateKey privateKey = converterParaPrivateKey(chaveRsa);

                        // Descriptografa a chave AES usando a chave privada RSA
                        byte[] chaveAesDescriptografada = descriptografiaComRSA(chaveAesCriptografada, privateKey);

                        // Converte o array de bytes de volta para um objeto SecretKey
                        SecretKey chaveAES = new SecretKeySpec(chaveAesDescriptografada, "AES");

                        // Descriptografa os dados JSON usando AES
                        byte[] dadosDescriptografados = descriptografiaComAES(dadosCriptografados, chaveAES);

                        // Converte o array de bytes de volta para uma String no formato JSON
                        String dadosJSON = new String(dadosDescriptografados, StandardCharsets.UTF_8);
                        JSONObject result = new JSONObject(dadosJSON);

                        // Chama o callback com o resultado descriptografado (dados JSON).
                        if (callback != null) {
                            callback.onResult(result);
                        }
                    } catch (Exception e) {
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (callback != null) {
                        callback.onError(databaseError.toException());
                    }
                }
            });
        } catch (Exception e) {
            if (callback != null) {
                callback.onError(e);
            }
        }
    }

    /**
     * Realiza a descriptografia dos dados usando AES.
     *
     * @param dadosCriptografados Os dados criptografados a serem descriptografados.
     * @param secretKey     A chave AES usada para descriptografia.
     * @return Os dados descriptografados.
     * @throws Exception Se ocorrer algum erro durante a descriptografia.
     */
    private static byte[] descriptografiaComAES(byte[] dadosCriptografados, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(dadosCriptografados);
    }

    /**
     * Realiza a descriptografia da chave AES usando RSA.
     *
     * @param chaveAesCriptografada A chave AES que será descriptografados.
     * @param privateKey    A chave privada RSA usada para descriptografia.
     * @return A chave AES descriptografada.
     * @throws Exception Se ocorrer algum erro durante a descriptografia.
     */
    private static byte[] descriptografiaComRSA(byte[] chaveAesCriptografada, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(chaveAesCriptografada);
    }

    /**
     * Converte um array de bytes em formato PKCS#8 para uma chave privada RSA.
     *
     * @param chaveRsa O array de bytes representando a chave privada RSA em formato PKCS#8.
     * @return A chave privada RSA no formato PrivateKey.
     * @throws Exception Caso ocorra algum erro durante a conversão.
     */
    private PrivateKey converterParaPrivateKey(byte[] chaveRsa) throws Exception {
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(chaveRsa));
    }
}