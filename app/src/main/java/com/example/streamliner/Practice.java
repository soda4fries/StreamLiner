package com.example.streamliner;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Practice implements Parcelable {
    private String title;
    private List<Question> questions;

    public Practice() {}

    public Practice(String title, List<Question> questions) {
        this.title = title;
        this.questions = questions;
    }

    // Parcelable constructor
    protected Practice(Parcel in) {
        title = in.readString();
        questions = new ArrayList<>();
        in.readTypedList(questions, Question.CREATOR);
    }

    // Creator
    public static final Creator<Practice> CREATOR = new Creator<Practice>() {
        @Override
        public Practice createFromParcel(Parcel in) {
            return new Practice(in);
        }

        @Override
        public Practice[] newArray(int size) {
            return new Practice[size];
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
