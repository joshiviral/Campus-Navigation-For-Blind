package com.example.dummy.campusnavforblind;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dummy.campusnavforblind.MainActivity;
import com.example.dummy.campusnavforblind.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by dummy on 2018-03-21.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    // on receiving remote message from firebase
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle(); // get message title
        String message = remoteMessage.getNotification().getBody(); //get message body

        Intent intent = new Intent(this,ViewNotification.class); // strat view notification activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"1");
        notificationBuilder.setContentTitle(title); // set title of notification
        notificationBuilder.setContentText(message); // set title of notification
        notificationBuilder.setAutoCancel(true); // alow auto cancel of notification
        notificationBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }); // vribrate phone on receiving
        notificationBuilder.setSound(alarmSound); // make sound on notification
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher); // set icon
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build()); //make notification

    }


}
