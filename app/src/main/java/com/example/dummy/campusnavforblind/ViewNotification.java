/*
This page will open when user click on open notification button from the home page.
Here user can see all college alerts posted by them.

Reference:

*/


package com.example.dummy.campusnavforblind;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewNotification extends AppCompatActivity {


    // api link which will hold all json data regarding college alerts
    private static final String URL_NOTIFICATION = "https://varun1995.000webhostapp.com/PUSH_NOTIFICATION/notificationApi.php";

    //a list to store all the notification data
    List<Notification> notificationList;

    //listview which will show all the fetched json data
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_notification);

        //getting the recyclerview layout which will hold all json data
        recyclerView = findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing notification list objet
        notificationList = new ArrayList<>();


        loadNotification(); //  method to fetch notification from given api link

    }

    private void loadNotification()
    {
        // getting json data from given api link
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           // if found any json data

                            //typecasting fetched string to json array object
                            JSONArray array = new JSONArray(response);

                            //going through all the object one by one
                            for (int i = 0; i < array.length(); i++) {

                                //getting notification object from json array
                                JSONObject notification = array.getJSONObject(i);

                                //adding fetched data to list view
                                notificationList.add(new Notification(
                                        notification.getString("title"),
                                        notification.getString("body")
                                ));
                            }

                            //creating adapter object with list view
                            NotificationAdapter adapter = new NotificationAdapter(ViewNotification.this, notificationList);

                            // adding adapter to list view whcih will show all records in list
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    // error message if failed to fetch data
                    }
                });

        //adding out json data request in line
        Volley.newRequestQueue(this).add(stringRequest);
    }

}