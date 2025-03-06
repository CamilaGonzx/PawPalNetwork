package com.example.pawpalnetwork.ui.admin;

import android.os.Bundle;

import com.example.pawpalnetwork.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pawpalnetwork.databinding.ActivityAdministradorBinding;

public class Administrador extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAdministradorBinding binding;

        binding = ActivityAdministradorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView2 = findViewById(R.id.nav_view3);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_admin_crearuser, R.id.navigation_admin_editar,R.id.navigation_admin_eliminar,R.id.navigation_admin_buscar)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_administrador);
        NavigationUI.setupWithNavController(binding.navView3, navController);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

}