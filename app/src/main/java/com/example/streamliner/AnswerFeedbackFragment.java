package com.example.streamliner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnswerFeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswerFeedbackFragment extends Fragment {
    private static final String ARG_IS_CORRECT = "isCorrect";
    private TextView feedbackTV;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    public AnswerFeedbackFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AnswerFeedbackFragment newInstance(boolean isCorrect) {
        AnswerFeedbackFragment fragment = new AnswerFeedbackFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_CORRECT, isCorrect);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_answer_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feedbackTV = view.findViewById(R.id.feedbackTV);

        if (getArguments() != null) {
            boolean isCorrect = getArguments().getBoolean(ARG_IS_CORRECT);
            showFeedback(isCorrect);
        }
    }

    private void showFeedback(boolean isCorrect) {
        String feedbackText = "Your answer is " + (isCorrect ? "CORRECT." : "WRONG.");
        feedbackTV.setText(feedbackText);
        feedbackTV.setTextColor(ContextCompat.getColor(getContext(),
                isCorrect ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
        ));
    }
}