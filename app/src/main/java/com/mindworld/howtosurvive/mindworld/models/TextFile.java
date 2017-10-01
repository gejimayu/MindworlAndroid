package com.mindworld.howtosurvive.mindworld.models;

import android.net.Uri;

public class TextFile {
    public String title;
    public String location;
    public String uriFile;
    public String url;

    public TextFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public TextFile(String title, String location, String uriFile, String url) {
        this.title = title;
        this.location = location;
        this.uriFile = uriFile;
        this.url = url;
    }

    public String getName() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getUri() {
        return uriFile;
    }

    public String getUrl() {
        return url;
    }
}
