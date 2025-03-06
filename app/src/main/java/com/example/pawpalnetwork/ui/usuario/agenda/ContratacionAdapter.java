package com.example.pawpalnetwork.ui.usuario.agenda;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Contratacion;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContratacionAdapter extends RecyclerView.Adapter<ContratacionAdapter.ContratacionViewHolder> {


    private List<Contratacion> contratacionList;
    private Servicio servicio;
    private UsuarioGeneral usuario;
    FirebaseFirestore db;
    public ContratacionAdapter(List<Contratacion> contratacionList, Servicio servicio, UsuarioGeneral usuario) {
        this.contratacionList = contratacionList;
        this.servicio = servicio;
        this.usuario = usuario;
    }

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
        db = FirebaseFirestore.getInstance();
        //BuscarServicio(contratacion.getIdServicio());
        holder.tvTitulo.setText("");
        holder.tvCliente.setText("");
        holder.tvUbicacion.setText("Ubicación: " + contratacion.getPuntoEncuentro());
        holder.tvHorasContratadas.setText("Horas: " + contratacion.getHoras());

        // Formateo de la fecha y hora de inicio
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        holder.tvHora.setText("Hora: " + contratacion.getHora());

        // Configura el estado de la contratación
        holder.estadoNotificacion.setText(contratacion.getStatus());

    }



    @Override
    public int getItemCount() {
        return contratacionList.size();
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
