package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private ImageView imageView;

    //Fragment

    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;
    private StatsFragment statsFragment;

    private FirebaseAuth mAuth;

    //for back press twice
    private static final int TIME_INTERVAL = 2000;
    private long backPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //progress dialog
        final Dialog dialog;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.show();

        imageView=findViewById(R.id.profile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Profile.class));
            }
        });

        //set username on navigation header
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid = mUser.getUid();
        DatabaseReference mUserInfoDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user = dataSnapshot.child("Name").getValue().toString();
                TextView name=findViewById(R.id.user);
                name.setText("Welcome "+user);

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mUserInfoDatabase.addListenerForSingleValueEvent(valueEventListener);

        mAuth=FirebaseAuth.getInstance();
        Toolbar toolbar=findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Manager");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle
                (
                        this,
                        drawerLayout,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close
                )
        {};
        toggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(toggle);


        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView=findViewById(R.id.bottomNavbar);
        frameLayout=findViewById(R.id.main_frame);

        dashboardFragment=new DashboardFragment();
        incomeFragment=new IncomeFragment();
        expenseFragment=new ExpenseFragment();
        statsFragment=new StatsFragment();
        setFragment(dashboardFragment);

        mAuth=FirebaseAuth.getInstance();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){

                    case R.id.dashboard:
                        setFragment(dashboardFragment);
                        return true;
                    case R.id.income:
                        setFragment(incomeFragment);
                        return true;
                    case R.id.expense:
                        setFragment(expenseFragment);
                        return true;
                    case R.id.stats:
                        setFragment(statsFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);

        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        else{
            if (backPressed + TIME_INTERVAL > System.currentTimeMillis()){
                super.onBackPressed();
                return;
            }
            else{
                Toast.makeText(this, "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
            }
            backPressed = System.currentTimeMillis();
        }

    }

    public void displaySelectedListener(int itemId){
        Fragment fragment = null;

        switch(itemId){
            case android.R.id.home:
                DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
                return;
            case R.id.dashboard:
                bottomNavigationView.setSelectedItemId(R.id.dashboard);
                fragment=new DashboardFragment();
                break;

            case R.id.income:
                bottomNavigationView.setSelectedItemId(R.id.income);
                fragment=new IncomeFragment();
                break;

            case R.id.expense:
                bottomNavigationView.setSelectedItemId(R.id.expense);
                fragment=new ExpenseFragment();
                break;
            case R.id.stats:
                bottomNavigationView.setSelectedItemId(R.id.stats);
                bottomNavigationView.findViewById(R.id.stats).performClick();
                bottomNavigationView.performClick();
                fragment=new StatsFragment();
                break;
            case R.id.account:
                startActivity(new Intent(getApplicationContext(), Profile.class));
                break;

            case R.id.about:
                startActivity(new Intent(getApplicationContext(), About.class));
                break;

            case R.id.logout:

                final AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Logout");
                builder.setIcon(R.drawable.ic_logout);
                builder.setMessage("Do you really want to Logout?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        mAuth.signOut();
//                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;

        }
        if(fragment!=null){
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i("ITEM ID", Integer.toString(item.getItemId()));
        displaySelectedListener(item.getItemId());
        return true;
    }

}