package com.jones.d424vacationplanner.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
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
import com.jones.d424vacationplanner.search.ExcursionSearch;
import com.jones.d424vacationplanner.search.Search;
import com.jones.d424vacationplanner.search.VacationSearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;
    private ImageButton downloadReport;
    private RadioButton radioVacation, radioExcursion;
    private RecyclerView recyclerViewResults;
    private RadioGroup radioGroupSearchType;
    private SearchResultsAdapter adapter;
    private TextView textViewNoResults, downloadText;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private VacationSearch vacationSearch;
    private ExcursionSearch excursionSearch;

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
        downloadReport = findViewById(R.id.btnGenerateReport);
        downloadText = findViewById(R.id.downloadReportText);

        // Button to go back to main page
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());


        // Initialize the RecyclerView and adapter
        // Scalability - Recycler view allows dynamic population
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultsAdapter<>(new ArrayList<>());
        recyclerViewResults.setAdapter(adapter);

        downloadReport.setOnClickListener(v -> generateReport(adapter.getSearchResults()));

        // Set click listener for search button
        buttonSearch.setOnClickListener(v -> performSearch());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    // Part B - Polymorphism - Use the Radio button to run VacationSearch or Excursion Search.
    // Use the generic performSearch that is overriding from the parent search class.
    private void performSearch() {
        Search<?> searchType = null;

        // Initialize the Vacation and Excursion search classes
        vacationSearch = new VacationSearch(AppDatabase.getInstance(this).vacationDAO());
        excursionSearch = new ExcursionSearch(AppDatabase.getInstance(this).excursionDAO());
        // Get the search query from the EditText
        String keyword = editTextSearch.getText().toString().trim();

        // Security Feature - Ensure user input is sanitary before proceeding.
        if (!isValidInput(keyword)) {
            Toast.makeText(this, "Input contains invalid characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected search type, Vacation or Excursion
        int selectedRadioId = radioGroupSearchType.getCheckedRadioButtonId();
        if (selectedRadioId == R.id.radioVacation) {
            // Search for Vacations
            searchType = vacationSearch;
        } else if (selectedRadioId == R.id.radioExcursion) {
            // Search for Excursions
            searchType = excursionSearch;
        }

        if (searchType != null) {
            searchType.performSearch(keyword, results -> runOnUiThread(() -> {
                adapter.updateResults(results);
                textViewNoResults.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
                downloadReport.setVisibility(results.isEmpty() ? View.GONE : View.VISIBLE );
                downloadText.setVisibility(results.isEmpty() ? View.GONE : View.VISIBLE );
            }));
        }

    }

    // Part B - Report. Generate reports with multiple columns, multiple rows, date-time stamps, and title
    private void generateReport(List<?> searchResults) {
        if (searchResults == null || searchResults.isEmpty()) {
            Toast.makeText(this, "No results to generate a report!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder reportContent = new StringBuilder();
        reportContent.append("Search Report\n------------------\n");

        for (Object item : searchResults) {
            if (item instanceof Vacation) {
                Vacation vacation = (Vacation) item;
                reportContent.append("Title: ").append(vacation.getTitle()).append("\n")
                        .append("Location: ").append(vacation.getPlace()).append("\n")
                        .append("Start Date: ").append(formatDate(vacation.getStartDate())).append("\n")
                        .append("End Date: ").append(formatDate(vacation.getEndDate())).append("\n")
                        .append("Date Created: ").append(formatLongDate(vacation.getDateCreated())).append("\n\n");
            } else if (item instanceof Excursion) {
                Excursion excursion = (Excursion) item;
                reportContent.append("Title: ").append(excursion.getTitle()).append("\n")
                        .append("Excursion Date: ").append(formatDate(excursion.getExcursionDate())).append("\n")
                        .append("Date Created: ").append(formatLongDate(excursion.getDateCreated())).append("\n\n");
            }
        }

        // Define file path
        File reportFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "SearchReport.txt");

        try (FileOutputStream fos = new FileOutputStream(reportFile)) {
            fos.write(reportContent.toString().getBytes());
            Toast.makeText(this, "Report saved: Internal Storage/Documents/SearchReport.txt", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(this, "Error saving report: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return outputFormat.format(date); // Format to only MM/dd/yyyy
    }

    private String formatLongDate(Long inputDate) {
        Date createdDate = new Date(inputDate);

        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return outputFormat.format(createdDate); // Format to only MM/dd/yyyy
    }

    public boolean isValidInput(String input) {
        return input.isEmpty() || input.matches("^[a-zA-Z0-9 ]+$");
    }
}