package com.example.pawpalnetwork.ui.usuario.notificaciones;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Contratacion;
import com.example.pawpalnetwork.bd.Notificacion;
import com.example.pawpalnetwork.bd.Review;
import com.example.pawpalnetwork.ui.usuario.detallescontratacion.DetallesContratacion;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {

    private final Context context;
    private  List<Notificacion> notificacionesList;

    public NotificacionAdapter(Context context, List<Notificacion> notificacionesList) {
        this.context = context;
        this.notificacionesList = notificacionesList;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notificacion, parent, false);
        return new NotificacionViewHolder(view);
    }

    public void setNotificacionesList(List<Notificacion> notificacionesList) {
        this.notificacionesList = notificacionesList;
        notifyDataSetChanged(); // Actualiza el adaptador para reflejar los cambios
    }
    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        Notificacion notificacion = notificacionesList.get(position);

        holder.tituloServicioNotificacion.setText(notificacion.getMensaje());
        holder.fechaContratacionNotificacion.setText("Fecha: " + notificacion.getFecha());
        holder.estadoNotificacion.setText(notificacion.getEstado());
        holder.descripcionNotificacion.setText("");
        int estadoColor = getEstadoColor(notificacion.getEstado());
        holder.estadoNotificacion.setBackgroundTintList(ContextCompat.getColorStateList(context, estadoColor));

        holder.itemView.setOnClickListener(v -> {
            if(notificacion.getEstado().equals("Finalizado"))
            {
mostrarDialogoReview(notificacion);
            }else {
                DetallesContratacion detallesFragment = new DetallesContratacion();
                Bundle args = new Bundle();
                args.putString("contratacionId", notificacion.getIdContratacion());
                args.putString("notificacion-Fecha", notificacion.getFecha());
                detallesFragment.setArguments(args);

                NavController navController = Navigation.findNavController((AppCompatActivity) context, R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_notifications_to_navigation_detalles, args);
            }
        });
    }
    private void mostrarDialogoReview(Notificacion noti) {
        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_review, null);
        builder.setView(dialogView);

        // Inicializar vistas del diálogo
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText etReviewComment = dialogView.findViewById(R.id.etReviewComment);
        Button btnSubmitReview = dialogView.findViewById(R.id.btnSubmitReview);
        Button btnCancelReview = dialogView.findViewById(R.id.btnCancelReview);

        // Crear el diálogo y mostrarlo
        AlertDialog dialog = builder.create();
        dialog.show();

        // Configurar botón de enviar
        btnSubmitReview.setOnClickListener(v -> {
            float estrellas = ratingBar.getRating();
            String comentario = etReviewComment.getText().toString().trim();

            if (estrellas > 0) {
                Review review = new Review();
                review.setId(FirebaseFirestore.getInstance().collection("reviews").document().getId());
                review.setIdUsuario(noti.getIdUsuario());
              //  review.setNombreUsuario(nombreUsuario);
                review.setIdProveedor(noti.getIdProveedor());
                review.setComentario(comentario);
                review.setCalificacion(estrellas);

                // Guardar la review
                guardarReview(review);
                notificarProveedor(noti);
                actualizarPromedioCalificaciones(noti.getIdProveedor());
                // Cerrar el diálogo
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Por favor selecciona una calificación", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar botón de cancelar
        btnCancelReview.setOnClickListener(v -> dialog.dismiss());
    }

    private void guardarReview(Review review) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("reviews").document(review.getId())
                .set(review)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "¡Gracias por tu calificación!", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al enviar la calificación", Toast.LENGTH_SHORT).show();
                });
    }

    private void actualizarPromedioCalificaciones(String idProveedor) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("reviews")
                .whereEqualTo("idProveedor", idProveedor)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("Firestore", "Error escuchando cambios en reviews", e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        double sumaCalificaciones = 0;
                        int totalReviews = snapshots.size();

                        // Sumar todas las calificaciones
                        for (DocumentSnapshot doc : snapshots) {
                            Double calificacion = doc.getDouble("calificacion");
                            if (calificacion != null) {
                                sumaCalificaciones += calificacion;
                            }
                        }

                        // Calcular el promedio
                        double promedio = sumaCalificaciones / totalReviews;

                        // Actualizar el promedio en el documento del usuario
                        db.collection("usuarios").document(idProveedor)
                                .update("rating", promedio)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Promedio actualizado: " + promedio))
                                .addOnFailureListener(error -> Log.e("Firestore", "Error actualizando el promedio", error));
                    } else {
                        // Si no hay reviews, el promedio es 0
                        db.collection("usuarios").document(idProveedor)
                                .update("calificacionPromedio", 0)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Promedio actualizado a 0"))
                                .addOnFailureListener(error -> Log.e("Firestore", "Error actualizando el promedio", error));
                    }
                });
    }



    private void notificarProveedor(Notificacion noti) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String notificacionId = db.collection("notificaciones").document().getId();
        LocalDateTime fechaHoy = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaFormateada = fechaHoy.format(formatter);

        Notificacion notificacion = new Notificacion(
                notificacionId,
                "Haz recibido una calificación",
                fechaFormateada,
                "REVIEW",
                "",
                noti.getIdServicio(),
                noti.getIdUsuario(),
                noti.getIdProveedor(),
                "PROVEEDOR",
                "REVIEW"
        );

        db.collection("notificaciones").document(notificacionId)
                .set(notificacion)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notificación enviada al proveedor"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error al enviar notificación", e));
    }

    @Override
    public int getItemCount() {
        return notificacionesList.size();
    }

    public static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        TextView tituloServicioNotificacion, estadoNotificacion, fechaContratacionNotificacion,descripcionNotificacion;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloServicioNotificacion = itemView.findViewById(R.id.tituloNotificacion);
            estadoNotificacion = itemView.findViewById(R.id.estadoNotificacion);
            fechaContratacionNotificacion = itemView.findViewById(R.id.fechaHoraNotificacion);
            descripcionNotificacion = itemView.findViewById(R.id.descripcionNotificacion);
        }
    }

    private int getEstadoColor(String estado) {
        switch (estado) {
            case "Aceptado":
                return R.color.acceptedColor;
            case "Cancelado":
                return R.color.cancelledColor;

            default:
                return R.color.defaultStatusColor;
        }
    }
}
