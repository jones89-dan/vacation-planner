package com.jones.d424vacationplanner.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jones.d424vacationplanner.R;
import com.jones.d424vacationplanner.database.AppDatabase;
import com.jones.d424vacationplanner.entitities.Excursion;

import java.util.List;
import java.util.concurrent.Executors;

public class LIstVacationExcursions extends AppCompatActivity {
    private Context context;
    private RecyclerView recyclerViewExcursions;
    private ExcursionRecyclerAdapter excursionAdapter;
    private AppDatabase db;
    private int vacationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_vacation_excursions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to go back to previous page
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(LIstVacationExcursions.this, ListVacations.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close current activity
        });

        // Button to create/add excursion to vacation
        ImageButton addExcursion = findViewById(R.id.addExcursion);

        addExcursion.setOnClickListener(v -> {
            Intent intent = new Intent(LIstVacationExcursions.this, CreateExcursion.class);
            intent.putExtra("id", getIntent().getIntExtra("id", -1)); // Get ID from previous activity
            intent.putExtra("vacationId", vacationId);
            startActivity(intent);
        });

        // Retrieve vacation ID from Intent
        vacationId = getIntent().getIntExtra("id", -1);
        if (vacationId == -1) {
            finish(); // Exit if no ID is found
            return;
        }

        // Initialize RecyclerView
        recyclerViewExcursions = findViewById(R.id.recyclerViewExcursions);
        recyclerViewExcursions.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getInstance(this);

        // Load Excursions
        loadExcursions();

    }

    // Load Excursions and pass to the Excursion Recycler Adapter
    private void loadExcursions() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Excursion> excursions = db.excursionDAO().getExcursionsByVacation(vacationId);

            runOnUiThread(() -> {
                excursionAdapter = new ExcursionRecyclerAdapter(excursions, this, vacationId);
                recyclerViewExcursions.setAdapter(excursionAdapter);
            });
        });

    }

}