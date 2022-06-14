package Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.Post;
import com.example.instagram.PostActivity;
import com.example.instagram.PostsAdapter;
import com.example.instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    // Constant number to load each time we want to load more posts
    private static final int loadRate = 5;

    // Number to skip when loading more posts
    private int skipVal;

    // Elements in the application
    private RecyclerView rvPosts;

    private static final String TAG = "HomeFragment";

    // Holds posts to put into the Recycler View
    List<Post> Posts;

    PostsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the elements
        rvPosts = view.findViewById(R.id.rvPosts);

        // Initialize the posts
        Posts = new ArrayList<>();

        // Query the posts and save them to the Posts list
        skipVal = 0;
        queryPosts();


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

    // Get posts that a user has created
    private void queryPosts() {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // Include data from the user table
        query.include(Post.KEY_USER);

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
                if (rvPosts.getAdapter() == null) {

                    // When the posts have been loaded, setup the recycler view -->
                    // Bind the adapter to the recycler view
                    adapter = new PostsAdapter(Posts, getContext());
                    rvPosts.setAdapter(adapter);

                    // Configure the Recycler View: Layout Manager
                    layoutManager = new LinearLayoutManager(getContext());
                    rvPosts.setLayoutManager(layoutManager);

                    // Used for infinite scrolling
                    rvPosts.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
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