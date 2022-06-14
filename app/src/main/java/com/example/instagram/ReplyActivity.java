package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ReplyActivity extends AppCompatActivity {

    private static final String TAG = "ReplyActivity";

    // Elements in the view
    EditText tvReply;
    Button replyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        // Get the view elements
        tvReply = findViewById(R.id.tvReply);
        replyBtn = findViewById(R.id.replyBtn);

        // Add an on click listener for the button
        replyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensure the text is not empty
                if (tvReply.getText().toString().isEmpty()) {
                    Toast.makeText(ReplyActivity.this, "Response is empty.", Toast.LENGTH_SHORT);
                    return;
                }

                // Create the comment
                ParseObject comment = ParseObject.create("Comment");
                //Comment comment = new Comment();

                // Get the post
                comment.put("post", getIntent().getParcelableExtra("post"));
//                comment.setPost(getIntent().getParcelableExtra("post"));
//
//                // Get the user information
                comment.put("user", ParseUser.getCurrentUser());
//                comment.setUser(ParseUser.getCurrentUser());
//
//                // Get the text
                comment.put("text", tvReply.getText().toString());
//                comment.setText(tvReply.toString());

                // Save the comment
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        // If an error occured, log it
                        if (e != null) {
                            Log.e(TAG, "Unable to save comment to database", e);
                            return;
                        }

                        Log.i(TAG, "Comment Saved!");

                        // Go back to the main
                        finish();
                    }
                });
            }
        });
    }
}