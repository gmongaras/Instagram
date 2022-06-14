package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailedPost extends AppCompatActivity {

    // Elements in the view
    ImageView det_image;
    TextView det_time;
    TextView det_username;
    TextView det_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_post);

        // Get all elements
        det_image = findViewById(R.id.det_image);
        det_time = findViewById(R.id.det_time);
        det_username = findViewById(R.id.det_username);
        det_desc = findViewById(R.id.det_desc);

        // Get the information out of the post
        Post post = (Post) getIntent().getExtras().get("Post");

        // Store the post text
        det_time.setText(MainActivity.getRelativeTimeAgo(post.getCreatedAt().toString()));
        det_username.setText(post.getUser().getUsername());
        det_desc.setText(post.getDescription());

        // Bind the image data
        Glide.with(this)
                .load(post.getImage().getUrl())
                .into(det_image);
    }
}