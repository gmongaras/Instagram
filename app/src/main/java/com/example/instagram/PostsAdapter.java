package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Objects;

import Fragments.ProfileFragment;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    // The posts in the recycler view
    List<Post> Posts;

    // The parent of the recycler view
    private ViewGroup parent;

    // Fragment manager for the home fragment
    FragmentManager fragmentManager;

    Context context;
    private static final String TAG = "PostsAdapter";

    // Constructor to create the adapter with context and a list
    public PostsAdapter(List<Post> posts, Context context, FragmentManager fragmentManager) {
        Posts = posts;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    // When a new view holder is created
    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create the view and inflate it
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);

        // Save the parent information
        this.parent = parent;

        // Return the view
        return new ViewHolder(view);
    }

    // Given a position in the Recycler View, bind data to that element
    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        // Get the item at the given position
        Post post = Posts.get(position);

        // Bind the post to the view holder
        holder.bind(post);

        // Put an onClick listener on the view to go into the detailed view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, DetailedPost.class);
                i.putExtra("Post", post);
                context.startActivity(i);
            }
        });
    }

    // Get the number of posts in the recycler view
    @Override
    public int getItemCount() {
        return Posts.size();
    }

    // The view holder to hold each post
    public class ViewHolder extends RecyclerView.ViewHolder {
        // The elements in the view holder
        TextView post_username;
        ImageView post_image;
        TextView post_desc;
        ImageView post_pfp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get the element contents
            post_username = itemView.findViewById(R.id.post_username);
            post_image = itemView.findViewById(R.id.prof_post_image);
            post_desc = itemView.findViewById(R.id.prof_post_desc);
            post_pfp = itemView.findViewById(R.id.post_pfp);
        }

        // Given a post, bind data to this object
        public void bind(Post post) {
            // Bind the text data
            post_username.setText(post.getUser().getUsername());
            if (post.getDescription().length() > 50) {
                post_desc.setText(post.getDescription().substring(0, 50) + "...");
            }
            else {
                post_desc.setText(post.getDescription());
            }

            // Bind the image data
            Glide.with(context)
                    .load(post.getImage().getUrl())
                    .into(post_image);

            // Bind the profile picture data
            Object img = post.getUser().get("pfp_img");
            if (img == null) {
                Glide.with(context)
                        .load(R.drawable.default_pfp)
                        .into(post_pfp);
            }
            else {
                Glide.with(context)
                        .load(((ParseFile) img).getUrl())
                        .error(R.drawable.default_pfp)
                        .circleCrop()
                        .into(post_pfp);
            }

            // Put an onClick listener onto the username
            post_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When the username is clicked, change the fragment to
                    // their user page
                    Fragment fragment;
                    if (Objects.equals(ParseUser.getCurrentUser().getUsername(), post.getUser().getUsername())) {
                        fragment = new ProfileFragment("main", ParseUser.getCurrentUser());
                    }
                    else {
                        fragment = new ProfileFragment("other", post.getUser());
                    }
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
            });
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        Posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        Posts.addAll(list);
        notifyDataSetChanged();
    }
}
