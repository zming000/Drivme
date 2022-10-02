package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DriverNavNotifications extends AppCompatActivity {
    //declare variable
    BottomNavigationView mbtmDNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_nav_notifications);

        //assign variable
        mbtmDNav = findViewById(R.id.btmDNav);
        navSelection();
    }

    private void navSelection() {
        //set homepage selected
        mbtmDNav.setSelectedItemId(R.id.notifications);

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
                    return true;
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), DriverNavSettings.class));
                    overridePendingTransition(0, 0);
                    finish();
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