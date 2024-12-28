package com.example.streamliner;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Quiz implements Parcelable {
    private String title;
    private List<Question> questions;

    public Quiz() {}

    public Quiz(String title, List<Question> questions) {
        this.title = title;
        this.questions = questions;
    }

    // Parcelable constructor
    protected Quiz(Parcel in) {
        title = in.readString();
        questions = new ArrayList<>();
        in.readTypedList(questions, Question.CREATOR);
    }

    // Creator
    public static final Creator<Quiz> CREATOR = new Creator<Quiz>() {
        @Override
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        @Override
        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

    // Getters and Setters
    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<Question> getQuestions() { return questions; }

    public void setQuestions(List<Question> questions) { this.questions = questions; }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(questions);
    }
}
