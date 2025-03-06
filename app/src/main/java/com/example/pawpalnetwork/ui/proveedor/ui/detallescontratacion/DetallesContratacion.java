package com.example.pawpalnetwork.ui.proveedor.ui.detallescontratacion;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pawpalnetwork.MainActivity;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Contratacion;
import com.example.pawpalnetwork.bd.DisponibilidadDia;
import com.example.pawpalnetwork.bd.Horario;
import com.example.pawpalnetwork.bd.Notificacion;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.example.pawpalnetwork.ui.proveedor.ui.procesoServicio.ProcesoServicio;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetallesContratacion extends Fragment {

    private DetallesContratacionViewModel mViewModel;
    private FirebaseFirestore db;
    private String contratacionId;
    private TextView tituloServicio, estadoServicio, fechaContratacion, puntoEncuentro,tvNombre,tvTelefono,tvTitulo;
    private TextView fechaInicioTextView;
    private TextView fechaFinTextView;
    private TextView horasDuracionTextView;
    private Button btnAceptar,btnCancelar,btnIniciar;
    private TextView precioTotalTextView;
    Contratacion contratacion;
    Servicio servicio;
    UsuarioGeneral usuario;
    String userId,fechaNoti;
    public static DetallesContratacion newInstance() {
        return new DetallesContratacion();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalles_contratacion, container, false);

        // Inicializa Firestore y las vistas
        db = FirebaseFirestore.getInstance();
        tituloServicio = view.findViewById(R.id.tituloServicio);
        estadoServicio = view.findViewById(R.id.estadoServicio);
        fechaContratacion = view.findViewById(R.id.fechaContratacion);
        puntoEncuentro = view.findViewById(R.id.puntoEncuentro);
        tvNombre = view.findViewById(R.id.nombreCliente);
        tvTelefono = view.findViewById(R.id.contactoCliente);
        fechaInicioTextView = view.findViewById(R.id.fechaInicio);
        fechaFinTextView =  view.findViewById(R.id.fechaFin);
        horasDuracionTextView =  view.findViewById(R.id.horasDuracion);
        precioTotalTextView =  view.findViewById(R.id.precioTotal);
        tvTitulo=view.findViewById(R.id.tvTituloDetalles);
        btnAceptar= view.findViewById(R.id.btnAceptar);
        btnCancelar = view.findViewById(R.id.btnCancelar);
        btnIniciar= view.findViewById(R.id.btnIniciar);
        tvTitulo.setText("Detalles del Cliente");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Obtén el UID del usuario actual
        }
        // Obtén el ID de la contratación desde los argumentos
        if (getArguments() != null) {
            contratacionId = getArguments().getString("contratacionId");

            cargarDetallesContratacion();
        }

        btnAceptar.setOnClickListener(v -> {
            aceptarServicio();
        });

        btnCancelar.setOnClickListener(v -> {
            mostrarDialogoConfirmacion();
        });

        btnIniciar.setOnClickListener(v -> intentarIniciarServicio());




        return view;
    }


    private void intentarIniciarServicio() {
        if (contratacion.getStatus().equals("Aceptado")) {
            // Primera vez intentando iniciar
            db.collection("contrataciones").document(contratacionId)
                    .update("status", "Esperando")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Esperando confirmación del cliente.", Toast.LENGTH_SHORT).show();
                        notificarCliente(
                                "El proveedor está listo para iniciar el servicio. Por favor confirma.",
                                "ESPERANDO",
                                "ESPERANDO"
                        );
                        btnAceptar.setVisibility(View.GONE);
                        btnCancelar.setVisibility(View.GONE);
                        btnIniciar.setVisibility(View.GONE);
                        estadoServicio.setText(contratacion.getStatus());
                        int estadoColor = getEstadoColor(contratacion.getStatus());
                        estadoServicio.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), estadoColor));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al iniciar el servicio.", Toast.LENGTH_SHORT).show();
                    });
        } else if (contratacion.getStatus().equals("Confirmado")) {
            // Cliente ya confirmó, iniciar definitivamente
            iniciarServicio();
        } else {
            Toast.makeText(getContext(), "No puedes iniciar el servicio aún.", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciarServicio() {
        db.collection("contrataciones").document(contratacionId)
                .update("status", "En Proceso")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "El servicio ha comenzado.", Toast.LENGTH_SHORT).show();
                    estadoServicio.setText("En Progreso");
                    btnIniciar.setVisibility(View.GONE);
                    notificarCliente(
                            "El proveedor ha iniciado el servicio",
                            "EN PROCESO",
                            "EN PROCESO"
                    );
                    Bundle args = new Bundle();
                    args.putSerializable("contratacion", contratacion);
                    args.putString("contratacionId", contratacionId);
                    NavController navController = Navigation.findNavController((AppCompatActivity) getContext(), R.id.nav_host_fragment_activity_proveedor);
                    navController.navigate(R.id.action_navigation_detalles_to_navigation_proceso, args);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al iniciar el servicio.", Toast.LENGTH_SHORT).show();
                });
    }


    private void notificarCliente(String mensaje, String estado, String tipoEvento) {
        String notificacionId = db.collection("notificaciones").document().getId();
        LocalDateTime fechaHoy = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaFormateada = fechaHoy.format(formatter);

        Notificacion notificacion = new Notificacion(
                notificacionId,
                mensaje,
                fechaFormateada,
                estado,
                contratacionId,
                contratacion.getIdServicio(),
                contratacion.getIdCliente(), // ID del cliente
                contratacion.getIdProveedor(),
                "PROVEEDOR",
                tipoEvento
        );

        db.collection("notificaciones").document(notificacionId)
                .set(notificacion)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notificación enviada al cliente: " + mensaje))
                .addOnFailureListener(e -> Log.w("Firestore", "Error al enviar notificación", e));
    }


    private void cargarDetallesServicio() {
        DocumentReference docRef = db.collection("servicios").document(contratacion.getIdServicio());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                     servicio = documentSnapshot.toObject(Servicio.class);

                    // Configura los datos en las vistas
                    tituloServicio.setText("Servicio: " + servicio.getTitulo());
                }
            }
        });
    }


    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmación de Cancelación")
                .setMessage("¿Estás seguro de que quieres cancelar este contrato?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    // Llama al método para cancelar el contrato si el usuario confirma
                    cancelarContrato(contratacionId);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // Cierra el diálogo sin hacer nada si el usuario cancela
                    dialog.dismiss();
                })
                .show();
    }

    private void cancelarContrato(String contratacionId) {
        db.collection("contrataciones").document(contratacionId)
                .update("status", "Cancelado")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Contrato cancelado exitosamente", Toast.LENGTH_SHORT).show();

                    // Opcional: Ocultar el botón de cancelar después de la actualización
                    btnCancelar.setVisibility(View.GONE);
                    estadoServicio.setText(contratacion.getStatus());
                    int estadoColor = getEstadoColor(contratacion.getStatus());
                    estadoServicio.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), estadoColor));

                    String notificacionId = db.collection("notificaciones").document().getId();
                    String mensaje = "El cliente ha cancelado el servicio que tenías programado.";
                    LocalDateTime fechaHoyh = LocalDateTime.now();

