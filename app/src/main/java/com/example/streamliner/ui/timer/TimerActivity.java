package com.example.streamliner.ui.timer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.streamliner.R;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private NumberPicker hourPicker, minutePicker, secondPicker;
    private TextView timerTextView;
    private Button startButton;
    private ImageView iconImageView;
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long timeConsumed= 0;
    private long timeLeftInMillis = 0;
    private MediaPlayer alarmSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // Initialize UI elements
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        secondPicker = findViewById(R.id.secondPicker);
        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        iconImageView = findViewById(R.id.iconImageView);

        // Initialize alarm sound
        alarmSound = MediaPlayer.create(this, R.raw.alarm);

        // Configure NumberPickers
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        // Start/Pause Pomodoro Timer functionality
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    pauseTimer();
                } else {
                    // Validate if time is selected
                    if (isTimeValid()) {
                        startTimer();
                    } else {
                        // Show toast notification for invalid time selection
                        Toast.makeText(TimerActivity.this, "Invalid! Please enter a time.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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

           long totalTime = (hours * 3600 + minutes * 60 + seconds) * 1000L;
//            long leftTime = totalTime-timeLeftInMillis;
//            if (leftTime == 0) return;
            if(timeLeftInMillis==0){
                timeLeftInMillis=totalTime;
            }

            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;

                    updateTimerText();
                }

                @Override
                public void onFinish() {
                    timeLeftInMillis=0;
                    isRunning = false;
                    startButton.setText("Start");

                    // Play alarm sound
                    playAlarm();
                }
            }.start();

            isRunning = true;
            startButton.setText("Pause ");
        }
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isRunning = false;
        startButton.setText("Resume ");
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
    protected void onPause() {
        super.onPause();
        if (isRunning) {
            pauseTimer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRunning && timeLeftInMillis > 0) {
            startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alarmSound != null) {
            alarmSound.release();
        }
    }
}
