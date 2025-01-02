package com.example.streamliner.Authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.streamliner.R;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPassword extends Fragment {

    private EditText ETNewPwd, ETConfirmPwd;
    private Button BTResetPwd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        // Initialize UI elements
        ETNewPwd = view.findViewById(R.id.ETNewPwd);
        ETConfirmPwd = view.findViewById(R.id.ETConfirmNewPwd);
        BTResetPwd = view.findViewById(R.id.BTResetPwd);

        // Retrieve email passed as an argument
        String email = requireArguments().getString("email");

        // Set button click listener
        BTResetPwd.setOnClickListener(v -> {
            String newPwd = ETNewPwd.getText().toString();
            String confirmPwd = ETConfirmPwd.getText().toString();

            if (TextUtils.isEmpty(newPwd)) {
                showError(ETNewPwd, "New password is required");
            } else if (TextUtils.isEmpty(confirmPwd)) {
                showError(ETConfirmPwd, "Password confirmation is required");
            } else if (!newPwd.equals(confirmPwd)) {
                showError(ETConfirmPwd, "Passwords do not match");
            } else {
                changePassword(email, newPwd);
            }
        });

        return view;
    }

    private void showError(EditText editText, String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        editText.setError(error);
        editText.requestFocus();
    }

    private void changePassword(String email, String newPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Send a password reset email
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Password reset email sent", Toast.LENGTH_LONG).show();

                // Navigate to LoginFragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_resetPassword_to_loginPage);
            } else {
                Toast.makeText(getContext(), "Error in sending password reset email", Toast.LENGTH_LONG).show();
            }
        });

    }
}