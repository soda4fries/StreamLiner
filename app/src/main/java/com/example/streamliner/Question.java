package com.example.streamliner;

import java.util.List;

public class Question {
    private String title;
    private List<String> answers;
    private int correctAnswerIndex;

    public Question() {}

    public Question(String title, List<String> answers, int correctAnswerIndex) {
        this.title = title;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    // Getters and Setters
    public String getTitle() { return title; }

    public void setTitle(String id) { this.title = title; }

    public List<String> getAnswers() { return answers; }

    public void setAnswers(List<String> answers) { this.answers = answers; }

    public int getCorrectAnswerIndex() { return correctAnswerIndex; }

    public void setCorrectAnswerIndex(int correctAnswerIndex) { this.correctAnswerIndex = correctAnswerIndex; }
}
