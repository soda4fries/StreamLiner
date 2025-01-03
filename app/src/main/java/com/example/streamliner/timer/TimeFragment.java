package com.example.streamliner.timer;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.streamliner.databinding.FragmentTimeBinding;
//import com.example.streamliner.ui.timer.countdown.TimerFragment;
//
//public class TimeFragment extends Fragment {
//
//    private FragmentTimeBinding binding;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        TimeViewModel timeViewModel =
//                new ViewModelProvider(this).get(TimeViewModel.class);
//
//        binding = FragmentTimeBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        binding.btnTimetable.setOnClickListener(v -> {
////            todo navigation to schedule fragment
//        });
//
//        final TextView textView = binding.btnTimer;
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // todo replace activity to fragment
//                Intent intent = new Intent(getActivity(), TimerActivity.class);
//                startActivity(intent);
//            }
//        });
//
//
//        return root;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}



//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.example.streamliner.R;
//import com.example.streamliner.ui.timer.schedule.ScheduleFragmentWithJava;
//import com.example.streamliner.ui.timer.countdown.TimerFragment;
//
//public class TimeFragment extends Fragment {
//
//    public TimeFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View rootView = inflater.inflate(R.layout.button_fragment, container, false);
//
//        // Initialize buttons
//        Button btnTimetable = rootView.findViewById(R.id.btnTimetable);
//        Button btnTimer = rootView.findViewById(R.id.btnTimer);
//
//        // Set click listener for the Timetable button
//        btnTimetable.setOnClickListener(v -> {
//            // Replace the current fragment with the ScheduleFragment
//            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragment_container, new ScheduleFragmentWithJava());
//            transaction.addToBackStack(null);  // Optional: Allows going back to the ButtonFragment
//            transaction.commit();
//        });
//
//        // Set click listener for the Timer button
//        btnTimer.setOnClickListener(v -> {
//            // Replace the current fragment with the TimerFragment
//            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragment_container, new TimerFragment());
//            transaction.addToBackStack(null);  // Optional: Allows going back to the ButtonFragment
//            transaction.commit();
//        });
//
//        return rootView;
//    }
//}


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.streamliner.R;

public class TimeFragment extends Fragment {

    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.button_fragment, container, false);

        // Initialize buttons
        Button btnTimetable = rootView.findViewById(R.id.btnTimetable);
        Button btnTimer = rootView.findViewById(R.id.btnTimer);

        // Set click listener for the Timetable button
        btnTimetable.setOnClickListener(v -> {
            // Navigate to the ScheduleFragment using NavController
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_navigation_time_to_navigation_schedule);
        });

        // Set click listener for the Timer button
        btnTimer.setOnClickListener(v -> {
            // Navigate to the TimerFragment using NavController
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_timeFragment_to_TimerFragment);
        });

        return rootView;
    }
}
