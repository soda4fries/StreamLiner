package com.example.streamliner;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.streamliner.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference coursesRef = database.getReference("Courses");

        /*Button btnAddCourse = findViewById(R.id.btnAddCourse);

        btnAddCourse.setOnClickListener(v -> {
            // Create a Course object
            Course course = new Course();
            course.setName("Discrete Mathematics");
            course.setSubject("Mathematics");
            course.setDescription("Explore the mathematics of logic, sets, graphs, and algorithms essential for computer science.");
            course.setField(Arrays.asList("Discrete Math"));
            course.setLearningOutcomes(Arrays.asList(
                    "Logic, set theory, and combinatorics.",
                    "Graph theory and algorithms.",
                    "Boolean algebra and its applications."
            ));
            course.setQuizzes(Arrays.asList("Logic and Set Theory Quiz", "Combinatorics Quiz", "Graph Theory Quiz"));
            course.setPractices(Arrays.asList("Boolean Expressions Practice", "Graph Traversal Practice", "Counting and Permutations Practice"));

            // Push the course object to Firebase
            coursesRef.push().setValue(course)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Course added successfully!"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Error adding course", e));
        });*/

        if (savedInstanceState == null) {
            // Add search fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.searchContainer, new SearchFragment())
                    .commit();

            // Add top courses fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.coursesContainer, new TopCoursesFragment())
                    .commit();
        }
    }
}