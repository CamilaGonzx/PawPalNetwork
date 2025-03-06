package com.example.pawpalnetwork.ui.proveedor.ui.editarpublicacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.UploadCallback;
import com.example.pawpalnetwork.bd.GestionBD;
import com.example.pawpalnetwork.bd.Servicio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class EditarPublicacion extends AppCompatActivity {

    // Declaración de las variables
    private Spinner spinnerServicio;
    private EditText etTituloAnuncio, etPrecio, etDescripcion;
    private RecyclerView recyclerView;
    private Button addPhotoButton, buttonEditar;
    private Switch swArchivar;
    private ProgressBar progressBar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ArrayList<Uri> photoList;
    private FotoAdapter adapter;
String userId,servicioId;
    GestionBD g;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_publicacion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerServicio = findViewById(R.id.spinnerServicio);
        etTituloAnuncio = findViewById(R.id.etTituloAnuncio);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);
        recyclerView = findViewById(R.id.recyclerView);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        buttonEditar = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        swArchivar = findViewById(R.id.swarchivar);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        g = new GestionBD(storage,db);



        photoList = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new FotoAdapter(this, photoList);
        recyclerView.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Obtén el UID del usuario actual
            // Usa el UID según sea necesario
        }

        servicioId = getIntent().getStringExtra("servicioId"); // Asume que se pasa el ID
        cargarDatosServicio(servicioId);



        addPhotoButton.setOnClickListener(v -> openGallery());

        buttonEditar.setOnClickListener(v -> guardarCambios());

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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == this.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            photoList.add(selectedImageUri);
            adapter.notifyItemInserted(photoList.size() - 1);
        }
    }

    private void cargarDatosServicio(String servicioId) {
        db.collection("servicios").document(servicioId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Servicio servicio = documentSnapshot.toObject(Servicio.class);
                        if (servicio != null) {
                            etTituloAnuncio.setText(servicio.getTitulo());
                            etPrecio.setText(String.valueOf(servicio.getPrecio()));
                            etDescripcion.setText(servicio.getDescripcion());

                            // Seleccionar el tipo de servicio en el Spinner
                            String tipoServicio = servicio.getTipo();
                            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                                    this, R.array.tipo_servicio, android.R.layout.simple_spinner_item);
                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerServicio.setAdapter(adapter2);
                            int spinnerPosition = adapter2.getPosition(tipoServicio);
                            spinnerServicio.setSelection(spinnerPosition);

                            swArchivar.setChecked(!servicio.isStatus());

                            // Cargar fotos existentes desde Firestore
                            if (servicio.getImagenesRutas() != null) {
                                photoList.clear(); // Asegúrate de limpiar la lista antes de agregar nuevas imágenes
                                for (String imageUrl : servicio.getImagenesRutas()) {
                                    // Convertir URL a Uri\
                                    if(!imageUrl.isEmpty()) {
                                        Uri imageUri = Uri.parse(imageUrl); //

                                        Log.e("FotoAdapter", "Entro" + imageUri);
                                        photoList.add(imageUri);
                                    }
                                }
                                Log.d("EditarPublicacion", "photoList contiene: " + photoList.size() + " imágenes.");
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("EditarPublicacion", "Error al cargar datos del servicio", e));
    }


    private void toggleUIState(boolean enabled) {
        spinnerServicio.setEnabled(enabled);
        etTituloAnuncio.setEnabled(enabled);
        etPrecio.setEnabled(enabled);
        etDescripcion.setEnabled(enabled);
        addPhotoButton.setEnabled(enabled);
        buttonEditar.setEnabled(enabled);
        swArchivar.setEnabled(enabled);
    }


    private void guardarCambios() {
        // Obtener los datos del formulario
        String titulo = etTituloAnuncio.getText().toString().trim();
        String tipo = spinnerServicio.getSelectedItem().toString();
        String precioStr = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        // Validaciones
        if (titulo.isEmpty() || precioStr.isEmpty() || descripcion.isEmpty() || photoList.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos y selecciona al menos una foto.", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor ingresa un precio válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Desactivar la UI y mostrar la barra de progreso
        toggleUIState(false);

        // Crear el objeto Servicio con los datos editados
        Servicio servicio = new Servicio();
        servicio.setId(servicioId);
        servicio.setTitulo(titulo);
        servicio.setTipo(tipo);
        servicio.setPrecio(precio);
        servicio.setDescripcion(descripcion);
        servicio.setStatus(!swArchivar.isChecked());
        servicio.setProveedorId(userId);

        // Subir imágenes y guardar el servicio
        g.uploadImagesAndSaveService(photoList, servicio, new UploadCallback() {
            @Override
            public void onUploadStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUploadProgress(int progress) {
                // Opcional: actualizar la barra de progreso si se necesita
            }

            @Override
            public void onUploadSuccess() {
                toggleUIState(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditarPublicacion.this, "Cambios guardados correctamente.", Toast.LENGTH_SHORT).show();
                finish(); // Cierra la actividad después de guardar
            }

            @Override
            public void onUploadFailure(String errorMessage) {
                toggleUIState(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditarPublicacion.this, "Error al guardar cambios: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


}