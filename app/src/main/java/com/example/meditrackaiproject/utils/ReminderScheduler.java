package com.example.meditrackaiproject.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.example.meditrackaiproject.models.Medicine;
import com.example.meditrackaiproject.reminders.MedicineReminderReceiver;
import java.util.Calendar;

public class ReminderScheduler {

    public static void scheduleMedicineReminders(Context context, Medicine medicine) {
        if (medicine.isMorning()) schedule(context, medicine.getName(), "Morning Dose", 10, 30);
        if (medicine.isAfternoon()) schedule(context, medicine.getName(), "Afternoon Dose", 15, 30);
        if (medicine.isEvening()) schedule(context, medicine.getName(), "Evening Dose", 19, 30);
        if (medicine.isNight() || medicine.isBeforeSleep()) schedule(context, medicine.getName(), "Before Sleeping Dose", 21, 30);
    }

    public static void cancelMedicineReminders(Context context, Medicine medicine) {
        if (medicine.isMorning()) cancel(context, medicine.getName(), 10, 30);
        if (medicine.isAfternoon()) cancel(context, medicine.getName(), 15, 30);
        if (medicine.isEvening()) cancel(context, medicine.getName(), 19, 30);
        if (medicine.isNight() || medicine.isBeforeSleep()) cancel(context, medicine.getName(), 21, 30);
    }

    private static void schedule(Context context, String medName, String doseTime, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MedicineReminderReceiver.class);
        intent.putExtra("medName", medName);
        intent.putExtra("doseTime", doseTime);
        
        int requestCode = (medName + hour + minute).hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            } catch (Exception e) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    private static void cancel(Context context, String medName, int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MedicineReminderReceiver.class);
        int requestCode = (medName + hour + minute).hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}