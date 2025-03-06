package com.example.pawpalnetwork.ui.proveedor;

import android.os.Bundle;

import com.example.pawpalnetwork.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pawpalnetwork.databinding.ActivityProveedorBinding;

public class Main_Proveedor extends AppCompatActivity {

    private ActivityProveedorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProveedorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView2 = findViewById(R.id.nav_view2);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home2,R.id.nav_crear, R.id.nav_agenda2, R.id.navigation_notifications2,R.id.nav_perfil_proveedor)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_proveedor);
       NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
       NavigationUI.setupWithNavController(binding.navView2, navController);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        binding.navView2.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home2) {
                // Navega a la sección Home
                navController.navigate(R.id.navigation_home2);
            } else if (item.getItemId() == R.id.nav_agenda2) {
                // Navega a la sección Agenda
                navController.navigate(R.id.nav_agenda2);
            } else if (item.getItemId() == R.id.navigation_notifications2) {
                // Limpia el back stack para notificaciones
                navController.popBackStack(R.id.navigation_notifications2, true);
                // Navega a la sección Notificaciones
                navController.navigate(R.id.navigation_notifications2);
            }  else if (item.getItemId() == R.id.nav_crear) {
                // Navega a la sección de Crear
                navController.navigate(R.id.nav_crear);
            }
            else if (item.getItemId() == R.id.nav_perfil_proveedor) {
                navController.navigate(R.id.nav_perfil_proveedor);
            }
            return true;
        });


    }

}