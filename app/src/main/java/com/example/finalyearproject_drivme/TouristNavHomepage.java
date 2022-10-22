package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalyearproject_drivme.TouristActivityFragments.TouristOngoingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class TouristNavHomepage extends AppCompatActivity {
    //declare variables
    TextView mtvTMoney;
    FloatingActionButton mfabTReload;
    BottomNavigationView mbtmTNav;
    CardView mcvBooking;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_nav_homepage);

        //assign variables
        mfabTReload = findViewById(R.id.fabTReload);
        mcvBooking = findViewById(R.id.cvBooking);
        mbtmTNav = findViewById(R.id.btmTNav);
        mtvTMoney = findViewById(R.id.tvTMoney);

        //go to reload ui
        mfabTReload.setOnClickListener(view -> {
            startActivity(new Intent(TouristNavHomepage.this, TouristReload.class));
            finishAffinity();
            finish();
        });

        getDrivPay();
        navSelection();
        tripSelection();
    }

    //set drivpay
    private void getDrivPay(){
        FirebaseFirestore getAmount = FirebaseFirestore.getInstance();
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        String id = spDrivme.getString(KEY_ID, null);

        getAmount.collection("User Accounts").document(id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();

                        mtvTMoney.setText("RM " + doc.getString("drivPay"));
                    }
                });
    }

    //choose trip option
    private void tripSelection() {
        mcvBooking.setOnClickListener(tripView -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            tripView = dialogInflater.inflate(R.layout.activity_tourist_trip_option, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog tripOptDialog = dialogBuilder.setView(tripView).create();

            tripOptDialog.show();

            //assign variables
            ImageView mivTripClose = tripView.findViewById(R.id.ivTripClose);
            Button mbtnHour = tripView.findViewById(R.id.btnHour);
            Button mbtnDay = tripView.findViewById(R.id.btnDay);

            mivTripClose.setOnClickListener(view -> tripOptDialog.dismiss());

            mbtnHour.setOnClickListener(view -> {
                Intent hourIntent = new Intent(getApplicationContext(), TouristHourTrip.class);
                hourIntent.putExtra("tripOpt", "Hour");
                startActivity(hourIntent);
                finishAffinity();
                finish();
            });

            mbtnDay.setOnClickListener(view -> {
                Intent dayIntent = new Intent(getApplicationContext(), TouristDayTrip.class);
                dayIntent.putExtra("tripOpt", "Day");
                startActivity(dayIntent);
                finishAffinity();
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
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, new TouristOngoingFragment()).commit();
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.cars:
                    startActivity(new Intent(getApplicationContext(), TouristNavCars.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.home:
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