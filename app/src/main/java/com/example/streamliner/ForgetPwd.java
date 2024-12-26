package com.example.streamliner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ForgetPwd extends AppCompatActivity {
private FirebaseAuth authProfile;
private String verificationCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_pwd);

        EditText ETEmail=findViewById(R.id.ETEmailFgtPwd);
        Button BTSendCode=findViewById(R.id.BTSendCode);
        authProfile=FirebaseAuth.getInstance();

        BTSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=ETEmail.getText().toString();

                if(TextUtils.isEmpty(email)){
                    ETEmail.setError("Email is required");
                    ETEmail.requestFocus();
                    return;
                }

                verificationCode=generateCode();
                sendEmail(email,verificationCode);

                    //Proceed to VerifyEmail Activity
                    Intent intent=new Intent(ForgetPwd.this, VerifyEmail.class);
                    intent.putExtra("email",email);
                    intent.putExtra("verificationCode",verificationCode);
                    startActivity(intent);

                }
                });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String generateCode(){
        return String.valueOf((int)(Math.random()*9000)+1000);//Generate 4 digit code
    }

    private void sendEmail(String email,String code){
        // Use an email-sending service to send the code
        // For example, use Firebase Functions, SendGrid, or your backend API

        Toast.makeText(ForgetPwd.this,"Verification code sent to "+email,Toast.LENGTH_LONG).show();
    }

}