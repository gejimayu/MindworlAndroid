package com.mindworld.howtosurvive.mindworld.models;

import android.net.Uri;

public class ImageFile {
    public String name;
    public String location;
    public String url;
    public String uriFile;

    public ImageFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public ImageFile(String name, String location, String uriFile, String url) {
        this.name = name;
        this.location = location;
        this.url = url;
        this.uriFile = uriFile;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return url;
    }

    public String getLocation() { return location; }

    public String getUri() {
        return uriFile;
    }
}
