package com.example.streamliner.Quiz.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamliner.Authentication.Model.User;
import com.example.streamliner.Quiz.Adapter.LeaderboardAdapter;
import com.example.streamliner.Quiz.Model.LeaderboardEntry;
import com.example.streamliner.databinding.FragmentQuizResultBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizResultFragment extends Fragment {
    private FragmentQuizResultBinding binding;
    private FirebaseDatabase database;
    private LeaderboardAdapter adapter;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuizResultBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
        int score = getArguments().getInt("score");
        String quizId = getArguments().getString("quizId");

        binding.scoreText.setText(String.format("Your Score: %d", score));

        setupRecyclerView();
        loadLeaderboard(quizId);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new LeaderboardAdapter();
        binding.leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.leaderboardRecyclerView.setAdapter(adapter);
    }

    private void loadLeaderboard(String quizId) {
        database.getReference("quizzes")
                .child(quizId)
                .child("leaderboard")
                .orderByValue()
                .limitToFirst(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<LeaderboardEntry> entries = new ArrayList<>();

                        for (DataSnapshot userScore : snapshot.getChildren()) {
                            String userId = userScore.getKey();
                            int score = userScore.getValue(Integer.class);
                            entries.add(new LeaderboardEntry(userId, score)); // Use userId temporarily
                        }

                        // Sort first to show scores quickly
                        entries.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
                        adapter.submitList(new ArrayList<>(entries));

                        // Then load names asynchronously
                        for (LeaderboardEntry entry : entries) {
                            firestore.collection("users")
                                    .document(entry.getUserName()) // userId is temporarily in userName
                                    .get()
                                    .addOnSuccessListener(doc -> {
                                        if (doc.exists()) {
                                            entry.setUserName(doc.getString("name"));
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DATABASE", "Error loading leaderboard", error.toException());
                    }
                });
    }
}
