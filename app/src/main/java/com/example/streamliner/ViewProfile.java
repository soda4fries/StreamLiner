package com.example.streamliner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewProfile extends Fragment {

    private TextView tvName, tvBirthday,tvPhoneNo,tvEmail;
    private Button btnEditProfile;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_view_profile,container,false);

        firestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        tvName=view.findViewById(R.id.TVName);
        tvBirthday=view.findViewById(R.id.TVBirthday);
        tvPhoneNo=view.findViewById(R.id.TVPhoneNo);
        tvEmail=view.findViewById(R.id.TVEmailViewProfile);

        // Fetch user data if the user is logged in
        if (firebaseUser != null) {
            fetchUserData(firebaseUser.getUid());
        }


        return view;
    }

    private  void fetchUserData(String userId){
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String birthday = documentSnapshot.getString("birthday");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");

                        // Set the profile data in the TextViews
                        tvName.setText(name);
                        tvBirthday.setText(birthday!=null ? birthday:"No birthday");
                        tvPhoneNo.setText(phone);
                        tvEmail.setText(email);
                    } else {
                        Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                });
    }


}