package com.example.streamliner.ui.loginAndRegister;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.streamliner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends Fragment {
    private EditText ETName, ETPhone, ETEmail, ETPassword, ETConfirmPassword;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_page, container, false);

        // Initialize UI elements
        ETName = view.findViewById(R.id.ETName);
        ETPhone = view.findViewById(R.id.ETPhone);
        ETEmail = view.findViewById(R.id.ETEmail);
        ETPassword = view.findViewById(R.id.ETPwd);
        ETConfirmPassword = view.findViewById(R.id.ETConfirmPwd);

        TextView TVGoToSignIn = view.findViewById(R.id.TVGoToSignIn);
        Button btnSignUp = view.findViewById(R.id.BTSignUp);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Sign Up button click listener
        btnSignUp.setOnClickListener(v -> {
            String textName = ETName.getText().toString();
            String textPhone = ETPhone.getText().toString();
            String textEmail = ETEmail.getText().toString();
            String textPassword = ETPassword.getText().toString();
            String textConfirmPassword = ETConfirmPassword.getText().toString();

            if (TextUtils.isEmpty(textName)) {
                showError(ETName, "Name is required");
            } else if (TextUtils.isEmpty(textPhone)) {
                showError(ETPhone, "Phone number is required");
            } else if (textPhone.length() != 10 && textPhone.length() != 11) {
                showError(ETPhone, "Phone number should be 10 or 11 digits");
            } else if (TextUtils.isEmpty(textEmail)) {
                showError(ETEmail, "Email is required");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                showError(ETEmail, "Valid email is required");
            } else if (TextUtils.isEmpty(textPassword)) {
                showError(ETPassword, "Password is required");
            } else if (TextUtils.isEmpty(textConfirmPassword)) {
                showError(ETConfirmPassword, "Password confirmation is required");
            } else if (!textPassword.equals(textConfirmPassword)) {
                showError(ETConfirmPassword, "Passwords do not match");
            } else {
                registerUser(textName, textPhone, textEmail, textPassword);
            }
        });

        // Navigate to LoginFragment
        TVGoToSignIn.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, new LoginPage())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void showError(EditText editText, String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        editText.setError(error);
        editText.requestFocus();
    }

    private void registerUser(String textName, String textPhone, String textEmail, String textPassword) {
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "User registered successfully", Toast.LENGTH_LONG).show();

                FirebaseUser firebaseUser = auth.getCurrentUser();

                // Update display name
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(textName).build();
                firebaseUser.updateProfile(profileChangeRequest);

                // Save user data into Firestore
                Map<String, Object> userDetails = new HashMap<>();
                userDetails.put("name", textName);
                userDetails.put("phone", textPhone);
                userDetails.put("email", textEmail);

                firestore.collection("users").document(firebaseUser.getUid())
                        .set(userDetails)
                        .addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                Toast.makeText(getContext(), "User registered successfully.", Toast.LENGTH_LONG).show();
                                // Navigate to LoginFragment
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.nav_host_fragment, new LoginPage())
                                        .commit();
                            } else {
                                Toast.makeText(getContext(), "User registration failed. Try again.", Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    showError(ETEmail, "Your email is invalid or already in use.");
                } catch (FirebaseAuthUserCollisionException e) {
                    showError(ETEmail, "User is already registered with this email.");
                } catch (Exception e) {
                    Log.e("RegisterError", e.getMessage());
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}