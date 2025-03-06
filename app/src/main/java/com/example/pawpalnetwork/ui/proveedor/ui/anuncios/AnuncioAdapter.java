package com.example.pawpalnetwork.ui.proveedor.ui.anuncios;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.GestionBD;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.ui.proveedor.ui.editarpublicacion.EditarPublicacion;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> {

    private List<Servicio> listaAnuncios;
    private Context context;
    private AnunciosPublicados fragmentoPrincipal;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AnuncioAdapter(Context context, List<Servicio> listaAnuncios, AnunciosPublicados fragmentoPrincipal) {
        this.context = context;
        this.listaAnuncios = listaAnuncios;
        this.fragmentoPrincipal = fragmentoPrincipal;
    }
    public void setAnuncios(List<Servicio> listaAnuncios) {
        this.listaAnuncios = listaAnuncios;
    }
    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_publicaciones, parent, false);
        return new AnuncioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {
        Servicio anuncio = listaAnuncios.get(position);
        Log.i("FragmentoPrincipal", "Este fue uno de los datos2: "+anuncio.toString());
        // Configurar los detalles del servicio en las vistas
        holder.tvTitulo.setText(anuncio.getTitulo());

        holder.tvTipo.setText(anuncio.getTipo());
         holder.tvPrecio.setText("$" + anuncio.getPrecio());

       holder.pic.setImageResource(imagenServicio(anuncio.getTipo()));

        // Obtener el nombre del usuario desde Firestore usando proveedorId

        // Botón Editar
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarPublicacion.class);

            intent.putExtra("servicioId", anuncio.getId());
            context.startActivity(intent);
        });
        holder.btnBorrar.setOnClickListener(v-> {
            new AlertDialog.Builder(context)
                      .setTitle("Eliminar Post")
                        .setMessage("¿Estás seguro de que quieres eliminar este post?")
                       .setPositiveButton("Eliminar", (dialog, which) -> {
                            db.collection("servicios").document(anuncio.getId().toString()).delete()
                                   .addOnSuccessListener(aVoid -> {
                                        fragmentoPrincipal.actualizarPosts("");
                                   }).addOnFailureListener(e -> Log.e("AnuncioAdapter", "Error al eliminar servicio", e));
                       })
                       .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss()) // Cierra el diálogo
                        .show();
        });

        // Botón Archivar
        holder.btnArchivar.setOnClickListener(v -> {
            db.collection("servicios").document(anuncio.getId().toString())
                    .update("status", false)
                    .addOnSuccessListener(aVoid -> fragmentoPrincipal.actualizarPosts(""))
                    .addOnFailureListener(e -> Log.e("AnuncioAdapter", "Error al archivar servicio", e));
        });
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


    @Override
    public int getItemCount() {
        return listaAnuncios.size();
    }

    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView tvTitulo, tvTipo, tvDuracion, tvPrecio;
        Button btnEditar, btnBorrar, btnArchivar;

        public AnuncioViewHolder(@NonNull View itemView) {
            super(itemView);

            pic = itemView.findViewById(R.id.pic);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);

            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);

            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnBorrar = itemView.findViewById(R.id.btnBorrar);
            btnArchivar = itemView.findViewById(R.id.btnArchivar);
        }
    }
}

