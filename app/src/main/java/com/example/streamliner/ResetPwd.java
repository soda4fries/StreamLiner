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

public class ResetPwd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_pwd);

        EditText ETNewPwd=findViewById(R.id.ETNewPwd);
        EditText ETConfirmPwd=findViewById(R.id.ETConfirmNewPwd);
        Button BTResetPwd=findViewById(R.id.BTResetPwd);

        BTResetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPwd=ETNewPwd.getText().toString();
                String confirmPwd=ETConfirmPwd.getText().toString();

                if(newPwd.equals(confirmPwd)){
                    //Navigate back to Login Activity
                    Intent intent=new Intent(ResetPwd.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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