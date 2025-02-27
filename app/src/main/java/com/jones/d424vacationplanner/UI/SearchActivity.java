package com.jones.d424vacationplanner.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jones.d424vacationplanner.R;
import com.jones.d424vacationplanner.dao.ExcursionDAO;
import com.jones.d424vacationplanner.dao.VacationDAO;
import com.jones.d424vacationplanner.database.AppDatabase;
import com.jones.d424vacationplanner.entitities.Excursion;
import com.jones.d424vacationplanner.entitities.Vacation;
import com.jones.d424vacationplanner.search.VacationSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;
    private RadioButton radioVacation, radioExcursion;
    private RecyclerView recyclerViewResults;
    private VacationDAO vacationDao;
    private ExcursionDAO excursionDao;
    private RadioGroup radioGroupSearchType;
    private SearchResultsAdapter adapter;
    private TextView textViewNoResults;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        // Initialize elements on the view activity
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        recyclerViewResults = findViewById(R.id.recyclerViewResults);
        radioGroupSearchType = findViewById(R.id.radioGroupOptions);
        radioVacation = findViewById(R.id.radioVacation);
        radioExcursion = findViewById(R.id.radioExcursion);
        textViewNoResults = findViewById(R.id.textViewNoResults);


        // Button to go back to main page
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        // Initialize the DAO and VacationSearch
        // Get DAO instances
        vacationDao = AppDatabase.getInstance(this).vacationDAO();
        excursionDao = AppDatabase.getInstance(this).excursionDAO();

        // Initialize the RecyclerView and adapter
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultsAdapter<>(new ArrayList<>());
        recyclerViewResults.setAdapter(adapter);

        // Set click listener for search button
        buttonSearch.setOnClickListener(v -> performSearch());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void performSearch() {
        // Get the search query from the EditText
        String keyword = editTextSearch.getText().toString().trim();

        // Get the selected search type, Vacation or Excursion
        int selectedRadioId = radioGroupSearchType.getCheckedRadioButtonId();
        if (selectedRadioId == R.id.radioVacation) {
            // Search for Vacations
            searchVacations(keyword);
        } else if (selectedRadioId == R.id.radioExcursion) {
            // Search for Excursions
            searchExcursions(keyword);
        }
    }

    // Vacation search, return results to the SearchResultsAdapter
    private void searchVacations(String keyword) {
        executorService.execute(() -> {
            List<Vacation> results = vacationDao.searchVacations("%" + keyword + "%");
            runOnUiThread(() -> {
                adapter.updateResults(results);
                if (results.isEmpty()) {
                    textViewNoResults.setVisibility(View.VISIBLE);
                } else {
                    textViewNoResults.setVisibility(View.GONE);
                }
            });
        });
    }

    // Excursion search, return results to the SearchResultsAdapter
    private void searchExcursions(String keyword) {
        executorService.execute(() -> {
            List<Excursion> results = excursionDao.searchExcursions("%" + keyword + "%");
            runOnUiThread(() -> {
                adapter.updateResults(results);
                if (results.isEmpty()) {
                    textViewNoResults.setVisibility(View.VISIBLE);
                } else {
                    textViewNoResults.setVisibility(View.GONE);
                }
            });
        });
    }
}