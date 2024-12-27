package com.example.streamliner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class CourseDetailsActivity extends AppCompatActivity {
    private TextView courseTitle, courseDescription, courseSubject, courseFields;
    private RecyclerView learningOutcomesRecyclerView;
    private Button addToMyCourseButton;
    private DatabaseReference databaseRef;
    private LearningOutcomesAdapter learningOutcomesAdapter;
    private List<String> learningOutcomesList, fieldsList;
    private ProgressBar loadingProgressBar;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("Courses");

        // Initialize views
        courseTitle = findViewById(R.id.courseTitle);
        courseSubject = findViewById(R.id.courseSubject);
        courseDescription = findViewById(R.id.courseDescription);
        courseFields = findViewById(R.id.courseFields);
        learningOutcomesRecyclerView = findViewById(R.id.learningOutcomesRecyclerView);
        addToMyCourseButton = findViewById(R.id.addToMyCourseButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Setup RecyclerViews
        learningOutcomesList = new ArrayList<>();
        fieldsList = new ArrayList<>();

        learningOutcomesAdapter = new LearningOutcomesAdapter(learningOutcomesList);
        learningOutcomesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        learningOutcomesRecyclerView.setAdapter(learningOutcomesAdapter);

        // Get course ID from intent
        courseId = getIntent().getStringExtra("courseId");
        if (courseId != null) {
            loadCourseDetails(courseId);
        }

        // Setup "Add to my course" button
        addToMyCourseButton.setOnClickListener(v -> addToMyCourse());

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void loadCourseDetails(String courseId) {
        loadingProgressBar.setVisibility(View.VISIBLE);

        databaseRef.child(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get course name
                String name = dataSnapshot.child("name").getValue(String.class);
                courseTitle.setText(name);

                // Get course subject
                String subject = dataSnapshot.child("subject").getValue(String.class);
                courseSubject.setText("Subject: " + subject);

                // Get course description
                String description = dataSnapshot.child("description").getValue(String.class);
                courseDescription.setText(description);

                // Get course field(s)
                fieldsList.clear();
                DataSnapshot fieldsSnapshot = dataSnapshot.child("field");
                for (DataSnapshot fieldSnapshot : fieldsSnapshot.getChildren()) {
                    String field = fieldSnapshot.getValue(String.class);
                    if (field != null) {
                        fieldsList.add(field);
                    }
                }

                if(fieldsList.size() == 1) {
                    courseFields.setText(fieldsList.get(0));
                }
                else {
                    courseFields.setText(fieldsList.get(0) + ", " + fieldsList.get(1));
                }

                // Get learning outcomes
                learningOutcomesList.clear();
                DataSnapshot learningOutcomesSnapshot = dataSnapshot.child("learningOutcomes");
                for (DataSnapshot outcomeSnapshot : learningOutcomesSnapshot.getChildren()) {
                    String outcome = outcomeSnapshot.getValue(String.class);
                    if (outcome != null) {
                        //Log.d("LearningOutcomes", "Outcome: " + outcome);
                        learningOutcomesList.add(outcome);
                    }
                }
                learningOutcomesAdapter.notifyDataSetChanged();

                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseDetailsActivity.this,
                        "Error loading course details: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void addToMyCourse() {
        // TODO: Implement the logic to add the course to the user's courses
        // This might involve updating a user's courses in Firebase
        Toast.makeText(this, "Course added to My Courses", Toast.LENGTH_SHORT).show();
        addToMyCourseButton.setEnabled(false);
        addToMyCourseButton.setText("Added to My Courses");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}