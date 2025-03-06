package com.example.pawpalnetwork.bd;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class RegistroHorarios {

    public void registrarHorarios(String providerId, DisponibilidadDia disponibilidadDia) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("proveedores")
                .document(providerId)
                .collection("disponibilidad")
                .document(disponibilidadDia.getFecha())
                .set(disponibilidadDia)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Horarios registrados correctamente"))
                .addOnFailureListener(e -> Log.w("Firebase", "Error al registrar horarios", e));
    }
}
