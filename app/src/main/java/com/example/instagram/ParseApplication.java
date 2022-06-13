package com.example.instagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register the parse models
        ParseObject.registerSubclass(Post.class);

        // Initialize parse
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("ylGCWmcvygeaH8YoOKkZmYWBcQUJORVTqEYvTVlb")
                .clientKey("OPuV9R81OEVVkPrBf2q0YM0mpHDsMxgzR34eGXYq")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
