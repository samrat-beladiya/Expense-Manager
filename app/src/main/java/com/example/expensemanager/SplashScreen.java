package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    //firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        isConnected();
    }

    private void isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activenetwork = connectivityManager.getActiveNetworkInfo();


        if (null != activenetwork) {

            if ((activenetwork.getType() == ConnectivityManager.TYPE_MOBILE) || (activenetwork.getType() == ConnectivityManager.TYPE_WIFI)) {

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mAuth = FirebaseAuth.getInstance();
                        if(mAuth.getCurrentUser()!=null){
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                        else{
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                        finish();
                    }
                },  800);
            }

        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(getApplicationContext(), CheckInternet.class));
                    finish();
                }
            },  800);

        }
    }
}