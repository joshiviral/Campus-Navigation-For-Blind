package com.example.dummy.campusnavforblind.ReminderServicePackage;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.dummy.campusnavforblind.AddReminderActivity;
import com.example.dummy.campusnavforblind.R;
import com.example.dummy.campusnavforblind.ReminderDatabasePackage.ReminderDbConfig;


public class ReminderService extends IntentService {

    private static final String TAG = ReminderService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 32;

    // get peniding instent for current uri reminder
    public static PendingIntent getReminderPendingIntent(Context context, Uri uri) {
        Intent action = new Intent(context, ReminderService.class);
        action.setData(uri);
        return PendingIntent.getService(context, 0, action, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public ReminderService() {
        super(TAG);
    }

    //method to give notification about reminder
    @Override
    protected void onHandleIntent(Intent intent) {

        //get notification manager
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // get uri from bundle
        Uri uri = intent.getData();

        //set notiifcation for reminder data
        Intent action = new Intent(this, AddReminderActivity.class);
        action.setData(uri);
        PendingIntent operation = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //get reminder title using uri and store it to cursor
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        String description = "";
        try {
            // getting title of rmeinder for given reminder
            if (cursor != null && cursor.moveToFirst()) {
                description = ReminderDbConfig.getColumnString(cursor, ReminderDbConfig.ReminderEntry.KEY_TITLE);
            }
        } finally {
            // traverse untill, cursor is empty
            if (cursor != null) {
                cursor.close();
            }
        }

        // build notification for reminder
        Notification note = new NotificationCompat.Builder(this)
                .setContentTitle("Reminder")  // setting title
                .setContentText(description) // setting body which is fetched from given uri
                .setSmallIcon(R.drawable.walksafe) // setting logo for notification
                .setContentIntent(operation)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }) // setting vibration
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI) // setting sound
                .setAutoCancel(true) // allowing auto cancel
                .build();

        manager.notify(NOTIFICATION_ID, note);
    }
}
