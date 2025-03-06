package com.example.pawpalnetwork.ui.usuario.agendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;

import java.util.List;
import java.util.Map;

public class FechaAdapter extends RecyclerView.Adapter<FechaAdapter.FechaViewHolder> {
    private final List<Map<String, String>> fechas;
    private final OnFechaClickListener onFechaClickListener;
    private int selectedPosition = -1;
    public interface OnFechaClickListener {
        void onFechaClick(String fechaFirebase);
    }

    public FechaAdapter(List<Map<String, String>> fechas, OnFechaClickListener onFechaClickListener) {
        this.fechas = fechas;
        this.onFechaClickListener = onFechaClickListener;
    }

    @NonNull
    @Override
    public FechaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fecha, parent, false);
        return new FechaViewHolder(view);
    }
    String fechaFirebase;
    @Override
    public void onBindViewHolder(@NonNull FechaViewHolder holder, int position) {
        Map<String, String> fecha = fechas.get(position);
        holder.tvDay.setText(fecha.get("day"));
        holder.tvDate.setText(fecha.get("dateDisplay")); // Mostrar en formato dd/MM

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.bg_dia_seleccionado);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.dias_rounded);
        }
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Obtener la posición actualizada

            // Verifica que currentPosition no sea RecyclerView.NO_POSITION
            if (currentPosition != RecyclerView.NO_POSITION) {
                selectedPosition = currentPosition;
                notifyDataSetChanged(); // Refresca la vista para aplicar el fondo al seleccionado

                fechaFirebase = fechas.get(currentPosition).get("dateFirebase"); // Formato dd-MM-yyyy para Firebase
                onFechaClickListener.onFechaClick(fechaFirebase);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fechas.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public String getSelectedFecha() {
        if (selectedPosition != -1) {
            return fechaFirebase;
        }
        return null;
    }

    public static class FechaViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvDate;

        public FechaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}

