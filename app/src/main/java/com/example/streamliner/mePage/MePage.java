package com.example.streamliner.mePage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.streamliner.R;
import com.example.streamliner.courseEnrollment.EnrolledCoursesFragment;
import com.example.streamliner.viewMarks.QuizMarksFragment;
import com.google.firebase.auth.FirebaseAuth;


public class MePage extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_me_page, container, false);

        Button btnMyCourses = view.findViewById(R.id.BtnMyCourses);
        Button btnMyMarks = view.findViewById(R.id.BtnMyMarks);
        Button btnViewProfile = view.findViewById(R.id.BtnViewProfile);
        Button btnFeedback = view.findViewById(R.id.BtnFeedback);
        Button btnLogOut = view.findViewById(R.id.BtnLogOut);

        // Set click listeners for each button
        btnMyCourses.setOnClickListener(v -> navigateToCourses());
        btnMyMarks.setOnClickListener(v -> navigateToMarks());
        btnViewProfile.setOnClickListener(v -> navigateToViewProfile());
        btnFeedback.setOnClickListener(v -> navigateToFeedback());
        btnLogOut.setOnClickListener(v -> logOut());
        return view;
    }

    private void navigateToCourses() {
        /*requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer1, new EnrolledCoursesFragment())
                .addToBackStack(null)
                .commit();*/

        // Navigate to My Courses screen
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_mePage_to_enrolledCoursesFragment);
    }

    private void navigateToMarks() {

        // Navigate to My Marks screen
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_mePage_to_quizMarksFragment);
    }

    private void navigateToViewProfile() {
        // Navigate to Edit Profile screen
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_mePage_to_viewProfile);
    }

    private void navigateToFeedback() {
        // Navigate to Feedback screen
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_mePage_to_feedback);
    }

    private void logOut() {
        // Log out user and navigate to Login screen
        FirebaseAuth.getInstance().signOut();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_mePage_to_loginPage);
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
}