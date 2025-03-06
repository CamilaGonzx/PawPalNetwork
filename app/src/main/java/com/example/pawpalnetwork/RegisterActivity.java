package com.example.pawpalnetwork;



import static com.example.pawpalnetwork.bd.NetworkUtils.isNetworkAvailable;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pawpalnetwork.bd.Geolocalizacion;
import com.example.pawpalnetwork.bd.GestionBD;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.example.pawpalnetwork.ui.proveedor.Main_Proveedor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_LOCATION = 1;

    ProgressBar progressBar;

    TextView tvRegistrate;
    TextView tvCreaCuenta;
    ImageButton profileImage;
    TextInputEditText tfNombre;
    TextInputEditText tfEmail;
    TextInputEditText tfPhone;
    TextInputEditText tfPassword;
    TextInputEditText tfCodigoPostal;
    Button btnRegistro,btnUbicacion;
    private FirebaseAuth mAuth;
    Drawable defaultd;
    Uri imageUri;
    GestionBD gestionBD;
    UsuarioGeneral us;
    boolean rol;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private double selectedLatitude;
    private double selectedLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mAuth = FirebaseAuth.getInstance();  // Inicializa FirebaseAuth
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        gestionBD = new GestionBD(storage, db);
        tvRegistrate = findViewById(R.id.tvRegistrate);
        profileImage = findViewById(R.id.profile_image);
        tfNombre = findViewById(R.id.tfNombre);
        tfEmail = findViewById(R.id.tfEmail);
        tfPhone = findViewById(R.id.tfPhone);
        tfPassword = findViewById(R.id.tfPassword);
        tfCodigoPostal = findViewById(R.id.tfCodigoPostal);
        btnRegistro = findViewById(R.id.btnRegistro);
        btnUbicacion = findViewById(R.id.btnUbicacion);
        progressBar = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        String tipo =  intent.getStringExtra("Tipo");

        defaultd = getResources().getDrawable(R.drawable.ic_defaultprof);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprobar permisos

                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Seleccionar imagen")
                        .setItems(new CharSequence[]{"Tomar foto", "Elegir de galería"}, (dialog, which) -> {
                            if (which == 0) {
                                // Tomar foto
                                if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                                } else {
                                    tomarFoto();
                                }
                            } else {
                                // Elegir de galería
                                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                if (pickImageIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(pickImageIntent, REQUEST_PICK_IMAGE);
                                }

                            }
                        })
                        .show();


            }
        });



        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre = tfNombre.getText().toString().trim();
                String email = tfEmail.getText().toString().trim();
                String telefono = tfPhone.getText().toString().trim();
                String contrasena = tfPassword.getText().toString().trim();
                String codigoPostal = tfCodigoPostal.getText().toString().trim();

                if (tipo.equals("Proveedor")) {
                    rol = true;
                } else {
                    rol = false;
                }



                if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || contrasena.isEmpty() || codigoPostal.isEmpty() || profileImage.getDrawable().getConstantState().equals(defaultd.getConstantState())) {
                    Toast.makeText(RegisterActivity.this, "Por favor, complete todos los campos.", Toast.LENGTH_LONG).show();
                }else  if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Correo electrónico no válido.", Toast.LENGTH_LONG).show();


                }else {
                    setLoadingState(true);
                    registerUserWithEmail(email, contrasena, nombre, telefono, codigoPostal, rol);
                }


            }
        });

        btnUbicacion.setOnClickListener(v -> {
            Intent intent2 = new Intent(RegisterActivity.this, SelectLocation.class);
            startActivityForResult(intent2, REQUEST_LOCATION);
        });

    }

    private void setLoadingState(boolean isLoading) {
        // Bloquear/desbloquear campos
        tfNombre.setEnabled(!isLoading);
        tfEmail.setEnabled(!isLoading);
        tfPhone.setEnabled(!isLoading);
        tfPassword.setEnabled(!isLoading);
        tfCodigoPostal.setEnabled(!isLoading);
        btnRegistro.setEnabled(!isLoading);
      //  btnUbicacion.setEnabled(!isLoading);
        profileImage.setEnabled(!isLoading);

        // Mostrar/ocultar ProgressBar
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    private void registerUserWithEmail(String email, String password, String nombre, String telefono, String codigoPostal, boolean rol) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No hay conexión a Internet. Intente nuevamente cuando esté conectado.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Registro exitoso en Firebase Authentication
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid(); // UID de Firebase como ID del usuario

                                // Crear objeto UsuarioGeneral con UID y demás datos
                                UsuarioGeneral usuario = new UsuarioGeneral(uid, nombre, email, password, telefono, codigoPostal, rol);

                                // Guardar el usuario en Firestore
                                gestionBD.agregarUsuario(usuario, imageUri, () -> {
                                    // Crear objeto Geolocalizacion
                                    Geolocalizacion geolocalizacion = new Geolocalizacion();
                                    geolocalizacion.setId(db.collection("geolocalizaciones").document().getId()); // Generar ID automático
                                    geolocalizacion.setUserId(uid); // Asigna el UID del usuario creado
                                    geolocalizacion.setLatitud(selectedLatitude); // Latitud seleccionada
                                    geolocalizacion.setLongitud(selectedLongitude); // Longitud seleccionada
                                    geolocalizacion.setRegion(codigoPostal); // Código postal como región

                                    // Guardar el objeto Geolocalizacion en Firestore
                                    db.collection("geolocalizaciones").document(geolocalizacion.getId())
                                            .set(geolocalizacion)
                                            .addOnSuccessListener(aVoid -> {
                                                // Éxito al guardar la geolocalización
                                                clearFields();
                                                setLoadingState(false);
                                                SharedPreferences preferences = getSharedPreferences("Session", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString("id_us", usuario.getId().toString());
                                                editor.apply();
                                                if (rol) {
                                                    Intent intent = new Intent(RegisterActivity.this, Main_Proveedor.class);
                                                    startActivity(intent);
                                                } else {
                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                // Manejo de errores al guardar la geolocalización
                                                Toast.makeText(RegisterActivity.this, "Error al guardar geolocalización: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            });
                                });
                            }
                        } else {
                            // Error en el registro
                         //   Toast.makeText(RegisterActivity.this, "Error al registrar el usuario: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            handleFirebaseAuthError(task.getException());
                            setLoadingState(false);
                        }
                    });
        }catch (Exception e)
        {
            Toast.makeText(RegisterActivity.this, "Error al registrar el usuario: " , Toast.LENGTH_LONG).show();
        }
    }

    private void handleFirebaseAuthError(Exception exception) {
        if (exception instanceof FirebaseAuthUserCollisionException) {
            // Error: el correo ya está registrado
            Toast.makeText(this, "Este correo electrónico ya está registrado. Prueba con otro.", Toast.LENGTH_LONG).show();
        } else if (exception instanceof FirebaseAuthWeakPasswordException) {
            // Error: la contraseña es demasiado débil
            Toast.makeText(this, "La contraseña es demasiado corta. Debe tener al menos 6 caracteres.", Toast.LENGTH_LONG).show();
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            // Error: el formato del correo es incorrecto
            Toast.makeText(this, "El formato del correo electrónico es inválido.", Toast.LENGTH_LONG).show();
        } else {
            // Otros errores genéricos
            Toast.makeText(this, "Error al registrar el usuario: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void clearFields() {
        // Restablece los campos de texto
        tfNombre.setText("");
        tfEmail.setText("");
        tfPhone.setText("");
        tfPassword.setText("");
        tfCodigoPostal.setText("");

        // Restablece la imagen predeterminada
        profileImage.setImageDrawable(defaultd);

        // Limpia la URI de imagen
        imageUri = null;
    }


    private void tomarFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Crear un archivo para almacenar la imagen
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Si el archivo fue creado, obtener su URI y pasarla a la cámara
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.pawpalnetwork.fileprovider", // Cambia esto al nombre de tu paquete
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefijo */
                ".jpg",         /* sufijo */
                storageDir      /* directorio */
        );
    }

    // Callback para manejar la respuesta de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION && resultCode == RESULT_OK && data != null) {
            // Manejo de los datos de ubicación seleccionados en SelectLocationActivity
            selectedLatitude = data.getDoubleExtra("latitude", 0.0);
            selectedLongitude = data.getDoubleExtra("longitude", 0.0);
            String codigoPostal = data.getStringExtra("codigoPostal");

            // Asigna el código postal al campo correspondiente
            tfCodigoPostal.setText(codigoPostal);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Manejo de la captura de imagen desde la cámara
            ImageButton imageV = findViewById(R.id.profile_image);
            imageV.setImageURI(imageUri); // Muestra la imagen usando el URI donde se guardó
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            // Manejo de la selección de imagen desde la galería
            ImageButton imageV = findViewById(R.id.profile_image);
            imageUri = data.getData();
            imageV.setImageURI(imageUri);
        }
    }


}