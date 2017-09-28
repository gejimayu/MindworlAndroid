package com.mindworld.howtosurvive.mindworld.models;

public class VideoFile {
    public String title;

    public VideoFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public VideoFile(String title) {
        this.title = title;
    }

    public String getName() {
        return title;
    }
}
