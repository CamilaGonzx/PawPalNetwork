package com.example.pawpalnetwork.ui.usuario.perfilproveedor;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.UsuarioGeneral;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PerfilProveedorUsuario extends AppCompatActivity {

    private PerfilProveedorvuViewModel mViewModel;
    private ImageView imageView3, imageView4;
    private TextView textView4, tvUserInfoTitle, tvUserInfo, tvRating;

    // TabLayout y ViewPager2 para las pestañas de Reviews y Anuncios
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    // RecyclerView para mostrar las reseñas
    private RecyclerView recyclerViewReseñas;
    UsuarioGeneral usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_proveedor_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
           usuario = (UsuarioGeneral) bundle.getSerializable("usuario");
        }

        // Inicialización de ImageViews y TextView en el encabezado
        imageView3 = findViewById(R.id.imageView3);
        imageView4 =findViewById(R.id.imageView4);
        textView4 = findViewById(R.id.textView4);
        tvUserInfoTitle =findViewById(R.id.tvUserInfoTitle);
        tvUserInfo = findViewById(R.id.tvUserInfo);
        tvRating =findViewById(R.id.tvRating);

        // Inicialización del TabLayout y ViewPager2
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);

        Glide.with(imageView4.getContext())
                .load(usuario.getFotoPerfil())
                .into(imageView4);

        textView4.setText(usuario.getNombre());
        if(usuario.getDescripcion()!= null) {
            tvUserInfo.setText(usuario.getDescripcion());
        }else {
            tvUserInfo.setText("No cuenta con descripción");
        }
        tvRating.setText(""+usuario.getRating());

        // Configuración de las pestañas
        setupTabLayout();
    }

    private void setupTabLayout() {


        // Configurar el adaptador con Servicio y Proveedor (si es necesario)
        TabPagerAdapter adapter = new TabPagerAdapter(this, usuario); // Pasamos ambos objetos

        adapter.addFragment(new DetallesServicioReviews(), "Reviews");
        adapter.addFragment(new AnunciosPublicadosvu(), "Anuncios");

        // Asignar adaptador al ViewPager
        viewPager2.setAdapter(adapter);

        // Vincular el TabLayout con el ViewPager2
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(adapter.getPageTitle(position))
        ).attach();
    }
}