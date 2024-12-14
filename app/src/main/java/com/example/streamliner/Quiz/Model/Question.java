package com.example.streamliner.Quiz.Model;

import java.util.List;

public class Question {
    private String text;
    private List<String> options;
    private int correctOptionIndex;
    private int points;

    public Question() {
    }

    public Question(String text, List<String> options, int correctOptionIndex, int points) {
        this.text = text;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.points = points;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}