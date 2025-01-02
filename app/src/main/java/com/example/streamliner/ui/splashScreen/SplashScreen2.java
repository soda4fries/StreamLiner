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

public class SplashScreen2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash_screen2, container, false);

        Button nextButton = view.findViewById(R.id.Screen2Next);

        nextButton.setOnClickListener(v -> {
            // Get NavController and navigate to the next fragment
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_splashScreen2_to_splashScreen3);
        });

        return view;
    }
}