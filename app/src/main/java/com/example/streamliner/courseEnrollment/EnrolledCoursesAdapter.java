package com.example.streamliner.courseEnrollment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.R;
import com.example.streamliner.courseDiscovery.Course;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class EnrolledCoursesAdapter extends RecyclerView.Adapter<EnrolledCoursesAdapter.CourseViewHolder> {
    private final List<Course> courses;
    private final Fragment fragment;

    public EnrolledCoursesAdapter(List<Course> courses, Fragment fragment) {
        this.courses = courses;
        this.fragment = fragment;
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

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descriptionText;
        private final MaterialCardView enrolledCourseCard;

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
                // Get NavController from the Fragment
                NavController navController = Navigation.findNavController(fragment.requireView());

                // Create Bundle for navigation arguments
                Bundle args = new Bundle();
                args.putString("courseId", course.getId());
                args.putString("courseName", course.getName());
                args.putString("courseDescription", course.getDescription());

                // Navigate using NavController
                navController.navigate(
                        R.id.action_enrolledCoursesFragment_to_studyMaterialsFragment,
                        args
                );
            });
        }
    }
}