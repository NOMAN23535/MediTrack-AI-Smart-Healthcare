package com.example.meditrackaiproject.reminders;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.meditrackaiproject.R;
import com.example.meditrackaiproject.models.Notification;
import com.example.meditrackaiproject.patient.MedicineManagementActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MedicineReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String medName = intent.getStringExtra("medName");
        String doseTime = intent.getStringExtra("doseTime");
        sendNotification(context, medName, doseTime);
        saveNotificationToFirebase(medName, doseTime);
    }

    private void sendNotification(Context context, String medName, String doseTime) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "medicine_reminders";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Medicine Reminders", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MedicineManagementActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.drugs)
                .setContentTitle("Medicine Reminder: " + doseTime)
                .setContentText("It's time to take your medicine: " + medName + ". It's time to take your medicine.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void saveNotificationToFirebase(String medName, String doseTime) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);
            String id = mDatabase.push().getKey();
            Notification notification = new Notification(id, "Medicine Reminder: " + doseTime, 
                    "It's time to take your medicine: " + medName + ". It's time to take your medicine.", "Reminder");
            if (id != null) {
                mDatabase.child(id).setValue(notification);
            }
        }
    }
}