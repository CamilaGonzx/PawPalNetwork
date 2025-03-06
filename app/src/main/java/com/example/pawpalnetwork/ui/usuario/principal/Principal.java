package com.example.pawpalnetwork.ui.usuario.principal;

import static android.content.Context.MODE_PRIVATE;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawpalnetwork.LoginActivity;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Geolocalizacion;
import com.example.pawpalnetwork.bd.Servicio;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;



public class Principal extends Fragment {

    private PrincipalViewModel mViewModel;
    private RecyclerView rvAnuncios;
    private ServicioAdapter adapter;
    private List<Servicio> serviciosList;
    private EditText etBuscar;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Button btncategory,btnPrecio,btnLimpiar;
    private GoogleMap googleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //\
        // cerrarSesion();
        View root = inflater.inflate(R.layout.fragment_principal, container, false);
        rvAnuncios = root.findViewById(R.id.rvAnuncios);
        etBuscar = root.findViewById(R.id.etbuscar);
        btncategory = root.findViewById(R.id.btncategory);
        btnPrecio= root.findViewById(R.id.btnprice);
        btnLimpiar = root.findViewById(R.id.btnLimpiar);
        serviciosList = new ArrayList<>();
        adapter = new ServicioAdapter(getContext(), serviciosList);
        rvAnuncios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAnuncios.setAdapter(adapter); // Ahora el adaptador estará ini
       // FloatingActionButton fabMap = root.findViewById(R.id.fabmap);
        //fabMap.setVisibility(View.GONE); // Esto ocultará el botón completamente

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
         fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        obtenerUbicacionActual();
        actualizarPosts("");
        btncategory.setOnClickListener(v -> {
            String[] categorias = getResources().getStringArray(R.array.tipo_servicio); // Usamos el array que definiste
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Selecciona una categoría");
            builder.setItems(categorias, (dialog, which) -> {
                String categoriaSeleccionada = categorias[which];
                actualizarPostsPorCategoria(categoriaSeleccionada);
            });
            builder.show();
        });

        btnPrecio.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View priceLayout = getLayoutInflater().inflate(R.layout.dialog_price_range, null); // Crea un layout `dialog_price_range`
            SeekBar seekBarMin = priceLayout.findViewById(R.id.seekBarMin); // Busca los SeekBars
            SeekBar seekBarMax = priceLayout.findViewById(R.id.seekBarMax);
            TextView textMinPrice = priceLayout.findViewById(R.id.textMinPrice); // El TextView para el precio mínimo
            TextView textMaxPrice = priceLayout.findViewById(R.id.textMaxPrice); // El TextView para el precio máximo

            // Establecer valores iniciales en los TextViews
            textMinPrice.setText(String.valueOf(seekBarMin.getProgress()));
            textMaxPrice.setText(String.valueOf(seekBarMax.getProgress()));

