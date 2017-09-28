package com.mindworld.howtosurvive.mindworld.models;

public class TextFile {
    public String title;

    public TextFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public TextFile(String title) {
        this.title = title;
    }
}
