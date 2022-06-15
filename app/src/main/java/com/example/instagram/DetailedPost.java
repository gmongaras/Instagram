package com.example.instagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DetailedPost extends AppCompatActivity {

    // Constant number to load each time we want to load more posts
    private static final int loadRate = 5;

    // Number to skip when loading more posts
    private int skipVal;

    private static final int REPLY_TAG = 90;
    private static final String TAG = "DetailedPost";

    // Elements in the view
    ImageView det_image;
    TextView det_time;
    TextView det_username;
    TextView det_desc;
    ImageView det_prof_image;
    ImageView ivReply_det;
    RecyclerView rvComments;

    // The comments
    List<Comment> comments;

    // The post information
    Post post;

    CommAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);

        // Get all elements
        det_image = findViewById(R.id.det_image);
        det_time = findViewById(R.id.det_time);
        det_username = findViewById(R.id.det_username);
        det_desc = findViewById(R.id.det_desc);
        det_prof_image = findViewById(R.id.det_prof_image);
        ivReply_det = findViewById(R.id.ivReply_det);
        rvComments = findViewById(R.id.rvComments);

        // Initialize the comments list
        comments = new ArrayList<>();

        // Get the information out of the post
        post = (Post) getIntent().getExtras().get("Post");

        // Store the post text
        det_time.setText(MainActivity.getRelativeTimeAgo(post.getCreatedAt().toString()));
        det_username.setText(post.getUser().getUsername());
        det_desc.setText(post.getDescription());

        // Bind the image data
        Glide.with(this)
                .load(post.getImage().getUrl())
                .into(det_image);
        Object img = post.getUser().get("pfp_img");
        if (img == null) {
            Glide.with(this)
                    .load(R.drawable.default_pfp)
                    .into(det_prof_image);
        }
        else {
            Glide.with(this)
                    .load(((ParseFile) img).getUrl())
                    .error(R.drawable.default_pfp)
                    .circleCrop()
                    .into(det_prof_image);
        }

        // Set an onClick listener for the reply button
        ivReply_det.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to go to the reply page
                Intent i = new Intent(DetailedPost.this, ReplyActivity.class);
                i.putExtra("post", post);
                startActivityForResult(i, REPLY_TAG);
            }
        });

        // Query for any comments that have been created
        skipVal = 0;
        queryComments();
    }


    // Get comments that have been created
    private void queryComments() {
        // Specify which class to query
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);

        // Include data from the user table
        query.include(Comment.KEY_USER);

        // Include data from the post
        query.include(Comment.KEY_POST);

        // Have the newest posts on top
        query.orderByDescending("updatedAt");

        // Filter only the current post.
        query.whereEqualTo("post", post);

        // Skip some posts
        query.setSkip(skipVal*loadRate);

        // Set the limit to loadRate posts
        query.setLimit(loadRate);

        // Find all the posts the user has created
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comms, ParseException e) {
                // If an error occurred, log an error
                if (e != null) {
                    Log.e(TAG, "Issue retrieving all posts", e);
                    return;
                }

                // Store all posts in the Posts list
                comments.addAll(comms);

                // Setup the recycler view if it isn't setup
                if (rvComments.getAdapter() == null) {

                    // When the posts have been loaded, setup the recycler view -->
                    // Bind the adapter to the recycler view
                    adapter = new CommAdapter(comments, DetailedPost.this);
                    rvComments.setAdapter(adapter);

                    // Configure the Recycler View: Layout Manager
                    layoutManager = new LinearLayoutManager(DetailedPost.this);
                    rvComments.setLayoutManager(layoutManager);

                    // Used for infinite scrolling
                    rvComments.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            queryComments();
                        }
                    });
                }

                adapter.notifyDataSetChanged();

                // Increase the skip value
                skipVal+=1;
            }
        });
    }


    // When the reply is sent back, we want to go back to the main page

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}