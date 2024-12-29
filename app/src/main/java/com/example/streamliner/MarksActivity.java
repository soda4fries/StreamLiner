package com.example.streamliner;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MarksActivity extends AppCompatActivity {
    private RecyclerView marksRecyclerView;
    private MarksAdapter adapter;
    private TextView totalMarksTV;
    private ProgressBar loadingProgressBar;
    private List<QuizMark> marksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

        marksRecyclerView = findViewById(R.id.marksRecyclerView);
        totalMarksTV = findViewById(R.id.totalMarksTV);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        marksList = new ArrayList<>();
        adapter = new MarksAdapter(marksList);
        marksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        marksRecyclerView.setAdapter(adapter);

        loadMarks();
    }

    private void loadMarks() {
        loadingProgressBar.setVisibility(View.VISIBLE);

        DatabaseReference marksRef = FirebaseDatabase.getInstance().getReference()
                .child("test")
                .child("userQuizzes");

        marksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                marksList.clear();
                int totalMarks = 0;

                for (DataSnapshot quizSnapshot : dataSnapshot.getChildren()) {
                    String title = quizSnapshot.child("title").getValue(String.class);
                    Boolean completed = quizSnapshot.child("completed").getValue(Boolean.class);
                    Integer mark = quizSnapshot.child("mark").getValue(Integer.class);
                    String date = quizSnapshot.child("date").getValue(String.class);

                    if (title != null && completed) {
                        marksList.add(new QuizMark(title, mark, completed, date));
                        totalMarks += mark;
                    }
                }

                adapter.notifyDataSetChanged();
                loadingProgressBar.setVisibility(View.GONE);

                totalMarksTV.setText("Total Marks: " + totalMarks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MarksActivity.this,
                        "Error loading courses: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }
}