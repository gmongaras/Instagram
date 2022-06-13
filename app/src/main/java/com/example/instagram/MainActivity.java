package com.example.instagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Elements in the application
    private Button btnLogout;
    private Toolbar tbMain;
    private RecyclerView rvPosts;

    private static final String TAG = "MainActivity";

    // Holds posts to put into the Recycler View
    List<Post> Posts;

    PostsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the elements
        btnLogout = findViewById(R.id.btnLogout);
        rvPosts = findViewById(R.id.rvPosts);

        // Get the action bar and set it up
        tbMain = (Toolbar) findViewById(R.id.tbMain);
        setSupportActionBar(tbMain);

        // Query the posts and save them to the Posts list
        queryPosts();




        // Add an onClick listener to log the user out
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts();
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }



    // Get posts that a user has created
    private void queryPosts() {
        // Clear out all old posts
        Posts = new ArrayList<>();

        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // Include data from the user table
        query.include(Post.KEY_USER);

        // Set the limit to 20 posts
        query.setLimit(20);

        // Find all the posts the user has created
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // If an error occurred, log an error
                if (e != null) {
                    Log.e(TAG, "Issue retrieving all posts", e);
                    return;
                }

                // Store all posts in the Posts list
                Posts.addAll(posts);

                // When the posts have been loaded, setup the recycler view -->
                // Bind the adapter to the recycler view
                adapter = new PostsAdapter(Posts, MainActivity.this);
                rvPosts.setAdapter(adapter);

                // Configure the Recycler View: Layout Manager
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                rvPosts.setLayoutManager(linearLayoutManager);
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


    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle clicks on the Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Click on the create post icon
            case R.id.miPost:
                // When the post button is clicked, navigate to create a new post
                Intent i = new Intent(this, PostActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}