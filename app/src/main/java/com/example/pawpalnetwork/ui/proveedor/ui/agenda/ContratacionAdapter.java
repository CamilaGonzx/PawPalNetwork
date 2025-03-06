package com.example.pawpalnetwork.ui.proveedor.ui.agenda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Contratacion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContratacionAdapter extends RecyclerView.Adapter<ContratacionAdapter.ContratacionViewHolder> {

    private List<Contratacion> contratacionList;

    public ContratacionAdapter(List<Contratacion> contratacionList) {
        this.contratacionList = contratacionList;
    }

    @NonNull
    @Override
    public ContratacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contrato, parent, false);
        return new ContratacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContratacionViewHolder holder, int position) {
        Contratacion contratacion = contratacionList.get(position);

        holder.tvTitulo.setText("" );
        holder.tvCliente.setText("");
        holder.tvUbicacion.setText("Ubicación: " + contratacion.getPuntoEncuentro());
        holder.tvHorasContratadas.setText("Horas: " + contratacion.getHoras());


        holder.tvHora.setText("Hora: " + contratacion.getHora());

        // Configura el estado de la contratación
        holder.estadoNotificacion.setText(contratacion.getStatus());
        int estadoColor = getEstadoColor(contratacion.getStatus());
        holder.estadoNotificacion.setBackgroundTintList(ContextCompat.getColorStateList(holder.estadoNotificacion.getContext(), estadoColor));
    }

    @Override
    public int getItemCount() {
        return contratacionList.size();
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


    public static class ContratacionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvCliente, tvHora, tvUbicacion, tvHorasContratadas, estadoNotificacion;

        public ContratacionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvHorasContratadas = itemView.findViewById(R.id.tvHorasContratadas);
            estadoNotificacion = itemView.findViewById(R.id.estadoNotificacion);
        }
    }
}
