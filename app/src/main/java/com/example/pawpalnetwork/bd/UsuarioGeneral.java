package com.example.pawpalnetwork.bd;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class UsuarioGeneral implements Serializable {

    private String id;
    private String nombre;
    private String email;
    private String contrasena;
    private String telefono;
    private String ubicacion;
    private float rating;
    private String descripcion;
    private String fotoPerfil; // URL a Firebase Storage
    private String status; // Estado del usuario (activo, inactivo, suspendido, etc.)
    private boolean rol; // "usuario" o "proveedor"

    // Relaciones para Usuarios
    private List<String> mascotasIds; // Lista de IDs de las mascotas (para usuarios)

    // Relaciones para Proveedores
    private List<String> serviciosIds; // Lista de IDs de servicios que ofrece (para proveedores)
    private List<String> publicacionesIds; // Lista de IDs de publicaciones realizadas (para proveedores)

    // Relaciones Compartidas
    private List<String> chatsIds; // Lista de IDs de los chats (compartido)

    // Horario y Disponibilidad (Solo para Proveedores)
    private String horarioInicio; // Hora de inicio general (formato HH:mm)
    private String horarioFin; // Hora de fin general (formato HH:mm)
    private List<String> diasDisponibles; // Días disponibles (ej. ["Lunes", "Martes", "Miércoles"])

    // Constructor


    public UsuarioGeneral() {
    }

    public UsuarioGeneral(String id, String nombre, String email, String contrasena, String telefono, String ubicacion, float rating, String descripcion, String fotoPerfil, String status, boolean rol, List<String> mascotasIds, List<String> serviciosIds, List<String> publicacionesIds, List<String> chatsIds, String horarioInicio, String horarioFin, List<String> diasDisponibles) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.ubicacion = ubicacion;
        this.rating = rating;
        this.descripcion = descripcion;
        this.fotoPerfil = fotoPerfil;
        this.status = status;
        this.rol = rol;
        this.mascotasIds = mascotasIds;
        this.serviciosIds = serviciosIds;
        this.publicacionesIds = publicacionesIds;
        this.chatsIds = chatsIds;
        this.horarioInicio = horarioInicio;
        this.horarioFin = horarioFin;
        this.diasDisponibles = diasDisponibles;
    }

    public UsuarioGeneral(String id, String nombre, String email, String contrasena, String telefono, String ubicacion, boolean rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.telefono = telefono;
        this.ubicacion = ubicacion;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRol() {
        return rol;
    }

    public void setRol(boolean rol) {
        this.rol = rol;
    }

    public List<String> getMascotasIds() {
        return mascotasIds;
    }

    public void setMascotasIds(List<String> mascotasIds) {
        this.mascotasIds = mascotasIds;
    }

    public List<String> getServiciosIds() {
        return serviciosIds;
    }

    public void setServiciosIds(List<String> serviciosIds) {
        this.serviciosIds = serviciosIds;
    }

    public List<String> getPublicacionesIds() {
        return publicacionesIds;
    }

    public void setPublicacionesIds(List<String> publicacionesIds) {
        this.publicacionesIds = publicacionesIds;
    }

    public List<String> getChatsIds() {
        return chatsIds;
    }

    public void setChatsIds(List<String> chatsIds) {
        this.chatsIds = chatsIds;
    }

    public String getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(String horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public String getHorarioFin() {
        return horarioFin;
    }

    public void setHorarioFin(String horarioFin) {
        this.horarioFin = horarioFin;
    }

    public List<String> getDiasDisponibles() {
        return diasDisponibles;
    }

    public void setDiasDisponibles(List<String> diasDisponibles) {
        this.diasDisponibles = diasDisponibles;
    }
}
