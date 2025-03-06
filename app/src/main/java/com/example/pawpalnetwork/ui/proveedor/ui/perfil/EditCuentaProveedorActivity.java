package com.example.pawpalnetwork.ui.proveedor.ui.perfil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.RegisterActivity;
import com.example.pawpalnetwork.SelectLocation;
import com.example.pawpalnetwork.bd.Geolocalizacion;
import com.example.pawpalnetwork.bd.GestionBD;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class EditCuentaProveedorActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText editextNombre,  editextEmail, editTextTelefono, editTextUbicacion, editTextEstado;
    private Button btnAceptar, btnUbicacion;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    GestionBD g;
    UsuarioGeneral us;
    private double selectedLatitude;
    private double selectedLongitude;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_cuenta_proveedor);

        editextNombre = findViewById(R.id.tfEditProvProfileName);
        editextEmail = findViewById(R.id.tfEditProvEmail);
        editTextTelefono = findViewById(R.id.tfEditProvTelefono);
        editTextUbicacion = findViewById(R.id.tfCodigoPostal);
        editTextEstado = findViewById(R.id.tfEditProvEstado);
        btnAceptar = findViewById(R.id.btneditaceptar);
        btnAceptar.setOnClickListener(this);
        btnUbicacion = findViewById(R.id.btnUbicacion);
        btnUbicacion.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        g = new GestionBD(storage,db);
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            db.collection("usuarios")  // Asumiendo que los datos están en la colección "usuarios"
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            UsuarioGeneral usuario = documentSnapshot.toObject(UsuarioGeneral.class);
                            if (usuario != null) {
                                editextNombre.setText(usuario.getNombre());
                                editextEmail.setText(usuario.getEmail());
                                editTextTelefono.setText(usuario.getTelefono());
                                editTextUbicacion.setText(usuario.getUbicacion());
                                editTextEstado.setText(usuario.getStatus());
                                us = usuario;
                            }
                        } else {
                            // El usuario no se encuentra en Firestore
                            Log.d("Firestore", "No se encontró el documento del usuario.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Manejo de errores
                        Toast.makeText(EditCuentaProveedorActivity.this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
            editTextUbicacion.setText(codigoPostal);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId()== R.id.btneditaceptar)
        {
            actualizar();
        }
        if(v.getId() == R.id.btnUbicacion)
        {
            Intent intent2 = new Intent(EditCuentaProveedorActivity.this, SelectLocation.class);
            startActivityForResult(intent2, REQUEST_LOCATION);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(EditCuentaProveedorActivity.this, proveedor_Perfil.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditCuentaProveedorActivity.this, proveedor_Perfil.class);
        startActivity(intent);
        finish();
    }

    private void actualizar()
    {
        if (editextNombre.getText().toString().isEmpty() || editextEmail.getText().toString().isEmpty() || editTextTelefono.getText().toString().isEmpty() || editTextUbicacion.getText().toString().isEmpty() || editTextEstado.getText().toString().isEmpty())
        {
            Toast.makeText(EditCuentaProveedorActivity.this, "Ningun dato debe quedarse en blanco", Toast.LENGTH_SHORT).show();
        }else
        {
            if(validaciones())
            {
                if (firebaseUser != null) {
                    firebaseUser.updateEmail(editextEmail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Firebase", "Email verification sent!");
                                        Toast.makeText(EditCuentaProveedorActivity.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                                        us.setNombre(editextNombre.getText().toString());
                                        us.setEmail(editextEmail.getText().toString());
                                        us.setTelefono(editTextTelefono.getText().toString());
                                        us.setStatus(editTextEstado.getText().toString());
                                        us.setUbicacion(editTextUbicacion.getText().toString());
                                        g.editarUsuario(us, null);
                                    } else {
                                        Log.w("Firebase", "Error sending verification email:", task.getException());
                                        Toast.makeText(EditCuentaProveedorActivity.this, "Error sending verification email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        }
    }

    private boolean validaciones()
    {
        String s= "";
        if(!validaLetras(editextNombre.getText().toString()))
        {
            s += "El nombre solo puede tener letras\n";
        }
        if (!validaCorreo(editextEmail.getText().toString())) {
            s += "El email no es valido\n";
        }
        if (!validaTelefono(editTextTelefono.getText().toString())) {
            s += "El telefono no es valido\n";
        }
        if(!s.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditCuentaProveedorActivity.this);
            builder.setMessage(s).setTitle("Verifica tus datos");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return s.isEmpty();
    }

    private boolean validaLetras(String s)
    {
        return s.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ ]+$");
    }

    private boolean validaTelefono(String s)
    {
        return s.matches("^((\\+?\\d+){10,13})$");
    }

    private boolean validaCorreo(String s)
    {
        return s.matches("[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,5}");
    }
}