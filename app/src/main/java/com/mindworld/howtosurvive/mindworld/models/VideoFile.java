package com.mindworld.howtosurvive.mindworld.models;

public class VideoFile {
    public String title;
    public String location;
    public String uriFile;
    public String url;
    public String uploaderID;

    public VideoFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public VideoFile(String title, String location, String uriFile, String url, String uploaderID) {
        this.title = title;
        this.location = location;
        this.uriFile = uriFile;
        this.url = url;
        this.uploaderID = uploaderID;
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

    public String getUploaderID() {
        return uploaderID;
    }
}
