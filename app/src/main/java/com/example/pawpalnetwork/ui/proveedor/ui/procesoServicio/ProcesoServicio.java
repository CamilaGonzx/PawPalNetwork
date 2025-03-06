package com.example.pawpalnetwork.ui.proveedor.ui.procesoServicio;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Contratacion;
import com.example.pawpalnetwork.bd.Notificacion;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProcesoServicio extends Fragment {

    private ProcesoServicioViewModel mViewModel;
    private ProgressBar progressBar;
    private Button btnFinalizar;
    private Handler handler = new Handler();
    private int progresoActual = 0;
    private FirebaseFirestore db;
    private Contratacion contratacion ;
    private String contratacionId;// Reemplaza con el ID rea
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

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            contratacion = (Contratacion) getArguments().getSerializable("contratacion");
            contratacionId = getArguments().getString("contratacionId");

        }
        // Configurar botón de finalizar
        btnFinalizar.setOnClickListener(v -> finalizarServicio());

        // Iniciar progreso automático
        iniciarProgreso();


        return view;
    }

    private void iniciarProgreso() {
        // Simular progreso: 1 minuto (60 segundos)
        final int tiempoTotal = 60; // En segundos
        final int incremento = 100 / tiempoTotal; // Incremento por segundo

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progresoActual < 100) {
                    progresoActual += incremento;
                    progressBar.setProgress(progresoActual);
                    handler.postDelayed(this, 1000); // Incrementa cada segundo
                } else {
                    // Cuando el progreso llegue al 100%, habilitar el botón
                    btnFinalizar.setEnabled(true);
                }
            }
        }, 1000);
    }

    private void finalizarServicio() {
        // Cambiar estado a Finalizado
        db.collection("contrataciones").document(contratacionId)
                .update("status", "FINALIZADO")
                .addOnSuccessListener(aVoid -> {
                    // Crear notificación
                    crearNotificacionFinalizacion();

                    // Mostrar mensaje al usuario
                    Toast.makeText(getContext(), "Servicio finalizado exitosamente", Toast.LENGTH_SHORT).show();

                    // Volver a la pantalla anterior o cerrar el fragmento
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al finalizar el servicio", Toast.LENGTH_SHORT).show();
                });
    }

    private void crearNotificacionFinalizacion() {
        // Crear ID único para la notificación
        String notificacionId = db.collection("notificaciones").document().getId();

        // Detalles de la notificación
        String mensaje = "El servicio ha sido finalizado exitosamente.";
        LocalDateTime fechaHoy = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaFormateada = fechaHoy.format(formatter);

        // Construir objeto de notificación
        Notificacion notificacion = new Notificacion(
                notificacionId,
                mensaje,
                fechaFormateada,
                "Finalizado",
                contratacionId,
                contratacion.getIdServicio(), // Servicio ID (opcional, si tienes el dato)
                contratacion.getIdCliente(), // Cliente ID (opcional, si tienes el dato)
                contratacion.getIdProveedor(), // Proveedor ID (opcional, si tienes el dato)
                "PROVEEDOR",
                "FINALIZADO"
        );

        // Guardar notificación en Firestore
        db.collection("notificaciones").document(notificacionId)
                .set(notificacion)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notificación creada exitosamente"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error al crear la notificación", e));
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProcesoServicioViewModel.class);
        // TODO: Use the ViewModel
    }

}