package com.example.dummy.campusnavforblind.ReminderServicePackage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

public class ReminderScheduler {

    public void setAlarm(Context context, long alarmTime, Uri reminderTask) {
        AlarmManager manager = ReminderManager.getAlarmManager(context);

        PendingIntent operation =
                ReminderService.getReminderPendingIntent(context, reminderTask); // call pending intent to set alarm in system



        if (Build.VERSION.SDK_INT >= 23) {
            // if system sdk is greater than 23, use this alarm management
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        } else if (Build.VERSION.SDK_INT >= 19) {
            // if system sdk is greater than 19, use this alarm management
            manager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        } else {
            // if system sdk is less than 19, use this alarm management
            manager.set(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        }
    }
}
