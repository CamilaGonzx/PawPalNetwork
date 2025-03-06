package com.example.pawpalnetwork.ui.admin.ui.editarusuario;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.SelectLocation;
import com.example.pawpalnetwork.bd.Geolocalizacion;
import com.example.pawpalnetwork.bd.GestionBD;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.example.pawpalnetwork.ui.proveedor.ui.perfil.proveedor_Perfil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditarUsuarioFragment extends Fragment {

    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CODE_MEDIA_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private static final int REQUEST_LOCATION = 1;

    AutoCompleteTextView tvRol;
    ImageButton profileImage;
    TextInputEditText tfNombre, tfEmail, tfPhone, tfPassword, tfCodigoPostal, tvId;
    Button btnContinuar, btnUbicacion, btnbuscar;
    Uri imageUri;
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;
    boolean rol;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private UsuarioGeneral us;

    public static EditarUsuarioFragment newInstance() {
        return new EditarUsuarioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_editar_usuario, container, false);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        profileImage = root.findViewById(R.id.profile_image_edit_admin);
        tfNombre = root.findViewById(R.id.tfNombre_edit_admin);
        tfEmail = root.findViewById(R.id.tfEmail_edit_admin);
        tfPhone = root.findViewById(R.id.tfPhone_edit_admin);
        tfPassword = root.findViewById(R.id.tfPassword_edit_admin);
        tfCodigoPostal = root.findViewById(R.id.tfCodigoPostal_edit_admin);
        tvRol = root.findViewById(R.id.tvRol_edit_admin);
        tvId = root.findViewById(R.id.etedid);
        btnbuscar = root.findViewById(R.id.btnedBuscar);
        btnContinuar = root.findViewById(R.id.btnRegistro_edit_admin);
        btnUbicacion = root.findViewById(R.id.btnUbicacione);

        btnbuscar.setOnClickListener(v -> buscarUsuario());
        btnContinuar.setOnClickListener(v -> actualizarUsuarioEnFirestore());
        btnUbicacion.setOnClickListener(v -> {
            Intent intent = new Intent(root.getContext(), SelectLocation.class);
            startActivityForResult(intent, REQUEST_LOCATION);
        });

        return root;
    }

    private void buscarUsuario() {
        String email = tvId.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, ingrese un correo electrónico para buscar", Toast.LENGTH_LONG).show();
            return;
        }

        desactivarCampos(true);

        db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    desactivarCampos(false);
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        us = document.toObject(UsuarioGeneral.class);

                        if (us != null) {
                            tfNombre.setText(us.getNombre());
                            tfEmail.setText(us.getEmail());
                            tfPhone.setText(us.getTelefono());
                            tfPassword.setText(us.getContrasena());
                            tvRol.setText(us.isRol() ? "Proveedor" : "Usuario");
                            tfCodigoPostal.setText(us.getUbicacion());

                            Glide.with(profileImage.getContext())
                                    .load(us.getFotoPerfil())
                                    .into(profileImage);
                        }
                    } else {
                        Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    desactivarCampos(false);
                    Toast.makeText(getContext(), "Error en la búsqueda: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void actualizarUsuarioEnFirestore() {
        if (us == null) {
            Toast.makeText(getContext(), "Primero busque un usuario para editar", Toast.LENGTH_LONG).show();
            return;
        }

        String nombre = tfNombre.getText().toString().trim();
        String email = tfEmail.getText().toString().trim();
        String telefono = tfPhone.getText().toString().trim();
        String contrasena = tfPassword.getText().toString().trim();
        String codigoPostal = tfCodigoPostal.getText().toString().trim();
        boolean rol = tvRol.getText().toString().equals("Proveedor");

        // Validación de campos
        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty() || contrasena.isEmpty() || codigoPostal.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Correo electrónico no válido", Toast.LENGTH_LONG).show();
            return;
        }

        desactivarCampos(true);

        // Mapa de datos del usuario para actualizar en Firestore
        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("nombre", nombre);
        usuarioMap.put("email", email);
        usuarioMap.put("telefono", telefono);
        usuarioMap.put("contrasena", contrasena);
        usuarioMap.put("ubicacion", codigoPostal);
        usuarioMap.put("rol", rol);

        // Actualizar el documento del usuario en Firestore
        db.collection("usuarios").document(us.getId())
                .update(usuarioMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Usuario actualizado correctamente.", Toast.LENGTH_LONG).show();

                    if (selectedLatitude != 0.0 && selectedLongitude != 0.0) {
                        Map<String, Object> geoMap = new HashMap<>();
                        geoMap.put("userId", us.getId());
                        geoMap.put("latitud", selectedLatitude);
                        geoMap.put("longitud", selectedLongitude);
                        geoMap.put("region", codigoPostal);

                        db.collection("geolocalizaciones").document(us.getId())
                                .set(geoMap)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(getContext(), "Geolocalización actualizada correctamente.", Toast.LENGTH_LONG).show();
                                    limpiarCampos();
                                    desactivarCampos(false);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Error al actualizar geolocalización: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    desactivarCampos(false);
                                });
                    } else {
                        limpiarCampos();
                        desactivarCampos(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al actualizar usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    desactivarCampos(false);
                });
    }

    private void desactivarCampos(boolean desactivar) {
        tfNombre.setEnabled(!desactivar);
        tfEmail.setEnabled(!desactivar);
        tfPhone.setEnabled(!desactivar);
        tfPassword.setEnabled(!desactivar);
        tfCodigoPostal.setEnabled(!desactivar);
        tvRol.setEnabled(!desactivar);
        profileImage.setEnabled(!desactivar);
        btnContinuar.setEnabled(!desactivar);
        btnbuscar.setEnabled(!desactivar);
        btnUbicacion.setEnabled(!desactivar);
    }

    private void limpiarCampos() {
        tfNombre.setText("");
        tfEmail.setText("");
        tfPhone.setText("");
        tfPassword.setText("");
        tfCodigoPostal.setText("");
        tvRol.setText("");
        tvId.setText("");
        profileImage.setImageResource(R.drawable.ic_defaultprof);
        imageUri = null;
        selectedLatitude = 0.0;
        selectedLongitude = 0.0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION && resultCode == RESULT_OK && data != null) {
            selectedLatitude = data.getDoubleExtra("latitude", 0.0);
            selectedLongitude = data.getDoubleExtra("longitude", 0.0);
            String codigoPostal = data.getStringExtra("codigoPostal");
            tfCodigoPostal.setText(codigoPostal);
        } else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                profileImage.setImageURI(imageUri);
            }
        }
    }

    // Métodos para manejar la cámara y galería van aquí
}


