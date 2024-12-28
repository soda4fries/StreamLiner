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
    private List<String> learningOutcomesList;
    private ProgressBar loadingProgressBar;
    private String courseId;
    private Course currentCourse;

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
        learningOutcomesAdapter = new LearningOutcomesAdapter(new ArrayList<>());
        learningOutcomesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        learningOutcomesRecyclerView.setAdapter(learningOutcomesAdapter);

        // Get course ID from intent
        courseId = getIntent().getStringExtra("courseId");
        if (courseId == null) {
            Toast.makeText(this, "Error: Course ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Setup  click listener for Add to My Course button
        addToMyCourseButton.setOnClickListener(v -> addCourseToUserList());

        // Load course details
        loadCourseDetails();
    }

    private void loadCourseDetails() {
        loadingProgressBar.setVisibility(View.VISIBLE);

        databaseRef.child(courseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentCourse = dataSnapshot.getValue(Course.class);
                if (currentCourse != null) {
                    currentCourse.setId(courseId);
                    updateUI(dataSnapshot);
                }
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

    private void updateUI(DataSnapshot dataSnapshot) {
        // Update course name
        courseTitle.setText(currentCourse.getName());

        // Get course subject
        courseSubject.setText("Subject: " + currentCourse.getSubject());

        // Get course description
        courseDescription.setText(currentCourse.getDescription());

        // Get course field(s)
        if(currentCourse.getField().size() == 1) {
            courseFields.setText(currentCourse.getField().get(0));
        }
        else {
            courseFields.setText(currentCourse.getField().get(0) + ", " + currentCourse.getField().get(1));
        }

        // Get learning outcomes
        learningOutcomesList = new ArrayList<>();
        DataSnapshot learningOutcomesSnapshot = dataSnapshot.child("learningOutcomes");
        for (DataSnapshot outcomeSnapshot : learningOutcomesSnapshot.getChildren()) {
            String outcome = outcomeSnapshot.getValue(String.class);
            if (outcome != null) {
                //Log.d("LearningOutcomes", "Outcome: " + outcome);
                learningOutcomesList.add(outcome);
            }
        }
        learningOutcomesAdapter.setLearningOutcomes(learningOutcomesList);
        learningOutcomesAdapter.notifyDataSetChanged();
    }

    private void addCourseToUserList() {
        DatabaseReference userCoursesRef = FirebaseDatabase.getInstance().getReference()
                .child("test").child("userCourses");

        // Check if course already exists in user's list
        userCoursesRef.orderByChild("id").equalTo(courseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // Course doesn't exist in user's list, add it
                            userCoursesRef.child(courseId).setValue(currentCourse)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(CourseDetailsActivity.this,
                                                "Course added successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(CourseDetailsActivity.this,
                                                "Failed to add course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(CourseDetailsActivity.this,
                                    "Course already in your list", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(CourseDetailsActivity.this,
                                "Error checking course: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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