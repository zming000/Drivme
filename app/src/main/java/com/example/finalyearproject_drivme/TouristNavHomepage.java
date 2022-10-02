package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TouristNavHomepage extends AppCompatActivity {
    //declare variables
    BottomNavigationView mbtmTNav;
    CardView mcvBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_nav_homepage);

        //assign variables
        mcvBooking = findViewById(R.id.cvBooking);
        mbtmTNav = findViewById(R.id.btmTNav);
        navSelection();
        tripSelection();

    }

    private void tripSelection() {
        mcvBooking.setOnClickListener(tripView -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            tripView = dialogInflater.inflate(R.layout.activity_tourist_trip_option, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog tripOptDialog = dialogBuilder.setView(tripView).create();

            tripOptDialog.show();

            ImageView mivTripClose = tripView.findViewById(R.id.ivTripClose);
            Button mbtnHour = tripView.findViewById(R.id.btnHour);
            Button mbtnDay = tripView.findViewById(R.id.btnDay);

            mivTripClose.setOnClickListener(view -> tripOptDialog.dismiss());

            mbtnHour.setOnClickListener(view -> {
                startActivity(new Intent(getApplicationContext(), TouristHourTrip.class));
                finish();
            });

            mbtnDay.setOnClickListener(view -> {
                startActivity(new Intent(getApplicationContext(), TouristDayTrip.class));
                finish();
            });
        });
    }

    private void navSelection() {
        //set homepage selected
        mbtmTNav.setSelectedItemId(R.id.home);

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