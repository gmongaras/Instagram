package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    // Elements in the activity
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the elements from the activity
        btnLogout = findViewById(R.id.btnLogout);



        // Add an onClick listener to log the user out
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }



    // Log the user out and send them back to the login page
    private void logout() {
        Toast.makeText(MainActivity.this, "Logging user out", Toast.LENGTH_SHORT).show();

        // Log the user out
        ParseUser.logOutInBackground();

        // Go back to the login page
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}