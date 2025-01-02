package com.example.streamliner.Authentication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.example.streamliner.R;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPassword extends Fragment {
    private FirebaseAuth authProfile;
    private String verificationCode;

    private EditText ETEmail;
    private Button BTSendCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        // Initialize views
        ETEmail = view.findViewById(R.id.ETEmailFgtPwd);
        BTSendCode = view.findViewById(R.id.BTSendCode);
        authProfile = FirebaseAuth.getInstance();

        // Set OnClickListener for the button
        BTSendCode.setOnClickListener(v -> {
            String email = ETEmail.getText().toString();

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

            verificationCode = generateCode();
            sendVerificationEmail(email, verificationCode);

            // Navigate to VerifyEmailFragment
            Bundle bundle = new Bundle();
            bundle.putString("email", email);
            bundle.putString("verificationCode", verificationCode);

            VerifyEmailPage verifyEmailFragment = new VerifyEmailPage();
            verifyEmailFragment.setArguments(bundle);

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_forgetPassword_to_verifyEmailPage);
        });

        return view;
    }

    private String generateCode() {
        return String.valueOf((int) (Math.random() * 9000) + 1000); // Generate 4-digit code
    }

    private void sendVerificationEmail(String email, String code) {
        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.sendgrid.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SendGridService service = retrofit.create(SendGridService.class);

        EmailRequest emailRequest = new EmailRequest(
                email,
                "Password Reset Verification Code",
                "Your verification code is: " + code,
                "thamjingqin@gmail.com" // Replace with the verified sender email
        );

        service.sendEmail(emailRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Verification code sent to " + email, Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(),"Please check your spam folder",Toast.LENGTH_LONG).show();
                } else {
                    Log.e("EmailError", "Failed to send email: " + response.errorBody());
                    Toast.makeText(getContext(), "Failed to send email", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("EmailError", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}