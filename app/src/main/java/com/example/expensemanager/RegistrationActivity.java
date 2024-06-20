package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mMobile;
    private EditText mEmail;
    private EditText mPassword;
    private Button signupButton;
    private TextView mLogin;

    private ProgressDialog mDialog;

    //Firebase...

    private FirebaseAuth mAuth;
    TextView regemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        signup();
    }

    private void signup(){
        mName=findViewById(R.id.name_signup);
        mMobile=findViewById(R.id.mobile_signup);
        mEmail=findViewById(R.id.email_signup);
        mPassword=findViewById(R.id.password_signup);
        signupButton=findViewById(R.id.btn_signup);
        mLogin=findViewById(R.id.login);


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=mName.getText().toString().trim();
                String mobile=mMobile.getText().toString().trim();
                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();

                //Name validation
                if(TextUtils.isEmpty(name)){
                    mName.setError("Name cannot be empty..");
                    mName.requestFocus();
                    return;
                }
                if(!name.matches("^[A-Za-z]+$")){
                    mName.setError("Name contains only alphabets..");
                    mName.requestFocus();
                    return;
                }
                if(name.length()<2){
                    mName.setError("Name must contains at least 2 characters..");
                    mName.requestFocus();
                    return;
                }

                //Phone no. validation
                if(TextUtils.isEmpty(mobile)){
                    mMobile.setError("Mobile cannot be empty..");
                    mMobile.requestFocus();
                    return;
                }
                if(!mobile.matches("^[0-9]+$")){
                    mMobile.setError("Mobile must have only numeric values..");
                    mMobile.requestFocus();
                    return;
                }
                if(mobile.length()<10){
                    mMobile.setError("Mobile must contain 10 digits..");
                    mMobile.requestFocus();
                    return;
                }
                if(mobile.length()>10){
                    mMobile.setError("Mobile must contain 10 digits..");
                    mMobile.requestFocus();
                    return;
                }

                //Email validation
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email cannot be empty..");
                    mEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmail.setError("Please enter valid email..");
                    mEmail.requestFocus();
                    return;
                }

                //Password validation
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password cannot be empty..");
                    mPassword.requestFocus();
                    return;
                }
                if(password.length()<6){
                    mPassword.setError("Must contain at least 6 characters..");
                    mPassword.requestFocus();
                    return;
                }
                if(password.length()>15){
                    mPassword.setError("Maximum 15 character allowed..");
                    mPassword.requestFocus();
                    return;
                }
                Log.i("val", name);
                Log.i("val", mobile);
                Log.i("val", email);
                Log.i("val", password);
                mDialog.setMessage("Please wait while we process your data");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            mDialog.dismiss();

                            String name=mName.getText().toString().trim();
                            String mobile=mMobile.getText().toString().trim();
                            String email=mEmail.getText().toString().trim();
                            FirebaseUser mUser=mAuth.getCurrentUser();

                            if(mAuth!=null) {
                                String uid = mUser.getUid();
                                DatabaseReference myRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                DatabaseReference userNameRef =  myRootRef.child("Name");
                                userNameRef.setValue(name);

                                DatabaseReference MobileRef =  myRootRef.child("Mobile");
                                MobileRef.setValue(mobile);

                                DatabaseReference EmailRef =  myRootRef.child("Email");
                                EmailRef.setValue(email);

                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();
                            }

                        }
                        else{
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Registration Failed!",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

    }
}

