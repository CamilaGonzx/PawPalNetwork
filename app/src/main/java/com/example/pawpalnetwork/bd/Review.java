package com.example.pawpalnetwork.bd;

import java.io.Serializable;

public class Review implements Serializable {
    private String id;
    private String idUsuario;
    private String nombreUsuario;
    private String idProveedor;
    private String comentario;
    private float calificacion;

    public Review() {
    }

    public Review(String id, String idUsuario, String nombreUsuario, String idProveedor, String comentario, float calificacion) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idProveedor = idProveedor;
        this.comentario = comentario;
        this.calificacion = calificacion;
    }

    public Review(String nombreUsuario,String comentario, float calificacion) {
        this.comentario = comentario;
        this.calificacion = calificacion;
        this.nombreUsuario = nombreUsuario;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }
}
