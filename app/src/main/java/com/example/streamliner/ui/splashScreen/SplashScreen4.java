package com.example.streamliner.ui.splashScreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.streamliner.R;

public class SplashScreen4 extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash_screen4, container, false);

        Button getStartedButton = view.findViewById(R.id.BtnGetStarted);

        // Set the click listener to navigate to Register Page
        getStartedButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.registerPage);  // Navigate to the register page
        });


        return view;
    }
}
