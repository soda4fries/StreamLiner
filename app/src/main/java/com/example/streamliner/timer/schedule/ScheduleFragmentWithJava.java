package com.example.streamliner.timer.schedule;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.streamliner.databinding.FragmentScheduleBinding;

public class ScheduleFragmentWithJava extends Fragment {
    private FragmentScheduleBinding binding;
    private ScheduleViewModelJava viewModel;
    private ScheduleAdapterWithJava adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupRecyclerView();
        observeCourses();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ScheduleViewModelJava.class);
    }

    private void setupRecyclerView() {
        adapter = new ScheduleAdapterWithJava(this::showCourseDialog);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 6);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });

        binding.scheduleRecyclerView.setLayoutManager(layoutManager);
        binding.scheduleRecyclerView.setAdapter(adapter);
        binding.scheduleRecyclerView.setHasFixedSize(true);
    }

    private void observeCourses() {
        viewModel.getCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                adapter.updateCourses(courses);
            }
        });
    }

    private void showCourseDialog(int row, int col) {
        CourseDialogFragmentWithJava.newInstance(row, col)
                .show(requireActivity().getSupportFragmentManager(), "course_dialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
