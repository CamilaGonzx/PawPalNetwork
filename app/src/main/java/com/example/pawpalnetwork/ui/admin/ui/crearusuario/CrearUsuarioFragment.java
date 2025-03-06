package com.example.pawpalnetwork.ui.admin.ui.crearusuario;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawpalnetwork.MainActivity;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.RegisterActivity;
import com.example.pawpalnetwork.SelectLocation;
import com.example.pawpalnetwork.bd.Geolocalizacion;
import com.example.pawpalnetwork.bd.GestionBD;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.example.pawpalnetwork.ui.proveedor.Main_Proveedor;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrearUsuarioFragment extends Fragment {

    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CODE_MEDIA_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private static final int REQUEST_LOCATION = 1;
    AutoCompleteTextView tvRol;
    ImageButton profileImage;
    TextInputEditText tfNombre;
    TextInputEditText tfEmail;
    TextInputEditText tfPhone;
    TextInputEditText tfPassword;
    TextInputEditText tfCodigoPostal;
    private ProgressBar progressBar;
    Button btnContinuar, btnUbicacion;
    private FirebaseAuth mAuth;
    private double selectedLatitude;
    private double selectedLongitude;
    Uri imageUri;
    GestionBD gestionBD;
    UsuarioGeneral us;
    boolean rol;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    public static CrearUsuarioFragment newInstance() {
        return new CrearUsuarioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_crear_usuario, container, false);

        mAuth = FirebaseAuth.getInstance();  // Inicializa FirebaseAuth
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        gestionBD = new GestionBD(storage, db);
        profileImage = root.findViewById(R.id.profile_image_admin);
        tfNombre = root.findViewById(R.id.tfNombre_admin);
        tfEmail = root.findViewById(R.id.tfEmail_admin);
        tfPhone = root.findViewById(R.id.tfPhone_admin);
        tfPassword = root.findViewById(R.id.tfPassword_admin);
        tfCodigoPostal = root.findViewById(R.id.tfCodigoPostal_admin);
        tvRol = root.findViewById(R.id.tvRol_admin);
        btnContinuar = root.findViewById(R.id.btnRegistro_admin);
        btnUbicacion = root.findViewById(R.id.btnUbicacion);
        progressBar= root.findViewById(R.id.progress_bar);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprobar permisos

                new AlertDialog.Builder(getContext())
                        .setTitle("Seleccionar imagen")
                        .setItems(new CharSequence[]{"Tomar foto", "Elegir de galería"}, (dialog, which) -> {
                            if (which == 0) {
                                // Tomar foto
                                if (checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                                } else {
                                    mCaptura();
                                }
                            } else {
                                // Elegir de galería
                                seleccionar_imagen();
                            }
                        })
                        .show();
            }
        });

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre = tfNombre.getText().toString().trim();
                String email = tfEmail.getText().toString().trim();
                String telefono = tfPhone.getText().toString().trim();
                String contrasena = tfPassword.getText().toString().trim();
                String codigoPostal = tfCodigoPostal.getText().toString().trim();
                if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || contrasena.isEmpty() || codigoPostal.isEmpty()) {
                    Toast.makeText(getContext(), "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Validación de formato de correo electrónico
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getContext(), "Correo electrónico no válido.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Validación de la imagen seleccionada
                if (imageUri == null) {
                    Toast.makeText(getContext(), "Por favor, seleccione una imagen de perfil.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Validación de ubicación seleccionada
                if (selectedLatitude == 0.0 && selectedLongitude == 0.0) {
                    Toast.makeText(getContext(), "Por favor, seleccione una ubicación.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tvRol.getText().toString().equals("Proveedor")) {
                    rol = true;
                } else {
                    rol = false;
                }

                registerUserWithEmail(email, contrasena, nombre, telefono, codigoPostal, rol);

            }
        });

        btnUbicacion.setOnClickListener(v -> {
            Intent intent2 = new Intent(root.getContext(), SelectLocation.class);
            startActivityForResult(intent2, REQUEST_LOCATION);
        });

        return root;
    }

    private void limpiarCampos() {
        tfNombre.setText("");
        tfEmail.setText("");
        tfPhone.setText("");
        tfPassword.setText("");
        tfCodigoPostal.setText("");
        tvRol.setText("");
        profileImage.setImageResource(R.drawable.ic_defaultprof);
        imageUri = null;
        selectedLatitude = 0.0;
        selectedLongitude = 0.0;
    }

    private void mostrarProgress(boolean mostrar) {

        // Desactivar campos y botones durante el progreso
        tfNombre.setEnabled(mostrar);
        tfEmail.setEnabled(mostrar);
        tfPhone.setEnabled(mostrar);
        tfPassword.setEnabled(mostrar);
        tfCodigoPostal.setEnabled(mostrar);
        tvRol.setEnabled(mostrar);
        profileImage.setEnabled(mostrar);
        btnContinuar.setEnabled(mostrar);
        btnUbicacion.setEnabled(mostrar);
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void registerUserWithEmail(String email, String password, String nombre, String telefono, String codigoPostal, boolean rol) {
        mostrarProgress(false);
        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "No hay conexión a Internet. Intente nuevamente cuando esté conectado.", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            UsuarioGeneral usuario = new UsuarioGeneral(uid, nombre, email, null, telefono, codigoPostal, rol);

                            gestionBD.agregarUsuario(usuario, imageUri, () -> {
                                Geolocalizacion geolocalizacion = new Geolocalizacion();
                                geolocalizacion.setId(db.collection("geolocalizaciones").document().getId());
                                geolocalizacion.setUserId(uid);
                                geolocalizacion.setLatitud(selectedLatitude);
                                geolocalizacion.setLongitud(selectedLongitude);
                                geolocalizacion.setRegion(codigoPostal);

                                db.collection("geolocalizaciones").document(geolocalizacion.getId())
                                        .set(geolocalizacion)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Usuario registrado exitosamente.", Toast.LENGTH_LONG).show();
                                            limpiarCampos();
                                            mostrarProgress(true);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al guardar geolocalización: " + e.getMessage(), Toast.LENGTH_LONG).show());
                            });
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al registrar el usuario: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    //CAMARA

    //Tomar foto
    @Deprecated
    public void mCaptura() {
        if (checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_MEDIA_PERMISSION);
        } else {
            openCamera();
        }
    }

    public void openCamera() {
        ContentValues values = new ContentValues();
        String s1 = "Nohay";
        values.put("title", "Id" + s1);
        imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        launcher_for_camera.launch(cameraIntent);
    }

    private ActivityResultLauncher<Intent> launcher_for_camera =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK && imageUri != null) {
                                profileImage.setImageURI(imageUri);
                            }
                        }
                    }
            );

    //Cargar imagen desde galeria
    public void seleccionar_imagen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_MEDIA_PERMISSION);
            } else {
                openGallery();
            }
        } else {
            if (checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_MEDIA_PERMISSION);
            } else {
                openGallery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_MEDIA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                profileImage.setImageURI(imageUri);
            }
        } else if (requestCode == REQUEST_LOCATION && resultCode == RESULT_OK && data != null) {
            // Manejo de los datos de ubicación seleccionados en SelectLocationActivity
            selectedLatitude = data.getDoubleExtra("latitude", 0.0);
            selectedLongitude = data.getDoubleExtra("longitude", 0.0);
            String codigoPostal = data.getStringExtra("codigoPostal");

            // Asigna el código postal al campo correspondiente
            tfCodigoPostal.setText(codigoPostal);


        }

    }
}