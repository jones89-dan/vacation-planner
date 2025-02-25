package com.jones.d424vacationplanner.UI;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.jones.d424vacationplanner.entitities.Vacation;
import com.jones.d424vacationplanner.receivers.VacationNotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CreateVacation extends AppCompatActivity {

    private String selectedStartDateStr; // Store selected start date string
    private String selectedEndDateStr;   // Store selected end date string

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to go back to main page
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        // Initialize the Start date button
        Button startDatePickerButton = findViewById(R.id.startDatePickerButton);


        startDatePickerButton.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Date picker ensures start dates is in the correct format B3 part C
            DatePickerDialog startDatePickerDialog = new DatePickerDialog(
                    CreateVacation.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedStartDateStr = (selectedMonth + 1)  + "/" + selectedDay + "/" + selectedYear; // Store date
                        Toast.makeText(CreateVacation.this, "Start Date: " + selectedStartDateStr, Toast.LENGTH_SHORT).show();
                    },
                    year, month, day
            );

            startDatePickerDialog.show();
        });

        // Initialize the End date button
        Button endDatePickerButton = findViewById(R.id.endDatePickerButton);

        endDatePickerButton.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Date picker ensures end dates is in the correct format B3 part C
            DatePickerDialog endDatePickerDialog = new DatePickerDialog(
                    CreateVacation.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedEndDateStr = (selectedMonth + 1)  + "/" + selectedDay + "/" + selectedYear; // Store date
                        Toast.makeText(CreateVacation.this, "End Date: " + selectedEndDateStr, Toast.LENGTH_SHORT).show();
                    },
                    year, month, day
            );

            endDatePickerDialog.show();
        });

        // Initialize Database
        AppDatabase db = AppDatabase.getInstance(this);

        // Save Vacation on Button Click
        View buttonCreateVacation = findViewById(R.id.buttonCreateVacation);
        buttonCreateVacation.setOnClickListener(v -> {
            try {
                saveVacation();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        // Request permission to send notifications if permission not granted
        if (checkSelfPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.SCHEDULE_EXACT_ALARM}, 1);
        }
    }

    // Save the vacation on button click
    private void saveVacation() throws ParseException {
        // Collect the vacation details
        EditText vacationTitle = findViewById(R.id.vacation_title_text);
        EditText vacationPlace = findViewById(R.id.vacation_hotel_text);
        Date selectedStartDate;
        Date selectedEndDate;

        // Trim extra space on the title and place
        String title = vacationTitle.getText().toString().trim();
        String place = vacationPlace.getText().toString().trim();

        // Check for empty fields
        if (title.isEmpty() || place.isEmpty()) {
            Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for empty date and convert to date format if not empty.
        if (selectedStartDateStr.isEmpty() || selectedEndDateStr.isEmpty()) {
            Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show();
            return;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            selectedStartDate = dateFormat.parse(selectedStartDateStr);
            selectedEndDate = dateFormat.parse(selectedEndDateStr);

            assert selectedStartDate != null;
            // B3 part D. Check if Start Date is after End Date and alert user to fix before proceeding.
            if (selectedStartDate.after(selectedEndDate)) {
                Toast.makeText(this, "Start date must be before end date!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create the vacation object
        Vacation newVacation = new Vacation(title, place, selectedStartDate, selectedEndDate);

        // Schedule notification if user selects the check box
        CheckBox checkBoxScheduleNotification = findViewById(R.id.checkboxScheduleNotification);
        boolean shouldScheduleNotification = checkBoxScheduleNotification.isChecked();

        if (shouldScheduleNotification) {
            // Schedule notifications for vacation start and end dates
            scheduleVacationNotification(selectedStartDate.getTime(), title, "starts today!", true);
            scheduleVacationNotification(selectedEndDate.getTime(), title, "ends today!", true);
        }

        // Connect to database and insert the new vacation
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(CreateVacation.this);
            db.vacationDAO().insert(newVacation);
            runOnUiThread(() -> Toast.makeText(CreateVacation.this, "Vacation Saved!", Toast.LENGTH_SHORT).show());
            finish();
        });
    }

    // Schedule the vacation notification using alarm manager
    @SuppressLint("ScheduleExactAlarm")
    private void scheduleVacationNotification(long triggerTime, String vacationTitle, String message,  boolean isVacation) {
        long currentTime = System.currentTimeMillis();

        // Create a Calendar instance and set the time using the current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);

        // Reset hours, minutes, seconds, and milliseconds to zero
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get the start of the current day in milliseconds
        long startOfDayInMillis = calendar.getTimeInMillis();

        // Prevent scheduling notifications for past dates
        if (triggerTime < startOfDayInMillis) {
            return; // Exit method early
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, VacationNotificationReceiver.class);
        intent.putExtra("title", vacationTitle);
        intent.putExtra("message", message);
        intent.putExtra("isVacation", isVacation);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, (int) triggerTime, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }
}