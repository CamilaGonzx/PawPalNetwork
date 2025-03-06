package com.example.pawpalnetwork.bd;

import java.io.Serializable;
import java.util.List;

public class Servicio implements Serializable {
    private String id;
    private String tipo;
    private String titulo;
    private String descripcion;
    private double precio;
    private String proveedorId; // ID del proveedor
    private String ubicacion;
    private boolean status;
    private List<String> imagenesRutas; // Lista de IDs de imágenes asociadas

    public Servicio() {
    }

    public Servicio(String id, String tipo, String titulo, String descripcion, double precio, String proveedorId, String ubicacion, boolean status, List<String> imagenesRutas) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.proveedorId = proveedorId;
        this.ubicacion = ubicacion;
        this.status = status;
        this.imagenesRutas = imagenesRutas;
    }

    @Override
    public String toString() {
        return "Servicio{" +
                "id='" + id + '\'' +
                ", tipo='" + tipo + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", proveedorId='" + proveedorId + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", status=" + status +
                ", imagenesRutas=" + imagenesRutas +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(String proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<String> getImagenesRutas() {
        return imagenesRutas;
    }

    public void setImagenesRutas(List<String> imagenesRutas) {
        this.imagenesRutas = imagenesRutas;
    }
}
