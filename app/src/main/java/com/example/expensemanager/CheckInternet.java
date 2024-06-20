package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class CheckInternet extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_internet);

        Button tv = findViewById(R.id.try_again);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConnected();
            }
        });
    }

    //check internet Connection
    private void isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetwork = connectivityManager.getActiveNetworkInfo();

        if (null != activenetwork) {

            if ((activenetwork.getType() == ConnectivityManager.TYPE_MOBILE) || (activenetwork.getType() == ConnectivityManager.TYPE_MOBILE)) {

                mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser()!=null){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                }
                else{
                    startActivity(new Intent(CheckInternet.this, LoginActivity.class));
                }
                finish();
            }

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

}