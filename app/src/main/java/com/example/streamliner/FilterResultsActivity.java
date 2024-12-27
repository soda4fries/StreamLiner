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

public class FilterResultsActivity extends AppCompatActivity {
    private RecyclerView resultsRecyclerView;
    private FilterResultsAdapter adapter;
    private TextView resultsTitle;
    private DatabaseReference databaseRef;
    private List<Course> coursesList;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_results);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("Courses");

        // Initialize views
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        resultsTitle = findViewById(R.id.resultsTitle);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Setup RecyclerView
        coursesList = new ArrayList<>();
        adapter = new FilterResultsAdapter(coursesList);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(adapter);

        // Handle incoming intent
        String searchQuery = getIntent().getStringExtra("searchQuery");
        ArrayList<String> selectedSubjects = getIntent().getStringArrayListExtra("selectedSubjects");

        if (searchQuery != null && !searchQuery.isEmpty()) {
            // Handle search by keyword
            resultsTitle.setText("Search Results");
            searchCoursesByKeyword(searchQuery);
        } else if (selectedSubjects != null && !selectedSubjects.isEmpty()) {
            // Handle search by subjects
            if (selectedSubjects.size() == 1) {
                resultsTitle.setText("Results for " + selectedSubjects.get(0));
            } else {
                resultsTitle.setText("Results for Selected Subjects");
            }
            loadCoursesForSubjects(selectedSubjects);
        }

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void searchCoursesByKeyword(String keyword) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        coursesList.clear();

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null) {
                        course.setId(courseSnapshot.getKey());
                        if (course.getName().toLowerCase().contains(keyword.toLowerCase())) {
                            coursesList.add(course);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                loadingProgressBar.setVisibility(View.GONE);

                if (coursesList.isEmpty()) {
                    showNoResultsMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FilterResultsActivity.this,
                        "Error loading courses: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadCoursesForSubjects(List<String> subjects) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        coursesList.clear();

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null) {
                        course.setId(courseSnapshot.getKey());
                        if (subjects.contains(course.getSubject())) {
                            coursesList.add(course);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                loadingProgressBar.setVisibility(View.GONE);

                if (coursesList.isEmpty()) {
                    showNoResultsMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FilterResultsActivity.this,
                        "Error loading courses: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showNoResultsMessage() {
        Toast.makeText(this, "No courses found", Toast.LENGTH_SHORT).show();
    }
}