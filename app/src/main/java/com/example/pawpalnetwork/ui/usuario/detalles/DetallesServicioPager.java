package com.example.pawpalnetwork.ui.usuario.detalles;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.example.pawpalnetwork.ui.usuario.agendar.Agendar;
import com.example.pawpalnetwork.ui.usuario.perfilproveedor.PerfilProveedorUsuario;

public class DetallesServicioPager extends Fragment {

    private DetallesServicioPagerViewModel mViewModel;

    public static DetallesServicioPager newInstance() {
        return new DetallesServicioPager();
    }
    private Servicio servicio;
    private UsuarioGeneral usuario;
    private ImageView profileImage;
    private TextView tvNombreUsuario;
    private TextView tvCalificacion,tvServicio;
    private ImageView imgserv;
    private TextView tvDescripcion;
    private Button btnDisponibilidad;
    private Button btnAgendar;
    private LinearLayout lluser;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalles_servicio_pager, container, false);
        if (getArguments() != null) {
            servicio = (Servicio) getArguments().getSerializable("servicio");
            usuario = (UsuarioGeneral) getArguments().getSerializable("usuario");
        }

        profileImage = view.findViewById(R.id.profileImage);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvCalificacion = view.findViewById(R.id.tvCalificacion);
        imgserv = view.findViewById(R.id.imgserv);
        tvServicio  =view.findViewById(R.id.tvServicio);
        tvDescripcion = view.findViewById(R.id.tvDescripcion);
        lluser = view.findViewById(R.id.lluserd);
        btnAgendar = view.findViewById(R.id.btnAgendar);

        Glide.with(profileImage.getContext())
                .load(usuario.getFotoPerfil())
                .into(profileImage);
        tvNombreUsuario.setText(usuario.getNombre());
        tvCalificacion.setText("Calificación: "+usuario.getRating()+ " Estrellas");
        imgserv.setImageResource(imagenServicio(servicio.getTipo()));
        tvServicio.setText(servicio.getTipo());
        tvDescripcion.setText(servicio.getDescripcion());

        btnAgendar.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), Agendar.class);
            Bundle args = new Bundle();
            args.putSerializable("servicio", servicio);
            args.putSerializable("usuario", usuario);
            intent.putExtras(args);
            startActivity(intent);
        });

        lluser.setOnClickListener(v -> {
            Context context = getContext();
            if (context != null) {
                Intent intent = new Intent(context, PerfilProveedorUsuario.class);
                Bundle args = new Bundle();
                args.putSerializable("usuario", usuario); // Asegúrate de que `usuario` sea serializable
                intent.putExtras(args);
                context.startActivity(intent);
            }
        });

        return view;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetallesServicioPagerViewModel.class);
        // TODO: Use the ViewModel
    }

}