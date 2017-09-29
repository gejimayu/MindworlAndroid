package com.mindworld.howtosurvive.mindworld.models;

public class ImageFile {
    public String name;
    public String location;
    public String url;

    public ImageFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public ImageFile(String name, String location, String url) {
        this.name = name;
        this.location = location;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return url;
    }

    public String getLocation() { return location; }
}
