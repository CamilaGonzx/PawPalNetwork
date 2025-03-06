package com.example.pawpalnetwork.bd;

import java.util.UUID;

public class Mensaje {
    private String id;
    private String chatId; // ID del chat al que pertenece
    private String remitenteId; // ID del remitente (Usuario)
    private String contenido;
    private long fecha; // Timestamp
    private int leido; // Estado de lectura del mensaje

    public Mensaje() {
    }

    public Mensaje(String id, String chatId, String remitenteId, String contenido, long fecha, int leido) {
        this.id = id;
        this.chatId = chatId;
        this.remitenteId = remitenteId;
        this.contenido = contenido;
        this.fecha = fecha;
        this.leido = leido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(String remitenteId) {
        this.remitenteId = remitenteId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public int getLeido() {
        return leido;
    }

    public void setLeido(int leido) {
        this.leido = leido;
    }
}
