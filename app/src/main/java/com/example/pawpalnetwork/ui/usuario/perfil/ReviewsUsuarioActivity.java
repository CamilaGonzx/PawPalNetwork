package com.example.pawpalnetwork.ui.usuario.perfil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.bd.Review;
import com.example.pawpalnetwork.ui.proveedor.ui.perfil.EditReviewsProveedorActivity;
import com.example.pawpalnetwork.ui.proveedor.ui.perfil.proveedor_Perfil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ReviewsUsuarioActivity extends AppCompatActivity {

    private ListView listView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_reviews_proveedor);

        listView = findViewById(R.id.lvReviews);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();

        obtenerReviewsPorUsuario(userId);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ReviewsUsuarioActivity.this, proveedor_Perfil.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReviewsUsuarioActivity.this, proveedor_Perfil.class);
        startActivity(intent);
        finish();
    }

    private void obtenerReviewsPorUsuario(String userId) {
        db.collection("reviews").whereEqualTo("idUsuario", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Review> reviewsList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                Review review = document.toObject(Review.class);
                                reviewsList.add(review);
                            }
                        }

                        List<String> comentariosList = new ArrayList<>();
                        for (Review review : reviewsList) {
                            comentariosList.add(review.getComentario());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ReviewsUsuarioActivity.this,
                                android.R.layout.simple_list_item_1, comentariosList);

                        listView.setAdapter(adapter);

                    } else {
                        Toast.makeText(ReviewsUsuarioActivity.this, "Error al obtener datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}