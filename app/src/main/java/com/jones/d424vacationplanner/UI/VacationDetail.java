package com.jones.d424vacationplanner.UI;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class VacationDetail extends AppCompatActivity {

    private EditText editTitle, editPlace, editTextStartDate, editTextEndDate;
    private Button buttonSave;
    private Button buttonDelete;
    private int vacationId;
    private AppDatabase db;
    private Calendar calendarStart, calendarEnd;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_detail);

        // Request permission to send notifications if permission not granted
        if (checkSelfPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.SCHEDULE_EXACT_ALARM}, 1);
        }

        // Initialize the views
        editTitle = findViewById(R.id.edit_vacation_title_text);
        editPlace = findViewById(R.id.edit_vacation_hotel_text);
        buttonSave = findViewById(R.id.button_edit_vacation);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);

        // Initialize the calendar instances for start and end date
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();

        // Set the default date format
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        // Set listeners for the start and end date fields
        editTextStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        editTextEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        db = AppDatabase.getInstance(this);
        vacationId = getIntent().getIntExtra("id", -1);

        if (vacationId == -1) {
            Toast.makeText(this, "Invalid vacation ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load Vacation Data
        Executors.newSingleThreadExecutor().execute(() -> {
            Vacation vacation = db.vacationDAO().getVacationById(vacationId);
            runOnUiThread(() -> {
                if (vacation != null) {

                    String formattedStartDate = formatDate(vacation.startDate);
                    String formattedEndDate = formatDate(vacation.endDate);

                    editTitle.setText(vacation.title);
                    editPlace.setText(vacation.place);
                    editTextStartDate.setText(formattedStartDate);
                    editTextEndDate.setText(formattedEndDate);
                } else {
                    Toast.makeText(this, "Vacation not found!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });

        // Save Updates
        buttonSave.setOnClickListener(view -> {

            String newTitle = editTitle.getText().toString().trim();
            String newPlace = editPlace.getText().toString().trim();
            String newStartDateStr = editTextStartDate.getText().toString().trim();
            String newEndDateStr = editTextEndDate.getText().toString().trim();
            // Start end Date date objects
            Date newStartDate;
            Date newEndDate;

            // Check for empty title or place
            if (newTitle.isEmpty() || newPlace.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Try to parse into dates
            try {
                newStartDate = dateFormat.parse(newStartDateStr);
                newEndDate = dateFormat.parse(newEndDateStr);

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // B3 part D. Check if Start Date is after End Date and alert user to fix before proceeding.
            assert newStartDate != null;

            if (newStartDate.after(newEndDate)) {
                Toast.makeText(this, "Start date must be before end date!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Schedule notification if user selects the check box
            CheckBox checkBoxScheduleNotification = findViewById(R.id.checkboxScheduleNotification);
            boolean shouldScheduleNotification = checkBoxScheduleNotification.isChecked();

            if (shouldScheduleNotification) {
                // Schedule notifications for vacation start and end dates
                // Add 5 min buffer for time check in milliseconds
                scheduleVacationNotification((newStartDate.getTime() + (5 * 60 * 1000)), newTitle, "starts today!", true);
                scheduleVacationNotification((newEndDate.getTime() + (5 * 60 * 1000)), newTitle, "ends today!", true);
            }

            Executors.newSingleThreadExecutor().execute(() -> {

                // Update Vacation values in the database.
                db.vacationDAO().updateVacation(vacationId, newTitle, newPlace, newStartDate, newEndDate);
                runOnUiThread(() -> {
                    Toast.makeText(VacationDetail.this, "Vacation Updated!", Toast.LENGTH_SHORT).show();
                    // Navigate back to the vacation list
                    Intent intent = new Intent(VacationDetail.this, ListVacations.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    finish(); // Close current activity
                });

            });

        });

        // Button to go back to main page
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(VacationDetail.this, ListVacations.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close current activity
        });

        // Button to delete vacation
        buttonDelete = findViewById(R.id.button_delete_vacation);

        buttonDelete.setOnClickListener(view -> {
            // Check with user user to confirm they want to delete vacation
            new android.app.AlertDialog.Builder(VacationDetail.this)
                    .setTitle("Delete Vacation")
                    .setMessage("Are you sure you want to delete this vacation?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteVacation())
                    .setNegativeButton("No", null)
                    .show();
        });

        // Get the copy button image and initiate the copy to clipboard function
        ImageButton buttonShare = findViewById(R.id.shareVacation);
        buttonShare.setOnClickListener(view -> copyToClipboard());
    }

    // Date picker ensures start and end dates is in the correct format B3 part C
    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = isStartDate ? calendarStart : calendarEnd;

        // Define the listener to set the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Set the calendar date based on the selected date
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Format the date and display it in the respective EditText
                    String formattedDate = dateFormat.format(calendar.getTime());
                    if (isStartDate) {
                        editTextStartDate.setText(formattedDate);
                    } else {
                        editTextEndDate.setText(formattedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    // Delete vacation function
    private void deleteVacation() {
        Executors.newSingleThreadExecutor().execute(() -> {

            Vacation vacation = db.vacationDAO().getVacationById(vacationId);

            try {
                db.vacationDAO().deleteVacationIfNoExcursions(vacation);
                runOnUiThread(() -> {
                    Toast.makeText(VacationDetail.this, "Vacation Deleted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VacationDetail.this, ListVacations.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    finish(); // Close current activity
                });
            } catch (SQLiteConstraintException e) {
                runOnUiThread(() -> Toast.makeText(this, "Cannot delete vacation with excursions!", Toast.LENGTH_LONG).show());
            }
        });
    }

    // Date Format helper function
    private String formatDate(Date date) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return outputFormat.format(date); // Format to only MM/dd/yyyy
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
        intent.putExtra("scheduledTime", triggerTime);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, (int) triggerTime, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    // Collect the vacation details for clipboard/sharing feature
    private String getVacationDetails() {
        String title = editTitle.getText().toString().trim();
        String place = editPlace.getText().toString().trim();
        String startDate = editTextStartDate.getText().toString().trim();
        String endDate = editTextEndDate.getText().toString().trim();

        return "Vacation Details:\n" +
                "Title: " + title + "\n" +
                "Location: " + place + "\n" +
                "Start Date: " + startDate + "\n" +
                "End Date: " + endDate;
    }

    // Uses Clipboard Manager to copy the vacation details to clipboard
    private void copyToClipboard() {
        String vacationDetails = getVacationDetails();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Vacation Details", vacationDetails);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Vacation details copied!", Toast.LENGTH_SHORT).show();
    }
}