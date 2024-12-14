package com.example.streamliner.Auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.streamliner.Auth.Model.User;
import com.example.streamliner.R;
import com.example.streamliner.databinding.FragmentRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.registerButton.setOnClickListener(v -> registerUser());
        binding.loginLink.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment));

        return binding.getRoot();
    }

    private void registerUser() {
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        String displayName = binding.displayNameInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = auth.getCurrentUser().getUid();
                        User newUser = new User(uid, email, displayName);

                        database.getReference("users").child(uid)
                                .setValue(newUser)
                                .addOnCompleteListener(dbTask -> {
                                    binding.progressBar.setVisibility(View.GONE);
                                    if (dbTask.isSuccessful()) {
                                        Navigation.findNavController(binding.getRoot())
                                                .navigate(R.id.action_registerFragment_to_chatsFragment);
                                    } else {
                                        Toast.makeText(getContext(), "Failed to create user profile",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}