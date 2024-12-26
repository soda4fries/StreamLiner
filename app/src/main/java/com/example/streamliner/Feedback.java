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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class Feedback extends Fragment {
    private RatingBar ratingBar;
    private EditText etFeedback;
    private Button btnFeedback;

    private FirebaseAuth mAuth;
    private DatabaseReference mFeedbackDatabase;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_feedback, container, false);
        ratingBar=view.findViewById(R.id.RateBarFeedback);
        etFeedback=view.findViewById(R.id.ETFeedback);
        btnFeedback=view.findViewById(R.id.BtnFeedback);

        mAuth=FirebaseAuth.getInstance();
        mFeedbackDatabase= FirebaseDatabase.getInstance().getReference("Feedback");

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
            DatabaseReference feedbackRef=mFeedbackDatabase.push();

            Map<String, Object>feedbackData=new HashMap<>();
            feedbackData.put("userId",userID);
            feedbackData.put("feedback",textFeedback);
            feedbackData.put("rating",rating);

            feedbackRef.setValue(feedbackData).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Feedback submitted successfully.",Toast.LENGTH_LONG).show();
                    etFeedback.setText(" ");
                    ratingBar.setRating(0);
                }else{
                    Toast.makeText(getContext(),"Failed to submit feedback.",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}