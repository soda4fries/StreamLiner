package com.example.streamliner.studyMaterials;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class QuizFragment extends Fragment implements QuestionFragment.OnQuestionInteractionListener {
    private String courseId, quizTitle;
    private int quizId; //practice index
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int[] userAnswers;
    private TextView titleTV;
    private Button submitAllButton;

    public QuizFragment() {
        // Required empty public constructor
    }

    public static QuizFragment newInstance(String courseId, int quizId, String quizTitle) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        //args.putString("userId", userId);
        args.putString("courseId", courseId);
        args.putInt("quizId", quizId);
        args.putString("quizTitle", quizTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        // Get quiz details from arguments
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId");
            quizId = getArguments().getInt("quizId");
            quizTitle = getArguments().getString("quizTitle");
        }

        // Initialize views
        titleTV = view.findViewById(R.id.titleTV);
        submitAllButton = view.findViewById(R.id.submitAllButton);

        // Set quiz title
        titleTV.setText(quizTitle);

        // Setup back button
        view.findViewById(R.id.backButton).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        loadQuestions();

        return view;
    }

    private void loadQuestions() {
        DatabaseReference questionsRef = FirebaseDatabase.getInstance().getReference()
                .child("Courses")
                .child(courseId)
                .child("quizzes")
                .child(String.valueOf(quizId))
                .child("questions");

        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questions = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Log.e("questionTitle", dataSnapshot.toString());
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
        QuestionFragment fragment = QuestionFragment.newInstance(
                questions.get(position), position, questions.size(), "quiz");
        fragment.setListener(this);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerQuiz, fragment)
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
        int mark = (correctAnswers * 100) / questions.size();

        // Get the current date in "dd-MM-yyyy" format
        String currentDate = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
                .format(new java.util.Date());

        submitAllButton.setVisibility(View.VISIBLE);

        submitAllButton.setOnClickListener(v -> {
            // Save completion status and score
            DatabaseReference userQuizRef = FirebaseDatabase.getInstance().getReference()
                    .child("test")
                    .child("userQuizzes");
                /*.child(courseId)
                .child("quizzes")
                .child(String.valueOf(quizId));*/

            DatabaseReference quizMarkEntryRef = userQuizRef.push();

            quizMarkEntryRef.child("title").setValue(quizTitle);
            quizMarkEntryRef.child("completed").setValue(true);
            quizMarkEntryRef.child("mark").setValue(mark);
            quizMarkEntryRef.child("date").setValue(currentDate)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Data stored successfully!",
                                    Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getContext(),
                                    "Failed to store data!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    public void showAnswerFeedback(boolean isCorrect) {} // Only for practices
}