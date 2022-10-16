package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class TouristNavHomepage extends AppCompatActivity {
    //declare variables
    TextView mtvTMoney;
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
        mcvBooking = findViewById(R.id.cvBooking);
        mbtmTNav = findViewById(R.id.btmTNav);
        mtvTMoney = findViewById(R.id.tvTMoney);

        getDrivPay();
        navSelection();
        tripSelection();
    }

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

    private void tripSelection() {
        mcvBooking.setOnClickListener(tripView -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            tripView = dialogInflater.inflate(R.layout.activity_tourist_trip_option, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog tripOptDialog = dialogBuilder.setView(tripView).create();

            tripOptDialog.show();

            ImageView mivTripClose = tripView.findViewById(R.id.ivTripClose);
            Button mbtnSlot = tripView.findViewById(R.id.btnSlot);
            Button mbtnDay = tripView.findViewById(R.id.btnDay);

            mivTripClose.setOnClickListener(view -> tripOptDialog.dismiss());

            mbtnSlot.setOnClickListener(view -> {
                Intent slotIntent = new Intent(getApplicationContext(), TouristSlotTrip.class);
                slotIntent.putExtra("tripOpt", "Slot");
                startActivity(slotIntent);
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