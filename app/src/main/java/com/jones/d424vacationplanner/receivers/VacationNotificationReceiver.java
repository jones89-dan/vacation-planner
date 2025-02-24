package com.jones.d424vacationplanner.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.jones.d424vacationplanner.R;

public class VacationNotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "vacation_notifications";
    static int notificationID;

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        boolean isVacation = intent.getBooleanExtra("isVacation", true);

        // Create notification channel
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the NotificationChannel only if it doesn't exist already
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Vacation & Excursion Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        int icon = isVacation ? R.drawable.baseline_beach_access_24 : R.drawable.baseline_hiking_24;
        String notificationTitle = isVacation ? "Vacation Reminder" : "Excursion Reminder";

        // Set up Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(notificationTitle)
                .setContentText(title + " " + message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Show Notification
        notificationManager.notify(notificationID++, builder.build());
    }
}
