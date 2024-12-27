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

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmail extends AppCompatActivity {

    private String email;
    private String correctCode;

    private EditText ETCode;
    private Button BTContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_email);

         ETCode=findViewById(R.id.ETCode);
         BTContinue=findViewById(R.id.BTContVerifyEmail);


        //Get email passed from Forget Password Activity
         email=getIntent().getStringExtra("email");
         correctCode=getIntent().getStringExtra("verificationCode");

        BTContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode=ETCode.getText().toString();

                //verify the code
                if(TextUtils.isEmpty(enteredCode)){
                    ETCode.setError("Code is required");
                    ETCode.requestFocus();
                    return;
                }else if(enteredCode.equals(correctCode)){
                    //Code is correct,proceed to Reset Password Activity
                    Intent intent=new Intent(VerifyEmail.this,ResetPwd.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                    finish();

                }else{
                    Toast.makeText(VerifyEmail.this,"Invalid code",Toast.LENGTH_LONG).show();
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


}