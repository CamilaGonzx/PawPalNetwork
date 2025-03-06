package com.example.pawpalnetwork.ui.usuario.procesoServicio;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Contratacion;
import com.example.pawpalnetwork.bd.Notificacion;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ProcesoServicio extends Fragment {

    private ProcesoServicioViewModel mViewModel;
    private ProgressBar progressBar;
    private Button btnFinalizar;
    private Handler handler = new Handler();
    private int progresoActual = 0;
    private FirebaseFirestore db;
    private Contratacion contratacion ;
    private String fecha;// Reemplaza con el ID rea
    public static ProcesoServicio newInstance() {
        return new ProcesoServicio();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_proceso_servicio, container, false);
        // Inicializar vistas
        progressBar = view.findViewById(R.id.progreso);
        btnFinalizar = view.findViewById(R.id.btnFinalizar);
        btnFinalizar.setVisibility(View.GONE);
        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {

            fecha = getArguments().getString("fechaNoti");

        }
        calcularTiempoRestante(fecha);

        return view;
    }

    private void calcularTiempoRestante(String fecha) {
        // Formato de la fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Parsear la fecha recibida
        LocalDateTime fechaObjetivo = LocalDateTime.parse(fecha, formatter);

        // Añadir un minuto a la fecha objetivo
        LocalDateTime fechaLimite = fechaObjetivo.plusMinutes(1);

        // Obtener la hora actual
        LocalDateTime ahora = LocalDateTime.now();

        // Calcular diferencia en segundos
        long segundosRestantes = ChronoUnit.SECONDS.between(ahora, fechaLimite);

        if (segundosRestantes > 0) {
            iniciarCuentaRegresiva(segundosRestantes);
        } else {
            Toast.makeText(getContext(), "El minuto ya ha pasado.", Toast.LENGTH_SHORT).show();
        }
    }



    private void iniciarCuentaRegresiva(long segundosTotales) {
        final Handler handler = new Handler();
        final long[] segundosRestantes = {segundosTotales};

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (segundosRestantes[0] > 0) {
                    Log.d("CuentaRegresiva", "Tiempo restante: " + segundosRestantes[0] + " segundos.");
                    segundosRestantes[0]--;
                    handler.postDelayed(this, 1000); // Repite cada segundo
                } else {
                    Log.d("CuentaRegresiva", "El minuto ha concluido.");
                    Toast.makeText(getContext(), "¡El minuto ha terminado!", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000);
    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProcesoServicioViewModel.class);
        // TODO: Use the ViewModel
    }

}