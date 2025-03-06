package com.example.pawpalnetwork.bd;

import java.io.Serializable;

public class Contratacion implements Serializable {
    private String idServicio;
    private String idCliente;
    private String idProveedor;
    private String fechaContratacion;    // Timestamp de la fecha de contratación
    private String fechaInicio;        // Fecha seleccionada para el inicio del servicio
    private String hora;
    private String puntoEncuentro;     // Dirección especificada
    private int horas;                 // Duración del servicio en horas
    private double total;              // Precio total calculado
    private String status;             // Estado del servicio

    //Pendiente
    //Aceptado
    //Cancelado
    //Iniciado P
    //Terminado
    //Caducado`
    public Contratacion(String idServicio, String idCliente, String idProveedor, String fechaContratacion,
                        String fechaInicio, String hora, String puntoEncuentro, int horas, double total, String status) {
        this.idServicio = idServicio;
        this.idCliente = idCliente;
        this.idProveedor = idProveedor;
        this.fechaContratacion = fechaContratacion;
        this.fechaInicio = fechaInicio;
        this.hora = hora;
        this.puntoEncuentro = puntoEncuentro;
        this.horas = horas;
        this.total = total;
        this.status = status;
    }

    public Contratacion() {
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(String fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getPuntoEncuentro() {
        return puntoEncuentro;
    }

    public void setPuntoEncuentro(String puntoEncuentro) {
        this.puntoEncuentro = puntoEncuentro;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
//Cuantas horas
//Que dia
//Punto de encuentro
//Total
//Fecha de Contratacion
//Status cancelado/pendiente/confirmado
