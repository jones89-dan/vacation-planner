package com.jones.d424vacationplanner.UI;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

public class ExcursionDetail extends AppCompatActivity {

    private EditText editTitle, editExcursionDate;
    private Calendar calendarExcursionDate;
    private SimpleDateFormat dateFormat;
    private AppDatabase db;
    private int excursionId;
    private Button buttonSave, buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Button to go back to previous page
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(ExcursionDetail.this, LIstVacationExcursions.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close current activity
        });

        // Request permission to send notifications if permission not granted
        if (checkSelfPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.SCHEDULE_EXACT_ALARM}, 1);
        }

        editTitle = findViewById(R.id.editExcursionTitleText);
        editExcursionDate = findViewById(R.id.editTextExcursionDate);
        buttonSave = findViewById(R.id.buttonEditExcursion);

        // Set the default date format
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        editExcursionDate.setOnClickListener(v -> showDatePickerDialog());

        db = AppDatabase.getInstance(this);

        excursionId = getIntent().getIntExtra("id", -1);

        if (excursionId == -1) {
            Toast.makeText(this, "Invalid excursion ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            Excursion excursion = db.excursionDAO().getExcursionById(excursionId);
            runOnUiThread(() -> {
                if (excursion != null) {
                    String formattedExcursionDate = formatDate(excursion.getExcursionDate());
                    editTitle.setText(excursion.getTitle());
                    editExcursionDate.setText(formattedExcursionDate);

                } else {
                    Toast.makeText(this, "Excursion not found!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });

        // Save Updates
        buttonSave.setOnClickListener(view -> {

            String newTitle = editTitle.getText().toString().trim();
            String newStartDateStr = editExcursionDate.getText().toString().trim();
            // New Excursion date object
            Date newExcusionDate;

            // Check for empty title or place
            if (newTitle.isEmpty()) {
                Toast.makeText(this, "Field cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Try to parse date
            try {
                newExcusionDate = dateFormat.parse(newStartDateStr);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }


            // Schedule notification if user selects the check box
            CheckBox checkBoxScheduleNotification = findViewById(R.id.checkboxScheduleNotification);
            boolean shouldScheduleNotification = checkBoxScheduleNotification.isChecked();

            if (shouldScheduleNotification) {
                // Schedule notifications for vacation start and end dates
                scheduleExcursionNotification(newExcusionDate.getTime(), newTitle, "starts today!");
            }
            // Schedule notifications for excursion date


            Executors.newSingleThreadExecutor().execute(() -> {

                // Get vacation to check start and end date
                int vacationId = getIntent().getIntExtra("vacationId", -1);
                Vacation vacation = db.vacationDAO().getVacationById(vacationId);

                if (vacationId == -1) {
                    runOnUiThread(() -> Toast.makeText(ExcursionDetail.this, "Error: Invalid vacation ID!", Toast.LENGTH_LONG).show());
                    return;
                }

                if (vacation == null) {
                    runOnUiThread(() -> Toast.makeText(ExcursionDetail.this, "Error: Vacation not found!", Toast.LENGTH_LONG).show());
                    return;
                }

                // Validate the excursion date is during vacation
                if (newExcusionDate.before(vacation.getStartDate()) || newExcusionDate.after(vacation.getEndDate())) {
                    runOnUiThread(() -> Toast.makeText(ExcursionDetail.this, "Excursion date must be within the vacation dates!", Toast.LENGTH_LONG).show());
                    return;
                }

                // Update Excursion values in the database.
                db.excursionDAO().updateExcursion(excursionId, newTitle, newExcusionDate);
                runOnUiThread(() -> {
                    Toast.makeText(ExcursionDetail.this, "Excursion Updated!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ExcursionDetail.this, ExcursionRecyclerAdapter.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    finish(); // Close current activity
                });
            });

        });

        // Button to delete excursion
        buttonDelete = findViewById(R.id.buttonDeleteExcursion);

        buttonDelete.setOnClickListener(view -> {
            // Check with user user to confirm they want to delete excursion
            new android.app.AlertDialog.Builder(ExcursionDetail.this)
                    .setTitle("Delete Excursion")
                    .setMessage("Are you sure you want to delete this excursion?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteExcursion())
                    .setNegativeButton("No", null)
                    .show();
        });

    }

    private void showDatePickerDialog() {

        // Define the listener to set the date
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Set the calendar date based on the selected date
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Format the date and display it in the respective EditText
                    String formattedDate = dateFormat.format(calendar.getTime());

                    editExcursionDate.setText(formattedDate);

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    // Date Format helper function
    private String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return outputFormat.format(date); // Format to only MM/dd/yyyy
    }

    // Delete excursion function
    private void deleteExcursion() {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.excursionDAO().deleteExcursionById(excursionId);
            runOnUiThread(() -> {
                Toast.makeText(ExcursionDetail.this, "Excursion Deleted!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ExcursionDetail.this, ExcursionRecyclerAdapter.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish(); // Close current activity
            });
        });
    }

    // Schedule the excursion notification using alarm manager
    @SuppressLint("ScheduleExactAlarm")
    private void scheduleExcursionNotification(long triggerTime, String excursionTitle, String message) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, VacationNotificationReceiver.class);
        intent.putExtra("title", excursionTitle);
        intent.putExtra("message", message);
        intent.putExtra("isVacation", false);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, (int) triggerTime, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }
}