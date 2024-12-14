package com.example.streamliner.Quiz.Fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamliner.Quiz.Model.Question;
import com.example.streamliner.Quiz.Model.Quiz;
import com.example.streamliner.Quiz.Model.QuizAttempt;
import com.example.streamliner.R;
import com.example.streamliner.databinding.FragmentQuizBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuizFragment extends Fragment {
    private FragmentQuizBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private Quiz currentQuiz;
    private QuizAttempt attempt;
    private int currentQuestionIndex = 0;
    private CountDownTimer timer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        String quizId = getArguments().getString("quizId");
        loadQuiz(quizId);

        binding.nextButton.setOnClickListener(v -> handleNextQuestion());

        return binding.getRoot();
    }

    private void loadQuiz(String quizId) {
        database.getReference("quizzes").child(quizId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    currentQuiz = snapshot.getValue(Quiz.class);
                    currentQuiz.setId(snapshot.getKey());
                    attempt = new QuizAttempt(auth.getCurrentUser().getUid(), quizId);
                    startQuiz();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Failed to load quiz", Toast.LENGTH_SHORT).show());
    }

    private void startQuiz() {
        startTimer();
        displayQuestion(currentQuestionIndex);
    }

    private void startTimer() {
        timer = new CountDownTimer(currentQuiz.getTimeLimit() * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.timerText.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                ));
            }

            @Override
            public void onFinish() {
                finishQuiz();
            }
        }.start();
    }

    private void displayQuestion(int index) {
        Question question = currentQuiz.getQuestions().get(index);
        binding.questionText.setText(question.getText());

        binding.optionsGroup.removeAllViews();
        for (int i = 0; i < question.getOptions().size(); i++) {
            RadioButton button = new RadioButton(getContext());
            button.setText(question.getOptions().get(i));
            button.setId(i);
            binding.optionsGroup.addView(button);
        }

        binding.nextButton.setText(index == currentQuiz.getQuestions().size() - 1 ? "Finish" : "Next");
    }

    private void handleNextQuestion() {
        int selectedId = binding.optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        attempt.storeAnswer(currentQuestionIndex, selectedId);

        if (currentQuestionIndex < currentQuiz.getQuestions().size() - 1) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        if (timer != null) {
            timer.cancel();
        }

        attempt.setEndTime(System.currentTimeMillis());
        calculateScore();

        // Update Firebase
        DatabaseReference attemptsRef = database.getReference("quiz_attempts")
                .child(currentQuiz.getId())
                .child(auth.getCurrentUser().getUid());

        attemptsRef.setValue(attempt)
                .addOnSuccessListener(aVoid -> {
                    // Update leaderboard
                    database.getReference("quizzes")
                            .child(currentQuiz.getId())
                            .child("leaderboard")
                            .child(auth.getCurrentUser().getUid())
                            .setValue(attempt.getScore())
                            .addOnSuccessListener(aVoid2 -> {
                                Bundle args = new Bundle();
                                args.putString("quizId", currentQuiz.getId());
                                args.putInt("score", attempt.getScore());
                                Navigation.findNavController(binding.getRoot())
                                        .navigate(R.id.action_quizFragment_to_quizResultFragment, args);
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Failed to save results", Toast.LENGTH_SHORT).show());
    }

    private void calculateScore() {
        int totalScore = 0;
        for (int i = 0; i < currentQuiz.getQuestions().size(); i++) {
            Question question = currentQuiz.getQuestions().get(i);
            Integer selectedAnswer = attempt.getAnswer(i);
            if (selectedAnswer != null && selectedAnswer == question.getCorrectOptionIndex()) {
                totalScore += question.getPoints();
            }
        }
        attempt.setScore(totalScore);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
        binding = null;
    }
}
