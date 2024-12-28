package com.example.streamliner;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EnrolledCoursesActivity extends AppCompatActivity {
    private RecyclerView coursesRecyclerView;
    private EnrolledCoursesAdapter adapter;
    private List<Course> userCourses;
    private DatabaseReference databaseRef;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_courses);

        // Initialize views
        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        /*
        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Courses");*/

        // Initialize RecyclerView
        userCourses = new ArrayList<>();
        adapter = new EnrolledCoursesAdapter(userCourses);
        coursesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        coursesRecyclerView.setAdapter(adapter);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Load user's courses
        loadUserCourses();
    }

    private void loadUserCourses() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        databaseRef.child("test").child("userCourses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCourses.clear();
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null) {
                        course.setId(courseSnapshot.getKey());
                        userCourses.add(course);
                    }
                }
                adapter.notifyDataSetChanged();
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EnrolledCoursesActivity.this, "Error loading courses: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }
}