package com.example.pawpalnetwork.ui.admin.ui.eliminarusuario;

import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.GestionBD;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EliminarUsuarioFragment extends Fragment {

    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CODE_MEDIA_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    ImageButton profileImage;
    TextInputEditText tvRol;
    TextInputEditText tfNombre;
    TextInputEditText tfEmail;
    TextInputEditText tfPhone;
    TextInputEditText tfPassword;
    TextInputEditText tfCodigoPostal;
    TextInputEditText tvId;
    Button btnContinuar;
    Button btnbuscar;

    Uri imageUri;
    GestionBD gestionBD;
    UsuarioGeneral us;
    boolean rol;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    public static EliminarUsuarioFragment newInstance() {
        return new EliminarUsuarioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_eliminar_usuario, container, false);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        gestionBD = new GestionBD(storage, db);
        profileImage = root.findViewById(R.id.profile_image_eliminar_admin);
        tfNombre = root.findViewById(R.id.tfNombre_eliminar_admin);
        tfEmail = root.findViewById(R.id.tfEmail_eliminar_admin);
        tfPhone = root.findViewById(R.id.tfPhone_eliminar_admin);
        tfPassword = root.findViewById(R.id.tfPassword_eliminar_admin);
        tfCodigoPostal = root.findViewById(R.id.tfCodigoPostal_eliminar_admin);
        tvRol = root.findViewById(R.id.tvRol_eliminar_admin);
        tvId = root.findViewById(R.id.et_eliminar_id);
        btnbuscar = root.findViewById(R.id.btnBuscar_eliminar);
        btnContinuar = root.findViewById(R.id.btnRegistro_eliminar_admin);

        btnbuscar.setOnClickListener(v -> {
            buscarUsuario();
        });

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // gestionBD.eliminarUsuario(us.getId(),);
                limpiarCampos();
            }
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
        tvId.setText("");
        profileImage.setImageResource(R.drawable.ic_defaultprof);
        imageUri = null;

    }
    private void buscarUsuario() {
        String email = tvId.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, ingrese un correo electrónico para buscar", Toast.LENGTH_LONG).show();
            return;
        }



        db.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {

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

                    Toast.makeText(getContext(), "Error en la búsqueda: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

}