package com.example.pawpalnetwork.ui.proveedor.ui.agenda;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;

import java.util.List;

public class DiaAdapter extends RecyclerView.Adapter<DiaAdapter.DiaViewHolder> {
    private List<Dia> diasList; // Clase `Dia` para almacenar el número y si tiene contrataciones
    private OnDayClickListener listener;

    public interface OnDayClickListener {
        void onDayClick(Dia dia);
    }

    public DiaAdapter(List<Dia> diasList, OnDayClickListener listener) {
        this.diasList = diasList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dia, parent, false);
        return new DiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaViewHolder holder, int position) {
        Log.e("Adpater","Entro");
        Dia dia = diasList.get(position);
        holder.tvDayNumber.setText(String.valueOf(dia.getDayNumber()));
        holder.tvDayName.setText(dia.getDayName());
        // Muestra el indicador solo si tiene una contratación
        holder.ivContractIndicator.setVisibility(dia.hasContract() ? View.VISIBLE : View.GONE);

        // Configura el clic en el día
        holder.itemView.setOnClickListener(v -> listener.onDayClick(dia));
    }

    @Override
    public int getItemCount() {
        return diasList.size();
    }

    public static class DiaViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNumber;
        TextView tvDayName;
        ImageView ivContractIndicator;

        public DiaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            ivContractIndicator = itemView.findViewById(R.id.ivContractIndicator);
        }
    }
}
