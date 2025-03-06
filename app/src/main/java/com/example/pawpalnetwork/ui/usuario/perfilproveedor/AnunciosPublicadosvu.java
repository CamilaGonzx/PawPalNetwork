package com.example.pawpalnetwork.ui.usuario.perfilproveedor;

import androidx.lifecycle.ViewModelProvider;

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

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.example.pawpalnetwork.ui.usuario.principal.ServicioAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class AnunciosPublicadosvu extends Fragment {

    private AnunciosPublicadosvuViewModel mViewModel;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private RecyclerView rvAnuncios;
    private ServicioAdapter adapter;
    private List<Servicio> serviciosList;
    public static AnunciosPublicadosvu newInstance() {
        return new AnunciosPublicadosvu();
    }
    UsuarioGeneral usuario;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_anuncios_publicadosvu, container, false);
        if (getArguments() != null) {

            usuario = (UsuarioGeneral) getArguments().getSerializable("usuario");
        }
        rvAnuncios = root.findViewById(R.id.rvServicios);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        serviciosList = new ArrayList<>();
        adapter = new ServicioAdapter(getContext(), serviciosList);
        rvAnuncios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAnuncios.setAdapter(adapter);
        actualizarPosts("");

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AnunciosPublicadosvuViewModel.class);
        // TODO: Use the ViewModel
    }

    public void actualizarPosts(String query) {
        db.collection("servicios")
                .whereEqualTo("status", true)
                .whereEqualTo("proveedorId",usuario.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Servicio> anunciosActualizados = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Servicio anuncio = document.toObject(Servicio.class);
                        if (anuncio != null) {
                            Log.i("FragmentoPrincipal", "Anuncio cargado: " + anuncio);

                            anunciosActualizados.add(anuncio);
                        }
                    }
                    if (adapter == null) {
                        adapter = new ServicioAdapter(getContext(), anunciosActualizados);
                        rvAnuncios.setAdapter(adapter);
                    } else {
                        adapter.setAnuncios(anunciosActualizados);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("AnunciosPublicados", "Error al actualizar los posts", e));
    }

}