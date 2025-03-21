package com.example.streamliner.viewMarks;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamliner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class QuizMarksFragment extends Fragment {
    private RecyclerView marksRecyclerView;
    private MarksAdapter adapter;
    private TextView totalMarksTV;
    private List<QuizMark> marksList;
    private DatabaseReference databaseRef;
    private FirebaseAuth auth;
    private ProgressBar loadingProgressBar;


    public QuizMarksFragment() {
        // Required empty public constructor
    }

    /* Use for userId
    public static QuizMarksFragment newInstance(String userId) {
        QuizMarksFragment fragment = new QuizMarksFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_marks, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        marksRecyclerView = view.findViewById(R.id.marksRecyclerView);
        totalMarksTV = view.findViewById(R.id.totalMarksTV);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        // Initialize RecyclerView
        marksList = new ArrayList<>();
        adapter = new MarksAdapter(marksList);
        marksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        marksRecyclerView.setAdapter(adapter);

        // Setup back button
        view.findViewById(R.id.backButton).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        loadMarks();

        return view;
    }

    private void loadMarks() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        String userId = auth.getCurrentUser().getUid();

        databaseRef.child("userQuizzes").child(userId).addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(getContext(),
                        "Error loading courses: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }
}