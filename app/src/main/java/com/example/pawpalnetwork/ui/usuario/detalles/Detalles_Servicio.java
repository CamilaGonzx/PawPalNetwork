package com.example.pawpalnetwork.ui.usuario.detalles;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Servicio;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Detalles_Servicio extends AppCompatActivity {


    private ImageButton leftArrow, rightArrow;
    private ViewPager viewPager;
    private RecyclerView recyclerViewThumbnails;
    private TabLayout tabLayout;
    private ViewPager2 pager;
    private List<String> imageUrls = new ArrayList<>(); // Lista de URLs de imágenes desde Firebase
    private int currentPosition = 0;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
Servicio se;
    private ArrayList<Uri> photoList;
    private UsuarioGeneral us;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalles_servicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        se = (Servicio) getIntent().getSerializableExtra("servicio");
        Log.i("Detalles","El Id del servicio al que se accedio es: "+se.getId());
        leftArrow = findViewById(R.id.leftArrow);
        rightArrow = findViewById(R.id.rightArrow);
        viewPager = findViewById(R.id.viewPager);
        recyclerViewThumbnails = findViewById(R.id.recyclerViewThumbnails);
        tabLayout = findViewById(R.id.tabLayout);
        pager = findViewById(R.id.Pager);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        photoList = new ArrayList<>();
        cargarDatosProveedor(se.getProveedorId());

        cargarDatosServicio(se.getId());

        // Configura la navegación de las flechas
        leftArrow.setOnClickListener(v -> {
            if (currentPosition > 0) {
                currentPosition--;
                viewPager.setCurrentItem(currentPosition, true);
            }
        });

        rightArrow.setOnClickListener(v -> {
            if (currentPosition < imageUrls.size() - 1) {
                currentPosition++;
                viewPager.setCurrentItem(currentPosition, true);
            }
        });

        // Listener para actualizar currentPosition al cambiar de página en el ViewPager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });




    }

    private void cargarDatosProveedor (String proveedorId)
    {
        db.collection("usuarios").document(proveedorId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists())
                    {
                         us = documentSnapshot.toObject(UsuarioGeneral.class);
                        // Solo configurar el adaptador una vez que tenemos los datos del proveedor
                        configurarViewPagerConProveedor(tabLayout, pager, us);
                    }
                }).addOnFailureListener(e -> Log.e("DerallesServicio","Error al cargar los datos de usuario"));
    }
    private void configurarViewPagerConProveedor(TabLayout tabLayout, ViewPager2 pager, UsuarioGeneral proveedor) {


        // Configurar el adaptador con Servicio y Proveedor (si es necesario)
        TabPagerAdapter adapter = new TabPagerAdapter(this, se, proveedor); // Pasamos ambos objetos
        adapter.addFragment(new DetallesServicioPager(), "Detalles");
        adapter.addFragment(new DetallesServicioReviews(), "Reviews");
        adapter.addFragment(new DetallesServicioUbicacion(), "Ubicación");

        // Asignar adaptador al ViewPager
        pager.setAdapter(adapter);

        // Vincular el TabLayout con el ViewPager2
        new TabLayoutMediator(tabLayout, pager,
                (tab, position) -> tab.setText(adapter.getPageTitle(position))
        ).attach();

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Encuentra el fragmento actual
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + position);
                if (fragment != null && fragment.getView() != null) {
                    fragment.getView().post(() -> {
                        if (fragment.getView() != null) {
                            // Calcula la altura del contenido del fragmento
                            int height = fragment.getView().getMeasuredHeight();
                            ViewGroup.LayoutParams layoutParams = pager.getLayoutParams();
                            layoutParams.height = height;
                            pager.setLayoutParams(layoutParams);
                        }
                    });
                } else {
                    Log.w("ViewPager2", "Fragment o su vista no están disponibles.");
                }
            }
        });
    }
    private void cargarDatosServicio(String servicioId) {
        db.collection("servicios").document(servicioId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Servicio servicio = documentSnapshot.toObject(Servicio.class);
                        if (servicio != null) {



                            // Cargar fotos existentes desde Firestore
                            if (servicio.getImagenesRutas() != null) {
                                photoList.clear(); // Asegúrate de limpiar la lista antes de agregar nuevas imágenes
                                for (String imageUrl : servicio.getImagenesRutas()) {
                                    // Convertir URL a Uri\
                                    if(!imageUrl.isEmpty()) {
                                        Uri imageUri = Uri.parse(imageUrl); //

                                        Log.e("FotoAdapter", "Entro" + imageUri);
                                        photoList.add(imageUri);
                                    }
                                }
                                Log.d("EditarPublicacion", "photoList contiene: " + photoList.size() + " imágenes.");
                                setupViewPager();
                                setupThumbnailRecyclerView();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("EditarPublicacion", "Error al cargar datos del servicio", e));
    }
    private void setupThumbnailRecyclerView() {
        ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter(this, photoList, position -> {
            viewPager.setCurrentItem(position, true);
            currentPosition = position;
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewThumbnails.setLayoutManager(layoutManager);
        recyclerViewThumbnails.setAdapter(thumbnailAdapter);
    }

    private void setupViewPager() {
        // Configuración del adaptador del ViewPager después de obtener todas las URLs
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, photoList);
        viewPager.setAdapter(adapter);
    }
}