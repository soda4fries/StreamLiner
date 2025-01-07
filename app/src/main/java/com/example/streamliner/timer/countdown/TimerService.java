package com.example.streamliner.timer.countdown;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.streamliner.R;
import com.example.streamliner.MainActivity;

public class TimerService extends Service {
    private final IBinder binder = new LocalBinder();
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long timeLeftInMillis = 0;
    private long initialTimeInMillis = 0;
    private MediaPlayer alarmSound;
    private Vibrator vibrator;

    public static final int NOTIFICATION_ID = 1;
    public static final int COMPLETE_NOTIFICATION_ID = 2;
    private static final String CHANNEL_ID = "TimerServiceChannel";
    private static final String COMPLETE_CHANNEL_ID = "TimerCompleteChannel";
    public static final String TIMER_TICK = "TIMER_TICK";
    public static final String TIME_LEFT = "TIME_LEFT";
    public static final String ACTION_STOP_TIMER = "com.example.streamliner.STOP_TIMER";
    public static final String ACTION_DISMISS_COMPLETION = "com.example.streamliner.DISMISS_COMPLETION";

    private static final long[] VIBRATION_PATTERN = {0, 1000, 500, 1000};

    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        alarmSound = MediaPlayer.create(this, R.raw.alarm);
        alarmSound.setLooping(true);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            // Remove existing channels first to ensure settings are updated
            manager.deleteNotificationChannel(CHANNEL_ID);
            manager.deleteNotificationChannel(COMPLETE_CHANNEL_ID);

            // Timer progress channel
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Service Channel",
                    NotificationManager.IMPORTANCE_LOW);
            serviceChannel.setSound(null, null);
            serviceChannel.enableVibration(false);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            // Timer completion channel with maximum importance
            NotificationChannel completeChannel = new NotificationChannel(
                    COMPLETE_CHANNEL_ID,
                    "Timer Complete Channel",
                    NotificationManager.IMPORTANCE_MAX);  // Changed to MAX importance

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            completeChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);  // Explicitly set MAX importance
            completeChannel.enableLights(true);
            completeChannel.setLightColor(0xFF0000); // Red
            completeChannel.enableVibration(true);
            completeChannel.setVibrationPattern(VIBRATION_PATTERN);
            completeChannel.setBypassDnd(true);
            completeChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            completeChannel.setShowBadge(true);  // Show badge on app icon
            completeChannel.setSound(null, audioAttributes); // We handle sound separately

            manager.createNotificationChannel(serviceChannel);
            manager.createNotificationChannel(completeChannel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_STOP_TIMER.equals(action)) {
                stopTimer();
                stopForeground(true);
                stopSelf();
            } else if (ACTION_DISMISS_COMPLETION.equals(action)) {
                dismissCompletionNotification();
            }
        }
        return START_NOT_STICKY;
    }

    public void startTimer(long milliseconds) {
        stopTimer(); // Clean up any existing timer

        initialTimeInMillis = milliseconds;
        timeLeftInMillis = milliseconds;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateNotification("Timer Running", formatTime(millisUntilFinished));
                broadcastTimerTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                timeLeftInMillis = 0;
                stopForeground(true);
                showTimerCompleteNotification();
                startVibration();
                playAlarm();
                broadcastTimerTick(0);
            }
        }.start();

        isRunning = true;
        startForeground(NOTIFICATION_ID, buildNotification("Timer Running", formatTime(timeLeftInMillis)));
    }

    private void showTimerCompleteNotification() {
        // Intent to open app
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Dismiss intent
        Intent dismissIntent = new Intent(this, TimerService.class);
        dismissIntent.setAction(ACTION_DISMISS_COMPLETION);
        PendingIntent dismissPendingIntent = PendingIntent.getService(
                this, 1, dismissIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Create a high-priority notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, COMPLETE_CHANNEL_ID)
                .setContentTitle("⏰ Timer Complete!")
                .setContentText("Timer finished! Tap to open")
                .setTicker("Timer Complete!")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", dismissPendingIntent)
                .setAutoCancel(false)  // Changed to false to make it persistent
                .setPriority(NotificationCompat.PRIORITY_HIGH)  // Set to HIGH instead of MAX
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)  // Make it persistent
                .setFullScreenIntent(pendingIntent, true)  // Force heads-up
                .setVibrate(new long[]{0, 500, 1000})  // Add vibration pattern
                .setLights(0xFF0000, 1000, 1000);  // Red LED flash

        // For pre-Oreo devices
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setDefaults(Notification.DEFAULT_ALL);
        }

        // Build and set flags
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_INSISTENT |
                Notification.FLAG_NO_CLEAR |
                Notification.FLAG_SHOW_LIGHTS;

        // Show the notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Cancel any existing notifications first
        notificationManager.cancel(COMPLETE_NOTIFICATION_ID);

        // Post the new notification
        notificationManager.notify(COMPLETE_NOTIFICATION_ID, notification);
    }

    private void dismissCompletionNotification() {
        stopAlarm();
        stopVibration();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(COMPLETE_NOTIFICATION_ID);
    }

    private void startVibration() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(VIBRATION_PATTERN, 0));
            } else {
                vibrator.vibrate(VIBRATION_PATTERN, 0);
            }
        }
    }

    private void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isRunning = false;
        timeLeftInMillis = 0;
        initialTimeInMillis = 0;
        stopAlarm();
        stopVibration();
        stopForeground(true);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.cancel(COMPLETE_NOTIFICATION_ID);
    }

    public void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isRunning = false;
        updateNotification("Timer Paused", formatTime(timeLeftInMillis));
    }

    public void resumeTimer() {
        if (timeLeftInMillis > 0) {
            startTimer(timeLeftInMillis);
        }
    }

    private Notification buildNotification(String title, String content) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this, TimerService.class);
        stopIntent.setAction(ACTION_STOP_TIMER);
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this, 0, stopIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setTicker(title)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        if (isRunning) {
            builder.setOngoing(true);
        }

        return builder.build();
    }

    private void updateNotification(String title, String content) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, buildNotification(title, content));
    }

    private void broadcastTimerTick(long millisUntilFinished) {
        Intent intent = new Intent(TIMER_TICK);
        intent.putExtra(TIME_LEFT, millisUntilFinished);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void playAlarm() {
        if (alarmSound != null && !alarmSound.isPlaying()) {
            alarmSound.start();
        }
    }

    private void stopAlarm() {
        if (alarmSound != null && alarmSound.isPlaying()) {
            alarmSound.stop();
            alarmSound.release();
            alarmSound = MediaPlayer.create(this, R.raw.alarm);
        }
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long getTimeLeftInMillis() {
        return timeLeftInMillis;
    }

    public long getInitialTimeInMillis() {
        return initialTimeInMillis;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (alarmSound != null) {
            alarmSound.release();
            alarmSound = null;
        }
    }
}