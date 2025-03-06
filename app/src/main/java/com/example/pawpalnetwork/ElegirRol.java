package com.example.pawpalnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pawpalnetwork.ui.proveedor.Main_Proveedor;

public class ElegirRol extends AppCompatActivity {

    TextView tvSeleccionaOpcion;
    CardView cardBuscoProfesional;
    ImageView ivIconoProfesional;
    TextView tvBuscoProfesional;
    TextView tvDescripcionProfesional;
    CardView cardSoyProveedor;
    ImageView ivIconoProveedor;
    TextView tvSoyProveedor;
    TextView tvDescripcionProveedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_elegir_rol);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        tvSeleccionaOpcion = findViewById(R.id.tvSeleccionaOpcion);
        cardBuscoProfesional = findViewById(R.id.cardBuscoProfesional);
        ivIconoProfesional = findViewById(R.id.ivIconoProfesional);
        tvBuscoProfesional = findViewById(R.id.tvBuscoProfesional);
        tvDescripcionProfesional = findViewById(R.id.tvDescripcionProfesional);
        cardSoyProveedor = findViewById(R.id.cardSoyProveedor);
        ivIconoProveedor = findViewById(R.id.ivIconoProveedor);
        tvSoyProveedor = findViewById(R.id.tvSoyProveedor);
        tvDescripcionProveedor = findViewById(R.id.tvDescripcionProveedor);

        // Acción cuando se presiona la Card "Busco un Profesional"
        cardBuscoProfesional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ElegirRol.this, RegisterActivity.class); // Reemplaza "RegistroActivity" con la actividad de registro
                intent.putExtra("Tipo","Usuario");
                startActivity(intent);
            }
        });

        // Acción cuando se presiona la Card "Soy un Proveedor"
        cardSoyProveedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ElegirRol.this, RegisterActivity.class); // Reemplaza "RegistroActivity" con la actividad de registro
                intent.putExtra("Tipo","Proveedor");
                startActivity(intent);
            }
        });


    }
}