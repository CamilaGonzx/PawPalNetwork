package com.example.pawpalnetwork.bd;

import java.util.List;

public class DisponibilidadDia {
    private String fecha;               // Fecha en formato "dd/MM/yyyy"
    private List<Horario> horarios;     // Lista de horarios disponibles para este día

    public DisponibilidadDia() {
        // Constructor vacío para Firebase
    }

    public DisponibilidadDia(String fecha, List<Horario> horarios) {
        this.fecha = fecha;
        this.horarios = horarios;
    }

    // Getters y Setters
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }
}

