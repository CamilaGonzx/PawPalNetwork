package com.example.pawpalnetwork.ui.usuario.agendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.DisponibilidadDia;
import com.example.pawpalnetwork.bd.Horario;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.example.pawpalnetwork.ui.usuario.confirmarServicio.ConfirmarServicio;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Agendar extends AppCompatActivity {

    private UsuarioGeneral us;
    private Servicio se;
    private FirebaseFirestore db;
    private ImageButton btnRegresar;
    private TextView tvTitulo, tvNombreProveedor, tvServicio, tvCalificacion, tvDistancia, tvHorariosDisponibles;
    private ImageView imgProveedor;
    private RecyclerView recyclerViewFechas, recyclerViewHorarios;
    private Button btnSiguiente;
    private FechaAdapter fechaAdapter;
    private HorarioAdapter horarioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Obtén datos del intent
        Bundle bundle = getIntent().getExtras();
        se = (Servicio) bundle.getSerializable("servicio");
        us = (UsuarioGeneral) bundle.getSerializable("usuario");

        // Inicializa Firebase
        db = FirebaseFirestore.getInstance();

        // Inicializa elementos de la UI
        btnRegresar = findViewById(R.id.btnRegresar);
        tvTitulo = findViewById(R.id.tvTitulo);
        imgProveedor = findViewById(R.id.imgProveedor);
        tvNombreProveedor = findViewById(R.id.tvNombreProveedor);
        tvServicio = findViewById(R.id.tvServicio);
        tvCalificacion = findViewById(R.id.tvCalificacion);
        tvDistancia = findViewById(R.id.tvDistancia);
        recyclerViewFechas = findViewById(R.id.recyclerViewFechas);
        recyclerViewHorarios = findViewById(R.id.recyclerViewHorarios);
        tvHorariosDisponibles = findViewById(R.id.tvHorariosDisponibles);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        // Cargar datos en UI
        Glide.with(imgProveedor.getContext())
                .load(us.getFotoPerfil())
                .into(imgProveedor);
        tvNombreProveedor.setText(us.getNombre());
        tvServicio.setText(se.getTipo());
        tvCalificacion.setText("★  " + us.getRating());
        tvDistancia.setText(us.getUbicacion());

        // Generar datos de prueba
       DatosPrueba p = new DatosPrueba(db);
        p.cargarDatosDePrueba(se.getProveedorId());

        // Configurar RecyclerView para fechas
        List<Map<String, String>> fechas = obtenerProximos7Dias();
        fechaAdapter = new FechaAdapter(fechas, this::mostrarHorariosDisponibles);
        recyclerViewFechas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFechas.setAdapter(fechaAdapter);

        // Configurar RecyclerView para horarios
        horarioAdapter = new HorarioAdapter(new ArrayList<>());
        recyclerViewHorarios.setLayoutManager(new GridLayoutManager(this, 5));
        recyclerViewHorarios.setAdapter(horarioAdapter);


        btnSiguiente.setOnClickListener(v -> {
            String selectedFecha  = fechaAdapter.getSelectedFecha();
            Horario selectedHorario = horarioAdapter.getSelectedHorario();

            if (selectedFecha != null && selectedHorario != null) {
                String fechaSeleccionada = selectedFecha;
                String horaSeleccionada = selectedHorario.getHora();

                // Crear Intent o Bundle según el caso
                Intent intent = new Intent(this, ConfirmarServicio.class);
                intent.putExtra("fechaSeleccionada", fechaSeleccionada);
                intent.putExtra("horaSeleccionada", horaSeleccionada);
                intent.putExtra("servicio",se);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Por favor selecciona una fecha y hora", Toast.LENGTH_SHORT).show();
            }
        });
        btnRegresar.setOnClickListener(v -> {
                finish();
        });
    }

    private List<Map<String, String>> obtenerProximos7Dias() {
        List<Map<String, String>> fechas = new ArrayList<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault()); // Ej: Lun
        SimpleDateFormat dateFormatFirebase = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()); // Para Firebase
        SimpleDateFormat dateFormatDisplay = new SimpleDateFormat("dd/MM", Locale.getDefault()); // Para mostrar en interfaz

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            Map<String, String> fecha = new HashMap<>();
            fecha.put("day", dayFormat.format(calendar.getTime()));              // Ej: Lun
            fecha.put("dateDisplay", dateFormatDisplay.format(calendar.getTime())); // Ej: 10/11
            fecha.put("dateFirebase", dateFormatFirebase.format(calendar.getTime())); // Ej: 10-11-2024
            fechas.add(fecha);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return fechas;
    }


    private void mostrarHorariosDisponibles(String fechaFirebase) {
        Log.d("ConsultaFirebase", "Consultando disponibilidad para la fecha: " + fechaFirebase); // Agregar log para verificar la fecha

        db.collection("usuarios")
                .document(se.getProveedorId())
                .collection("disponibilidad")
                .document(fechaFirebase)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        DisponibilidadDia disponibilidadDia = documentSnapshot.toObject(DisponibilidadDia.class);
                        if (disponibilidadDia != null && disponibilidadDia.getHorarios() != null) {
                            List<Horario> horariosDisponibles = new ArrayList<>();
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat dateFormatFecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            String fechaActualStr = dateFormatFecha.format(calendar.getTime());
                            int horaActual = calendar.get(Calendar.HOUR_OF_DAY); // Obtener la hora en formato 24h
                                for (Horario horario : disponibilidadDia.getHorarios()) {
                                    if (!horario.isReservado()) {

                                        if (disponibilidadDia.getFecha().equals(fechaActualStr)) {
                                            String[] partesHora = horario.getHora().split(":");
                                            int horaHorario = Integer.parseInt(partesHora[0]);
                                            if (horaHorario > horaActual) {

                                                horariosDisponibles.add(horario); // Agregar solo si es posterior a la hora actual
                                            }
                                        } else {
                                            horariosDisponibles.add(horario);
                                        }
                                    }

                                    // Actualizar el adaptador con los horarios disponibles
                                    horarioAdapter.actualizarHorarios(horariosDisponibles);

                                }


                        } else {
                            Toast.makeText(this, "No se encontraron horarios para la fecha seleccionada", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar los horarios", Toast.LENGTH_SHORT).show());
    }




}