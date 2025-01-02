package com.example.streamliner.Authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.streamliner.R;
import com.example.streamliner.databinding.FragmentLoginPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginPage extends Fragment {
    private FragmentLoginPageBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginPageBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();

        setupListeners();
        setupWindowInsets();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            navigateToChats();
        }
    }

    private void setupListeners() {
        binding.BTSignIn.setOnClickListener(v -> {
            String email = binding.ETEmailLogin.getText().toString().trim();
            String password = binding.ETPwdLogin.getText().toString().trim();

            if (validateInputs(email, password)) {
                loginUser(email, password);
            }
        });

        binding.TVFgtPwd.setOnClickListener(v -> navigateToFragment(R.id.action_loginPage_to_forgetPassword));
        binding.TVGoToRegister.setOnClickListener(v -> navigateToFragment(R.id.action_loginPage_to_registerPage));
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            showError(binding.ETEmailLogin, "Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(binding.ETEmailLogin, "Valid email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            showError(binding.ETPwdLogin, "Password is required");
            return false;
        }
        return true;
    }

    private void showError(View view, String message) {
        if (view instanceof android.widget.EditText) {
            ((android.widget.EditText) view).setError(message);
            view.requestFocus();
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void loginUser(String email, String password) {
        binding.BTSignIn.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    binding.BTSignIn.setEnabled(true);
                    if (task.isSuccessful()) {
                        navigateToChats();
                    } else {
                        handleLoginError(task.getException());
                    }
                });
    }

    private void handleLoginError(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            showError(binding.ETEmailLogin, "User does not exist or is no longer valid. Please register again.");
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            showError(binding.ETEmailLogin, "Invalid credentials. Kindly check and re-enter.");
        } else {
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToChats() {
        navigateToFragment(R.id.action_loginPage_to_chatsFragment);
    }

    private void navigateToFragment(int actionId) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(actionId);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}