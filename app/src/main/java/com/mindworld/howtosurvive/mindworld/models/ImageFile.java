package com.mindworld.howtosurvive.mindworld.models;

public class ImageFile {
    public String name;
    public String location;
    public String uriFile;
    public String url;
    public String uploaderID;

    public ImageFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public ImageFile(String name, String location, String uriFile, String url, String uploaderID) {
        this.name = name;
        this.location = location;
        this.uriFile = uriFile;
        this.url = url;
        this.uploaderID = uploaderID;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getLocation() {
        return location;
    }

    public String getUri() {
        return uriFile;
    }

    public String getUploaderID() {
        return uploaderID;
    }
}
