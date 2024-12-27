package com.example.streamliner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Feedback extends Fragment {
    private RatingBar ratingBar;
    private EditText etFeedback;
    private Button btnFeedback;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_feedback, container, false);

        ratingBar=view.findViewById(R.id.RateBarFeedback);
        etFeedback=view.findViewById(R.id.ETFeedback);
        btnFeedback=view.findViewById(R.id.BtnFeedback);

        mAuth=FirebaseAuth.getInstance();
        mFirestore= FirebaseFirestore.getInstance();

        btnFeedback.setOnClickListener(v -> submitFeedBack());

        return view;
    }

    private void submitFeedBack() {
        String textFeedback=etFeedback.getText().toString();
        float rating=ratingBar.getRating();

        if(rating==0){
            Toast.makeText(getContext(),"Please provide a rating.",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(textFeedback)){
            Toast.makeText(getContext(),"Please provide feedback.",Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            String userID=currentUser.getUid();

            // Create a Map to store feedback data
            Map<String, Object> feedbackData = new HashMap<>();
            feedbackData.put("userId", userID);
            feedbackData.put("feedback", textFeedback);
            feedbackData.put("rating", rating);
            feedbackData.put("timestamp", FieldValue.serverTimestamp());  // Adds a timestamp

            // Store feedback in Firestore
            mFirestore.collection("feedback")
                    .add(feedbackData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Feedback submitted successfully.", Toast.LENGTH_LONG).show();
                            etFeedback.setText(" ");
                            ratingBar.setRating(0);
                        } else {
                            Toast.makeText(getContext(), "Failed to submit feedback.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error submitting feedback: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        }
    }
}