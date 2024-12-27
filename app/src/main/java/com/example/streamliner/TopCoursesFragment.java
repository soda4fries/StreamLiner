package com.example.streamliner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

    /*public static TopCoursesFragment newInstance() {
        return new TopCoursesFragment();
    }*/

    private RecyclerView coursesRecyclerView;
    private FilterResultsAdapter adapter;
    private List<Course> coursesList;
    private DatabaseReference databaseRef;
    private ProgressBar loadingProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_courses, container, false);

        coursesRecyclerView = view.findViewById(R.id.coursesRecyclerView);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("Courses");

        // Initialize course list and adapter
        coursesList = new ArrayList<>();
        adapter = new FilterResultsAdapter(coursesList);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        coursesRecyclerView.setAdapter(adapter);

        // Load top courses
        loadTopCourses();

        return view;
    }

    private void loadTopCourses() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coursesList.clear();
                long childrenCount = dataSnapshot.getChildrenCount();
                long count = 0;
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    count++;
                    if (count == 1 || count == 5 || count == 9 || count == 13 || count == childrenCount) {
                        Course course = courseSnapshot.getValue(Course.class);
                        if (course != null) {
                            course.setId(courseSnapshot.getKey());
                            coursesList.add(course);
                        }
                    }
                    if (count == 13) break; // Stop after the 5th course
                }
                adapter.notifyDataSetChanged();

                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error loading courses: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    //add click listener for view more courses

}