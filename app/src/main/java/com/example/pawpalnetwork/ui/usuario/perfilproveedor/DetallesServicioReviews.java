package com.example.pawpalnetwork.ui.usuario.perfilproveedor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Review;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.example.pawpalnetwork.ui.usuario.detalles.DetallesServicioReviewsViewModel;
import com.example.pawpalnetwork.ui.usuario.detalles.ReviewAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetallesServicioReviews extends Fragment {

    private DetallesServicioReviewsViewModel mViewModel;
    private Servicio servicio;
    private UsuarioGeneral usuario;
    private ImageView profileImage;
    private TextView tvNombreUsuario;
    private TextView tvCalificacion;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private LinearLayout lluser;
    private List<Review> reviewList;
    String nombreUsuario;
    public static DetallesServicioReviews newInstance() {
        return new DetallesServicioReviews();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalles_servicio_reviews, container, false);

        if (getArguments() != null) {
            servicio = (Servicio) getArguments().getSerializable("servicio");
            usuario = (UsuarioGeneral) getArguments().getSerializable("usuario");
        }

        db = FirebaseFirestore.getInstance();
        profileImage = view.findViewById(R.id.profileImage);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvCalificacion = view.findViewById(R.id.tvCalificacion);
        recyclerViewReviews = view.findViewById(R.id.recyclerViewReviews);
        lluser = view.findViewById(R.id.lluser);
        Glide.with(profileImage.getContext())
                .load(usuario.getFotoPerfil())
                .into(profileImage);
        tvNombreUsuario.setText(usuario.getNombre());
        tvCalificacion.setText("Calificación: "+usuario.getRating()+ " Estrellas");

        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        buscarNombreUsuario(usuario.getId());
        // Inicializar la lista de reviews (de Firestore o de datos simulados)
        cargarReviews();


        lluser.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), PerfilProveedorUsuario.class);
            Bundle args = new Bundle();
            args.putSerializable("usuario", usuario);
            intent.putExtras(args);
            startActivity(intent);
        });


        return view;
    }
    private void cargarReviews() {
        // Inicializa la lista de reviews
        reviewList = new ArrayList<>();

        // Consulta a Firestore para obtener las reviews del proveedor
        db.collection("reviews")
                .whereEqualTo("idProveedor", usuario.getId()) // Filtra por el ID del proveedor
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Recorre los documentos obtenidos de Firestore
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convierte cada documento en un objeto Review
                            Review review = document.toObject(Review.class);
                            review.setNombreUsuario(nombreUsuario);
                            reviewList.add(review);
                        }

                        // Configurar el adaptador con los datos obtenidos
                        reviewAdapter = new ReviewAdapter(getContext(), reviewList);
                        recyclerViewReviews.setAdapter(reviewAdapter);
                    } else {
                        // Manejar error al obtener datos
                        Log.w("Firestore", "Error obteniendo las reviews.", task.getException());
                    }
                });
    }

    private void buscarNombreUsuario(String idUsuario) {
        db.collection("usuarios").document(idUsuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtén el nombre del usuario
                        nombreUsuario = documentSnapshot.getString("nombre");

                    } else {

                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error al obtener el nombre del usuario", e);

                });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetallesServicioReviewsViewModel.class);
        // TODO: Use the ViewModel
    }

}