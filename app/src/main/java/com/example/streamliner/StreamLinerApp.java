package com.example.streamliner;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StreamLinerApp extends Application {
    private static final String TAG = "StreamLinerApp";
    private static StreamLinerApp instance;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initializeFirebase();
    }

    private void initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this);
            auth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase", e);
        }
    }

    public static StreamLinerApp getInstance() {
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }
}