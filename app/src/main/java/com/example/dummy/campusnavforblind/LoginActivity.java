/*
This is login page activity, which deals with user login
 */
package com.example.dummy.campusnavforblind;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;  // edittext of email and password field
    private FirebaseAuth auth; // firebase authentication object
    private ProgressBar progressBar; // progress bar object
    private Button btnSignup, btnLogin, btnReset; // Button of login,signup and forgot password
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance(); // get firebase instance

        // if session data exist with firebase, then to need for internet, go to home page
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, ActivityHomePage.class));
            finish();
        }


        setContentView(R.layout.activity_login); // setting layout


        // edittext object of email and password
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);

        // object of progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //object of sign up,login and forgot password
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);


        auth = FirebaseAuth.getInstance(); // getting firebase instance

        // on clicking signup button, go to signup page
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        //on clicking reset button, go to forgot password page
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

       // on clicking login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get user entered email and password in edittext
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                // if email is empty, then give toast and return
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // if password field is empty, give toast message and return
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // set progress bar visible
                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            // if authenticated
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // set progress bar invisible
                                progressBar.setVisibility(View.GONE);
                                // if fails
                                if (!task.isSuccessful()) {
                                    // if password less than 6 digit, give error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        // give error message
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    //if authetication happen to be true, go to home page
                                    Intent intent = new Intent(LoginActivity.this, ActivityHomePage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

}
