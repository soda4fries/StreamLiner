package com.example.streamliner.courseDiscovery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.streamliner.R;
import com.example.streamliner.courseEnrollment.CourseDetailsFragment;

import java.util.List;

public class FilterResultsAdapter extends RecyclerView.Adapter<FilterResultsAdapter.CourseViewHolder> {
    private final List<Course> courses;
    private final Fragment fragment;

    public FilterResultsAdapter(List<Course> courses, Fragment fragment) {
        this.courses = courses;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_result, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    // Make it not static
    public class CourseViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descriptionText;
        private final Button viewButton;

        CourseViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.courseTitle);
            descriptionText = itemView.findViewById(R.id.courseDescription);
            viewButton = itemView.findViewById(R.id.viewButton);
        }

        void bind(Course course) {
            titleText.setText(course.getName());
            descriptionText.setText(course.getDescription());

            viewButton.setOnClickListener(v -> {
                CourseDetailsFragment fragment = CourseDetailsFragment.newInstance(course.getId());

                FilterResultsAdapter.this.fragment.requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer1, fragment)
                        .addToBackStack(null)
                        .commit();

                /*Context context = itemView.getContext();
                Intent intent = new Intent(context, CourseDetailsActivity.class);
                intent.putExtra("courseId", course.getId());
                context.startActivity(intent);*/
            });
        }
    }
}
