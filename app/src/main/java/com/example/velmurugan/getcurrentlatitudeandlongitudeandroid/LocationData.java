package com.example.velmurugan.getcurrentlatitudeandlongitudeandroid;

/**
 * Essa classe representa os dados de localização contendo latitude, longitude e timestamp.
 */
public class LocationData {
    private double latitude;
    private double longitude;
    private long timestamp;

    /**
     * Constrói um novo objeto LocationData com a latitude, longitude e timestamp fornecidos.
     *
     * @param latitude  o valor da latitude
     * @param longitude o valor da longitude
     * @param timestamp o valor do timestamp
     */
    public LocationData(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    /**
     * Obtém a latitude da localização.
     *
     * @return A latitude da localização.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Obtém a longitude da localização.
     *
     * @return A longitude da localização.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Obtém o timestamp da localização.
     *
     * @return O timestamp da localização.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Define o valor da latitude.
     *
     * @param latitude o valor da latitude a ser definido
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Define o valor da longitude.
     *
     * @param longitude o valor da longitude a ser definido
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Define o valor do timestamp.
     *
     * @param timestamp o valor do timestamp a ser definido
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}