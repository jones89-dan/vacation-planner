package com.jones.d424vacationplanner.UI;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jones.d424vacationplanner.R;
import com.jones.d424vacationplanner.database.AppDatabase;
import com.jones.d424vacationplanner.entitities.Vacation;

import java.util.List;
import java.util.concurrent.Executors;

public class ListVacations extends AppCompatActivity {

    private VacationRecyclerAdapter vacationAdapter;
    private RecyclerView recyclerView;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_vacations);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to go back to main page
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        // Recycler View
        recyclerView = findViewById(R.id.recyclerVacationView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Defined the Database
        db = AppDatabase.getInstance(this);

        // Call method to get vacations
        loadVacations();
    }

    // Collect the vacations from the database
    private void loadVacations() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Vacation> vacations = db.vacationDAO().getAllVacations();

            runOnUiThread(() -> {
                vacationAdapter = new VacationRecyclerAdapter(vacations, this);
                recyclerView.setAdapter(vacationAdapter);
            });
        });
    }
}