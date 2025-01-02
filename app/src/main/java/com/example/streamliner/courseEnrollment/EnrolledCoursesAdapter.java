package com.example.streamliner.courseEnrollment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.R;
import com.example.streamliner.studyMaterials.StudyMaterialsFragment;
import com.example.streamliner.courseDiscovery.Course;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class EnrolledCoursesAdapter extends RecyclerView.Adapter<EnrolledCoursesAdapter.CourseViewHolder> {
    private List<Course> courses;
    private Fragment fragment;

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
                StudyMaterialsFragment fragment = StudyMaterialsFragment.newInstance(course.getId(), course.getName(), course.getDescription());

                EnrolledCoursesAdapter.this.fragment.requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer1, fragment)
                        .addToBackStack(null)
                        .commit();

                /*Context context = itemView.getContext();
                Intent intent = new Intent(context, VideoLearningActivity.class);
                intent.putExtra("courseId", course.getId());
                intent.putExtra("courseName", course.getName());
                intent.putExtra("courseDescription", course.getDescription());
                context.startActivity(intent);*/
            });
        }
    }
}
