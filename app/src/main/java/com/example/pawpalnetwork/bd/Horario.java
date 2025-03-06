package com.example.pawpalnetwork.bd;

public class Horario {
    private String hora;       // Ejemplo: "08:00"
    private boolean reservado; // Indica si está reservado o no

    public Horario() {
        // Constructor vacío para Firebase
    }

    public Horario(String hora, boolean reservado) {
        this.hora = hora;
        this.reservado = reservado;
    }

    // Getters y Setters
    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public boolean isReservado() {
        return reservado;
    }

    public void setReservado(boolean reservado) {
        this.reservado = reservado;
    }
}

