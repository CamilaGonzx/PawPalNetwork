package com.example.pawpalnetwork.ui.proveedor.ui.editarpublicacion;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pawpalnetwork.R;

import java.util.ArrayList;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.PhotoViewHolder> {

    private Context context;
    private ArrayList<Uri> photoList;

    public FotoAdapter(Context context, ArrayList<Uri> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_foto, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Log.d("FotoAdapter", "onBindViewHolder llamado para posición: " + position);
        Uri photoUri = photoList.get(position);

        Glide.with(context)
                .load(photoUri)
                .placeholder(R.drawable.ic_borrarcuenta)
                .error(R.drawable.ic_location)
                .into(holder.photoImageView);

        holder.deleteButton.setOnClickListener(v -> {
            if(!photoList.isEmpty()) {
                photoList.remove(position);
                notifyItemRemoved(position);
            }
            });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        ImageButton deleteButton;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}