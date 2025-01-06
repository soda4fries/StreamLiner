package com.example.streamliner.Authentication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.streamliner.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends Fragment {
    private FirebaseAuth authProfile;
    private EditText ETEmail;
    private Button BTSendCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        // Initialize views
        ETEmail = view.findViewById(R.id.ETEmailFgtPwd);
        BTSendCode = view.findViewById(R.id.BTSendCode);
        authProfile = FirebaseAuth.getInstance();

        // Set OnClickListener for the button
        BTSendCode.setOnClickListener(v -> {
            String email = ETEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                ETEmail.setError("Email is required");
                ETEmail.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                ETEmail.setError("Please enter a valid email address");
                ETEmail.requestFocus();
                return;
            }

            // Show progress or disable button
            BTSendCode.setEnabled(false);

            // Send password reset email using Firebase
            authProfile.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Password reset link sent to " + email,
                                    Toast.LENGTH_LONG).show();

                            // Navigate back to login or show confirmation
                            NavController navController = Navigation.findNavController(
                                    requireActivity(),
                                    R.id.nav_host_fragment
                            );
                            navController.navigateUp();
                        } else {
                            Toast.makeText(getContext(),
                                    "Failed to send password reset email. " +
                                            task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        BTSendCode.setEnabled(true);
                    });
        });

        return view;
    }
}