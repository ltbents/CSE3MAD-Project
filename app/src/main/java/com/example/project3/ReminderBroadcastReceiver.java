package com.example.project3;

import android.app.NotificationChannel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    // Method called when a reminder is received
    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract task details from the intent
        String taskId = intent.getStringExtra("taskId");
        String taskDescription = intent.getStringExtra("taskDescription");

        // Show a notification or perform other actions
        showNotification(context, taskId, taskDescription);
    }

    private void showNotification(Context context, String taskId, String taskDescription) {
        // Method to show a notification for the task reminder
        // Get the notification manager system service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "task_reminder_channel"; // Define notification channel ID for Android Oreo and above

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// Create notification channel for Android Oreo and above
            NotificationChannel channel = new NotificationChannel(channelId, "Task Reminder", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.baseline_access_alarm_24)// Set notification icon
                .setContentTitle("Task Reminder")// Set notification title
                .setContentText(taskDescription)// Set notification content text
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set notification priority
                .setAutoCancel(true);// Allow notification to be dismissed when clicked
        // Notify the system to display the notification
        notificationManager.notify(taskId.hashCode(), builder.build());
    }
}
