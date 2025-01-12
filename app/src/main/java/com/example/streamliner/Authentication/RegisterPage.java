package com.example.streamliner.Authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.streamliner.R;
import com.example.streamliner.databinding.FragmentRegisterPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends Fragment {
    private static final String TAG = "RegisterPage";
    private FragmentRegisterPageBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if there's any existing session and clear it
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            auth.signOut();
        }
        firestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Sign Up button click listener
        binding.BTSignUp.setOnClickListener(v -> validateAndRegister());

        // Navigate to LoginFragment
        binding.TVGoToSignIn.setOnClickListener(v -> {
            try {
                Log.d(TAG, "Navigating to LoginPage");
                NavController navController = Navigation.findNavController(v);
                // Navigate with pop behavior to prevent stack buildup
                navController.navigate(R.id.action_registerPage_to_loginPage, null, new NavOptions.Builder()
                        .setPopUpTo(navController.getCurrentDestination().getId(), true)
                        .build());
            } catch (Exception e) {
                Log.e(TAG, "Navigation error: ", e);
                showToast("Navigation error occurred");
            }
        });
    }

    private void validateAndRegister() {
        String textName = binding.ETName.getText().toString().trim();
        String textPhone = binding.ETPhone.getText().toString().trim();
        String textEmail = binding.ETEmail.getText().toString().trim();
        String textPassword = binding.ETPwd.getText().toString();
        String textConfirmPassword = binding.ETConfirmPwd.getText().toString();

        if (!validateInputs(textName, textPhone, textEmail, textPassword, textConfirmPassword)) {
            return;
        }

        registerUser(textName, textPhone, textEmail, textPassword);
    }

    private boolean validateInputs(String name, String phone, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            showError(binding.ETName, "Name is required");
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            showError(binding.ETPhone, "Phone number is required");
            return false;
        }
        if (phone.length() != 10 && phone.length() != 11) {
            showError(binding.ETPhone, "Phone number should be 10 or 11 digits");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            showError(binding.ETEmail, "Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(binding.ETEmail, "Valid email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            showError(binding.ETPwd, "Password is required");
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            showError(binding.ETConfirmPwd, "Password confirmation is required");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showError(binding.ETConfirmPwd, "Passwords do not match");
            return false;
        }
        return true;
    }

    private void showError(View view, String error) {
        if (!isAdded()) return;

        if (view instanceof android.widget.EditText) {
            ((android.widget.EditText) view).setError(error);
            view.requestFocus();
        }
        showToast(error);
    }

    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(String name, String phone, String email, String password) {
        if (!isAdded()) return;

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!isAdded()) return;

                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser == null) {
                            showToast("Registration failed. Please try again.");
                            return;
                        }

                        updateUserProfile(firebaseUser, name, phone, email);
                    } else {
                        handleRegistrationError(task.getException());
                    }
                });
    }

    private void updateUserProfile(FirebaseUser firebaseUser, String name, String phone, String email) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        firebaseUser.updateProfile(profileUpdate)
                .addOnCompleteListener(task -> {
                    if (!isAdded()) return;

                    if (task.isSuccessful()) {
                        saveUserToFirestore(firebaseUser, name, phone, email);
                    } else {
                        Log.e(TAG, "Profile update failed", task.getException());
                        showToast("Failed to update profile");
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser firebaseUser, String name, String phone, String email) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", name);
        userDetails.put("phone", phone);
        userDetails.put("email", email);

        firestore.collection("users").document(firebaseUser.getUid())
                .set(userDetails)
                .addOnCompleteListener(task -> {
                    if (!isAdded()) return;

                    if (task.isSuccessful()) {
                        showToast("Registration successful");
                        navigateToChats();
                    } else {
                        Log.e(TAG, "Firestore save failed", task.getException());
                        showToast("Failed to save user details");
                    }
                });
    }

    private void handleRegistrationError(Exception exception) {
        if (!isAdded()) return;

        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            showError(binding.ETPwd, "Password must be at least 6 characters long.");
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            showError(binding.ETEmail, "User is already registered with this email.");
        } else {
            Log.e(TAG, "Registration error", exception);
            showToast(exception != null ? exception.getMessage() : "Registration failed");
        }
    }

    private void navigateToChats() {
        try {
            if (isAdded() && getView() != null) {
                NavController navController = Navigation.findNavController(getView());
                // Use SafeArgs or add navigation options
                navController.navigate(R.id.action_registerPage_to_chatsFragment, null, new NavOptions.Builder()
                        .setPopUpTo(R.id.mobile_navigation, true)
                        .build());
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation error: ", e);
            showToast("Navigation error occurred");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}