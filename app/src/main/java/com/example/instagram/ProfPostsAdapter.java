package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProfPostsAdapter extends RecyclerView.Adapter<ProfPostsAdapter.ViewHolder> {
    // The posts in the recycler view
    List<Post> Posts;

    Context context;
    private static final String TAG = "ProfPostsAdapter";

    // Constructor to create the adapter with context and a list
    public ProfPostsAdapter(List<Post> posts, Context context) {
        Posts = posts;
        this.context = context;
    }

    // When a new view holder is created
    @NonNull
    @Override
    public ProfPostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create the view and inflate it
        View view = LayoutInflater.from(context).inflate(R.layout.prof_item_post, parent, false);

        // Return the view
        return new ViewHolder(view);
    }

    // Given a position in the Recycler View, bind data to that element
    @Override
    public void onBindViewHolder(@NonNull ProfPostsAdapter.ViewHolder holder, int position) {
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
        ImageView prof_post_image;
        TextView prof_post_desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Get the element contents
            prof_post_image = itemView.findViewById(R.id.prof_post_image);
            prof_post_desc = itemView.findViewById(R.id.prof_post_desc);
        }

        // Given a post, bind data to this object
        public void bind(Post post) {
            // Bind the text data
            if (post.getDescription().length() > 50) {
                prof_post_desc.setText(post.getDescription().substring(0, 50) + "...");
            }
            else {
                prof_post_desc.setText(post.getDescription());
            }

            // Bind the image data
            Glide.with(context)
                    .load(post.getImage().getUrl())
                    .into(prof_post_image);
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
