package com.example.pawpalnetwork.ui.usuario.agendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Horario;

import java.util.List;

public class HorarioAdapter extends RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder> {
    private List<Horario> horarios;
    private int selectedPosition = -1;
    public HorarioAdapter(List<Horario> horarios) {
        this.horarios = horarios;
    }

    public void actualizarHorarios(List<Horario> nuevosHorarios) {
        horarios = nuevosHorarios;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hora, parent, false);
        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
        Horario horario = horarios.get(position);
        holder.tvHora.setText(horario.getHora());

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.bg_hora_seleccionada);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_hora);
        }

        // Solo muestra la hora
        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Obtener la posición actualizada

            // Verifica que currentPosition no sea RecyclerView.NO_POSITION
            if (currentPosition != RecyclerView.NO_POSITION) {
                selectedPosition = currentPosition;
                notifyDataSetChanged(); // Refresca la vista para aplicar el fondo al seleccionado
            }
        });
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public Horario getSelectedHorario() {
        if (selectedPosition != -1) {
            return horarios.get(selectedPosition);
        }
        return null;
    }



    @Override
    public int getItemCount() {
        return horarios.size();
    }

    public static class HorarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvHora;

        public HorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora = itemView.findViewById(R.id.tvHora);
        }
    }
}