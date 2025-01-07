package com.example.streamliner.timer.countdown;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.streamliner.R;

import java.util.Locale;

public class TimerFragment extends Fragment {

    private NumberPicker hourPicker, minutePicker, secondPicker;
    private TextView timerTextView;
    private Button startButton;
    private ImageView iconImageView;
    private TimerService timerService;
    private boolean bound = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getService();
            bound = true;
            updateUIFromService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private final BroadcastReceiver timerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TimerService.TIMER_TICK.equals(intent.getAction())) {
                long timeLeft = intent.getLongExtra(TimerService.TIME_LEFT, 0);
                updateTimerText(timeLeft);
                if (timeLeft == 0) {
                    updateStartButton(false);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start and bind to TimerService
        Intent intent = new Intent(getActivity(), TimerService.class);
        requireActivity().startService(intent);
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        // Initialize UI elements
        hourPicker = view.findViewById(R.id.hourPicker);
        minutePicker = view.findViewById(R.id.minutePicker);
        secondPicker = view.findViewById(R.id.secondPicker);
        timerTextView = view.findViewById(R.id.timerTextView);
        startButton = view.findViewById(R.id.startButton);
        iconImageView = view.findViewById(R.id.iconImageView);

        // Configure NumberPickers
        setupNumberPickers();

        // Setup broadcast receiver
        IntentFilter filter = new IntentFilter(TimerService.TIMER_TICK);
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(timerUpdateReceiver, filter);

        // Start/Pause Timer functionality
        startButton.setOnClickListener(v -> {
            if (!bound) return;

            if (timerService.isRunning()) {
                pauseTimer();
            } else {
                if (timerService.getTimeLeftInMillis() > 0) {
                    timerService.resumeTimer();
                    updateStartButton(true);
                } else {
                    if (isTimeValid()) {
                        long totalTimeMillis = (hourPicker.getValue() * 3600L +
                                minutePicker.getValue() * 60L +
                                secondPicker.getValue()) * 1000L;
                        timerService.startTimer(totalTimeMillis);
                        updateStartButton(true);
                    } else {
                        Toast.makeText(getContext(), "Please set a valid time", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    private void setupNumberPickers() {
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);
    }

    private void updateUIFromService() {
        if (bound && timerService != null) {
            if (timerService.isRunning()) {
                updateTimerText(timerService.getTimeLeftInMillis());
                updateStartButton(true);
            } else if (timerService.getTimeLeftInMillis() > 0) {
                updateTimerText(timerService.getTimeLeftInMillis());
                updateStartButton(false);
            }

            // Restore number picker values from initial time if timer is running
            if (timerService.getInitialTimeInMillis() > 0) {
                long totalSeconds = timerService.getInitialTimeInMillis() / 1000;
                hourPicker.setValue((int) (totalSeconds / 3600));
                minutePicker.setValue((int) ((totalSeconds % 3600) / 60));
                secondPicker.setValue((int) (totalSeconds % 60));
            }
        }
    }

    private boolean isTimeValid() {
        return hourPicker.getValue() > 0 ||
                minutePicker.getValue() > 0 ||
                secondPicker.getValue() > 0;
    }

    private void pauseTimer() {
        if (bound) {
            timerService.pauseTimer();
            updateStartButton(false);
        }
    }

    private void updateTimerText(long millisUntilFinished) {
        int hours = (int) (millisUntilFinished / 1000) / 3600;
        int minutes = (int) ((millisUntilFinished / 1000) % 3600) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds);
        timerTextView.setText(timeFormatted);
    }

    private void updateStartButton(boolean isRunning) {
        if (isRunning) {
            startButton.setText("Pause");
        } else if (timerService != null && timerService.getTimeLeftInMillis() > 0) {
            startButton.setText("Resume");
        } else {
            startButton.setText("Start");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(timerUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bound) {
            requireActivity().unbindService(serviceConnection);
            bound = false;
        }
    }
}