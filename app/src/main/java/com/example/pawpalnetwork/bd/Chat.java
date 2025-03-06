package com.example.pawpalnetwork.bd;

import java.util.UUID;
import java.util.List;

public class Chat {
    private UUID id;
    private UUID usuario1Id; // ID del primer usuario
    private UUID usuario2Id; // ID del segundo usuario

    // Relaciones
    private List<UUID> mensajesIds; // Lista de IDs de los mensajes en el chat

    // Sugerencias adicionales
    private String ultimoMensaje; // Contenido del último mensaje (opcional)
    private long ultimaActividad; // Timestamp de la última actividad en el chat

    public Chat() {
    }

    public Chat(UUID id, UUID usuario1Id, UUID usuario2Id, List<UUID> mensajesIds, String ultimoMensaje, long ultimaActividad) {
        this.id = id;
        this.usuario1Id = usuario1Id;
        this.usuario2Id = usuario2Id;
        this.mensajesIds = mensajesIds;
        this.ultimoMensaje = ultimoMensaje;
        this.ultimaActividad = ultimaActividad;
    }

    public long getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(long ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public List<UUID> getMensajesIds() {
        return mensajesIds;
    }

    public void setMensajesIds(List<UUID> mensajesIds) {
        this.mensajesIds = mensajesIds;
    }

    public UUID getUsuario2Id() {
        return usuario2Id;
    }

    public void setUsuario2Id(UUID usuario2Id) {
        this.usuario2Id = usuario2Id;
    }

    public UUID getUsuario1Id() {
        return usuario1Id;
    }

    public void setUsuario1Id(UUID usuario1Id) {
        this.usuario1Id = usuario1Id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
