package com.example.pawpalnetwork.ui.usuario.agendar;

public class Dia {
    private int dayNumber;
    private String dayName;
    private int status;

    public Dia(int dayNumber, String dayName, int status) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.status = status;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public String getDayName() {
        return dayName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}


