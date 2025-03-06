package com.example.pawpalnetwork.bd;

import java.util.UUID;
import java.util.List;

public class Publicacion {
    private UUID id;
    private String contenido;
    private long fecha; // Timestamp
    private UUID autorId; // ID del autor (Proveedor)
    private UUID servicioId; // ID del servicio asociado (si aplica)
    private String status; // Estado de la publicación (visible, oculto, etc.)

    // Relaciones
    private List<UUID> imagenesIds; // Lista de IDs de imágenes asociadas

    private boolean publicada; // Estado de visibilidad

    public Publicacion() {
    }

    public Publicacion(UUID id, String contenido, long fecha, UUID autorId, UUID servicioId, String status, List<UUID> imagenesIds, boolean publicada) {
        this.id = id;
        this.contenido = contenido;
        this.fecha = fecha;
        this.autorId = autorId;
        this.servicioId = servicioId;
        this.status = status;
        this.imagenesIds = imagenesIds;
        this.publicada = publicada;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UUID getAutorId() {
        return autorId;
    }

    public void setAutorId(UUID autorId) {
        this.autorId = autorId;
    }

    public UUID getServicioId() {
        return servicioId;
    }

    public void setServicioId(UUID servicioId) {
        this.servicioId = servicioId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UUID> getImagenesIds() {
        return imagenesIds;
    }

    public void setImagenesIds(List<UUID> imagenesIds) {
        this.imagenesIds = imagenesIds;
    }

    public boolean isPublicada() {
        return publicada;
    }

    public void setPublicada(boolean publicada) {
        this.publicada = publicada;
    }
}

