package com.example.pawpalnetwork.ui.admin.ui.estadisticas;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pawpalnetwork.LoginActivity;
import com.example.pawpalnetwork.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class EstadisticasFragment extends Fragment {

    public static EstadisticasFragment newInstance() {
        return new EstadisticasFragment();
    }
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_estadisticas, container, false);

        Button btnCerrarSesion = root.findViewById(R.id.btnCerrarSesion);
        Button btnCreditos = root.findViewById(R.id.btnCreditos);

        btnCerrarSesion.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            // Redirige a la pantalla de inicio o login
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            // Cierra la actividad actual
        });

        btnCreditos.setOnClickListener(view -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Acerca de ")
                    .setMessage("" +
                            "Unidad Academica Profesional Tianguistenco \n" +
                            "Ingenieria en Software \n" +
                            "Programacion Movil \n" +
                            "Profresora Elizabeth Pulido \n" +
                            "Camila Ximena Gonzalez Alvarez \n" +
                            "Aylen Noemi Pichardo Uriostegui \n" +
                            "Crhistian Esquivel de Jesus \n" +
                            "Version 1.0 2024 ")
                    .setPositiveButton("Aceptar", null).show();
        });



        return root;
    }
}