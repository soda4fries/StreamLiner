package com.example.streamliner.viewMarks;

public class QuizMark {
    // Variables for View Quiz Marks
    private String title;
    private int mark;
    private boolean completed;
    private String date; // Date when user submits the quiz

    public QuizMark() {}

    public QuizMark(String title, int mark, boolean completed, String date) {
        this.title = title;
        this.mark = mark;
        this.completed = completed;
        this.date = date;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public int getMark() { return mark; }

    public void setMark(int mark) {this.mark = mark; }

    public boolean getCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }
}
