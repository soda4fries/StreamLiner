package com.example.streamliner.courseEnrollment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.streamliner.viewMarks.QuizMarksFragment;
import com.example.streamliner.R;
import com.example.streamliner.courseDiscovery.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EnrolledCoursesFragment extends Fragment {
    private RecyclerView coursesRecyclerView;
    private EnrolledCoursesAdapter adapter;
    private List<Course> userCourses;
    private DatabaseReference databaseRef;
    private Button viewMarksButton;
    private ProgressBar loadingProgressBar;

    public EnrolledCoursesFragment() {
        // Required empty public constructor
    }

    /* Use for userId
    public static EnrolledCoursesFragment newInstance(String userId) {
        EnrolledCoursesFragment fragment = new EnrolledCoursesFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrolled_courses, container, false);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        coursesRecyclerView = view.findViewById(R.id.coursesRecyclerView);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        viewMarksButton = view.findViewById(R.id.viewMarksButton);

        // Initialize RecyclerView
        userCourses = new ArrayList<>();
        adapter = new EnrolledCoursesAdapter(userCourses, this);
        coursesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        coursesRecyclerView.setAdapter(adapter);

        viewMarksButton.setOnClickListener(v -> {
            /*NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_enrolledCoursesFragment_to_quizMarksFragment);*/

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer1, new QuizMarksFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Setup back button
        view.findViewById(R.id.backButton).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Load user's courses
        loadUserCourses();

        return view;
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
                Toast.makeText(getContext(),
                        "Error loading courses: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }
}