package com.example.streamliner;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PracticeActivity extends AppCompatActivity implements QuestionFragment.OnQuestionInteractionListener {
    private String courseId;
    private int practiceId; //practice index
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int[] userAnswers;
    private TextView titleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        courseId = getIntent().getStringExtra("courseId");
        practiceId = getIntent().getIntExtra("practiceId", 0);
        String practiceTitle = getIntent().getStringExtra("practiceTitle");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(practiceTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        titleTV = findViewById(R.id.titleTV);
        titleTV.setText(practiceTitle);

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        loadQuestions();
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
                Toast.makeText(PracticeActivity.this,
                        "Error loading questions", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showQuestion(int position) {
        QuestionFragment fragment = QuestionFragment.newInstance(
                questions.get(position), position, questions.size(), "practice");
        fragment.setListener(this);

        getSupportFragmentManager()
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
                        Toast.makeText(this,
                                "Practice completed! Score: " + score + "%",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this,
                                "Error saving results",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    public void showAnswerFeedback(boolean isCorrect) {
        AnswerFeedbackFragment feedbackFragment = AnswerFeedbackFragment.newInstance(isCorrect);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.feedbackContainer, feedbackFragment)
                .commit();
    }
}