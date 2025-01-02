package com.example.streamliner.studyMaterials;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Question implements Parcelable {
    private String title;
    private List<String> answers;
    private int correctIndex;

    public Question() {}

    public Question(String title, List<String> answers, int correctAnswerIndex) {
        this.title = title;
        this.answers = answers;
        this.correctIndex = correctAnswerIndex;
    }

    // Getters and Setters
    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<String> getAnswers() { return answers; }

    public void setAnswers(List<String> answers) { this.answers = answers; }

    public int getCorrectIndex() { return correctIndex; }

    public void setCorrectIndex(int correctAnswerIndex) { this.correctIndex = correctAnswerIndex; }

    @Override
    public String toString() {
        return "Question{" +
                "title='" + title + '\'' +
                ", answers=" + answers +
                ", correctAnswerIndex=" + correctIndex +
                '}';
    }

    // Parcelable implementation
    protected Question(Parcel in) {
        title = in.readString();
        answers = in.createStringArrayList();
        correctIndex = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeStringList(answers);
        dest.writeInt(correctIndex);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
