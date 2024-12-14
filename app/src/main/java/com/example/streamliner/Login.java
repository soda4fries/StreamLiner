package com.example.streamliner;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {

    EditText ETEmailLogin,ETPasswordLogin;
    FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ETEmailLogin=findViewById(R.id.ETEmailLogin);
        ETPasswordLogin=findViewById(R.id.ETPwdLogin);

        authProfile=FirebaseAuth.getInstance();

        Button BTSignIn=findViewById(R.id.BTSignIn);
        TextView TVFgtPwd=findViewById(R.id.TVFgtPwd);
        TextView TVRegisterNow=findViewById(R.id.TVGoToRegister);

        //Navigate to the main app screen after login
        BTSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail=ETEmailLogin.getText().toString();
                String textPassword=ETPasswordLogin.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(Login.this,"Enter email",Toast.LENGTH_LONG).show();
                    ETEmailLogin.setError("Email is required");
                    ETEmailLogin.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(Login.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    ETEmailLogin.setError("Valid email is required");
                    ETEmailLogin.requestFocus();
                }else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(Login.this,"Enter password",Toast.LENGTH_LONG).show();
                    ETPasswordLogin.setError("Password is required");
                    ETPasswordLogin.requestFocus();
                }else{
                    loginUser(textEmail,textPassword);
                }
            }
        });

        //Navigate to Forget Password Activity
        TVFgtPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this, ForgetPwd.class);
                startActivity(intent);
                finish();
            }
        });

        //Navigate to Register Activity
        TVRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loginUser(String email, String password) {
        authProfile.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this,"You are logged in now",Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        ETEmailLogin.setError("User does not exists or is no longer valid. Please register again.");
                        ETEmailLogin.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        ETEmailLogin.setError("Invalid credentials. Kindly check and re-enter.");
                        ETEmailLogin.requestFocus();
                    } catch (Exception e) {
                        Log.e("Login",e.getMessage());
                        Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }
}