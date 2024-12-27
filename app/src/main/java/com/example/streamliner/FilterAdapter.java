package com.example.streamliner;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {
    private List<String> subjects;
    private List<String> selectedSubjects;

    public FilterAdapter(List<String> subjects) {
        this.subjects = subjects;
        this.selectedSubjects = new ArrayList<>();
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        String subject = subjects.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public List<String> getSelectedSubjects() {
        return selectedSubjects;
    }

    public void clearSelection() {
        selectedSubjects.clear();
        notifyDataSetChanged();
    }

    class FilterViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        FilterViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.subjectCheckBox);
        }

        void bind(final String subject) {
            checkBox.setText(subject);
            checkBox.setTextColor(Color.WHITE);
            checkBox.setChecked(selectedSubjects.contains(subject));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedSubjects.contains(subject)) {
                        selectedSubjects.add(subject);
                    }
                } else {
                    selectedSubjects.remove(subject);
                }
            });
        }
    }
}
