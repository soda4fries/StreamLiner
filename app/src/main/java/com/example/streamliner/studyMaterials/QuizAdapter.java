package com.example.streamliner.studyMaterials;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.R;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private final List<Quiz> quizzes;
    private final OnQuizClickListener listener;

    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz, int position);
    }

    public QuizAdapter(List<Quiz> quizzes, OnQuizClickListener listener) {
        this.quizzes = quizzes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_aisyah, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.practiceTV.setText("Quiz " + (position + 1));
        holder.titleTV.setText(quiz.getTitle());

        if (quiz.getQuestions() != null) {
            holder.questionCountTV.setText(quiz.getQuestions().size() + " Questions");
        }

        holder.startButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuizClick(quiz, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        TextView questionCountTV;
        TextView practiceTV;
        Button startButton;

        QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            practiceTV = itemView.findViewById(R.id.practiceTV);
            titleTV = itemView.findViewById(R.id.titleTV);
            questionCountTV = itemView.findViewById(R.id.questionCountTV);
            startButton = itemView.findViewById(R.id.startButton);
        }
    }
}
