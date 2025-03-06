package com.example.pawpalnetwork;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.example.pawpalnetwork.ui.admin.Administrador;
import com.example.pawpalnetwork.ui.proveedor.Main_Proveedor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    ImageView ivMainImage;
    TextView tvBienvenido;
    TextView tvIngresaCuenta;
    ImageView ivAdditionalImage;
    TextInputEditText tfEmailOrPhone;
    TextInputEditText tfPassword;
    CheckBox cbRecordarme;
    TextView tvOlvidarContrasena;
    Button btnIngresar;
    TextView tvNoCuenta;
    TextView tvCrearCuenta;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FirebaseApp.initializeApp(this);
         db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true) // Habilitar persistencia sin conexión
                .build();
        db.setFirestoreSettings(settings);
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        ivMainImage = findViewById(R.id.ivMainImage);
        tvBienvenido = findViewById(R.id.tvBienvenido);
        tvIngresaCuenta = findViewById(R.id.tvIngresaCuenta);
        ivAdditionalImage = findViewById(R.id.ivAdditionalImage);
        tfEmailOrPhone = findViewById(R.id.tfEmailOrPhone);
        tfPassword = findViewById(R.id.tfPassword);
        cbRecordarme = findViewById(R.id.cbRecordarme);
        tvOlvidarContrasena = findViewById(R.id.tvOlvidarContrasena);
        btnIngresar = findViewById(R.id.btnIngresar);
        tvNoCuenta = findViewById(R.id.tvNoCuenta);
        tvCrearCuenta = findViewById(R.id.tvCrearCuenta);
        SharedPreferences preferences = getSharedPreferences("Session", MODE_PRIVATE);
        String uid = preferences.getString("uid", null);
        if (uid != null) {
            obtenerRolDelUsuario(uid); // Redirige automáticamente si hay sesión
        }

        askNotificationPermission();
        tvCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ElegirRol.class);
                startActivity(intent);
            }
        });

        btnIngresar.setOnClickListener(v -> iniciarSesion());
        ;

    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    private void iniciarSesion() {
        String email = tfEmailOrPhone.getText().toString().trim();
        String password = tfPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No hay conexión a Internet. Intente nuevamente cuando esté conectado.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.equals("admin@paw.mx") && password.equals("admin12345")) {
            Intent intent = new Intent(LoginActivity.this, Administrador.class); // Cambia AdminActivity a tu actividad de administrador
            startActivity(intent);
            finish(); // Cerrar LoginActivity para que no se pueda regresar
            return;
        }
        // Autenticación con Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();  // Obtén el UID del usuario autenticado
                            guardarSesion(uid);
                            obtenerRolDelUsuario(user.getUid()); // Obtener datos adicionales de Firestore
                        }
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void obtenerRolDelUsuario(String uid) {
        db.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UsuarioGeneral usuario = documentSnapshot.toObject(UsuarioGeneral.class);
                        if (usuario != null) {
                            redirigirSegunRol(usuario.isRol());
                        }
                    } else {
                        Toast.makeText(this, "No se encontró la información del usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener datos del usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    private void redirigirSegunRol(boolean rol) {
        if (rol) {
            // Redirigir al proveedor
            Intent intent = new Intent(LoginActivity.this, Main_Proveedor.class);

            startActivity(intent);
        } else {
            // Redirigir al usuario
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish(); // Cerrar LoginActivity para que no se regrese al login al presionar atrás
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void guardarSesion(String uid) {
        SharedPreferences preferences = getSharedPreferences("Session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("uid", uid);
        editor.apply();
    }


    private void askNotificationPermission() {
        // Solo es necesario para el nivel de API >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // El SDK de FCM (y tu aplicación) pueden enviar notificaciones.
                Log.d("Permissions", "Permiso de notificaciones ya concedido.");
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Muestra una UI educativa explicando por qué se necesita el permiso
                new AlertDialog.Builder(this)
                        .setTitle("Permiso de Notificaciones")
                        .setMessage("Para recibir notificaciones sobre tus servicios, permite las notificaciones.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Solicita el permiso cuando el usuario selecciona "OK"
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        })
                        .setNegativeButton("No gracias", (dialog, which) -> {
                            // El usuario ha rechazado el permiso
                            Log.d("Permissions", "El usuario ha seleccionado no permitir las notificaciones.");
                        })
                        .show();
            } else {
                // Solicita el permiso directamente sin mostrar una UI educativa
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

}