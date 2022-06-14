package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private EditText etUsername_reg;
    private EditText etPassword_reg;
    private EditText etPassword_reg2;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // If the user is already logged in, go straight to the main activity
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        // Get the items in the view
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        etUsername_reg = findViewById(R.id.etUsername_reg);
        etPassword_reg = findViewById(R.id.etPassword_reg);
        etPassword_reg2 = findViewById(R.id.etPassword_reg2);
        btnRegister = findViewById(R.id.btnRegister);

        // Create an onclick listener for the Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        // Create an onClick listener for the Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRegister.setClickable(false);
                Log.i(TAG, "onClick register button");

                // Send a toast if the passwords do not match
                if (etPassword_reg.getText() == etPassword_reg2.getText()) {
                    Toast.makeText(LoginActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
                    btnRegister.setClickable(true);
                    return;
                }

                // Log the user in
                String username = etUsername_reg.getText().toString();
                String password = etPassword_reg.getText().toString();
                registerUser(username, password);
            }
        });
    }


    // Register a user to the app given the username and password
    private void registerUser(String username, String password) {
        Log.i(TAG, "Attempting to register user " + username);

        // Create a new user
        ParseUser user = new ParseUser();

        // Fill in the user details
        user.setUsername(username);
        user.setPassword(password);

        // Sign the user up
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(LoginActivity.this, "User Registered!", Toast.LENGTH_SHORT);
                }
                else {
                    Log.e(TAG, "Unable to register user", e);
                    Toast.makeText(LoginActivity.this, "Unable to register user", Toast.LENGTH_SHORT);
                }
            }
        });

        btnRegister.setClickable(true);
    }

    // Login a user to the app given the username and password
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);

        // Log the user in using our Parse backend
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                // If the is a problem, then e will not be null.
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                // If e is null, then there is no error.
                // In this case, the user successfully logged in and
                // we should take them to the main page
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Go to the main activity when the user has logged in
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}