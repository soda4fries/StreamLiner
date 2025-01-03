package com.example.streamliner.Authentication;

import android.os.Bundle;

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


public class VerifyEmailPage extends Fragment {


    private String email;
    private String correctCode;

    private EditText ETCode;
    private Button BTContinue;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_email_page, container, false);

        ETCode = view.findViewById(R.id.ETCode);
        BTContinue = view.findViewById(R.id.BTContVerifyEmail);

        // Retrieve arguments passed from parent Activity or Fragment
        if (getArguments() != null) {
            email = getArguments().getString("email");
            correctCode = getArguments().getString("verificationCode");
        }

        BTContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = ETCode.getText().toString();

                // Verify the code
                if (TextUtils.isEmpty(enteredCode)) {
                    ETCode.setError("Code is required");
                    ETCode.requestFocus();
                } else if (enteredCode.equals(correctCode)) {
                    // Code is correct, navigate to Reset Password Fragment or Activity
                    ResetPassword resetPasswordFragment = new ResetPassword();
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email);
                    resetPasswordFragment.setArguments(bundle);

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_verifyEmailPage_to_resetPassword);
                } else {
                    Toast.makeText(requireContext(), "Invalid code", Toast.LENGTH_LONG).show();
                }
            }
        });


        return view;
    }
}