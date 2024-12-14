package com.example.streamliner.Quiz.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.Quiz.Model.Quiz;
import com.example.streamliner.R;

import java.util.ArrayList;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private final OnQuizClickListener listener;
    private List<Quiz> quizzes;

    public QuizAdapter(OnQuizClickListener listener) {
        this.quizzes = new ArrayList<>();
        this.listener = listener;
    }

    public void submitList(List<Quiz> newQuizzes) {
        this.quizzes = newQuizzes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.bind(quiz, listener);
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descriptionText;
        private final TextView topicText;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            topicText = itemView.findViewById(R.id.topicText);
        }

        public void bind(Quiz quiz, OnQuizClickListener listener) {
            titleText.setText(quiz.getTitle());
            descriptionText.setText(quiz.getDescription());
            topicText.setText(quiz.getTopic());

            itemView.setOnClickListener(v -> listener.onQuizClick(quiz));
        }
    }
}