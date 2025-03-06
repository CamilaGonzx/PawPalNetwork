package com.example.pawpalnetwork.bd;

import java.util.UUID;
import java.util.List;

public class Mascota {
    private UUID id;
    private String nombre;
    private int edad;
    private String tipo;
    private String raza;
    private UUID duenoId; // ID del dueño (Usuario)
    private String foto; // URL a Firebase Storage
    private String status; // Estado de la mascota (activo, en servicio, etc.)

    // Relaciones
    private List<UUID> serviciosIds; // Lista de IDs de servicios relacionados

    public Mascota() {
    }

    public Mascota(UUID id, String nombre, int edad, String tipo, String raza, UUID duenoId, String foto, String status, List<UUID> serviciosIds) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.tipo = tipo;
        this.raza = raza;
        this.duenoId = duenoId;
        this.foto = foto;
        this.status = status;
        this.serviciosIds = serviciosIds;
    }

    public List<UUID> getServiciosIds() {
        return serviciosIds;
    }

    public void setServiciosIds(List<UUID> serviciosIds) {
        this.serviciosIds = serviciosIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public UUID getDuenoId() {
        return duenoId;
    }

    public void setDuenoId(UUID duenoId) {
        this.duenoId = duenoId;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