// Formateador para la fecha y la hora
                    DateTimeFormatter formatterh = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    String fechaHoyFormateadah = fechaHoyh.format(formatterh);


                    Notificacion notificacionCancelacion = new Notificacion(
                            notificacionId,
                            mensaje,
                            fechaHoyFormateadah,
                            "Cancelado",
                            contratacionId,
                            contratacion.getIdServicio(),
                            contratacion.getIdCliente(),
                            contratacion.getIdProveedor(),
                            "PROVEEDOR",
                            "CANCELACION"
                    );

                    db.collection("notificaciones").document(notificacionId)
                            .set(notificacionCancelacion)
                            .addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Notificación de cancelación creada"))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error al crear notificación de cancelación", e));


                        actualizarDisponibilidad(contratacion.getIdProveedor(),contratacion.getFechaInicio(),contratacion.getHora());

                        btnCancelar.setVisibility(View.GONE);
                        btnAceptar.setVisibility(View.GONE);


                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cancelar el contrato", Toast.LENGTH_SHORT).show();
                    Log.w("Firestore", "Error al cancelar el contrato", e);
                });
    }

    private void actualizarDisponibilidad(String idProveedor, String fecha, String hora) {
        db.collection("usuarios")
                .document(idProveedor)
                .collection("disponibilidad")
                .document(fecha)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        DisponibilidadDia disponibilidadDia = documentSnapshot.toObject(DisponibilidadDia.class);
                        if (disponibilidadDia != null && disponibilidadDia.getHorarios() != null) {
                            // Buscar el horario especificado y actualizar su estado
                            for (Horario horario : disponibilidadDia.getHorarios()) {
                                if (horario.getHora().equals(hora)) {
                                    horario.setReservado(false);
                                    break;
                                }
                            }

                            // Guardar la lista de horarios actualizada
                            db.collection("usuarios")
                                    .document(idProveedor)
                                    .collection("disponibilidad")
                                    .document(fecha)
                                    .set(disponibilidadDia)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Horario actualizado como no disponible", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar la disponibilidad", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(getContext(), "No se encontró disponibilidad para la fecha especificada", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al obtener la disponibilidad", Toast.LENGTH_SHORT).show());
    }


    private void cargarDetallesUsuario() {
        DocumentReference docRef = db.collection("usuarios").document(contratacion.getIdCliente());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    usuario = documentSnapshot.toObject(UsuarioGeneral.class);
                    tvNombre.setText("Nombre del Cliente: "+usuario.getNombre());
                    tvTelefono.setText("Telefono: "+ usuario.getTelefono());
                }
            }
        });
    }


    private void cargarDetallesContratacion() {
        DocumentReference docRef = db.collection("contrataciones").document(contratacionId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    contratacion = documentSnapshot.toObject(Contratacion.class);

                    if(contratacion.getStatus().equals("Cancelado"))
                    {
                        btnCancelar.setVisibility(View.GONE);
                        btnAceptar.setVisibility(View.GONE);
                    }else if (contratacion.getStatus().equals("Aceptado")) {
                        btnCancelar.setVisibility(View.GONE);
                        btnAceptar.setVisibility(View.GONE);
                        btnIniciar.setVisibility(View.VISIBLE);
                    }else if(contratacion.getStatus().equals("Esperando"))
                    {
                        btnAceptar.setVisibility(View.GONE);
                        btnCancelar.setVisibility(View.GONE);
                        btnIniciar.setVisibility(View.GONE);

                    }else if (contratacion.getStatus().equals("Confirmado"))
                    {
                        btnAceptar.setVisibility(View.GONE);
                        btnCancelar.setVisibility(View.GONE);
                        btnIniciar.setVisibility(View.VISIBLE); // Iniciar definitivament
                    }else if (contratacion.getStatus().equals("En Proceso"))
                    {
                        btnAceptar.setVisibility(View.GONE);
                        btnCancelar.setVisibility(View.GONE);
                        btnIniciar.setVisibility(View.GONE); // Iniciar definitivament
                    }else if (contratacion.getStatus().equals("FINALIZADO"))
                    {
                        btnAceptar.setVisibility(View.GONE);
                        btnCancelar.setVisibility(View.GONE);
                        btnIniciar.setVisibility(View.GONE); // Iniciar definitivament
                    }
                    // Configura los datos en las vistas
                    estadoServicio.setText(contratacion.getStatus());

                    int estadoColor = getEstadoColor(contratacion.getStatus());
                    estadoServicio.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), estadoColor));
                    fechaContratacion.setText("Fecha de Contratación: " + contratacion.getFechaContratacion());
                    puntoEncuentro.setText("Punto de Encuentro: " + contratacion.getPuntoEncuentro());
                    fechaInicioTextView.setText("Fecha de Inicio: "+contratacion.getFechaInicio() + " "+contratacion.getHora());
                    fechaFinTextView.setText("Fecha de Fin: "+contratacion.getFechaInicio() + " "+contratacion.getHora());
                    precioTotalTextView.setText("Precio Total: "+contratacion.getTotal());
                    cargarDetallesServicio();
                    cargarDetallesUsuario();
                }
            }
        });
    }




    private void aceptarServicio() {
        // Actualiza el estado de la contratación a "Aceptado"
        db.collection("contrataciones").document(contratacionId)
                .update("status", "Aceptado")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Servicio aceptado exitosamente", Toast.LENGTH_SHORT).show();

                    // Actualiza la vista del estado del servicio
                    estadoServicio.setText("Aceptado");
                    int estadoColor = getEstadoColor("Aceptado");
                    estadoServicio.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), estadoColor));

                    // Crea la notificación de aceptación para el cliente
                    String notificacionId = db.collection("notificaciones").document().getId();
                    String mensaje = "El proveedor ha aceptado tu contratación.";
                    LocalDateTime fechaHoy = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String fechaFormateada = fechaHoy.format(formatter);

                    Notificacion notificacionAceptacion = new Notificacion(
                            notificacionId,
                            mensaje,
                            fechaFormateada,
                            "Aceptado",
                            contratacionId,
                            contratacion.getIdServicio(),
                            contratacion.getIdCliente(), // ID del cliente
                            contratacion.getIdProveedor(),
                            "CLIENTE",
                            "ACEPTACION"
                    );

                    // Guarda la notificación en Firestore
                    db.collection("notificaciones").document(notificacionId)
                            .set(notificacionAceptacion)
                            .addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Notificación de aceptación creada"))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error al crear notificación de aceptación", e));

                        btnCancelar.setVisibility(View.GONE);
                        btnAceptar.setVisibility(View.GONE);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al aceptar el servicio", Toast.LENGTH_SHORT).show();
                    Log.w("Firestore", "Error al actualizar el estado de la contratación", e);
                });
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetallesContratacionViewModel.class);
        // TODO: Use the ViewModel
    }

    private int getEstadoColor(String estado) {
        switch (estado) {
            case "Aceptado":
                return R.color.acceptedColor;
            case "Cancelado":
                return R.color.cancelledColor;
            case "EsperandoConfirmacionCliente":
                    return R.color.pendingColor;
            case "ClienteListo":
                return R.color.acceptedColor;

            default:
                return R.color.defaultStatusColor;
        }
    }

}