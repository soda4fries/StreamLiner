package com.example.streamliner.courseEnrollment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamliner.R;
import com.example.streamliner.courseDiscovery.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailsFragment extends Fragment {
    private TextView courseTitle, courseDescription, courseSubject, courseFields;
    private RecyclerView learningOutcomesRecyclerView;
    private Button addToMyCourseButton;
    private DatabaseReference databaseRef;
    private FirebaseAuth auth;
    private LearningOutcomesAdapter learningOutcomesAdapter;
    private List<String> learningOutcomesList;
    private ProgressBar loadingProgressBar;
    private String courseId;
    private Course currentCourse;

    public CourseDetailsFragment() {
        // Required empty public constructor
    }

    public static CourseDetailsFragment newInstance(String courseId) {
        CourseDetailsFragment fragment = new CourseDetailsFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_course_details, container, false);

        // Get search/filter input from arguments
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId");
        }

        if (courseId == null) {
            Toast.makeText(getContext(),
                    "Error: Course ID not found",
                    Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return null; // Return null since no view is inflated
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Courses");

        // Initialize views
        courseTitle = view.findViewById(R.id.courseTitle);
        courseSubject = view.findViewById(R.id.courseSubject);
        courseDescription = view.findViewById(R.id.courseDescription);
        courseFields = view.findViewById(R.id.courseFields);
        learningOutcomesRecyclerView = view.findViewById(R.id.learningOutcomesRecyclerView);
        addToMyCourseButton = view.findViewById(R.id.addToMyCourseButton);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        // Setup RecyclerViews
        learningOutcomesAdapter = new LearningOutcomesAdapter(new ArrayList<>());
        learningOutcomesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        learningOutcomesRecyclerView.setAdapter(learningOutcomesAdapter);

        // Setup back button
        view.findViewById(R.id.backButton).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Setup  click listener for Add to My Course button
        addToMyCourseButton.setOnClickListener(v -> addCourseToUserList());

        // Load course details
        loadCourseDetails();

        return view;
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
                Toast.makeText(getContext(),
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
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userCoursesRef = FirebaseDatabase.getInstance().getReference()
                .child("enrolledCourses").child(userId);

        // Check if course already exists in user's list
        userCoursesRef.orderByChild("id").equalTo(courseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            // Course doesn't exist in user's list, add it
                            userCoursesRef.child(courseId).setValue(currentCourse)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(),
                                                "Course added successfully",
                                                Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(),
                                                "Failed to add course: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getContext(),
                                    "Course already in your list",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(),
                                "Error checking course: " + databaseError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}