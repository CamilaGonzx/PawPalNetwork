package com.example.pawpalnetwork.ui.proveedor.ui.anuncios;

import static android.content.Context.MODE_PRIVATE;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pawpalnetwork.LoginActivity;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.GestionBD;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class AnunciosPublicados extends Fragment {

    private AnunciosPublicadosViewModel mViewModel;
    ImageView imgPerf;
    TextView tvNombreUs;
    SearchView searchView;
    Switch switchArch;
    RecyclerView recyclerViewAnuncios;
    ArrayList<Servicio> listaAnuncios;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    GestionBD g;
    UsuarioGeneral us;
    AnuncioAdapter adapter;
    String userId;
    public static AnunciosPublicados newInstance() {
        return new AnunciosPublicados();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_anuncios_publicados, container, false);
        imgPerf = root.findViewById(R.id.img_perf);
        tvNombreUs = root.findViewById(R.id.tvNombreUs);
        searchView = root.findViewById(R.id.searchView);
        switchArch = root.findViewById(R.id.switcharch);

        recyclerViewAnuncios = root.findViewById(R.id.recyclerviewanuncios);
        recyclerViewAnuncios.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        g = new GestionBD(storage,db);
        recyclerViewAnuncios.setAdapter(adapter);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Obtén el UID del usuario actual
        }



        Log.i(" Anuncios","id"+userId);
        actualizarPosts("");
        g.obtenerUsuarioPorId(userId,
                usuario -> {
                    tvNombreUs.setText("Hola, "+usuario.getNombre());
                    Glide.with(imgPerf.getContext())
                            .load(usuario.getFotoPerfil())
                            .into(imgPerf);
                },
                error -> {
                    // Acción en caso de fallo (onFailure)
                    System.err.println("Error al obtener el usuario: " + error.getMessage());
                    // Aquí podrías mostrar un mensaje de error en la UI
                }
        );

        switchArch.setOnCheckedChangeListener((buttonView, isChecked) -> actualizarPosts(""));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                actualizarPosts(query); // Llamar con el término de búsqueda
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                actualizarPosts(newText); // Llamar cada vez que cambie el texto
                return false;
            }
        });

//
        //cerrarSesion();
        return root;
    }

    private void cerrarSesion() {
        // Cierra sesión en Firebase
        FirebaseAuth.getInstance().signOut();

        // Elimina el UID de SharedPreferences
        SharedPreferences preferences = getContext().getSharedPreferences("Session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("uid");  // Elimina el UID guardado
        editor.apply();

        // Redirige al usuario a la pantalla de inicio de sesión
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el historial de actividades
        startActivity(intent);

    }


    @Override
    public void onResume() {
        super.onResume();
        actualizarPosts("");
    }
    public void actualizarPosts(String query) {


        db.collection("servicios")
                .whereEqualTo("status", !switchArch.isChecked())
                .whereEqualTo("proveedorId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Servicio> anunciosActualizados = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Servicio anuncio = document.toObject(Servicio.class);
                        if (anuncio != null && (query.isEmpty() || anuncio.getTitulo().toLowerCase().contains(query.toLowerCase()))) {
                            Log.i("FragmentoPrincipal", "Anuncio cargado: " + anuncio);
                            anunciosActualizados.add(anuncio);
                        }
                    }
                    Log.i("AnuncioAdapter", "Tamaño de listaAnuncios en getItemCount: " + anunciosActualizados.size());
                    if (adapter == null) {
                        // Si el adaptador es nulo, inicialízalo y configura el RecyclerView
                        adapter = new AnuncioAdapter(getContext(), anunciosActualizados, this);
                        recyclerViewAnuncios.setAdapter(adapter);
                    } else {
                        // Si ya existe, solo actualiza los datos del adaptador
                        adapter.setAnuncios(anunciosActualizados); // Método para actualizar la lista en el adaptador
                    }

                    adapter.notifyDataSetChanged(); // Notifica cambios al RecyclerView

                })
                .addOnFailureListener(e -> Log.e("FragmentoPrincipal", "Error al actualizar los posts", e));


    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AnunciosPublicadosViewModel.class);
        // TODO: Use the ViewModel
    }

}