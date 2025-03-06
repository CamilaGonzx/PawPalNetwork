package com.example.pawpalnetwork.ui.proveedor.ui.crearanuncio;

import static android.content.Context.MODE_PRIVATE;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.GestionBD;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrearAnuncio extends Fragment {

    private CrearAnuncioViewModel mViewModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ArrayList<Uri> photoList;
    private FotoAdapter adapter;

    private Spinner spinnerServicio;
    private TextView tvTituloAnuncio, tvPrecio, tvDescripcion, tvSeleccionarFotos;
    private EditText etTituloAnuncio, etPrecio, etDescripcion;
    private RecyclerView recyclerView;
    private Button addPhotoButton, publicarButton;
    private ProgressBar progressBar;


    GestionBD g;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    String userId;

    private List<String> imagenesRutas;
    public static CrearAnuncio newInstance() {
        return new CrearAnuncio();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_crearanuncio, container, false);

        spinnerServicio = root.findViewById(R.id.spinnerServicio);
        tvTituloAnuncio = root.findViewById(R.id.tvTituloAnuncio);
        etTituloAnuncio = root.findViewById(R.id.etTituloAnuncio);
        tvPrecio = root.findViewById(R.id.tvPrecio);
        etPrecio = root.findViewById(R.id.etPrecio);
        tvDescripcion = root.findViewById(R.id.tvDescripcion);
        etDescripcion = root.findViewById(R.id.etDescripcion);
        tvSeleccionarFotos = root.findViewById(R.id.tvSeleccionarFotos);
        recyclerView = root.findViewById(R.id.recyclerView);
        addPhotoButton = root.findViewById(R.id.addPhotoButton);
        publicarButton = root.findViewById(R.id.button);
        progressBar = root.findViewById(R.id.progressBar);


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        g = new GestionBD(storage,db);

        photoList = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new FotoAdapter(getContext(), photoList);
        recyclerView.setAdapter(adapter);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Obtén el UID del usuario actual
            // Usa el UID según sea necesario
        }


        addPhotoButton.setOnClickListener(v -> openGallery());

        publicarButton.setOnClickListener(v -> guardarDatos());

        return root;
    }

    public void guardarDatos()
    {
        String tipo = spinnerServicio.getSelectedItem().toString();
        String titulo = etTituloAnuncio.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();


        // Validaciones
        if (titulo.isEmpty() || precioStr.isEmpty() || descripcion.isEmpty() || photoList.isEmpty()) {
            Toast.makeText(getContext(), "Por favor completa todos los campos y selecciona al menos una foto.", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Por favor ingresa un precio válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        toggleUIState(false);

        // Crear objeto Servicio y asignar los datos capturados
        Servicio servicio = new Servicio();
        servicio.setId(UUID.randomUUID().toString());
        servicio.setTitulo(titulo);
        servicio.setPrecio(precio);
        servicio.setProveedorId(userId);
        servicio.setStatus(true);
        servicio.setDescripcion(descripcion);
        servicio.setTipo(tipo); // Asumiendo que el tipo es una pr
        g.uploadImagesAndSaveService(photoList, servicio, new UploadCallback() {
            @Override
            public void onUploadStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUploadProgress(int progress) {
                // Opcional: actualizar la barra de progreso si es necesario
            }

            @Override
            public void onUploadSuccess() {
                toggleUIState(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Anuncio publicado correctamente.", Toast.LENGTH_SHORT).show();
                resetForm();
            }

            @Override
            public void onUploadFailure(String errorMessage) {
                toggleUIState(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void resetForm() {
        etTituloAnuncio.setText("");
        etPrecio.setText("");
        etDescripcion.setText("");
        photoList.clear();
        adapter.notifyDataSetChanged();
    }

    private void toggleUIState(boolean enabled) {
        spinnerServicio.setEnabled(enabled);
        etTituloAnuncio.setEnabled(enabled);
        etPrecio.setEnabled(enabled);
        etDescripcion.setEnabled(enabled);
        addPhotoButton.setEnabled(enabled);
        publicarButton.setEnabled(enabled);
        progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            photoList.add(selectedImageUri);
            adapter.notifyItemInserted(photoList.size() - 1);
        }
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CrearAnuncioViewModel.class);
        // TODO: Use the ViewModel
    }

}