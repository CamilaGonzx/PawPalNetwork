package com.example.pawpalnetwork.ui.proveedor.ui.perfil;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.pawpalnetwork.LoginActivity;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.GestionBD;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.example.pawpalnetwork.ui.usuario.perfil.Perfil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class proveedor_Perfil extends AppCompatActivity implements View.OnClickListener{

    private ImageView foto_perfil, edit_profile, foto_edit;
    private TextView nombre, correo;
    private View cambiar_contrasenia, borrar_cuenta, cerrar_sesion, edit_cuenta, reviews, configuracion, acercade;
    private TextInputEditText editextNombre,  editextEmail, editTextCurrentPasswrd, editTextNewPass , editTextConfPass;
    private SharedPreferences sharedPreferences, preferences;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    GestionBD g;
    UsuarioGeneral us;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    private static final int REQUEST_CODE_MEDIA_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proveedor_perfil);

        //COMPONENTES
        foto_perfil = findViewById(R.id.ivProfilePicture);
        edit_profile = findViewById(R.id.ivEditProfile);
        edit_profile.setOnClickListener(this);
        cambiar_contrasenia = findViewById(R.id.llEditContrasenia);
        cambiar_contrasenia.setOnClickListener(this);
        borrar_cuenta = findViewById(R.id.llEditBorrarCuenta);
        borrar_cuenta.setOnClickListener(this);
        cerrar_sesion = findViewById(R.id.llEditCerrarSesion);
        cerrar_sesion.setOnClickListener(this);
        edit_cuenta = findViewById(R.id.llEditCuenta);
        edit_cuenta.setOnClickListener(this);
        reviews = findViewById(R.id.llEditReviews);
        reviews.setOnClickListener(this);
        configuracion = findViewById(R.id.llEditConfiguracion);
        configuracion.setOnClickListener(this);
        acercade = findViewById(R.id.llEditAcercade);
        acercade.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        nombre = findViewById(R.id.tvProfileName);
        correo = findViewById(R.id.tvUsername);

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
                                nombre.setText(usuario.getNombre());
                                correo.setText(usuario.getEmail());
                                us = usuario;
                                Glide.with(foto_perfil.getContext())
                                        .load(usuario.getFotoPerfil())
                                        .into(foto_perfil);
                            }
                        } else {
                            // El usuario no se encuentra en Firestore
                            Log.d("Firestore", "No se encontró el documento del usuario.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Manejo de errores
                        Toast.makeText(proveedor_Perfil.this, "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.ivEditProfile)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);

            editextNombre = dialogView.findViewById(R.id.tfEditProfileName);
            editextEmail = dialogView.findViewById(R.id.ivEditEmail);
            foto_edit = dialogView.findViewById(R.id.ivEditProfilePicture);

            foto_edit.setOnClickListener(v1 -> mostrarOpcionesImagen());

            //recuperacion de la base
            editextNombre.setText(us.getNombre());
            editextEmail.setText(us.getEmail());
            Glide.with(foto_edit.getContext())
                    .load(us.getFotoPerfil())
                    .into(foto_edit);

            builder.setView(dialogView)
                    .setTitle("Editar Perfil")
                    .setPositiveButton( "Aceptar", (dialog, which) -> actualizar_perfil())
                    .setNegativeButton("Cerrar", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (v.getId() == R.id.llEditCuenta)
        {
            Intent intent = new Intent(proveedor_Perfil.this, EditCuentaProveedorActivity.class);
            startActivity(intent);
            finish();
        }else if (v.getId() == R.id.llEditContrasenia)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_change_password, null);

            editTextCurrentPasswrd = dialogView.findViewById(R.id.tfcurrentPassword);
            editTextNewPass = dialogView.findViewById(R.id.tfnewPassword);
            editTextConfPass = dialogView.findViewById(R.id.tfConfirmnewPassword);
            Button btn_cambiar_Password = dialogView.findViewById(R.id.btnEditCambiarPassword);

            btn_cambiar_Password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (firebaseUser != null) {
                        String currentPassword = editTextCurrentPasswrd.getText().toString();

                        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPassword);
                        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (editTextNewPass.getText().toString().equals(editTextConfPass.getText().toString())) {
                                        firebaseUser.updatePassword(editTextNewPass.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            us.setContrasena(editTextNewPass.getText().toString());
                                                            g.editarUsuario(us,null);
                                                            Toast.makeText(proveedor_Perfil.this, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.w("FirebaseAuth", "Error al actualizar la contraseña.", task.getException());
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(proveedor_Perfil.this, "No coinciden las contraseñas", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(proveedor_Perfil.this, "Contraseña anterior incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.w("FirebaseAuth", "No hay usuario autenticado.");
                    }
                }
            });

            builder.setView(dialogView)
                    .setTitle("Editar contraseña")
                    .setPositiveButton("Cerrar", null)
                    .show();
        } else if (v.getId() == R.id.llEditReviews)
        {
            Intent intent = new Intent(proveedor_Perfil.this, EditReviewsProveedorActivity.class);
            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.llEditBorrarCuenta)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Borrar cuenta")
                    .setMessage("Esta accion borrara tu cuenta para siempre, ¿Deseas continuar?")
                    .setNegativeButton("Cancelar", null)
                    .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            borrar_cuenta();
                        }
                    })
                    .show();
        } else if (v.getId() == R.id.llEditCerrarSesion)
        {
            cerrar_sesion();
        } else if (v.getId() == R.id.llEditConfiguracion)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_configuraciones, null);

            sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE);
            RadioButton ap_blanco = dialogView.findViewById(R.id.apariencia_blanco);
            RadioButton ap_obscura = dialogView.findViewById(R.id.apariencia_obscura);
            RadioButton ap_predefinida = dialogView.findViewById(R.id.apariencia_predefinida);

            int themeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            switch (themeMode) {
                case AppCompatDelegate.MODE_NIGHT_NO:
                    ap_blanco.setChecked(true);
                    break;
                case AppCompatDelegate.MODE_NIGHT_YES:
                    ap_obscura.setChecked(true);
                    break;
                case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                    ap_predefinida.setChecked(true);
                    break;
            }

            RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group_ap);
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.apariencia_blanco) {
                    setThemeMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (checkedId == R.id.apariencia_obscura) {
                    setThemeMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (checkedId == R.id.apariencia_predefinida) {
                    setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
            });

            builder.setView(dialogView)
                    .setTitle("Configuraciones")
                    .setNegativeButton("Regresar", null)
                    .setPositiveButton("Aceptar", null)
                    .show();
        } else if (v.getId() == R.id.llEditAcercade)
        {
            getCreditos();
        }
    }

    public void cargar_contenedor_imagen(ImageView contenedor, Uri imUri)
    {
        contenedor.setImageURI(imUri);
    }

    private void actualizar_perfil()
    {
        if (editextNombre.getText().toString().isEmpty() || editextEmail.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Ningun dato debe quedarse en blanco", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(proveedor_Perfil.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                                        us.setNombre(editextNombre.getText().toString());
                                        us.setEmail(editextEmail.getText().toString());
                                        g.editarUsuario(us, imageUri);
                                    } else {
                                        Log.w("Firebase", "Error sending verification email:", task.getException());
                                        Toast.makeText(proveedor_Perfil.this, "Error sending verification email", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        }
    }

    public void borrar_cuenta()
    {
        if (firebaseUser != null) {
            firebaseUser.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            g.eliminarUsuario(us.getId(),true);
                            Toast.makeText(getApplicationContext(), "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                            cerrar_sesion();
                        } else {
                            Toast.makeText(getApplicationContext(), "No se pudo eliminar el usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
//        Intent intent = new Intent(proveedor_Perfil.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
    }

    private void cerrar_sesion() {

        FirebaseAuth.getInstance().signOut();

        SharedPreferences preferences =this.getSharedPreferences("Session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("uid");
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el historial de actividades
        startActivity(intent);

    }

    //CAMARA
    private void mostrarOpcionesImagen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen")
                .setItems(new CharSequence[]{"Tomar foto", "Seleccionar de galería"}, (dialog, which) -> {
                    if (which == 0) {
                        mCaptura();
                    } else {
                        seleccionar_imagen();
                    }
                })
                .show();
    }

    //Tomar foto
    @Deprecated
    public void mCaptura()
    {
        if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_MEDIA_PERMISSION);
        } else {
            openCamera();
        }
    }

    public void openCamera()
    {
        ContentValues values = new ContentValues();
        String s1= "Nohay";
        values.put("title", "Id"+ s1);
        imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
                                cargar_contenedor_imagen(foto_edit, imageUri);
                            }
                        }
                    }
            );

    //Cargar imagen desde galeria
    public void seleccionar_imagen()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_MEDIA_PERMISSION);
            } else {
                openGallery();
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                cargar_contenedor_imagen(foto_edit, imageUri);
            }
        }
    }

    //Misc
    public void getCreditos()
    {
        new AlertDialog.Builder(this)
                .setTitle("Acerca de ")
                .setMessage(
                        "Camila Ximena Gonzalez Alvarez \n" +

                        "Version 1.0 2024 ")
                .setPositiveButton("Aceptar", null).show();
    }

    private void setThemeMode(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
        sharedPreferences.edit().putInt("theme_mode", mode).apply();
        recreate(); // Recargar la actividad para aplicar el nuevo tema
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
        if(!s.isEmpty())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(proveedor_Perfil.this);
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

    private boolean validaCorreo(String s)
    {
        return s != null && Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }
}