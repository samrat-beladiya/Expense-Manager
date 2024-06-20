package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String str;
    Button changepass;
    Button deleteaccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile();
    }


    void profile(){
        //Progress dialog
        final Dialog dialog;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.show();

        changepass=findViewById(R.id.changepass);
        deleteaccount=findViewById(R.id.deleteaccount);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid = mUser.getUid();
        DatabaseReference mUserInfoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("Name").getValue().toString();
                TextView name=findViewById(R.id. name_profile);
                TextView welcomename=findViewById(R.id.user_name);
                welcomename.setText("Welcome "+userName);
                name.setText(userName);

                String userMobile = dataSnapshot.child("Mobile").getValue().toString();
                TextView mobile=findViewById(R.id.mobile_profile);
                mobile.setText(userMobile);

                String userEmail = dataSnapshot.child("Email").getValue().toString();
                TextView email=findViewById(R.id.email_profile);
                email.setText(userEmail);

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mUserInfoDatabase.addListenerForSingleValueEvent(valueEventListener);

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ResetPasswordActivity.class));
                finish();
            }
        });

        deleteaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(Profile.this);
                dialog.setIcon(R.drawable.ic_delete_forever);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will result in completely removing your account from Expense Manager and you will no longer be able to access this account and your all data will be lost. " +
                        "In future if you wish to use the same email then you need to register again.");
                dialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(Profile.this,"Account Deleted Successfully..",Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(Profile.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog=dialog.create();
                alertDialog.show();
            }
        });

        ImageView back_arrow=findViewById(R.id.back);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

}