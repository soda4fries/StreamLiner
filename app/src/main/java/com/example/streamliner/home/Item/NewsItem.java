package com.example.streamliner.home.Item;

import androidx.annotation.Nullable;

public class NewsItem {
    private String id;
    private String title;
    private String description;
    private long timestamp;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NewsItem newsItem = (NewsItem) obj;
        return id.equals(newsItem.id) &&
                title.equals(newsItem.title) &&
                description.equals(newsItem.description) &&
                timestamp == newsItem.timestamp;
    }
}
