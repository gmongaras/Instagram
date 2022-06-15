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
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

public class CommAdapter extends RecyclerView.Adapter<CommAdapter.ViewHolder> {

    // List of items
    List<Comment> comments;

    Context context;
    private static final String TAG = "CommAdapter";

    // Constructor to create the adapter with context and a list
    public CommAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    // When a view holder is created
    @NonNull
    @Override
    public CommAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view and inflate it
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);

        // Return the view
        return new ViewHolder(view);
    }

    // Given a position in the Recycler View, bind data to that element
    @Override
    public void onBindViewHolder(@NonNull CommAdapter.ViewHolder holder, int position) {
        // Get the item at the given position
        ParseObject comment = comments.get(position);

        // Bind the comment to the view holder
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    // Holds each comment view
    public class ViewHolder extends RecyclerView.ViewHolder {

        // The elements in the view
        ImageView comm_img;
        TextView comm_username;
        TextView comm_time;
        TextView comm_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize all the elements
            comm_img = itemView.findViewById(R.id.comm_img);
            comm_username = itemView.findViewById(R.id.comm_username);
            comm_time = itemView.findViewById(R.id.comm_time);
            comm_text = itemView.findViewById(R.id.comm_text);
        }

        public void bind(ParseObject comment) {
            // Bind text data to the view
            comm_username.setText(comment.getParseUser("user").getUsername());
            comm_time.setText(comment.getCreatedAt().toString());
            comm_text.setText(comment.getString("text"));

            // Bind the image data
            Object img = comment.getParseUser("user").get("pfp_img");
            if (img == null) {
                Glide.with(context)
                        .load(R.drawable.default_pfp)
                        .into(comm_img);
            }
            else {
                Glide.with(context)
                        .load(((ParseFile) img).getUrl())
                        .error(R.drawable.default_pfp)
                        .circleCrop()
                        .into(comm_img);
            }
        }

        ;
    }
}
