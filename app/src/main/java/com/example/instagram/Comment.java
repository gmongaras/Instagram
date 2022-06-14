package com.example.instagram;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    // Keys (columns names) in the database for each post
    public static final String KEY_TEXT  = "text";
    public static final String KEY_USER  = "user";
    public static final String KEY_POST  = "post";

    // Getter and setter methods for the description
    public String getText() {
        return getString(KEY_TEXT);
    }
    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    // Getter and setter methods for the user
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    // Getter and setter methods for the post
    public String getPost() {
        return getString(KEY_POST);
    }
    public void setPost(String post) {
        put(KEY_POST, post);
    }
}
