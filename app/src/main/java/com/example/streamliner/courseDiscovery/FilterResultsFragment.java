package com.example.streamliner.courseDiscovery;

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

import com.example.streamliner.R;
import com.example.streamliner.courseEnrollment.EnrolledCoursesFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilterResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterResultsFragment extends Fragment {
    private RecyclerView resultsRecyclerView;
    private FilterResultsAdapter adapter;
    private TextView resultsTitle, moreCourses, goToMyCourses;
    private DatabaseReference databaseRef;
    private List<Course> coursesList, displayedCourses;
    private ProgressBar loadingProgressBar;
    private int currentIndex;
    private static final int COURSES_PER_PAGE = 5;
    private String searchQuery;
    private ArrayList<String> selectedSubjects;

    public FilterResultsFragment() {
        // Required empty public constructor
    }

    public static FilterResultsFragment newInstance(String searchQuery, ArrayList<String> selectedSubjects, int currentIndex) {
        FilterResultsFragment fragment = new FilterResultsFragment();
        Bundle args = new Bundle();
        args.putString("searchQuery", searchQuery);
        args.putStringArrayList("selectedSubjects", selectedSubjects);
        args.putInt("currentIndex", currentIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_results, container, false);

        // Get search/filter input from arguments
        if (getArguments() != null) {
            searchQuery = getArguments().getString("searchQuery");
            selectedSubjects = getArguments().getStringArrayList("selectedSubjects");
            currentIndex = getArguments().getInt("currentIndex");
        }

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("Courses");

        // Initialize views
        resultsRecyclerView = view.findViewById(R.id.resultsRecyclerView);
        resultsTitle = view.findViewById(R.id.resultsTitle);
        moreCourses = view.findViewById(R.id.moreCourses);
        goToMyCourses = view.findViewById(R.id.goToMyCourses);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        // Setup RecyclerView
        coursesList = new ArrayList<>();
        displayedCourses = new ArrayList<>();
        adapter = new FilterResultsAdapter(displayedCourses, this);

        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        resultsRecyclerView.setAdapter(adapter);

        if (searchQuery != null && !searchQuery.isEmpty()) {
            // Handle search by keyword
            resultsTitle.setText("Search Results");
            Log.d("FRargs", "searchQuery: " + searchQuery);
            searchCoursesByKeyword(searchQuery);
        } else if (selectedSubjects != null && !selectedSubjects.isEmpty()) {
            // Handle search by subjects
            if (selectedSubjects.size() == 1) {
                resultsTitle.setText("Results for " + selectedSubjects.get(0));
            } else {
                resultsTitle.setText("Results for Selected Subjects");
            }
            Log.d("FRargs", "selectedSubjects: " + selectedSubjects.toString());
            loadCoursesForSubjects(selectedSubjects);
        }

        // Setup back button
        view.findViewById(R.id.backButton).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Setup More Courses click listener
        moreCourses.setOnClickListener(v -> loadMoreCourses());

        // Setup Go To My Courses button click listener
        goToMyCourses.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer1, new EnrolledCoursesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    // Search query
    private void searchCoursesByKeyword(String keyword) {
        loadingProgressBar.setVisibility(View.VISIBLE);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coursesList.clear();
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null) {
                        course.setId(courseSnapshot.getKey());
                        if (course.getName().toLowerCase().contains(keyword.toLowerCase())) {
                            coursesList.add(course);
                        }
                    }
                }
                loadMoreCourses();
                loadingProgressBar.setVisibility(View.GONE);

                if (coursesList.isEmpty()) {
                    showNoResultsMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),
                        "Error loading courses: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    // Filter search
    private void loadCoursesForSubjects(List<String> subjects) {
        loadingProgressBar.setVisibility(View.VISIBLE);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                coursesList.clear();
                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
                    Course course = courseSnapshot.getValue(Course.class);
                    if (course != null) {
                        course.setId(courseSnapshot.getKey());
                        if (subjects.contains(course.getSubject())) {
                            coursesList.add(course);
                        }
                    }
                }
                loadMoreCourses();
                loadingProgressBar.setVisibility(View.GONE);

                if (coursesList.isEmpty()) {
                    showNoResultsMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),
                        "Error loading courses: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadMoreCourses() {
        Log.d("FRargs", "currentIndex: " + currentIndex);
        int endIndex = Math.min(currentIndex + COURSES_PER_PAGE, coursesList.size());
        for (int i = currentIndex; i < endIndex; i++) {
            displayedCourses.add(coursesList.get(i));
        }
        adapter.notifyDataSetChanged();
        currentIndex = endIndex;

        if (currentIndex >= coursesList.size()) {
            moreCourses.setEnabled(false);
            moreCourses.setAlpha(0.5f);
            Toast.makeText(requireContext(),
                    "No more courses available",
                    Toast.LENGTH_SHORT).show();
        }

        if (coursesList.size() <= COURSES_PER_PAGE) {
            moreCourses.setEnabled(false);
            moreCourses.setAlpha(0.5f);
        }
    }

    private void showNoResultsMessage() {
        Toast.makeText(requireContext(),
                "No courses found",
                Toast.LENGTH_SHORT).show();
    }
}