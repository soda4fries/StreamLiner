package com.example.streamliner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);


        EditText ETName=findViewById(R.id.ETName);
        EditText ETPhone=findViewById(R.id.ETPhone);
        EditText ETEmail =findViewById(R.id.ETEmail);
        EditText ETPassword=findViewById(R.id.ETPwd);
        EditText ETConfirmPassword=findViewById(R.id.ETConfirmPwd);


        Button btnSignUp=findViewById(R.id.BTSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtain the entered data
                String textName,textPhone,textEmail,textPassword,textConfirmPassword;
                textName=ETName.getText().toString();
                textPhone=ETPhone.getText().toString();
                textEmail= ETEmail.getText().toString();
                textPassword=ETPassword.getText().toString();
                textConfirmPassword=ETConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(textName)){
                    Toast.makeText(Register.this,"Enter name",Toast.LENGTH_LONG).show();
                    ETName.setError("Name is required");
                    ETName.requestFocus();
                } else if(TextUtils.isEmpty(textPhone)){
                    Toast.makeText(Register.this,"Enter phone number",Toast.LENGTH_LONG).show();
                    ETPhone.setError("Phone number is required");
                    ETPhone.requestFocus();
                }else if(textPhone.length()!=10){
                    Toast.makeText(Register.this,"Please re-enter your phone no.",Toast.LENGTH_LONG).show();
                    ETPhone.setError("Phone number should be 10 digits");
                    ETPhone.requestFocus();
                } else if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(Register.this,"Enter email",Toast.LENGTH_LONG).show();
                    ETEmail.setError("Email is required");
                    ETEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(Register.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    ETEmail.setError("Valid email is required");
                    ETEmail.requestFocus();
                } else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(Register.this,"Enter password",Toast.LENGTH_LONG).show();
                    ETPassword.setError("Password is required");
                    ETPassword.requestFocus();
                } else if(TextUtils.isEmpty(textConfirmPassword)){
                    Toast.makeText(Register.this,"Confirm password",Toast.LENGTH_SHORT).show();
                    ETConfirmPassword.setError("Password confirmation is required");
                    ETConfirmPassword.requestFocus();
                }else if(!textPassword.equals(textConfirmPassword)){
                    Toast.makeText(Register.this,"Please enter same password",Toast.LENGTH_SHORT).show();
                    ETConfirmPassword.setError("Password confirmation is required");
                    ETConfirmPassword.requestFocus();
                    //Clear the entered passwords
                    ETPassword.clearComposingText();
                    ETConfirmPassword.clearComposingText();
                }else{
                    //all fields are fulfil the requirements, then register the user
                    registerUser(textName,textPhone,textEmail,textPassword,textConfirmPassword);
                }


            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void registerUser(String textName, String textPhone, String textEmail, String textPassword, String textConfirmPassword) {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this,"User registered successfully",Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser=auth.getCurrentUser();
                    if(firebaseUser!=null) {
                        firebaseUser.sendEmailVerification();
                    }

                    //Navigate to Login activity
                    Intent intent=new Intent(Register.this, Login.class);
                    startActivity(intent);
                    finish(); //close register activity
                }else{
                    Toast.makeText(Register.this,"Registration failed:"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    Log.e("RegisterError",task.getException().toString());
                }
            }
        });
    }
}