package com.example.pawpalnetwork.bd;

public class Notificacion {
    private String idNotificacion;
    private String mensaje;
    private String fecha;
    private String estado;
    private String idContratacion;
    private String idServicio;
    private String idUsuario;
    private String idProveedor;
    private String tipoUsuario; // "PROVEEDOR" o "CLIENTE"
    private String tipoEvento; // "CONTRATACION", "CANCELACION", "ACEPTACION"


    public Notificacion() {
    }

    public Notificacion(String idNotificacion, String mensaje, String fecha, String estado, String idContratacion, String idServicio, String idUsuario, String idProveedor, String tipoUsuario, String tipoEvento) {
        this.idNotificacion = idNotificacion;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.estado = estado;
        this.idContratacion = idContratacion;
        this.idServicio = idServicio;
        this.idUsuario = idUsuario;
        this.idProveedor = idProveedor;
        this.tipoUsuario = tipoUsuario;
        this.tipoEvento = tipoEvento;
    }



    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    // Getters y Setters
    public String getIdNotificacion() {
        return idNotificacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public String getIdContratacion() {
        return idContratacion;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setIdNotificacion(String idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setIdContratacion(String idContratacion) {
        this.idContratacion = idContratacion;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
}