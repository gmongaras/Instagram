package com.example.instagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class DetailedPost extends AppCompatActivity {

    private static final int REPLY_TAG = 90;
    private static final String TAG = "DetailedPost";

    // Elements in the view
    ImageView det_image;
    TextView det_time;
    TextView det_username;
    TextView det_desc;
    ImageView det_prof_image;
    ImageView ivReply_det;

    // The post information
    Post post;

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
    }


    // When the reply is sent back, we want to go back to the main page

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}