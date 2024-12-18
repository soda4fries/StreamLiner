package com.example.streamliner.ui.timer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.streamliner.databinding.FragmentHomeBinding;
import com.example.streamliner.databinding.FragmentTimeBinding;
import com.example.streamliner.ui.home.HomeViewModel;

public class TimeFragment extends Fragment {

    private FragmentTimeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TimeViewModel timeViewModel =
                new ViewModelProvider(this).get(TimeViewModel.class);

        binding = FragmentTimeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.btnTimer;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimerActivity.class);
                startActivity(intent);
            }
        });
      //  timeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}