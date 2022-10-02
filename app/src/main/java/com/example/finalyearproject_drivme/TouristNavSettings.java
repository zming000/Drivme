package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TouristNavSettings extends AppCompatActivity {
    //declare variables
    BottomNavigationView mbtmTNav;
    ConstraintLayout mclickTProfile, mclickTCP, mclickTHelp, mclickTSwitch, mclickTAbout;
    Button mbtnTLogout;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_nav_settings);

        //assign variables
        mclickTProfile = findViewById(R.id.clickTProfile);
        mclickTCP = findViewById(R.id.clickTCP);
        mclickTHelp = findViewById(R.id.clickTHelp);
        mclickTSwitch = findViewById(R.id.clickTSwitch);
        mclickTAbout = findViewById(R.id.clickTAbout);
        mbtnTLogout = findViewById(R.id.btnTLogout);
        mbtmTNav = findViewById(R.id.btmTNav);
        navSelection();

        mclickTProfile.setOnClickListener(view -> {
            //go profile ui
        });

        mclickTCP.setOnClickListener(view -> {
            //go change password ui
        });

        mclickTHelp.setOnClickListener(view -> {
            //go help center ui
        });

        mclickTSwitch.setOnClickListener(view -> {
            //go switch account ui
        });

        mclickTAbout.setOnClickListener(view -> {
            //go about us ui
        });

        mbtnTLogout.setOnClickListener(view -> {
            //logout
            spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
            spDrivme.edit().clear().commit();

            startActivity(new Intent(getApplicationContext(), Role.class));
            finishAffinity();
            finish();
        });
    }

    private void navSelection() {
        //set homepage selected
        mbtmTNav.setSelectedItemId(R.id.settings);

        //perform listener
        mbtmTNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.activity:
                    startActivity(new Intent(getApplicationContext(), TouristNavActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.chat:
                    startActivity(new Intent(getApplicationContext(), TouristNavChat.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), TouristNavHomepage.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.notifications:
                    startActivity(new Intent(getApplicationContext(), TouristNavNotifications.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.settings:
                    return true;
            }
            return false;
        });
    }

    //quit application
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Leaving Drivme?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    finishAffinity();
                    finish();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}