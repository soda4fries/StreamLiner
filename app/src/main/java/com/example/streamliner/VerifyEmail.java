package com.example.streamliner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VerifyEmail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_email);

        EditText ETCode=findViewById(R.id.ETCode);
        Button BTContinue=findViewById(R.id.BTContVerifyEmail);


        //Get email passed from Forget Password Activity
        String email=getIntent().getStringExtra("EMAIL");

        BTContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=ETCode.getText().toString();

                //verify the code
                if(!code.isEmpty()){
                    //Navigate to Reset Password Activity
                    Intent intent=new Intent(VerifyEmail.this,ResetPwd.class);
                    startActivity(intent);
                    finish();
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