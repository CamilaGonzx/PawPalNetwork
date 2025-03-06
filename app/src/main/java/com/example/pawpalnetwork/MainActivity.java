package com.example.pawpalnetwork;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pawpalnetwork.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_agenda,R.id.navigation_notifications, R.id.navigation_perfil)
                .build();
      NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);



        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding.navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home) {
                // Navega a la sección Home
                navController.navigate(R.id.navigation_home);
            } else if (item.getItemId() == R.id.navigation_agenda) {
                // Navega a la sección Agenda
                navController.navigate(R.id.navigation_agenda);
            } else if (item.getItemId() == R.id.navigation_perfil) {
                // Navega a la sección Agenda
                navController.navigate(R.id.navigation_perfil);
            } else if (item.getItemId() == R.id.navigation_notifications) {
                // Limpia el back stack para notificaciones
                navController.popBackStack(R.id.navigation_notifications, true);
                // Navega a la sección Notificaciones
                navController.navigate(R.id.navigation_notifications);
            } else if (item.getItemId() == R.id.navigation_perfil) {
                navController.navigate(R.id.navigation_perfil);
            }
            return true;
        });


    }

}