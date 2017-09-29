package com.mindworld.howtosurvive.mindworld.models;

public class VideoFile {
    public String title;
    public String location;

    public VideoFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public VideoFile(String title, String location) {
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
