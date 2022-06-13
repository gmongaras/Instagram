package com.example.instagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    // Keys (columns names) in the database for each post
    public static final String KEY_DESCRIPTION  = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER  = "user";

    // Getter and setter methods for the description
    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    // Getter and setter methods for the image
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile Image) {
        put(KEY_IMAGE, Image);
    }

    // Getter and setter methods for the user who made this post
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
