package com.example.velmurugan.getcurrentlatitudeandlongitudeandroid;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.concurrent.Semaphore;

/**
 * Classe LocationThread é uma subclasse da classe Thread, responsável por atualizar
 * a localização e a interface do usuário em intervalos regulares.
 */
public class LocationThread extends Thread {

    private final MainActivity mainActivity;
    private final Semaphore semaphore;

    /**
     * Construtor da classe LocationThread.
     *
     * @param activity   A referência para a MainActivity, onde a localização será atualizada.
     * @param semaphore  Um semáforo para controlar o acesso compartilhado aos recursos.
     */
    public LocationThread(MainActivity activity, Semaphore semaphore) {
        this.mainActivity = activity;
        this.semaphore = semaphore;
    }

    /**
     * O método run é chamado quando a thread é iniciada. Ele atualiza a localização e a interface do usuário
     * em intervalos regulares, enquanto a thread não for interrompida.
     */
    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                semaphore.acquire(); // Aguarda permissão para acessar o recurso compartilhado
                if (mainActivity.getPercursoIniciado()) {
                    mainActivity.runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            mainActivity.atualizarLocalizacao(); // Atualiza a localização e a interface do usuário
                        }
                    });
                }
            } catch (InterruptedException e) {
                break;
            } finally {
                semaphore.release(); // Libera o semáforo para outros threads
            }

            try {
                Thread.sleep(1000); // Aguarda por 1 segundo antes de verificar novamente a localização
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}