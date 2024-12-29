package com.example.streamliner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class QuizActivity extends AppCompatActivity implements QuestionFragment.OnQuestionInteractionListener {
    private String courseId;
    private int quizId; //practice index
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int[] userAnswers;
    private String quizTitle;
    private TextView titleTV;
    private Button submitAllButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        courseId = getIntent().getStringExtra("courseId");
        quizId = getIntent().getIntExtra("quizId", 0);
        quizTitle = getIntent().getStringExtra("quizTitle");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(quizTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        titleTV = findViewById(R.id.titleTV);
        submitAllButton = findViewById(R.id.submitAllButton);

        // Set quiz title
        titleTV.setText(quizTitle);

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        loadQuestions();
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
                Toast.makeText(QuizActivity.this,
                        "Error loading questions", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void showQuestion(int position) {
        QuestionFragment fragment = QuestionFragment.newInstance(
                questions.get(position), position, questions.size(), "quiz");
        fragment.setListener(this);

        getSupportFragmentManager()
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
                            Toast.makeText(this, "Data stored successfully!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to store data!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        /*SubmitButtonFragment submitButtonFragment = SubmitButtonFragment.newInstance(quizTitle, true, mark, currentDate);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.submitButtonContainer, submitButtonFragment)
                .commit();*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    public void showAnswerFeedback(boolean isCorrect) {}
}