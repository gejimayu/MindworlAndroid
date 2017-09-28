package com.mindworld.howtosurvive.mindworld.models;

import static android.R.attr.author;

public class ImageFile {
    public String name;
    public String url;

    public ImageFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public ImageFile(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getImageName() {
        return name;
    }

    public String getImageURL() {
        return url;
    }
}
