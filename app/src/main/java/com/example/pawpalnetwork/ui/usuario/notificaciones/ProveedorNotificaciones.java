package com.example.pawpalnetwork.ui.usuario.notificaciones;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Contratacion;
import com.example.pawpalnetwork.bd.Notificacion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ProveedorNotificaciones extends Fragment {

    private ProveedorNotificacionesViewModel mViewModel;
    private RecyclerView recyclerView;
    private NotificacionAdapter adapter;
    private List<Notificacion> contratacionesList;
    private FirebaseFirestore db;
String userId;
    public static ProveedorNotificaciones newInstance() {
        return new ProveedorNotificaciones();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        // Inicializa Firestore y RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewNotificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializa la lista y el adaptador
        contratacionesList = new ArrayList<>();
        adapter = new NotificacionAdapter(getContext(), contratacionesList);
        recyclerView.setAdapter(adapter);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Obtén el UID del usuario actual
            // Usa el UID según sea necesario
        }

        escucharCambiosEnNotificacionesCliente(false);
        return view;

    }



    private void escucharCambiosEnNotificacionesCliente(boolean esProveedor) {
        Log.i("Firestore", "Notificaciones para cliente: " + userId);
        db.collection("notificaciones")
                .whereEqualTo(esProveedor ? "idProveedor" : "idUsuario", userId)
                .whereIn("tipoEvento", Arrays.asList("CONTRATACION", "ACEPTACION", "CANCELACION","ESPERANDO","EN PROCESO","CONFIRMACIÓN","FINALIZADO","REVIEW"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notificacion> notificacionesList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notificacion notificacion = document.toObject(Notificacion.class);
                            String mensaje;
                            switch (notificacion.getTipoEvento()) {
                                case "CONTRATACION":
                                    mensaje = esProveedor
                                            ? "Te han contratado para el servicio: " + notificacion.getMensaje()
                                            : "Has contratado un servicio.";
                                    break;
                                case "ACEPTACION":
                                    mensaje = esProveedor
                                            ? "Has aceptado una contratación."
                                            : "Tu contratación ha sido aceptada.";
                                    break;
                                case "CANCELACION":
                                    mensaje = esProveedor
                                            ? "Servicio Cancelado."
                                            : "Servicio Cancelado.";
                                    break;

                                case "EN PROCESO":
                                    mensaje = esProveedor
                                            ? "SERVICIO EN PROCESO"
                                            : notificacion.getMensaje();
                                    break;
                                case "CONFIRMACIÓN":
                                    mensaje = esProveedor
                                            ? notificacion.getMensaje()
                                            : "Haz confirmado el inicio del servicio";
                                    break;
                                case "ESPERANDO":
                                    mensaje = esProveedor
                                            ? "Esperando Confirmacion por parte del Cliente."
                                            : notificacion.getMensaje();
                                    break;
                                case "FINALIZADO":
                                    mensaje = esProveedor
                                            ? "SE ha finaluiz="
                                            : notificacion.getMensaje();
                                    break;
                                default:
                                    mensaje = "Notificación de evento desconocido.";
                                    break;
                            }

// Establece el mensaje en la notificación
                            notificacion.setMensaje(mensaje);


                            notificacionesList.add(notificacion);
                        }

                        // Ordena manualmente por fecha en orden descendente
                        Collections.sort(notificacionesList, (n1, n2) -> n2.getFecha().compareTo(n1.getFecha()));

                        // Actualiza el adaptador con la lista ordenada
                        adapter.setNotificacionesList(notificacionesList);
                    } else {
                        Log.w("Firestore", "Error obteniendo documentos.", task.getException());
                    }
                });

    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProveedorNotificacionesViewModel.class);
        // TODO: Use the ViewModel
    }

}