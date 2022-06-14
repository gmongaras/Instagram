package Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.Post;
import com.example.instagram.PostActivity;
import com.example.instagram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int RESULT_OK = -1;

    // Elements in the activity
    private EditText etDescription;
    private Button btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;

    private File photoFile; // Variable to save a photo to
    public String photoFileName = "photo.jpg"; // Name of file to save a photo to

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the elements from the activity
        etDescription = view.findViewById(R.id.etDescription);
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        ivPostImage = view.findViewById(R.id.ivPostImage);
        btnSubmit = view.findViewById(R.id.btnSubmit);



        // Add an on click listener to submit
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the button unclickable
                btnSubmit.setClickable(false);

                // Get the post details
                String description = etDescription.getText().toString();

                // Error handling with the description
                if (description.isEmpty()) {
                    Toast.makeText(view.getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();

                    // Make the button clickable
                    btnSubmit.setClickable(true);
                    return;
                }

                // Error handling with the photo
                if (photoFile == null || ivPostImage.getDrawable() == null) {
                    Toast.makeText(view.getContext(), "There is no image!", Toast.LENGTH_SHORT).show();

                    // Make the button clickable
                    btnSubmit.setClickable(true);
                    return;
                }

                // Get the user who made the post
                ParseUser currentUser = ParseUser.getCurrentUser();

                // Save a new post to the database
                savePost(description, currentUser, photoFile);

                Toast.makeText(view.getContext(), "Post saved!", Toast.LENGTH_SHORT).show();

                // Make the button clickable
                btnSubmit.setClickable(true);
            }
        });

        // Add an on click listener to take a photo with the camera
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
    }

    // Save a new post to the database
    private void savePost(String description, ParseUser user, File photoFile) {
        // Create the post object and set the information
        Post post = new Post();
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(user);

        // Save the post
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // If an error occurred, log it
                if (e != null) {
                    Log.e(TAG, "Unable to save post", e);
                    Toast.makeText(getContext(), "Unable to save post", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Post saved!");

                // Clear out the description text and the image
                etDescription.setText("");
                ivPostImage.setImageResource(0);

                // Go back to the main activity
                //finish();
            }
        });
    }


    // Launch the camera to allow the user to take a picture with it
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        // If a camera cannot be found, log it
        else {
            Log.e(TAG, "Camera could not be opened");
        }
    }

    // When a child application (the camera) returns to this application, this method
    // is run.
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When the camera is done, load the picture into the app
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPostImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}