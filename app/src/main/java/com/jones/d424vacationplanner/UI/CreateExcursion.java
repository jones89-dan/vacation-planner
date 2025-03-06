package com.jones.d424vacationplanner.UI;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jones.d424vacationplanner.R;
import com.jones.d424vacationplanner.database.AppDatabase;
import com.jones.d424vacationplanner.entitities.Excursion;
import com.jones.d424vacationplanner.entitities.Vacation;
import com.jones.d424vacationplanner.receivers.VacationNotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CreateExcursion extends AppCompatActivity {
    private int vacationId;
    private String selectedExcursionDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_excursion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to go back to previous page
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        // Get the vacation ID passed from LIstVacationExcursions
        vacationId = getIntent().getIntExtra("id", -1);
        if (vacationId == -1) {
            Toast.makeText(this, "Error: No vacation ID found!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize database
        AppDatabase db = AppDatabase.getInstance(this);

        // Initialize Excursion date button
        Button excursionDate = findViewById(R.id.excursionDatePickerButton);

        excursionDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Date picker ensures start dates is in the correct format B3 part C
            DatePickerDialog excursionDatePickerDialog = new DatePickerDialog(
                    CreateExcursion.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedExcursionDateStr = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear; // Store date
                        Toast.makeText(CreateExcursion.this, "Excursion Date: " + selectedExcursionDateStr, Toast.LENGTH_SHORT).show();
                    },
                    year, month, day
            );

            excursionDatePickerDialog.show();
        });

        // Save Excursion on Button Click
        View buttonCreateExcursion = findViewById(R.id.buttonCreateExcursion);
        buttonCreateExcursion.setOnClickListener(v -> {
            try {
                saveExcursion();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Save the Excursion on button click
    private void saveExcursion() throws ParseException {
        // Collect the vacation details
        EditText excursionTitle = findViewById(R.id.excursionTitleText);
        Date selectedExcursionDate;

        // Trim extra space on the title and place
        String title = excursionTitle.getText().toString().trim();

        // Input validation - Check for empty fields
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter title field", Toast.LENGTH_SHORT).show();
            return;
        }

        // Security Feature - Ensure user input is sanitary before proceeding.
       if (!isValidInput(title)) {
           Toast.makeText(this, "Input contains invalid characters", Toast.LENGTH_SHORT).show();
           return;
       }

        // Input Validation - Check for empty date and convert to date format if not empty.
        if (selectedExcursionDateStr == null || selectedExcursionDateStr.isEmpty()) {
            Toast.makeText(this, "Please select excursion date", Toast.LENGTH_SHORT).show();
            return;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            selectedExcursionDate = dateFormat.parse(selectedExcursionDateStr);
        }

        // Create the excursion object
        Excursion newExcursion = new Excursion(vacationId, title, selectedExcursionDate);

        // Schedule notification if user selects the check box
        CheckBox checkBoxScheduleNotification = findViewById(R.id.checkboxScheduleNotification);
        boolean shouldScheduleNotification = checkBoxScheduleNotification.isChecked();

        if (shouldScheduleNotification) {
            // Schedule notifications for vacation start and end dates
            scheduleExcursionNotification(selectedExcursionDate.getTime(), title, "starts today!", false);
        }

        // Connect to database and insert the new vacation
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(CreateExcursion.this);
            // Get vacation to check start and end date
            Vacation vacation = db.vacationDAO().getVacationById(vacationId);

            // Input Validation - Validate the excursion date is during vacation
            if (selectedExcursionDate.before(vacation.getStartDate()) || selectedExcursionDate.after(vacation.getEndDate())) {
                        runOnUiThread(() -> Toast.makeText(CreateExcursion.this, "Excursion date must be within the vacation dates!", Toast.LENGTH_LONG).show());
            } else {
                // Save the Excursion
                db.excursionDAO().insert(newExcursion);
                runOnUiThread(() -> Toast.makeText(CreateExcursion.this, "Excursion Saved!", Toast.LENGTH_SHORT).show());
                Intent intent = new Intent(CreateExcursion.this, LIstVacationExcursions.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish(); // Close current activity
            }

        });

    }

    // Schedule the excursion notification using alarm manager
    @SuppressLint("ScheduleExactAlarm")
    private void scheduleExcursionNotification(long triggerTime, String excursionTitle, String message, boolean isVacation) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, VacationNotificationReceiver.class);
        intent.putExtra("title", excursionTitle);
        intent.putExtra("message", message);
        intent.putExtra("isVacation", isVacation);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, (int) triggerTime, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    // Security Feature - Check for sanitary input, to prevent SQL injection
    public boolean isValidInput(String input) {
        return input.matches("^[a-zA-Z0-9 ]+$");
    }

}