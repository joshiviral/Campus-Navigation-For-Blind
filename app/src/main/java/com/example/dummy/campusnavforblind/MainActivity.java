package com.example.dummy.campusnavforblind;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // button objects for change password,delete account, sign out,notification button, subscribe and unsubscribe
    private Button  btnChangePassword, btnRemoveUser,notification,
             changePassword, signOut,cancel,subscribe,unsubscribe;

    // edit text for new password
    private EditText  newPassword;

    //progress bar object
    private ProgressBar progressBar;

    // firebase authentication object
    private FirebaseAuth.AuthStateListener authListener;
    // fire base object
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // php link where code for subscription and unsubscription exists
       final String app_server_url = "https://varun1995.000webhostapp.com/PUSH_NOTIFICATION/fcm_insert.php";

        //getting firebase auth instance
        auth = FirebaseAuth.getInstance();
        //getting current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // get current user, if not found go to login page.
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // start login page
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        //getting all button objects
        changePassword = (Button) findViewById(R.id.changePass);
        cancel = (Button)findViewById(R.id.cancel);
        subscribe = (Button)findViewById(R.id.subscibe);
        unsubscribe = (Button)findViewById(R.id.unsubscribe);

        btnChangePassword = (Button) findViewById(R.id.change_password_button);
        btnRemoveUser = (Button) findViewById(R.id.remove_user_button);
        signOut = (Button) findViewById(R.id.sign_out);
        notification = (Button)findViewById(R.id.notification);

        //edittext for new password
        newPassword = (EditText) findViewById(R.id.newPassword);


        // when user opens page, make newpasword text field,change,cancel,subscribe and unsubscribe button  invisible
        newPassword.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        subscribe.setVisibility(View.GONE);
        unsubscribe.setVisibility(View.GONE);

        //get progressbar object
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // if noting, set progressbar to hidden state
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        // on clicking collge alert subscription button
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // make all button invisible, only make change password field, change button and cancel button visible.
                newPassword.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                subscribe.setVisibility(View.VISIBLE);
                unsubscribe.setVisibility(View.VISIBLE);

                //--
                btnChangePassword.setVisibility(View.GONE);
                btnRemoveUser.setVisibility(View.GONE);
                signOut.setVisibility(View.GONE);
                notification.setVisibility(View.GONE);

                //getting device token from shared preferences
                final String token = SharedPrefManager.getInstance(getApplicationContext()).getDeviceToken();
               // Toast.makeText(MainActivity.this,token, Toast.LENGTH_LONG).show();
                //--
                //on clicking subscribe button
                subscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // ask user to confirm
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setTitle("Succession of this event will enable you to receive notifications regarding college alert.");
                        builder.setMessage("Are you sure you want to unsubscribe?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                // if yes, then send gevice token and flag value to given url.
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // toast on sending error
                                Toast.makeText(MainActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams(){
                                // add toke and flag value to parameter
                                Map<String,String> params = new HashMap<String, String>();
                                Log.d("Final token",token);
                                params.put("token",token);
                                params.put("flag","1");

                                return params;
                            }
                        };
                                //send data to given url
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);

                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // if no, do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                });
                //on clicking unsubscribe button
                unsubscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //ask user to confirm
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setTitle("Succession of this event will enable you not to receive any notification regarding college alert.");
                        builder.setMessage("Are you sure you want to unsubscribe?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                            //if yes, send token and flag to given url
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(MainActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                {
                                    @Override
                                    protected Map<String, String> getParams(){
                                        Map<String,String> params = new HashMap<String, String>();
                                        Log.d("Final token",token);
                                        params.put("token",token);
                                        params.put("flag","0");
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                requestQueue.add(stringRequest);

                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // if no,do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();


                    }
                });
            }
        });

        // oncliking change password button
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // make change button, cancel button, password field visible, make rest button invisible
                newPassword.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                subscribe.setVisibility(View.GONE);
                unsubscribe.setVisibility(View.GONE);

                //--
                btnChangePassword.setVisibility(View.GONE);
                btnRemoveUser.setVisibility(View.GONE);
                signOut.setVisibility(View.GONE);
                notification.setVisibility(View.GONE);

                //--
            }
        });

        // on clicking change button
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make progressbar visible
                progressBar.setVisibility(View.VISIBLE);

                // if user session exist and passwors is not null, update password
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        // password length less than 6, do nothing
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        // update password to user entered new password
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // if success, make toast of success, and go to login page
                                            Toast.makeText(MainActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            // if fails to update, give error toast
                                            Toast.makeText(MainActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    // if password is null, give error
                    newPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        // on clicking cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // make nre password field, cancel button, change button,subscribe and unsubscribe button invisible
                newPassword.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                subscribe.setVisibility(View.GONE);
                unsubscribe.setVisibility(View.GONE);

                //// make delete account, signout, change password and alert subscription button visible
                btnChangePassword.setVisibility(View.VISIBLE);
                btnRemoveUser.setVisibility(View.VISIBLE);
                signOut.setVisibility(View.VISIBLE);
                notification.setVisibility(View.VISIBLE);
                //--
            }
        });

        // on clicking delete account button
        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ask user confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete your account?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        //if confirms yes and user instance is not null, then delete account, and go to login page
                        progressBar.setVisibility(View.VISIBLE);
                        if (user != null) {
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                finish();
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                        //---
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    // if no, dismiss dialogue
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }

        });

        //on clicking signout button
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut(); // signout user
            }
        });

    }

    //sign out method
    public void signOut() {
        //make current user signout of app.
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        // listen to authentication instance
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
