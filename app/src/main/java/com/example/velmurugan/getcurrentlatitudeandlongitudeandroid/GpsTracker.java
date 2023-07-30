package com.example.velmurugan.getcurrentlatitudeandlongitudeandroid;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * GpsTracker é uma classe que gerencia o rastreamento da localização GPS do dispositivo Android.
 * Ela implementa a interface LocationListener para receber atualizações de localização do LocationManager.
 */
class GpsTracker implements LocationListener {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // Distância mínima (em metros) para considerar uma atualização de localização
    private static final long MAX_TIME_BW_UPDATES = 1000 * 86400; // Tempo máximo (em milissegundos) entre atualizações de localização (1 dia)
    private double latitude; // Latitude atual do dispositivo
    private double longitude; // Longitude atual do dispositivo

    private Location location; // Objeto Location usado para obter a localização atual
    private final List<LocationData> LocationDataList; // Lista de dados de localização armazenados
    private final Context mContext;

    /**
     * Construtor da classe GpsTracker.
     *
     * @param context O contexto da aplicação para acessar serviços do sistema.
     */
    public GpsTracker(Context context) {
        this.mContext = context;
        LocationDataList = new ArrayList<>();
        getLocation();
    }

    /**
     * Obtém a localização atual do dispositivo.
     *
     * @return Um objeto LocationData representando a localização atual do dispositivo.
     */
    public LocationData getLocation() {
        try {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            if (location == null) {
                // Verifica se a permissão de localização foi concedida
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Solicita a permissão de localização caso não tenha sido concedida
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                }

                // Solicita atualizações de localização ao LocationManager
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MAX_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                // Obtém a última localização conhecida.
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                // Se a localização for válida, atualiza os valores de latitude e longitude e adiciona os dados da localização à lista
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    addLocationData(latitude, longitude);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retorna o último objeto LocationData adicionado à lista, ou null se a lista estiver vazia
        return LocationDataList.isEmpty() ? null : LocationDataList.get(LocationDataList.size() - 1);
    }

    /**
     * Adiciona os dados da localização atual à lista de dados de localização, ordenando-os pelo timestamp.
     *
     * @param latitude  A latitude da localização atual.
     * @param longitude A longitude da localização atual.
     */
    private void addLocationData(double latitude, double longitude) {
        long timestamp = System.currentTimeMillis();
        LocationData LocationData = new LocationData(latitude, longitude, timestamp);
        LocationDataList.add(LocationData);

        // Ordena a lista de LocationData com base no timestamp, do mais antigo ao mais recente
        Collections.sort(LocationDataList, new Comparator<LocationData>() {
            @Override
            public int compare(LocationData c1, LocationData c2) {
                return Long.compare(c1.getTimestamp(), c2.getTimestamp());
            }
        });
    }

    /**
     * Obtém a lista de dados de localização ordenados por timestamp.
     *
     * @return A lista de LocationData ordenada.
     */
    public List<LocationData> getSortedLocationData() {
        return LocationDataList;
    }

    /**
     * Chamado quando a localização é alterada (recebe uma nova atualização de localização).
     * Atualiza a latitude e longitude e adiciona os dados de localização à lista.
     *
     * @param location O novo objeto Location que representa a nova localização do dispositivo.
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            addLocationData(latitude, longitude);
        }
    }

    /**
     * Método chamado quando o status do provedor de localização muda.
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}