package com.example.streamliner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPwd extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_pwd);


        EditText ETNewPwd=findViewById(R.id.ETNewPwd);
        EditText ETConfirmPwd=findViewById(R.id.ETConfirmNewPwd);
        Button BTResetPwd=findViewById(R.id.BTResetPwd);

        String email=getIntent().getStringExtra("email");

        BTResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPwd=ETNewPwd.getText().toString();
                String confirmPwd=ETConfirmPwd.getText().toString();

                if(TextUtils.isEmpty(newPwd)){
                    Toast.makeText(ResetPwd.this,"Enter new password",Toast.LENGTH_LONG).show();
                    ETNewPwd.setError("New password is required");
                    ETNewPwd.requestFocus();
                } else if(TextUtils.isEmpty(confirmPwd)){
                    Toast.makeText(ResetPwd.this,"Confirm new password",Toast.LENGTH_SHORT).show();
                    ETConfirmPwd.setError("Password confirmation is required");
                    ETConfirmPwd.requestFocus();
                }else if(!newPwd.equals(confirmPwd)){
                    Toast.makeText(ResetPwd.this,"Please enter same password",Toast.LENGTH_SHORT).show();
                    ETConfirmPwd.setError("Password confirmation is required");
                    ETConfirmPwd.requestFocus();

                }else{
                    changePassword(email,newPwd);
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void changePassword(String email,String newPassword){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=auth.getCurrentUser();

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ResetPwd.this, "Password reset email sent", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ResetPwd.this, "Error in sending password reset email", Toast.LENGTH_LONG).show();
                }
            }
        });
        /*firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ResetPwd.this,"Password has been changed",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ResetPwd.this,Login.class);
                    startActivity(intent);
                    finish();
                }else{
                    try{
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(ResetPwd.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

         */
    }
}