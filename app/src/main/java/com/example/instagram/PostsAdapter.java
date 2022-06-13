package com.example.instagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    // The posts in the recycler view
    List<Post> Posts;

    Context context;
    private static final String TAG = "PostsAdapter";

    // Constructor to create the adapter with context and a list
    public PostsAdapter(List<Post> posts, Context context) {
        Posts = posts;
        this.context = context;
    }

    // When a new view holder is created
    @NonNull
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create the view and inflate it
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get the element contents
            post_username = itemView.findViewById(R.id.post_username);
            post_image = itemView.findViewById(R.id.post_image);
            post_desc = itemView.findViewById(R.id.post_desc);
        }

        // Given a post, bind data to this object
        public void bind(Post post) {
            // Bind the text data
            post_username.setText(post.getUser().getUsername());
            post_desc.setText(post.getDescription());

            // Bind the image data if it's not null
            Glide.with(context)
                    .load(post.getImage().getUrl())
                    .into(post_image);
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