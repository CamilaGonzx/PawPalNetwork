package com.example.pawpalnetwork.bd;

import java.util.UUID;

public class ServicioMascota {
    private UUID id;
    private UUID mascotaId; // ID de la mascota
    private UUID servicioId; // ID del servicio
    private long fecha; // Timestamp de la fecha en que se solicita el servicio
    private long hora; // Timestamp para la hora específica de solicitud
    private String estado; // Estado del servicio (pendiente, completado, cancelado, etc.)

    // Sugerencias adicionales
    private boolean rechazado; // Estado de si fue rechazado
    private String comentarios; // Comentarios sobre el servicio

    public ServicioMascota() {
    }

    public ServicioMascota(UUID id, UUID mascotaId, UUID servicioId, long fecha, long hora, String estado, boolean rechazado, String comentarios) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.servicioId = servicioId;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.rechazado = rechazado;
        this.comentarios = comentarios;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(UUID mascotaId) {
        this.mascotaId = mascotaId;
    }

    public UUID getServicioId() {
        return servicioId;
    }

    public void setServicioId(UUID servicioId) {
        this.servicioId = servicioId;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isRechazado() {
        return rechazado;
    }

    public void setRechazado(boolean rechazado) {
        this.rechazado = rechazado;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
}
