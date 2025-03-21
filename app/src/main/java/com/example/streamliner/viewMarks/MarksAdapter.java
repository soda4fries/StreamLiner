package com.example.streamliner.viewMarks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.R;

import java.util.List;

public class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.MarkViewHolder> {
    private final List<QuizMark> marks;

    public MarksAdapter(List<QuizMark> marks) {
        this.marks = marks;
    }

    @NonNull
    @Override
    public MarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_mark, parent, false);
        return new MarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkViewHolder holder, int position) {
        QuizMark mark = marks.get(position);
        holder.quizTitleTV.setText(mark.getTitle());
        holder.dateTV.setText(mark.getDate());
        holder.markTV.setText(String.valueOf(mark.getMark()));
    }

    @Override
    public int getItemCount() {
        return marks.size();
    }

    static class MarkViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitleTV;
        TextView dateTV;
        TextView markTV;

        MarkViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitleTV = itemView.findViewById(R.id.quizTitleTV);
            dateTV = itemView.findViewById(R.id.dateTV);
            markTV = itemView.findViewById(R.id.markTV);
        }
    }
}
