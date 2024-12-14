package com.example.streamliner.Quiz.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.streamliner.Quiz.Adapter.QuizAdapter;
import com.example.streamliner.Quiz.Model.Quiz;
import com.example.streamliner.R;
import com.example.streamliner.databinding.FragmentQuizListBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizListFragment extends Fragment {
    private FragmentQuizListBinding binding;
    private FirebaseDatabase database;
    private QuizAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentQuizListBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();

        setupRecyclerView();
        loadQuizzes();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new QuizAdapter(quiz -> {
            Bundle args = new Bundle();
            args.putString("quizId", quiz.getId());
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_quizListFragment_to_quizFragment, args);
        });
        binding.quizRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.quizRecyclerView.setAdapter(adapter);
    }

    private void loadQuizzes() {
        binding.progressBar.setVisibility(View.VISIBLE);
        database.getReference("quizzes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Quiz> quizzes = new ArrayList<>();
                        for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                            Quiz quiz = quizSnapshot.getValue(Quiz.class);
                            quiz.setId(quizSnapshot.getKey());
                            quizzes.add(quiz);
                        }
                        adapter.submitList(quizzes);
                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

