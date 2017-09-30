package com.mindworld.howtosurvive.mindworld.models;

import android.net.Uri;

public class VideoFile {
    public String title;
    public String location;
    public Uri uriFile;
    public String url;

    public VideoFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public VideoFile(String title, String location, Uri uriFile, String url) {
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

    public Uri getUri() {
        return uriFile;
    }

    public String getUrl() {
        return url;
    }
}
