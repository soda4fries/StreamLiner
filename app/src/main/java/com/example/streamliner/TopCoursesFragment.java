package com.example.streamliner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TopCoursesFragment extends Fragment {

    //private TopCoursesViewModel mViewModel;

    private RecyclerView coursesRecyclerView;
    private FilterResultsAdapter adapter;
    private List<Course> coursesList, displayedCourses;
    private DatabaseReference databaseRef;
    private ProgressBar loadingProgressBar;
    private TextView moreCourses;
    private int currentIndex = 0;
    private static final int COURSES_PER_PAGE = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_courses, container, false);

        coursesRecyclerView = view.findViewById(R.id.coursesRecyclerView);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        moreCourses = view.findViewById(R.id.moreCourses);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("Courses");

        // Initialize course list and adapter
        coursesList = new ArrayList<>();
        displayedCourses = new ArrayList<>();
        adapter = new FilterResultsAdapter(displayedCourses);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        coursesRecyclerView.setAdapter(adapter);

        // Set up More Courses click listener
        moreCourses.setOnClickListener(v -> loadMoreCourses());

        // Load top courses
        loadAllCourses();

        return view;
    }

    private void loadAllCourses() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coursesList.clear();
                int count = 0;
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null) {
                        course.setId(courseSnapshot.getKey());
                        if (count % 3 == 0 ) {
                            coursesList.add(course);
                        }
                        count++;
                    }
                }
                Log.d("courseSize", "courses: " + coursesList.size());
                loadMoreCourses();
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error loading courses: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadMoreCourses() {
        int endIndex = Math.min(currentIndex + COURSES_PER_PAGE, coursesList.size());
        for (int i = currentIndex; i < endIndex; i++) {
            displayedCourses.add(coursesList.get(i));
            Log.d("displayedCourses", "courses: " + coursesList.get(i));
        }
        adapter.notifyDataSetChanged();
        currentIndex = endIndex;

        if (currentIndex >= coursesList.size()) {
            moreCourses.setEnabled(false);
            moreCourses.setAlpha(0.5f);
            Toast.makeText(getContext(), "No more courses available", Toast.LENGTH_SHORT).show();
        }
    }

}