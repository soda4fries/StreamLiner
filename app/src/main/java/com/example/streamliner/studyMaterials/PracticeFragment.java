package com.example.streamliner.studyMaterials;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamliner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PracticeFragment extends Fragment implements QuestionFragment.OnQuestionInteractionListener {
    private String courseId, practiceTitle;
    private int practiceId; //practice index
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int[] userAnswers;
    private TextView titleTV, feedBackTV;

    public PracticeFragment() {
        // Required empty public constructor
    }

    public static PracticeFragment newInstance(String courseId, int practiceId, String practiceTitle) {
        PracticeFragment fragment = new PracticeFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        args.putInt("practiceId", practiceId);
        args.putString("practiceTitle", practiceTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practice, container, false);

        // Get practice details from arguments
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId");
            practiceId = getArguments().getInt("practiceId");
            practiceTitle = getArguments().getString("practiceTitle");
        }

        // Initialize view
        feedBackTV = view.findViewById(R.id.feedBackTV);
        titleTV = view.findViewById(R.id.titleTV);
        titleTV.setText(practiceTitle);

        // Setup back button
        view.findViewById(R.id.backButton).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        loadQuestions();

        return view;
    }

    private void loadQuestions() {
        DatabaseReference questionsRef = FirebaseDatabase.getInstance().getReference()
                .child("Courses")
                .child(courseId)
                .child("practices")
                .child(String.valueOf(practiceId))
                .child("questions");

        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questions = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("questionTitle", dataSnapshot.toString());
                    Question question = snapshot.getValue(Question.class);
                    if (question != null) {
                        questions.add(question);
                    }
                }
                userAnswers = new int[questions.size()];
                for (int i = 0; i < userAnswers.length; i++) {
                    userAnswers[i] = -1; // -1 indicates no answer selected
                }
                showQuestion(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),
                        "Error loading questions", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void showQuestion(int position) {
        feedBackTV.setVisibility(View.INVISIBLE); // Set feedBackTV to invisible if it's not already invisible

        QuestionFragment fragment = QuestionFragment.newInstance(
                questions.get(position), position, questions.size(), "practice");
        fragment.setListener(this);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    public void onPreviousClicked() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showQuestion(currentQuestionIndex);
        }
    }

    @Override
    public void onNextClicked() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }
    }

    @Override
    public void onAnswerSelected(int answerIndex) {
        userAnswers[currentQuestionIndex] = answerIndex;
        boolean isCorrect = answerIndex == questions.get(currentQuestionIndex).getCorrectIndex();
        showAnswerFeedback(isCorrect);
    }

    @Override
    public void onSubmitClicked() {
        // Calculate score
        int correctAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers[i] != -1 && userAnswers[i] == questions.get(i).getCorrectIndex()) {
                correctAnswers++;
            }
        }
        int score = (correctAnswers * 100) / questions.size();

        // Save completion status and score
        DatabaseReference userPracticeRef = FirebaseDatabase.getInstance().getReference()
                .child("test")
                .child("userCourses")
                .child(courseId)
                .child("practices")
                .child(String.valueOf(practiceId));

        userPracticeRef.child("completed").setValue(true);
        userPracticeRef.child("score").setValue(score)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "Practice completed! Score: " + score + "%",
                                Toast.LENGTH_LONG).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(),
                                "Error saving results",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAnswerFeedback(boolean isCorrect) {
        feedBackTV.setVisibility(View.VISIBLE);

        String feedbackText = "Your answer is " + (isCorrect ? "CORRECT." : "WRONG.");
        feedBackTV.setText(feedbackText);
        feedBackTV.setTextColor(ContextCompat.getColor(getContext(),
                isCorrect ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
        ));

        /*AnswerFeedbackFragment feedbackFragment = AnswerFeedbackFragment.newInstance(isCorrect);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.feedbackContainer, feedbackFragment)
                .commit();*/
    }
}