package com.example.streamliner.Quiz.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamliner.Auth.Model.User;
import com.example.streamliner.Quiz.Adapter.LeaderboardAdapter;
import com.example.streamliner.Quiz.Model.LeaderboardEntry;
import com.example.streamliner.databinding.FragmentQuizResultBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizResultFragment extends Fragment {
    private FragmentQuizResultBinding binding;
    private FirebaseDatabase database;
    private LeaderboardAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuizResultBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();

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
                .limitToLast(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<LeaderboardEntry> entries = new ArrayList<>();
                        for (DataSnapshot userScore : snapshot.getChildren()) {
                            String userId = userScore.getKey();
                            int score = userScore.getValue(Integer.class);

                            // Load user details
                            database.getReference("users")
                                    .child(userId)
                                    .get()
                                    .addOnSuccessListener(userSnapshot -> {
                                        User user = userSnapshot.getValue(User.class);
                                        assert user != null;
                                        entries.add(new LeaderboardEntry(
                                                user.getDisplayName(),
                                                score
                                        ));

                                        if (entries.size() == snapshot.getChildrenCount()) {
                                            entries.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
                                            adapter.submitList(entries);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load leaderboard",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
