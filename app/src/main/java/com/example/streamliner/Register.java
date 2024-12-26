package com.example.streamliner;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
EditText ETName,ETPhone,ETEmail,ETPassword,ETConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);


        ETName=findViewById(R.id.ETName);
        ETPhone=findViewById(R.id.ETPhone);
        ETEmail =findViewById(R.id.ETEmail);
        ETPassword=findViewById(R.id.ETPwd);
        ETConfirmPassword=findViewById(R.id.ETConfirmPwd);


        TextView TVGoToSignIn=findViewById(R.id.TVGoToSignIn);
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
                }else if(textPhone.length()!=10 && textPhone.length() != 11){
                    Toast.makeText(Register.this,"Please re-enter your phone no.",Toast.LENGTH_LONG).show();
                    ETPhone.setError("Phone number should be 10 or 11 digits");
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

                }else{
                    //all fields are fulfil the requirements, then register the user
                    registerUser(textName,textPhone,textEmail,textPassword,textConfirmPassword);
                }

            }
        });

        //Navigate to Login Activity
        TVGoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Register.this, Login.class);
                startActivity(intent);
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
        //create user profile
        auth.createUserWithEmailAndPassword(textEmail,textPassword).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this,"User registered successfully",Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser=auth.getCurrentUser();

                    //Update display name of user
                    UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(textName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Save user data into firebase realtime database
                    ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(textPhone,textEmail);

                    //Extracting user reference from database
                    DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this,"User registered successfully.",Toast.LENGTH_LONG).show();

                                //Navigate to Login activity
                                Intent intent=new Intent(Register.this, Login.class);
                                startActivity(intent);
                                finish(); //close register activity
                            }else{
                                Toast.makeText(Register.this,"User registered failed.Please try again",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    //firebaseUser.sendEmailVerification();

                }else{
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        ETPassword.setError("Your email is invalid or already in use.");
                        ETPassword.requestFocus();

                    }catch(FirebaseAuthUserCollisionException e){
                        ETPassword.setError("User is already registered with this email. Use another email.");
                        ETPassword.requestFocus();

                    } catch (Exception e) {
                        Log.e("RegisterError",e.getMessage());
                        Toast.makeText(Register.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}