package com.example.streamliner.ui.mePage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamliner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class EditProfile extends Fragment {
    EditText etName, etBirthday, etPhoneNo;
    TextView tvEmail;
    Button btnSaveProfile;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        etName = view.findViewById(R.id.ETProfileName);
        etBirthday = view.findViewById(R.id.ETBirthday);
        etPhoneNo = view.findViewById(R.id.ETPhoneNo);
        tvEmail = view.findViewById(R.id.TVEmailEditProfile);
        btnSaveProfile = view.findViewById(R.id.BtnSave);

        if (firebaseUser != null) {
            fetchUserData(firebaseUser.getUid());
        }

        // Save the updated profile
        btnSaveProfile.setOnClickListener(v -> {
            String newName = etName.getText().toString();
            String newPhone = etPhoneNo.getText().toString();
            String newBirthday = etBirthday.getText().toString();

            // Create a Map to hold the updated fields
            Map<String, Object> updatedUserDetails = new HashMap<>();

            if (!newName.isEmpty()) updatedUserDetails.put("name", newName);
            if (!newPhone.isEmpty()) updatedUserDetails.put("phone", newPhone);
            if (!newBirthday.isEmpty()) updatedUserDetails.put("birthday", newBirthday);

            if (updatedUserDetails.isEmpty()) {
                Toast.makeText(getContext(), "No changes made", Toast.LENGTH_SHORT).show();
            } else {
                updateProfile(updatedUserDetails);
            }
        });
        return view;
    }

    private void fetchUserData(String userId) {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String birthday = documentSnapshot.getString("birthday");
                        String phone = documentSnapshot.getString("phone");
                        String email = documentSnapshot.getString("email");

                        // Set the profile data in the EditText fields
                        etName.setText(name);
                        etBirthday.setText(birthday);
                        etPhoneNo.setText(phone);
                        tvEmail.setText(email);
                    } else {
                        Toast.makeText(getContext(), "User not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfile(Map<String,Object> updatedUserDetails) {
        firestore.collection("users").document(firebaseUser.getUid())
                .update(updatedUserDetails)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed(); // Go back to the ViewProfileFragment
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                });


    }
}