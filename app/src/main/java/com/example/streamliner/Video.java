package com.example.streamliner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Video {
    private String link;
    private String title;

    public Video() { }

    public Video(String link, String title) {
        this.link = link;
        this.title = title;
    }

    public String getLink() { return link; }

    public void setLink(String link) { this.link = link; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getVideoId() {
        if (link == null) return "";
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(link);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public String getThumbnailUrl() {
        return "https://img.youtube.com/vi/" + getVideoId() + "/0.jpg";
    }
}
