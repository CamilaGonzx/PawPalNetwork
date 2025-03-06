package com.example.pawpalnetwork.ui.proveedor.ui.agenda;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Dia {
    private int dayNumber;
    private String dayName;
    private boolean hasContract;

    public Dia(int dayNumber, String dayName, boolean hasContract) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.hasContract = hasContract;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public String getDayName() {
        return dayName;
    }

    public boolean hasContract() {
        return hasContract;
    }
}
