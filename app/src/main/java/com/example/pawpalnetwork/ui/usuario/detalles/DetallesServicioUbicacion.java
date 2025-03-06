package com.example.pawpalnetwork.ui.usuario.detalles;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Geolocalizacion;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class DetallesServicioUbicacion extends Fragment implements OnMapReadyCallback {

    private DetallesServicioUbicacionViewModel mViewModel;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Servicio servicio;
    private UsuarioGeneral usuario;


    private FirebaseFirestore db;
    private FirebaseStorage storage;
    Geolocalizacion g;

    public static DetallesServicioUbicacion newInstance() {
        return new DetallesServicioUbicacion();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalles_servicio_ubicacion, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        if (getArguments() != null) {
            servicio = (Servicio) getArguments().getSerializable("servicio");
            usuario = (UsuarioGeneral) getArguments().getSerializable("usuario");
        }
        //Log.i("Geolocalizaciones","ENTRO: "+servicio.getProveedorId());
        obtenerGeolocalizacion(servicio.getProveedorId());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DetallesServicioUbicacionViewModel.class);
        // TODO: Use the ViewModel
    }

    private void obtenerGeolocalizacion(String userId) {
        Log.d("DetallesUbicacion", "Intentando obtener geolocalización para userId: " + userId);

        db.collection("geolocalizaciones")
                .whereEqualTo("userId",userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        g = documentSnapshot.toObject(Geolocalizacion.class);

                        // Inicializar el fragmento del mapa
                        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                        if (mapFragment == null) {
                            mapFragment = SupportMapFragment.newInstance();
                            getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
                        }
                        mapFragment.getMapAsync(this);
                    } else {
                        Log.e("DetallesUbicacion", "No se encontró ningún documento con userId: " + userId);
                    }
                })
                .addOnFailureListener(e -> Log.e("DetallesUbicacion", "Error al cargar datos de geolocalización", e));
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Definir la ubicación del servicio
        LatLng ubicacionServicio = new LatLng(g.getLatitud(),g.getLongitud()); // Ejemplo: Buenos Aires, Argentina

        // Colocar marcador en la ubicación
        mMap.addMarker(new MarkerOptions().position(ubicacionServicio).title("Ubicación del Servicio"));

        // Mover la cámara a la ubicación
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionServicio, 15));

        // Opcional: Configurar un área de alcance
        drawServiceRadius(ubicacionServicio, 700);
    }

    private void drawServiceRadius(LatLng location, int radiusInMeters) {
        CircleOptions circleOptions = new CircleOptions()
                .center(location)
                .radius(radiusInMeters)
                .strokeWidth(2)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(50, 0, 0, 255)); // Azul semi-transparente

        mMap.addCircle(circleOptions);
    }
}