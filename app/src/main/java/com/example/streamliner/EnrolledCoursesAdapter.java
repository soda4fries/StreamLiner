package com.example.streamliner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class EnrolledCoursesAdapter extends RecyclerView.Adapter<EnrolledCoursesAdapter.CourseViewHolder> {
    private List<Course> courses;

    public EnrolledCoursesAdapter(List<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public EnrolledCoursesAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_enrolled_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnrolledCoursesAdapter.CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descriptionText;
        private MaterialCardView enrolledCourseCard;

        CourseViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.courseTitle);
            descriptionText = itemView.findViewById(R.id.courseDescription);
            enrolledCourseCard = itemView.findViewById(R.id.enrolledCourseCard);
        }

        void bind(Course course) {
            titleText.setText(course.getName());
            descriptionText.setText(course.getDescription());

            enrolledCourseCard.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, CourseDetailsActivity.class);
                intent.putExtra("courseId", course.getId());
                context.startActivity(intent);
            });
        }
    }
}