            // Configura los listeners para actualizar los precios mientras el usuario mueve los SeekBar
            seekBarMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textMinPrice.setText(String.valueOf(progress)); // Actualiza el precio mínimo en tiempo real
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            seekBarMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textMaxPrice.setText(String.valueOf(progress)); // Actualiza el precio máximo en tiempo real
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            builder.setView(priceLayout)
                    .setTitle("Selecciona rango de precio")
                    .setPositiveButton("Aplicar", (dialog, which) -> {
                        int minPrice = seekBarMin.getProgress();
                        int maxPrice = seekBarMax.getProgress();
                        actualizarPostsPorRangoDePrecio(minPrice, maxPrice); // Llama a tu método para actualizar los anuncios con los nuevos precios
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnLimpiar.setOnClickListener(v -> {
            actualizarPosts("");
        });

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarAnuncios(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        return root;

    }


    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicita el permiso si no está concedido
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Obtener la ubicación del usuario si el permiso ya está concedido
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
               // mostrarMapa(userLocation);
                obtenerServiciosCercanos(userLocation, 1000); // Aquí definimos un radio inicial, como 1000 metros
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, obtener la ubicación
                obtenerUbicacionActual();
            } else {
                // Permiso denegado, muestra un mensaje al usuario o desactiva funciones
                Toast.makeText(getContext(), "Se requiere permiso de ubicación para mostrar servicios cercanos.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void mostrarMapa(LatLng userLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View mapLayout = getLayoutInflater().inflate(R.layout.dialog_map_radius, null);
        MapView mapView = mapLayout.findViewById(R.id.mapView);
        SeekBar seekBarRadius = mapLayout.findViewById(R.id.seekBarRadius);
        TextView radiusLabel = mapLayout.findViewById(R.id.radiusLabel);

        mapView.onCreate(null);
        mapView.onResume();

        // `getMapAsync` es asíncrono, y se llama cuando el mapa está listo
        mapView.getMapAsync(map -> {
            googleMap = map; // Asigna el objeto GoogleMap a la variable de instancia
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            googleMap.addMarker(new MarkerOptions().position(userLocation).title("Tu ubicación"));

            // Añadir el círculo con el radio ajustable en el mapa
            CircleOptions circleOptions = new CircleOptions()
                    .center(userLocation)
                    .radius(seekBarRadius.getProgress() * 100)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF);
            Circle circle = googleMap.addCircle(circleOptions);

            // Cambia el radio del círculo cuando se mueve el seekbar
            seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    circle.setRadius(progress * 100); // Ajusta el radio del círculo
                    radiusLabel.setText("Radio: " + (progress * 100) + " m");
                    obtenerServiciosCercanos(userLocation, progress * 100);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        });

        builder.setView(mapLayout)
                .setTitle("Servicios Cercanos")
                .setPositiveButton("Cerrar", null)
                .show();
    }


    private void obtenerServiciosCercanos(LatLng userLocation, double radio) {
        db.collection("geolocalizacion")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Geolocalizacion geo = document.toObject(Geolocalizacion.class);
                            if (geo != null) {
                                LatLng servicioLocation = new LatLng(geo.getLatitud(), geo.getLongitud());
                                float[] distancia = new float[1];
                                Location.distanceBetween(userLocation.latitude, userLocation.longitude, servicioLocation.latitude, servicioLocation.longitude, distancia);

                                if (distancia[0] <= radio) {
                                    agregarMarcadorServicio(servicioLocation, geo.getUserId());
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Error al obtener servicios cercanos", e));
    }


    private void agregarMarcadorServicio(LatLng location, String serviceName) {
        if (googleMap != null) { // Asegúrate de que el mapa está inicializado
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(serviceName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location)) // Icono personalizado
            );
        }
    }




    private void actualizarPostsPorCategoria(String categoriaSeleccionada) {
        db.collection("servicios")
                .whereEqualTo("status", true) // Filtra solo los servicios activos
                .whereEqualTo("tipo", categoriaSeleccionada) // Filtra por categoría
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Servicio> anunciosFiltrados = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Servicio anuncio = document.toObject(Servicio.class);
                        if (anuncio != null) {
                            anunciosFiltrados.add(anuncio);
                        }
                    }
                    if (adapter == null) {
                        adapter = new ServicioAdapter(getContext(), anunciosFiltrados);
                        rvAnuncios.setAdapter(adapter);
                    } else {
                        adapter.setAnuncios(anunciosFiltrados);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Error al filtrar por categoría", e));
    }

    private void actualizarPostsPorRangoDePrecio(int minPrice, int maxPrice) {
        db.collection("servicios")
                .whereEqualTo("status", true)
                .whereGreaterThanOrEqualTo("precio", minPrice) // Filtra por precio mínimo
                .whereLessThanOrEqualTo("precio", maxPrice) // Filtra por precio máximo
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Servicio> anunciosFiltrados = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Servicio anuncio = document.toObject(Servicio.class);
                        if (anuncio != null) {
                            anunciosFiltrados.add(anuncio);
                        }
                    }
                    if (adapter == null) {
                        adapter = new ServicioAdapter(getContext(), anunciosFiltrados);
                        rvAnuncios.setAdapter(adapter);
                    } else {
                        adapter.setAnuncios(anunciosFiltrados);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Error al filtrar por rango de precio", e));
    }

    private void buscarAnuncios(String query) {
        db.collection("servicios")
                .whereEqualTo("status", true)
                // Filtra el nombre del servicio según el texto introducido
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Servicio> anunciosFiltrados = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Servicio anuncio = document.toObject(Servicio.class);
                        if (anuncio != null && anuncio.getTitulo().toLowerCase().contains(query.toLowerCase())) {
                            anunciosFiltrados.add(anuncio);
                        }
                    }
                    if (adapter == null) {
                        adapter = new ServicioAdapter(getContext(), anunciosFiltrados);
                        rvAnuncios.setAdapter(adapter);
                    } else {
                        adapter.setAnuncios(anunciosFiltrados);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Error al buscar anuncios", e));
    }


    private void mostrarDialogoMapaConRadio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View mapLayout = getLayoutInflater().inflate(R.layout.dialog_map_radius, null);
        MapView mapView = mapLayout.findViewById(R.id.mapView);
        SeekBar seekBarRadius = mapLayout.findViewById(R.id.seekBarRadius);
        TextView radiusLabel = mapLayout.findViewById(R.id.radiusLabel);

        // Inicia el mapa
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(googleMap -> {
            LatLng location = new LatLng(-34, 151); // Define una ubicación inicial
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

            // Añade un círculo en el mapa
            CircleOptions circleOptions = new CircleOptions()
                    .center(location)
                    .radius(seekBarRadius.getProgress() * 100)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF);
            Circle circle = googleMap.addCircle(circleOptions);

            // Cambia el radio del círculo cuando se mueve el seekbar
            seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    circle.setRadius(progress * 100); // Ajusta el radio del círculo
                    radiusLabel.setText("Radio: " + (progress * 100) + " m");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        });

        builder.setView(mapLayout)
                .setTitle("Selecciona el radio de búsqueda")
                .setPositiveButton("Aplicar", (dialog, which) -> {
                    // Lógica para buscar con el radio seleccionado
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    public void actualizarPosts(String query) {
        db.collection("servicios")
                .whereEqualTo("status", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Servicio> anunciosActualizados = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Servicio anuncio = document.toObject(Servicio.class);
                        if (anuncio != null) {
                            Log.i("FragmentoPrincipal", "Anuncio cargado: " + anuncio);

                            anunciosActualizados.add(anuncio);
                        }
                    }
                    if (adapter == null) {
                        adapter = new ServicioAdapter(getContext(), anunciosActualizados);
                        rvAnuncios.setAdapter(adapter);
                    } else {
                        adapter.setAnuncios(anunciosActualizados);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("AnunciosPublicados", "Error al actualizar los posts", e));
    }






    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PrincipalViewModel.class);
        // TODO: Use the ViewModel
    }

}