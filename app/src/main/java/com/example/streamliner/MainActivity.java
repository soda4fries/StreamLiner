package com.example.streamliner;

import android.os.Bundle;

import com.example.streamliner.courseDiscovery.SearchFragment;
import com.example.streamliner.courseDiscovery.TopCoursesFragment;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Button btnAddCourse = findViewById(R.id.btnAddCourse);
        String courseId = "-OF2yaOybWi9flRHkvI2";

        // Initialize Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference coursesRef = database.getReference("Courses");
        //DatabaseReference quizRef = coursesRef.child(courseId).child("quizzes");

            btnAddCourse.setOnClickListener(v -> {
                // Ensure courseId is set correctly before proceeding
                if (courseId.isEmpty()) {
                    Toast.makeText(this, "Course ID is not set!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Reference to the specific course's quizzes node
                DatabaseReference quizRef = coursesRef.child(courseId).child("quizzes");

                // Prepare quiz data
                // Quiz 1: Has title and a list of questions
                List<Question> quiz1Questions = new ArrayList<>();
                quiz1Questions.add(new Question("What is a linear transformation?",
                        Arrays.asList("A transformation that preserves addition and scalar multiplication", "A transformation that changes the determinant of a matrix", "A transformation that modifies the rank of a matrix"), 0));

                quiz1Questions.add(new Question("What is the matrix representation of a linear transformation?",
                        Arrays.asList("A matrix of zeros", "A matrix that maps vectors from one space to another", "A diagonal matrix"), 1));

                quiz1Questions.add(new Question("What is the result of applying a linear transformation to the zero vector?",
                        Arrays.asList("A non-zero vector", "The zero vector", "A scalar"), 1));

                quiz1Questions.add(new Question("What does it mean if a linear transformation is invertible?",
                        Arrays.asList("It maps different vectors to the same vector", "It has a determinant of 0", "It has an inverse transformation"), 2));

                quiz1Questions.add(new Question("Which of the following is not a property of linear transformations?",
                        Arrays.asList("Additivity", "Scalar multiplication", "Non-linearity"), 2));

                Quiz quiz1 = new Quiz("Linear Transformations Quiz", quiz1Questions);

                // Quiz 2: Only title
                Quiz quiz2 = new Quiz("Matrix Operations Quiz", new ArrayList<>());

                // Quiz 3: Only title
                Quiz quiz3 = new Quiz("Eigenvalues and Eigenvectors Quiz", new ArrayList<>());

                // Add quizzes to a list
                List<Quiz> quizList = new ArrayList<>();
                quizList.add(quiz1);
                quizList.add(quiz2);
                quizList.add(quiz3);

                // Push quizzes to Firebase
                for (Quiz quiz : quizList) {
                    quizRef.push().setValue(quiz)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, "Quiz added successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to add quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });*/

        if (savedInstanceState == null) {
            SearchFragment fragment = SearchFragment.newInstance(0);
            // Add search fragment and top courses fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer1, fragment)
                    .addToBackStack("SearchAndTopCoursesFragments")
                    .commit();
        }
    }
}