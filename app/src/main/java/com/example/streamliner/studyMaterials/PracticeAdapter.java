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

public class PracticeAdapter extends RecyclerView.Adapter<PracticeAdapter.PracticeViewHolder> {
    private List<Practice> practices;
    private OnPracticeClickListener listener;

    public interface OnPracticeClickListener {
        void onPracticeClick(Practice practice, int position);
    }

    public PracticeAdapter(List<Practice> practices, OnPracticeClickListener listener) {
        this.practices = practices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PracticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_practice, parent, false);
        return new PracticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PracticeViewHolder holder, int position) {
        Practice practice = practices.get(position);
        holder.practiceTV.setText("Practice " + Integer.toString(position + 1));
        holder.titleTV.setText(practice.getTitle());

        if (practice.getQuestions() != null) {
            holder.questionCountTV.setText(practice.getQuestions().size() + " Questions");
        }

        holder.startButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPracticeClick(practice, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return practices.size();
    }

    /*public void updatePractices(List<Practice> newPractices) {
        this.practices = newPractices;
        notifyDataSetChanged();
    }*/

    static class PracticeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        TextView questionCountTV;
        TextView practiceTV;
        Button startButton;

        PracticeViewHolder(@NonNull View itemView) {
            super(itemView);
            practiceTV = itemView.findViewById(R.id.practiceTV);
            titleTV = itemView.findViewById(R.id.titleTV);
            questionCountTV = itemView.findViewById(R.id.questionCountTV);
            startButton = itemView.findViewById(R.id.startButton);
        }
    }
}
