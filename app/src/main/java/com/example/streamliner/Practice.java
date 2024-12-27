package com.example.streamliner;

import java.util.List;

public class Practice {
    private String title;
    private List<Question> questions;

    public Practice() {}

    public Practice(String title, List<Question> questions) {
        this.title = title;
        this.questions = questions;
    }

    // Getters and Setters
    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<Question> getQuestions() { return questions; }

    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
