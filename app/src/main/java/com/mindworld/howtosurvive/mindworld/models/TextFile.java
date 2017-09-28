package com.mindworld.howtosurvive.mindworld.models;

import java.util.HashMap;
import java.util.Map;

public class TextFile {
    public String uid;
    public String author;
    public String title;
    public String body;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public TextFile() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public TextFile(String uid, String author, String title, String body) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
    }
}
