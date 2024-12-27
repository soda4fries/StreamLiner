package com.example.streamliner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LearningOutcomesAdapter extends RecyclerView.Adapter<LearningOutcomesAdapter.ViewHolder> {
    private List<String> learningOutcomes;

    public LearningOutcomesAdapter(List<String> learningOutcomes) {
        this.learningOutcomes = learningOutcomes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_learning_outcome, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String outcome = learningOutcomes.get(position);
        holder.outcomeText.setText(outcome);
    }

    @Override
    public int getItemCount() {
        return learningOutcomes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView outcomeText;
        ImageView tickIcon;

        ViewHolder(View itemView) {
            super(itemView);
            outcomeText = itemView.findViewById(R.id.outcomeText);
            tickIcon = itemView.findViewById(R.id.tickIcon);
        }
    }
}
