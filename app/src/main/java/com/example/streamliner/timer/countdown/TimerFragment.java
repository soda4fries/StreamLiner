package com.example.streamliner.timer.countdown;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.streamliner.R;

import java.util.Locale;

public class TimerFragment extends Fragment {

    private NumberPicker hourPicker, minutePicker, secondPicker;
    private TextView timerTextView;
    private Button startButton;
    private ImageView iconImageView;
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long timeLeftInMillis = 0;
    private MediaPlayer alarmSound;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        // Initialize UI elements
        hourPicker = view.findViewById(R.id.hourPicker);
        minutePicker = view.findViewById(R.id.minutePicker);
        secondPicker = view.findViewById(R.id.secondPicker);
        timerTextView = view.findViewById(R.id.timerTextView);
        startButton = view.findViewById(R.id.startButton);
        iconImageView = view.findViewById(R.id.iconImageView);

        // Initialize alarm sound
        alarmSound = MediaPlayer.create(getContext(), R.raw.alarm);

        // Configure NumberPickers
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        // Start/Pause Timer functionality
        startButton.setOnClickListener(v -> {
            if (isRunning) {
                pauseTimer();
            } else {
                // Validate if time is selected
                if (isTimeValid()) {
                    startTimer();
                } else {
                    // Show toast notification for invalid time selection
                    Toast.makeText(getContext(), "Invalid! Please enter a time.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean isTimeValid() {
        // Check if at least one picker has a value greater than 0
        int hours = hourPicker.getValue();
        int minutes = minutePicker.getValue();
        int seconds = secondPicker.getValue();

        return hours > 0 || minutes > 0 || seconds > 0; // Time is valid if any picker has a non-zero value
    }

    private void startTimer() {
        if (!isRunning) {
            // Calculate total time in milliseconds
            int hours = hourPicker.getValue();
            int minutes = minutePicker.getValue();
            int seconds = secondPicker.getValue();

            long totalTime = (hours * 3600L + minutes * 60L + seconds) * 1000L;
            if (timeLeftInMillis == 0) {
                timeLeftInMillis = totalTime;
            }

            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateTimerText();
                }

                @Override
                public void onFinish() {
                    timeLeftInMillis = 0;
                    isRunning = false;
                    startButton.setText("Start");

                    // Play alarm sound
                    playAlarm();
                }
            }.start();

            isRunning = true;
            startButton.setText("Pause");
        }
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isRunning = false;
        startButton.setText("Resume");
    }

    private void updateTimerText() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    private void playAlarm() {
        alarmSound.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isRunning) {
            pauseTimer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isRunning && timeLeftInMillis > 0) {
            startTimer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (alarmSound != null) {
            alarmSound.release();
        }
    }
}
