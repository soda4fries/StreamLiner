package com.example.streamliner.Authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamliner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginPage extends Fragment {

    private EditText ETEmailLogin, ETPasswordLogin;
    private FirebaseAuth authProfile;
    NavController navController ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_page, container, false);

        ETEmailLogin = view.findViewById(R.id.ETEmailLogin);
        ETPasswordLogin = view.findViewById(R.id.ETPwdLogin);

        authProfile = FirebaseAuth.getInstance();

        Button BTSignIn = view.findViewById(R.id.BTSignIn);
        TextView TVFgtPwd = view.findViewById(R.id.TVFgtPwd);
        TextView TVRegisterNow = view.findViewById(R.id.TVGoToRegister);

        // Navigate to the main app screen after login
        BTSignIn.setOnClickListener(v -> {

            String textEmail = ETEmailLogin.getText().toString();
            String textPassword = ETPasswordLogin.getText().toString();

            if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(getContext(), "Enter email", Toast.LENGTH_LONG).show();
                ETEmailLogin.setError("Email is required");
                ETEmailLogin.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(getContext(), "Please re-enter your email", Toast.LENGTH_LONG).show();
                ETEmailLogin.setError("Valid email is required");
                ETEmailLogin.requestFocus();
            } else if (TextUtils.isEmpty(textPassword)) {
                Toast.makeText(getContext(), "Enter password", Toast.LENGTH_LONG).show();
                ETPasswordLogin.setError("Password is required");
                ETPasswordLogin.requestFocus();
            } else {

                loginUser(textEmail, textPassword);
            }
        });

        // Navigate to Forget Password Fragment
        TVFgtPwd.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_loginPage_to_forgetPassword);
        });

        // Navigate to Register Fragment
        TVRegisterNow.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_loginPage_to_registerPage);
        });

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        return view;
    }

    private void loginUser(String email, String password) {

       authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "You are logged in now", Toast.LENGTH_LONG).show();
                //navigate to me page
                navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_loginPage_to_mePage);

            } else {
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidUserException e) {
                    ETEmailLogin.setError("User does not exist or is no longer valid. Please register again.");
                    ETEmailLogin.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    ETEmailLogin.setError("Invalid credentials. Kindly check and re-enter.");
                    ETEmailLogin.requestFocus();
                } catch (Exception e) {
                    Log.e("LoginFragment", e.getMessage());
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}