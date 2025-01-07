package com.example.streamliner.timer.countdown;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import com.example.streamliner.R;

public class TimerBroadcastReceiver extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;
    public static final String ACTION_TIMER_FINISHED = "com.example.streamliner.TIMER_FINISHED";
    public static final String ACTION_DISMISS_TIMER = "com.example.streamliner.DISMISS_TIMER";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_TIMER_FINISHED.equals(action)) {
            // Play alarm sound
            playAlarmSound(context);

        } else if (ACTION_DISMISS_TIMER.equals(action)) {
            // Stop alarm sound if playing
            stopAlarmSound();

            // Clear notification
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(TimerService.NOTIFICATION_ID);
        }
    }

    private void playAlarmSound(Context context) {
        // Create and play alarm sound on the main thread
        new Handler(Looper.getMainLooper()).post(() -> {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });
    }

    private void stopAlarmSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}