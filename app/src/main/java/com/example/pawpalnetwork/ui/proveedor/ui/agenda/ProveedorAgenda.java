package com.example.pawpalnetwork.ui.proveedor.ui.agenda;

import static android.content.Context.MODE_PRIVATE;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Contratacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ProveedorAgenda extends Fragment {

    private ProveedorAgendaViewModel mViewModel;
    private Calendar calendar;
    private ImageView btnPrevMonth;
    private TextView tvMonth;
    private ImageView btnNextMonth;

    private ImageView btnPrevWeek;
    private RecyclerView recyclerViewWeek;
    private ImageView btnNextWeek;

    private TextView tvDayDetailsTitle;
    private RecyclerView recyclerViewDayDetails;

    private DiaAdapter diaAdapter;
String userId;
    private List<Dia> diasList;
    View root;

    public static ProveedorAgenda newInstance() {
        return new ProveedorAgenda();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_proveedor_agenda, container, false);

        calendar = Calendar.getInstance(); // Inicializa con la fecha actual
        btnPrevMonth = root.findViewById(R.id.btnPrevMonth);
        tvMonth = root.findViewById(R.id.tvMonth);
        btnNextMonth = root.findViewById(R.id.btnNextMonth);

        // Inicializar la sección de la semana actual
        btnPrevWeek = root.findViewById(R.id.btnPrevWeek);
        recyclerViewWeek = root.findViewById(R.id.recyclerViewWeek);
        btnNextWeek = root.findViewById(R.id.btnNextWeek);
       recyclerViewWeek.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        diasList = new ArrayList<>();
        diaAdapter = new DiaAdapter(diasList, dia -> {
            // Obtener el rango de tiempo para el día seleccionado
            Calendar inicioDia = Calendar.getInstance();
            inicioDia.set(Calendar.DAY_OF_MONTH, dia.getDayNumber());
            inicioDia.set(Calendar.HOUR_OF_DAY, 0);
            inicioDia.set(Calendar.MINUTE, 0);
            inicioDia.set(Calendar.SECOND, 0);
            inicioDia.set(Calendar.MILLISECOND, 0);
            Calendar finDia = (Calendar) inicioDia.clone();
            finDia.add(Calendar.DAY_OF_MONTH, 1);

            long fechaInicioDia = inicioDia.getTimeInMillis();

           // Log.e("Fechas","FechaInicioDIa: "+fechaInicioDia);
            // Cargar las contrataciones para este día específico
            cargarContratacionesDelDia(fechaInicioDia);
        });

        recyclerViewWeek.setAdapter(diaAdapter);  // Configura el adaptador en el RecyclerView

        // Inicializar la sección de detalles del día seleccionado
        tvDayDetailsTitle = root.findViewById(R.id.tvDayDetailsTitle);
        recyclerViewDayDetails = root.findViewById(R.id.recyclerViewDayDetails);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Obtén el UID del usuario actual
        }

        recyclerViewDayDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewDayDetails.setAdapter(new DiaAdapter(new ArrayList<>(), dia -> {
            // Lógica para mostrar detalles del día seleccionado
        }));

        // Configura la navegación de mes y semana
        setupMesNavigation();
        setupSemanaNavigation();

        // Actualiza el mes y los días de la semana actual
        actualizarMes();
        actualizarDiasSemana();
        return root;
    }


    private void setupMesNavigation() {


        btnPrevMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1); // Retrocede un mes
            actualizarMes();
            actualizarDiasSemana(); // Actualiza la semana para el nuevo mes
        });

        btnNextMonth.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1); // Avanza un mes
            actualizarMes();
            actualizarDiasSemana();
        });
    }


    private void setupSemanaNavigation() {

        btnPrevWeek.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, -1); // Retrocede una semana
            actualizarDiasSemana();
        });

        btnNextWeek.setOnClickListener(v -> {
            calendar.add(Calendar.WEEK_OF_YEAR, 1); // Avanza una semana
            actualizarDiasSemana();
        });
    }
    private void actualizarDiasSemana() {
        // Borra la lista de días antes de actualizar
        diasList.clear();

        Calendar semanaInicio = (Calendar) calendar.clone(); // Clona la fecha actual
        semanaInicio.set(Calendar.DAY_OF_WEEK, semanaInicio.getFirstDayOfWeek()); // Mueve al primer día de la semana
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault()); // Formato para el nombre abreviado del día

        int mesActual = semanaInicio.get(Calendar.MONTH); // Guardar el mes actual

        for (int i = 0; i < 7; i++) {
            int diaNumero = semanaInicio.get(Calendar.DAY_OF_MONTH);
            String diaNombre = dayFormat.format(semanaInicio.getTime()).toUpperCase(); // Ejemplo: "LUN", "MAR", etc.

            // Verifica si el día tiene contrataciones o eventos
            boolean tieneContratacion = verificarContratacion(diaNumero, semanaInicio.get(Calendar.MONTH), semanaInicio.get(Calendar.YEAR));
            Log.i("AGENDA", "Dia " + diaNumero);

            // Agrega el día a la lista
            diasList.add(new Dia(diaNumero, diaNombre, tieneContratacion));

            // Avanza al siguiente día
            semanaInicio.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Verificar si el mes ha cambiado
        int nuevoMes = semanaInicio.get(Calendar.MONTH); // Obtener el mes después de llenar la semana
        if (nuevoMes != mesActual) {
            actualizarMes(); // Actualizar el mes en la interfaz si ha cambiado
        }

        Log.i("AnuncioAdapter", "Tamaño de listaAnuncios en getItemCount: " + diasList.size());

        // Notifica al adaptador sobre los cambios en los datos
        diaAdapter.notifyDataSetChanged();
    }

    private void cargarContratacionesDelDia(long fechaInicioDia) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Filtrar contrataciones por el rango de fechas del día seleccionado
        db.collection("contrataciones")
                .whereEqualTo("idProveedor",userId)
                .whereEqualTo("fechaInicio",fechaInicioDia)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Contratacion> contrataciones = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Contratacion contratacion = document.toObject(Contratacion.class);
                            contrataciones.add(contratacion);
                        }


                        contrataciones.sort(Comparator.comparingLong(contratacion -> Long.parseLong(contratacion.getFechaInicio())));

                        // Actualizar el adaptador con las contrataciones ordenadas
                        ContratacionAdapter contratacionAdapter = new ContratacionAdapter(contrataciones);
                        recyclerViewDayDetails.setAdapter(contratacionAdapter);
                    } else {
                        Log.e("FirebaseError", "Error obteniendo contrataciones", task.getException());
                    }
                });
    }


    private boolean verificarContratacion(int dia, int mes, int anio) {
        // Aquí puedes implementar la lógica para verificar si hay contrataciones para ese día
        // Puede ser una consulta a tu base de datos o Firestore
        return false; // Cambia esto con tu lógica de verificación real
    }

    private void actualizarMes() {
        String mes = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        int year = calendar.get(Calendar.YEAR);
        tvMonth.setText(String.format("%s %d", mes, year));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProveedorAgendaViewModel.class);
        // TODO: Use the ViewModel
    }

}