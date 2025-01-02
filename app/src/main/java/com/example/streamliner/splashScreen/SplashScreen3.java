package com.example.streamliner.splashScreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.streamliner.R;

public class SplashScreen3 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash_screen3, container, false);

        Button nextButton = view.findViewById(R.id.Screen3Next);

        nextButton.setOnClickListener(v -> {
            // Get NavController and navigate to the next fragment
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_splashScreen3_to_splashScreen4);
        });

        return view;
    }


}