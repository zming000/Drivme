package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;

import java.util.Objects;

public class DriverNavHomepage extends AppCompatActivity {
    //declare variable
    TextView mtvDMoney, mtvFullName, mtvHomeRating;
    RatingBar mrbHomeDriver;
    RatingReviews mrrHomeDriver;
    CardView mcvDrivPay;
    BottomNavigationView mbtmDNav;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_nav_homepage);

        //assign variable
        mbtmDNav = findViewById(R.id.btmDNav);
        mtvDMoney = findViewById(R.id.tvDMoney);
        mtvFullName = findViewById(R.id.tvFullName);
        mcvDrivPay = findViewById(R.id.cvDrivPay);
        mtvHomeRating = findViewById(R.id.tvHomeRating);
        mrbHomeDriver = findViewById(R.id.rbHomeDriver);
        mrrHomeDriver = findViewById(R.id.rrHomeDriver);

        mcvDrivPay.setOnClickListener(view -> {
            startActivity(new Intent(DriverNavHomepage.this, UserTransactionHistory.class));
        });

        setDetails();
        navSelection();
    }

    private void setDetails(){
        FirebaseFirestore getAmount = FirebaseFirestore.getInstance();
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        String id = spDrivme.getString(KEY_ID, null);

        getAmount.collection("User Accounts").document(id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();

                        mtvDMoney.setText("RM " + doc.getString("drivPay"));
                        mtvFullName.setText(doc.getString("lastName") + " " + doc.getString("firstName"));
                        mtvHomeRating.setText(String.valueOf(doc.getDouble("rating")));
                        mrbHomeDriver.setRating(Float.parseFloat(String.valueOf(doc.getDouble("rating"))));

                        //rating and reviews
                        Pair[] colors = new Pair[]{
                                new Pair<>(Color.parseColor("#0F7D63"), Color.parseColor("#9FD5B5")),
                                new Pair<>(Color.parseColor("#0F7D63"), Color.parseColor("#9FD5B5")),
                                new Pair<>(Color.parseColor("#0F7D63"), Color.parseColor("#9FD5B5")),
                                new Pair<>(Color.parseColor("#0F7D63"), Color.parseColor("#9FD5B5")),
                                new Pair<>(Color.parseColor("#0F7D63"), Color.parseColor("#9FD5B5"))
                        };

                        int[] raters = new int[]{
                                Objects.requireNonNull(doc.getLong("5 stars")).intValue(),
                                Objects.requireNonNull(doc.getLong("4 stars")).intValue(),
                                Objects.requireNonNull(doc.getLong("3 stars")).intValue(),
                                Objects.requireNonNull(doc.getLong("2 stars")).intValue(),
                                Objects.requireNonNull(doc.getLong("1 star")).intValue()
                        };

                        mrrHomeDriver.createRatingBars(100, BarLabels.STYPE3, colors, raters);
                    }
                });
    }

    private void navSelection() {
        //set homepage selected
        mbtmDNav.setSelectedItemId(R.id.home);

        //perform listener
        mbtmDNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.activity:
                    startActivity(new Intent(getApplicationContext(), DriverNavActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.rating:
                    startActivity(new Intent(getApplicationContext(), DriverNavRating.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.home:
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