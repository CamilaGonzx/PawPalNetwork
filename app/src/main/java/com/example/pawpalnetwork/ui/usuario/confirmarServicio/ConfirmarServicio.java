package com.example.pawpalnetwork.ui.usuario.confirmarServicio;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pawpalnetwork.MainActivity;
import com.example.pawpalnetwork.R;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pawpalnetwork.bd.Contratacion;
import com.example.pawpalnetwork.bd.DisponibilidadDia;
import com.example.pawpalnetwork.bd.Horario;
import com.example.pawpalnetwork.bd.Notificacion;
import com.example.pawpalnetwork.bd.Servicio;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfirmarServicio extends AppCompatActivity {

    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvDelDireccion, tvDelFecha,tvServicio,tvPrecio,tvTotal;
    private Button btnObtenerDireccion, btnConfirmarDetalles;
    ImageView imgserv;
    Servicio se;
String userId,fecha,hora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_servicio);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Obtén el UID del usuario actual
        }


        Intent intent = getIntent();
            se = (Servicio) intent.getSerializableExtra("servicio");
            fecha = intent.getStringExtra("fechaSeleccionada");
            hora= intent.getStringExtra("horaSeleccionada");
        tvDelDireccion = findViewById(R.id.tvDelDireccion);
        tvDelFecha = findViewById(R.id.tvDelFecha);
        btnObtenerDireccion = findViewById(R.id.btnObtenerDireccion);
        btnConfirmarDetalles = findViewById(R.id.btnConfirmarDetalles);
imgserv = findViewById(R.id.ivserrvicio);
tvServicio = findViewById(R.id.tvDelservConOne);
tvPrecio = findViewById(R.id.tvDelSevPrecioOne);
tvTotal =findViewById(R.id.tvDelTotal);



        tvDelFecha.setText(fecha+"  "+hora);

        imgserv.setImageResource(imagenServicio(se.getTipo()));
        tvServicio.setText(se.getTipo());
        tvPrecio.setText("$"+se.getPrecio());
tvTotal.setText("$"+se.getPrecio());

        // Configuración del botón para obtener la ubicación
        btnObtenerDireccion.setOnClickListener(v -> obtenerUbicacion());

        // Configuración del botón para confirmar y guardar detalles
        btnConfirmarDetalles.setOnClickListener(v -> confirmarYGuardarDetalles());
    }

    public int imagenServicio(String servicio) {
        Log.e("GestionBD", "El servicio es nulo"+servicio);
        switch (servicio) {
            case "Pasear":
                return R.drawable.pasear;
            case "Entrenamiento":
                return R.drawable.entrenamiento;
            case "Veterinario":
                return R.drawable.veterinario;
            case "Baño y Estetica":
                return R.drawable.banoestetica;
            case "DayCare":
                return R.drawable.daycare;
            default:
                return R.drawable.default_service; // Imagen por defecto si no coincide
        }
    }


    private void obtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            obtenerDireccion(location);
                        } else {
                            Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void obtenerDireccion(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String direccion = address.getAddressLine(0);
                tvDelDireccion.setText(direccion);
            } else {
                Toast.makeText(this, "No se encontró una dirección", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al obtener la dirección", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarYGuardarDetalles() {
        // Datos de ejemplo - Asumimos que tienes estos datos desde la selección anterior
        String idServicio = se.getId();
        String idCliente = userId;
        String idProveedor = se.getProveedorId();
        String fechaSeleccionada = fecha;
        String horaSeleccionada = hora;  // Ejemplo, debes obtener la hora seleccionada
        String direccion = tvDelDireccion.getText().toString();
        double total = se.getPrecio();
        LocalDate fechaHoy = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaHoyFormateada = fechaHoy.format(formatter);
        if (direccion.isEmpty()) {
            Toast.makeText(this, "Por favor, seleccione una dirección", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto de contratación
        Contratacion contratacion = new Contratacion(
                idServicio,
                idCliente,
                idProveedor,
                fechaHoyFormateada,
                fechaSeleccionada,
                horaSeleccionada,  // Ejemplo; ajustar si hay fecha de fin
                direccion,
                1,  // Horas de servicio de ejemplo
                total,
                "pendiente" // Estado inicial
        );

        // Guardar en Firestore
        db.collection("contrataciones")
                .add(contratacion)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Contratación guardada correctamente", Toast.LENGTH_SHORT).show();

                    // Marcar horario como reservado en Firestore
                    actualizarDisponibilidad(idProveedor, fechaSeleccionada, horaSeleccionada);

                    // Crear y guardar la notificación para el proveedor
                    String notificacionId = db.collection("notificaciones").document().getId();
                    String mensaje = "Te han contratado para el servicio: " + se.getTipo();


                    LocalDateTime fechaHoyh = LocalDateTime.now();

// Formateador para la fecha y la hora
                    DateTimeFormatter formatterh = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String fechaHoyFormateadah = fechaHoyh.format(formatterh);

                    Notificacion notificacionProveedor = new Notificacion(
                            notificacionId,
                            mensaje,
                            fechaHoyFormateadah,
                            "Pendiente",
                            documentReference.getId(),
                            contratacion.getIdServicio(),
                            userId,
                            contratacion.getIdProveedor(),// ID de la contratación
                            "PROVEEDOR",
                            "CONTRATACION"
                    );

                    // Guardar la notificación en la colección "notificaciones"
                    db.collection("notificaciones").document(notificacionId)
                            .set(notificacionProveedor)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notificación para proveedor creada"))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error al crear notificación", e));
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar la contratación", Toast.LENGTH_SHORT).show());
    }

    private void actualizarDisponibilidad(String idProveedor, String fecha, String hora) {
        db.collection("usuarios")
                .document(idProveedor)
                .collection("disponibilidad")
                .document(fecha)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        DisponibilidadDia disponibilidadDia = documentSnapshot.toObject(DisponibilidadDia.class);
                        if (disponibilidadDia != null && disponibilidadDia.getHorarios() != null) {
                            // Buscar el horario especificado y actualizar su estado
                            for (Horario horario : disponibilidadDia.getHorarios()) {
                                if (horario.getHora().equals(hora)) {
                                    horario.setReservado(true);
                                    break;
                                }
                            }

                            // Guardar la lista de horarios actualizada
                            db.collection("usuarios")
                                    .document(idProveedor)
                                    .collection("disponibilidad")
                                    .document(fecha)
                                    .set(disponibilidadDia)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Horario actualizado como no disponible", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar la disponibilidad", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "No se encontró disponibilidad para la fecha especificada", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al obtener la disponibilidad", Toast.LENGTH_SHORT).show());

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    // Método para manejar el permiso de ubicación
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
