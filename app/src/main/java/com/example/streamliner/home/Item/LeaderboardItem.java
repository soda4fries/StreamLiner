package com.example.streamliner.home.Item;

import java.util.Objects;

// Data Classes
public class LeaderboardItem {
    private String userId;
    private String username;
    private int points;

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LeaderboardItem that = (LeaderboardItem) obj;
        return points == that.points &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, points);
    }
}