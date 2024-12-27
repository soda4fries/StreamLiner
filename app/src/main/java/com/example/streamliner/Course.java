package com.example.streamliner;

import java.util.List;

public class Course {
    private String id;
    private String name;
    private String description;
    private String subject;
    private List<String> fields;
    private List<String> learningOutcomes;
    private List<Quiz> quizzes;
    private List<Practice> practices;

    public Course() {}

    public Course(String name, String description, String subject, List<String> fields, List<String> learningOutcomes, List<Quiz> quizzes, List<Practice> practices) {
        this.name = name;
        this.description = description;
        this.subject = subject;
        this.fields = fields;
        this.learningOutcomes = learningOutcomes;
        this.quizzes = quizzes;
        this.practices = practices;
    }

    // Getters and Setters
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getSubject() { return subject; }

    public void setSubject(String subject) { this.subject = subject; }

    public List<String> getField() { return fields; }

    public void setField(List<String> fields) { this.fields = fields; }

    public List<String> getLearningOutcomes() { return learningOutcomes; }

    public void setLearningOutcomes(List<String> learningOutcomes) { this.learningOutcomes = learningOutcomes; }

    public List<Quiz> getQuizzes() { return quizzes; }

    public void setQuizzes(List<Quiz> quizzes) { this.quizzes = quizzes; }

    public List<Practice> getPractices() { return practices; }

    public void setPractices(List<Practice> practices) { this.practices = practices; }
}
