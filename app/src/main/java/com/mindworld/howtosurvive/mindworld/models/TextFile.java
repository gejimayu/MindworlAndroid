package com.mindworld.howtosurvive.mindworld.models;

public class TextFile {
    public String title;
    public String location;

    public TextFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public TextFile(String title, String location) {
        this.title = title;
        this.location = location;
    }

    public String getName() {
        return title;
    }

    public String getLocation() {
        return location;
    }
}
