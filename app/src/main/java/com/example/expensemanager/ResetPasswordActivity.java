package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_reset_password);
        EditText mailText=findViewById(R.id.forgot_password_email);
        Button sendEmail_btn=findViewById(R.id.btn_reset_password);


        sendEmail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mailText.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    mailText.setError("Please enter Email..");
                    mailText.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mailText.setError("Please enter valid email..");
                    mailText.requestFocus();
                    return;
                }

                 mAuth.sendPasswordResetEmail(email)
                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "Please Check your mail for Password reset Instructions", Toast.LENGTH_LONG).show();
                                    mAuth.signOut();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finishAffinity();
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Failed to send Email", Toast.LENGTH_LONG).show();
                                }
                             }
                         });
            }
        });


    }
}