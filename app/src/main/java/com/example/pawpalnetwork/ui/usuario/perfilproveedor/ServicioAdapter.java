package com.example.pawpalnetwork.ui.usuario.perfilproveedor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.ui.usuario.detalles.Detalles_Servicio;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {
    private Context context;
    private List<Servicio> serviciosList;
    public ServicioAdapter(Context context, List<Servicio> serviciosList) {
        this.context = context;
        this.serviciosList = serviciosList;

    }
    public void setAnuncios(List<Servicio> listaAnuncios) {
        this.serviciosList = listaAnuncios;
        notifyDataSetChanged(); // Notifica cambios cada vez que se actualice la lista
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_anuncio, parent, false);
        return new ServicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {
        Servicio servicio = serviciosList.get(position);

        if (holder.pic != null) {  // Verifica que 'pic' esté inicializado
            holder.pic.setImageResource(imagenServicio(servicio.getTipo()));
        }

Log.i("Servicio Adapter", "El servicio es: "+servicio.toString());
        holder.tvTitulo.setText(servicio.getTitulo());
        holder.tvNombre.setText("" + servicio.getDescripcion());
        holder.tvTipo.setText("Tipo: " + servicio.getTipo());
        holder.tvPrecio.setText("$" + servicio.getPrecio());

        // Establecer el campo "Cercanía" o "Ranking"
        holder.tvCercania.setText("Ranking: 1");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Detalles_Servicio.class);
                intent.putExtra("servicio",servicio);

                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return serviciosList.size();
    }


    public int imagenServicio(String servicio) {
        Log.e("GestionBD", "El servicio es nulo"+servicio);
        switch (servicio) {
            case "Pasear":
                return R.drawable.pasear;
            case "Entrenamiento":
                return R.drawable.entrenamiento;
            case "Veterinario":
                return R.drawable.veterinario;
            case "Baño y Estetica":
                return R.drawable.banoestetica;
            case "DayCare":
                return R.drawable.daycare;
            default:
                return R.drawable.default_service; // Imagen por defecto si no coincide
        }
    }
    public static class ServicioViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView pic;
        TextView tvTitulo, tvNombre, tvTipo, tvPrecio, tvCercania;

        public ServicioViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.ivserv);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvCercania = itemView.findViewById(R.id.tvCercania);
        }
    }
}
