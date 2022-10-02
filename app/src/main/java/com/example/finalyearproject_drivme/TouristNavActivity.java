package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TouristNavActivity extends AppCompatActivity {
    //declare variable
    BottomNavigationView mbtmTNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_nav_activity);

        //assign variable
        mbtmTNav = findViewById(R.id.btmTNav);
        navSelection();
    }

    private void navSelection() {
        //set homepage selected
        mbtmTNav.setSelectedItemId(R.id.activity);

        //perform listener
        mbtmTNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.activity:
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
                    startActivity(new Intent(getApplicationContext(), TouristNavSettings.class));
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