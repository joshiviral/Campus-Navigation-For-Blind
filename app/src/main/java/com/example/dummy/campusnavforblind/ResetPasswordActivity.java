package com.example.dummy.campusnavforblind;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail; // reset password edittext field
    private Button btnReset, btnBack; // button for reset and back button
    private FirebaseAuth auth; // firebase object
    private ProgressBar progressBar; // progress bar object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password); // set layout file

        inputEmail = (EditText) findViewById(R.id.email); // email edittext
        btnReset = (Button) findViewById(R.id.btn_reset_password); // reset button
        btnBack = (Button) findViewById(R.id.btn_back); // back button
        progressBar = (ProgressBar) findViewById(R.id.progressBar); // progressbar button

        auth = FirebaseAuth.getInstance(); // get instance of firebase

        // on clicking back button, go to login page
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // on clicking reset button
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get string data from editbox
                String email = inputEmail.getText().toString().trim();

                // if edittext field is empty , give toast message and return to login page
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                // make progress bar visible
                progressBar.setVisibility(View.VISIBLE);
                //method to send password recovery link to given email id
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // if link sent then, give toast for go to email
                                    Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // if no link sent, give error message
                                    Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }
                                // make progressbar invisible
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

}
