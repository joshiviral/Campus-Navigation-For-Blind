package com.example.dummy.campusnavforblind.ReminderServicePackage;

import android.app.AlarmManager;
import android.content.Context;

public class ReminderManager {

    private static final String TAG = ReminderManager.class.getSimpleName();
    private static AlarmManager aManager; // get alarm manager

    // if alarm already exist for given thing, bring error
    public static synchronized void injectAlarmManager(AlarmManager alarmManager) {
        if (aManager != null) {
            throw new IllegalStateException("Alarm Manager Already Set");
        }
        aManager = alarmManager;
    }

    // if alarm is not set, make object of alarm manage to work on alarm data
   static synchronized AlarmManager getAlarmManager(Context context) {
        if (aManager == null) {
            aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        return aManager;
    }
}
