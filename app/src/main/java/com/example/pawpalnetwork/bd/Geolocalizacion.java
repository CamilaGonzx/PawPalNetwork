package com.example.pawpalnetwork.bd;

import java.util.UUID;

public class Geolocalizacion {
    private String id;
    private String userId; // ID del servicio al que pertenece la geolocalización
    private double latitud;
    private double longitud;
    private String region; // Información adicional sobre la región o dirección

    public Geolocalizacion() {
    }

    public Geolocalizacion(String id, String userId, double latitud, double longitud, String region) {
        this.id = id;
        this.userId = userId;
        this.latitud = latitud;
        this.longitud = longitud;
        this.region = region;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
