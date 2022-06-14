package Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Picture;
import android.media.MediaParser;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.FileUtils;
import com.example.instagram.Post;
import com.example.instagram.PostActivity;
import com.example.instagram.PostsAdapter;
import com.example.instagram.ProfPostsAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    // Constant number to load each time we want to load more posts
    private static final int loadRate = 20;

    // Constant number of columns in the Grid View
    private static final int NUMBER_OF_COLS = 2;

    // Number to skip when loading more posts
    private int skipVal;

    // Elements in the fragment
    RecyclerView rvProfPosts;
    ImageView prof_pfp;
    TextView prof_username;

    private static final String TAG = "ProfileFragment";

    // Holds posts in the recycler view
    List<Post> Posts;

    ProfPostsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    GridLayoutManager layoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the elements
        rvProfPosts = view.findViewById(R.id.rvProfPosts);
        prof_pfp = view.findViewById(R.id.prof_pfp);
        prof_username = view.findViewById(R.id.prof_username);


        // Initialize the posts
        Posts = new ArrayList<>();

        // Query the posts and save them to the Posts list
        skipVal = 0;
        queryPosts();


        // Setup the profile content
        prof_username.setText(ParseUser.getCurrentUser().getUsername());
        Object img = ParseUser.getCurrentUser().get("pfp_img");
        if (img == null) {
            Glide.with(view.getContext())
                    .load(R.drawable.default_pfp)
                    .into(prof_pfp);
        }
        else {
            Glide.with(view.getContext())
                    .load(((ParseFile) img).getUrl())
                    .error(R.drawable.default_pfp)
                    .circleCrop()
                    .into(prof_pfp);
        }



        // When the user profile picture is clicked, allow the
        // user to upload a new profile picture
        prof_pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 21);
            }
        });



        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Clear out all old posts
                Posts = new ArrayList<>();
                skipVal = 0;

                // Get all posts
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



    // When the get file intent is done, get the file information
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the user sent back an image
        if ((data != null) && requestCode == 21) {
            // Get the URI of the image
            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            selectedImage = Bitmap.createScaledBitmap(selectedImage, 200, 200, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapBytes = stream.toByteArray();

            ParseFile imageFile = new ParseFile("pfp_img", bitmapBytes);
            imageFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ParseUser.getCurrentUser().put("pfp_img", imageFile);
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    // When the image is saved, display it
                                    Object img = ParseUser.getCurrentUser().get("pfp_img");
                                    Glide.with(getContext())
                                            .load(((ParseFile) img).getUrl())
                                            .error(R.drawable.default_pfp)
                                            .circleCrop()
                                            .into(prof_pfp);
                                }
                                else {
                                    Log.e(TAG, "File upload issue", e);
                                }
                            }
                        });
                    }
                    else {
                        Log.e(TAG, "File Save Issue", e);
                    }
                }
            });
        }


    }


    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
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

    // Get posts that a user has created
    private void queryPosts() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // Include data from the user table
        query.include(Post.KEY_USER);

        // Get only this user's posts
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        // Have the newest posts on top
        query.orderByDescending("updatedAt");

        // Skip some posts
        query.setSkip(skipVal*loadRate);

        // Set the limit to loadRate posts
        query.setLimit(loadRate);

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

                // Setup the recycler view if it isn't setup
                if (rvProfPosts.getAdapter() == null) {

                    // When the posts have been loaded, setup the recycler view -->
                    // Bind the adapter to the recycler view
                    adapter = new ProfPostsAdapter(Posts, getContext());
                    rvProfPosts.setAdapter(adapter);

                    // Configure the Recycler View: Layout Manager
                    layoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLS);
                    rvProfPosts.setLayoutManager(layoutManager);

                    // Used for infinite scrolling
                    rvProfPosts.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            queryPosts();
                        }
                    });
                }

                adapter.notifyDataSetChanged();

                // Increase the skip value
                skipVal+=1;
            }
        });
    }
}