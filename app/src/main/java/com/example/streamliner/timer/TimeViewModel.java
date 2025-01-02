package com.example.streamliner.timer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TimeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TimeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is time fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}