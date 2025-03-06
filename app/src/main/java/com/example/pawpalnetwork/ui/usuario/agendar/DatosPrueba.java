package com.example.pawpalnetwork.ui.usuario.agendar;

import com.example.pawpalnetwork.bd.DisponibilidadDia;
import com.example.pawpalnetwork.bd.Horario;
import com.google.firebase.firestore.FirebaseFirestore;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatosPrueba {

    private FirebaseFirestore db;

    public DatosPrueba(FirebaseFirestore db) {
        this.db = db;
    }

    // Método para crear horarios de prueba en intervalos de 30 minutos
    public List<Horario> generarHorarios(String horarioInicio, String horarioFin) {
        List<Horario> horarios = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            Date inicio = sdf.parse(horarioInicio);
            Date fin = sdf.parse(horarioFin);
            Calendar cal = Calendar.getInstance();
            cal.setTime(inicio);

            while (cal.getTime().before(fin)) {
                String hora = sdf.format(cal.getTime());
                horarios.add(new Horario(hora, false)); // Agregamos cada horario con reservado en false
                cal.add(Calendar.MINUTE, 60); // Incrementa por 30 minutos
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return horarios;
    }

    // Método para crear disponibilidad para los próximos 7 días y cargarla en Firebase solo si no existe
    public void cargarDatosDePrueba(String providerId) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String fecha = dateFormat.format(calendar.getTime());

            // Comprueba si ya existen horarios para la fecha actual
            db.collection("usuarios")
                    .document(providerId)
                    .collection("disponibilidad")
                    .document(fecha)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (!documentSnapshot.exists()) {
                            // Genera los horarios solo si no existen
                            List<Horario> horarios = generarHorarios("08:00", "18:00"); // Horario de 08:00 a 18:00
                            DisponibilidadDia disponibilidadDia = new DisponibilidadDia(fecha, horarios);

                            // Guarda la disponibilidad en Firebase
                            db.collection("usuarios")
                                    .document(providerId)
                                    .collection("disponibilidad")
                                    .document(fecha)
                                    .set(disponibilidadDia)
                                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Horarios registrados correctamente para la fecha: " + fecha))
                                    .addOnFailureListener(e -> Log.w("Firebase", "Error al registrar horarios para la fecha: " + fecha, e));
                        } else {
                            Log.d("Firebase", "Horarios ya existen para la fecha: " + fecha);
                        }
                    })
                    .addOnFailureListener(e -> Log.w("Firebase", "Error al verificar horarios para la fecha: " + fecha, e));

            // Avanza al día siguiente
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }
}
