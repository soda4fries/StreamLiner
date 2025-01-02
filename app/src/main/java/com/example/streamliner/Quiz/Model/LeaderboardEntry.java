package com.example.streamliner.Quiz.Model;

public class LeaderboardEntry {
    private final String userName;
    private final int score;

    public LeaderboardEntry(String userName, int score) {
        this.userName = userName;
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public int getScore() {
        return score;
    }
}