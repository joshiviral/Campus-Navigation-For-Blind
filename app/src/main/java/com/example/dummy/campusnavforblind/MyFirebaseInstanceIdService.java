package com.example.dummy.campusnavforblind;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by dummy on 2018-03-21.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG="MyFirebaseInstIDService";

    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String recent_token = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d("Firebase Notification", "Refreshed token: " + recent_token);

        //calling the method store token and passing token to that method
        storeToken(recent_token);
    }
    private void storeToken(String token) {
        //storing deivce token to sharedpreferences
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
    }

}
