package com.example.streamliner;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
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
        initializeGooglePlayServices();
        initializeFirebase();
    }

    private void initializeGooglePlayServices() {
        try {
            // Initialize security provider
            ProviderInstaller.installIfNeeded(this);

            // Initialize Google Play Services
            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            int result = googleAPI.isGooglePlayServicesAvailable(this);
            if (result != ConnectionResult.SUCCESS) {
                Log.e(TAG, "Google Play Services not available: " + result);
            }
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "Google Play Services repairable error", e);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Google Play Services not available", e);
        }
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