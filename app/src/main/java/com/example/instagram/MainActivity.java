package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import Fragments.ComposeFragment;
import Fragments.HomeFragment;
import Fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    // Constant number to load each time we want to load more posts
    private static final int loadRate = 5;

    // Number to skip when loading more posts
    private int skipVal;

    // Elements in the application
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNav;

    private static final String TAG = "MainActivity";

    // The fragment being displayed
    Fragment fragment;

    // Holds posts to put into the Recycler View
    List<Post> Posts;

    PostsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the elements
        bottomNav = findViewById(R.id.bottomNav);

        // Initialize the posts
        Posts = new ArrayList<>();



        // Fragments we can go to
        final Fragment Home = new HomeFragment();
        final Fragment Compose = new ComposeFragment();
        final Fragment Profile = new ProfileFragment();


        // Allow clicks on the Bottom Navigation View
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            // Note: a MenuItem will be one of the items in the menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    // If the menu item clicked is home
                    case R.id.action_home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        fragment = Home;
                        break;

                    // If the menu item clicked is compose
                    case R.id.action_compose:
                        Toast.makeText(MainActivity.this, "Compose", Toast.LENGTH_SHORT).show();
                        fragment = Compose;
                        break;

                    // If the menu item clicked is profile
                    case R.id.action_profile:
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        fragment = Profile;
                        break;

                    // If the id of logout has been tapped, log the user out
                    case R.id.action_logout:
                        Toast.makeText(MainActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();
                        logout();
                }

                // Replace the frame layout with one of the fragments
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();

                return true;
            }
        });

        // Set the default fragment to load
        bottomNav.setSelectedItemId(R.id.action_home);
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