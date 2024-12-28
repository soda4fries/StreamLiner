package com.example.streamliner;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizzesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizzesFragment extends Fragment {
    private static final String TAG = "QuizFragment";
    private RecyclerView quizzesRecyclerView;
    private QuizAdapter adapter;
    private List<Quiz> quizList;
    private DatabaseReference databaseRef;
    private ProgressBar loadingProgressBar;
    private String courseId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    public QuizzesFragment() {
        // Required empty public constructor
    }

    public static QuizzesFragment newInstance(String courseId) {
        QuizzesFragment fragment = new QuizzesFragment();
        Bundle args = new Bundle();
        args.putString("courseId", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quizzes, container, false);

        // Get courseId from arguments
        if (getArguments() != null) {
            courseId = getArguments().getString("courseId");
        }

        // Initialize views
        quizzesRecyclerView = view.findViewById(R.id.quizzesRecyclerView);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        quizList = new ArrayList<>();
        adapter = new QuizAdapter(quizList, new QuizAdapter.OnQuizClickListener() {
            @Override
            public void onQuizClick(Quiz quiz, int position) {
                Intent intent = new Intent(getContext(), PracticeActivity.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("quizTitle", quiz.getTitle());
                startActivity(intent);
            }
        });

        // Setup RecyclerView
        Log.d(TAG, "Setup RecycleView");
        quizzesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quizzesRecyclerView.setAdapter(adapter);

        // Load quizzes
        loadQuizzes();

        return view;
    }

    private void loadQuizzes() {
        if (courseId == null) return;

        loadingProgressBar.setVisibility(View.VISIBLE);
        DatabaseReference quizzesRef = FirebaseDatabase.getInstance().getReference()
                .child("Courses")
                .child(courseId)
                .child("quizzes");

        quizzesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizList.clear();
                Log.d(TAG, "Quizzes data received. Count: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Quiz quiz = snapshot.getValue(Quiz.class);
                    if (quiz != null) {
                        quizList.add(quiz);
                        Log.d(TAG, "Loaded quiz: " + quiz.getTitle());
                    }
                }
                adapter.notifyDataSetChanged();
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading practices: " + error.getMessage());
                Toast.makeText(getContext(),
                        "Error loading practices",
                        Toast.LENGTH_SHORT).show();
                loadingProgressBar.setVisibility(View.GONE);
            }
        });
    }
}