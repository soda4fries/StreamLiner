package com.example.streamliner.Quiz.Model;

import java.util.HashMap;
import java.util.Map;

public class QuizAttempt {
    private String userId;
    private String quizId;
    private long startTime;
    private long endTime;
    private int score;
    private Map<String, Integer> answers;

    public QuizAttempt() {
        // Required empty constructor for Firebase
        this.answers = new HashMap<>();
    }

    public QuizAttempt(String userId, String quizId) {
        this.userId = userId;
        this.quizId = quizId;
        this.startTime = System.currentTimeMillis();
        this.answers = new HashMap<>();
    }

    // Modified method to store answer
    public void storeAnswer(int questionIndex, int selectedOption) {
        answers.put(String.valueOf(questionIndex), selectedOption);
    }

    // Modified method to get answer
    public Integer getAnswer(int questionIndex) {
        return answers.get(String.valueOf(questionIndex));
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<String, Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, Integer> answers) {
        this.answers = answers;
    }
}