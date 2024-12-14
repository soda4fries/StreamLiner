package com.example.streamliner.Quiz.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quiz {
    private String id;
    private String title;
    private String description;
    private String topic;
    private long createdAt;
    private int timeLimit; // in minutes
    private List<Question> questions;
    private Map<String, Integer> leaderboard; // userId -> score

    public Quiz() {}

    public Quiz(String title, String description, String topic, int timeLimit) {
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.timeLimit = timeLimit;
        this.createdAt = System.currentTimeMillis();
        this.questions = new ArrayList<>();
        this.leaderboard = new HashMap<>();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
    public Map<String, Integer> getLeaderboard() { return leaderboard; }
    public void setLeaderboard(Map<String, Integer> leaderboard) { this.leaderboard = leaderboard; }
}