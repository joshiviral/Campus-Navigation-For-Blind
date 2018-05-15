package com.example.dummy.campusnavforblind;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {


    private static SharedPrefManager sharedPrefManager; // share preference manager object
    private static Context cntx; // context object

    private SharedPrefManager(Context context) {
        cntx = context;
    }

    // method to get instance of sharepreference manager
    public static synchronized SharedPrefManager getInstance(Context context) {
        // no object of share preference manager, make a new one and return that
        if (sharedPrefManager == null) {
            sharedPrefManager = new SharedPrefManager(context);
        }
        return sharedPrefManager;
    }

    //method to save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = cntx.getSharedPreferences("FCMSharedPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit(); // open share manager in edit mode
        editor.putString("deviceToken", token); // add device token to editor
        editor.apply(); // save to share manager
        return true;
    }

    // method to fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = cntx.getSharedPreferences("FCMSharedPreference", Context.MODE_PRIVATE);
        return  sharedPreferences.getString("deviceToken", null);// return token on method call
    }
}
