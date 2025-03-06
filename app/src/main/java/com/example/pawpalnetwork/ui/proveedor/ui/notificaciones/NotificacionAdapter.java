package com.example.pawpalnetwork.ui.proveedor.ui.notificaciones;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Notificacion;
import com.example.pawpalnetwork.ui.usuario.detallescontratacion.DetallesContratacion;

import java.util.List;

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

            if(notificacion.getEstado().equals("REVIEW"))
            {

            }else {
                DetallesContratacion detallesFragment = new DetallesContratacion();
                Bundle args = new Bundle();
                args.putString("contratacionId", notificacion.getIdContratacion());
                detallesFragment.setArguments(args);

                NavController navController = Navigation.findNavController((AppCompatActivity) context, R.id.nav_host_fragment_activity_proveedor);
                navController.navigate(R.id.action_navigation_notifications_to_navigation_detalles2, args);
            }
        });
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
