package com.example.streamliner.timer.schedule;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.databinding.ItemScheduleCellBinding;

import java.util.ArrayList;
import java.util.List;
public class ScheduleAdapterWithJava extends RecyclerView.Adapter<ScheduleAdapterWithJava.CellViewHolder> {

    private final CellClickListener cellClickListener;
    private List<CourseJava> courses = new ArrayList<>();
    private final int timeSlots = 11; // 8 time slots + 1 header row
    private final int daysOfWeek = 6; // 5 days + 1 time column
    private final CourseJava[][] cells = new CourseJava[timeSlots][daysOfWeek];

    private final String[] timeSlotLabels = {
            "",  // First cell empty
            "8:00-9:00am",
            "9:00-10:00am",
            "10:00-11:00am",
            "11:00-12:00pm",
            "12:00-1:00pm",
            "1:00-2:00pm",
            "2:00-3:00pm",
            "3:00-4:00pm",
            "4:00-5:00pm",
            "5:00-6:00pm"
    };

    private final String[] weekDayLabels = {"Timeslot\\Day", "MON", "TUE", "WED", "THU", "FRI"};

    public interface CellClickListener {
        void onCellClick(int row, int col);
    }

    public ScheduleAdapterWithJava(CellClickListener listener) {
        this.cellClickListener = listener;
    }

    @NonNull
    @Override
    public CellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemScheduleCellBinding binding = ItemScheduleCellBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CellViewHolder(binding, cellClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CellViewHolder holder, int position) {
        int row = position / daysOfWeek;
        int col = position % daysOfWeek;

        if (row == 0) {
            // First row shows weekdays
            holder.bind(null, row, col, true, weekDayLabels[col]);
        } else if (col == 0) {
            // First column shows time slots
            holder.bind(null, row, col, true, timeSlotLabels[row]);
        } else {
            // Other cells show courses
            CourseJava course = cells[row - 1][col];
            holder.bind(course, row, col, false, "");
        }
    }

    @Override
    public int getItemCount() {
        return timeSlots * daysOfWeek;
    }

    public void updateCourses(List<CourseJava> newCourses) {
        courses = newCourses;
        // Reset cells
        for (CourseJava[] cell : cells) {
            for (int j = 0; j < cell.length; j++) {
                cell[j] = null;
            }
        }

        // Fill courses
        for (CourseJava course : courses) {
            int startRow = course.getStartTime() - 1;
            for (int i = 0; i < course.getDuration(); i++) {
                if (startRow + i < timeSlots - 1) {
                    cells[startRow + i][course.getDayOfWeek()] = course;
                }
            }
        }

        notifyDataSetChanged();
    }

    static class CellViewHolder extends RecyclerView.ViewHolder {
        private final ItemScheduleCellBinding binding;
        private final CellClickListener listener;

        CellViewHolder(ItemScheduleCellBinding binding, CellClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        void bind(CourseJava course, int row, int col, boolean isHeader, String headerText) {
            if (isHeader) {
                binding.cellText.setText(headerText);
                binding.getRoot().setCardBackgroundColor(Color.LTGRAY);
                binding.getRoot().setOnClickListener(null);
               binding.cellText.setTextColor(Color.BLACK);
            } else if (course != null) {
                binding.cellText.setText(course.getTitle());
                binding.getRoot().setCardBackgroundColor(course.getColor());
                binding.getRoot().setOnClickListener(v -> listener.onCellClick(row - 1, col));
                binding.cellText.setTextColor(Color.WHITE);
            } else {
                binding.cellText.setText("");
                binding.getRoot().setCardBackgroundColor(Color.WHITE);
                if (row > 0 && col > 0) {
                    binding.getRoot().setOnClickListener(v -> listener.onCellClick(row - 1, col));
                } else {
                    binding.getRoot().setOnClickListener(null);
                }
                binding.cellText.setTextColor(Color.BLACK);
            }
        }
    }

        }