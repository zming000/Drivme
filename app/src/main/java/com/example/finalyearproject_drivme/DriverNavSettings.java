package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DriverNavSettings extends AppCompatActivity {
    //declare variables
    BottomNavigationView mbtmDNav;
    ConstraintLayout mclickDProfile, mclickDCP, mclickDHelp, mclickDSwitch, mclickDAbout;
    Button mbtnDLogout;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_nav_settings);

        //assign variable
        mclickDProfile = findViewById(R.id.clickDProfile);
        mclickDCP = findViewById(R.id.clickDCP);
        mclickDHelp = findViewById(R.id.clickDHelp);
        mclickDSwitch = findViewById(R.id.clickDSwitch);
        mclickDAbout = findViewById(R.id.clickDAbout);
        mbtnDLogout = findViewById(R.id.btnDLogout);
        mbtmDNav = findViewById(R.id.btmDNav);
        navSelection();

        mclickDProfile.setOnClickListener(view -> {
            //go profile ui
        });

        mclickDCP.setOnClickListener(view -> {
            //go change password ui
        });

        mclickDHelp.setOnClickListener(view -> {
            //go help center ui
        });

        mclickDSwitch.setOnClickListener(view -> {
            //go switch account ui
        });

        mclickDAbout.setOnClickListener(view -> {
            //go about us ui
        });

        mbtnDLogout.setOnClickListener(view -> {
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
        mbtmDNav.setSelectedItemId(R.id.settings);

        //perform listener
        mbtmDNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.activity:
                    startActivity(new Intent(getApplicationContext(), DriverNavActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.chat:
                    startActivity(new Intent(getApplicationContext(), DriverNavChat.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), DriverNavHomepage.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.notifications:
                    startActivity(new Intent(getApplicationContext(), DriverNavNotifications.class));
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