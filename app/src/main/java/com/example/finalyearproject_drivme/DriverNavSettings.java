package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DriverNavSettings extends AppCompatActivity {
    //declare variables
    BottomNavigationView mbtmDNav;
    ConstraintLayout mclickDProfile, mclickDCP, mclickDSwitch, mclickDAbout;
    Button mbtnDLogout;
    SharedPreferences spDrivme;
    FirebaseFirestore checkTourist, updateAcc;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";
    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_nav_settings);

        //assign variable
        mclickDProfile = findViewById(R.id.clickDProfile);
        mclickDCP = findViewById(R.id.clickDCP);
        mclickDSwitch = findViewById(R.id.clickDSwitch);
        mclickDAbout = findViewById(R.id.clickDAbout);
        mbtnDLogout = findViewById(R.id.btnDLogout);
        mbtmDNav = findViewById(R.id.btmDNav);
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        navSelection();

        mclickDProfile.setOnClickListener(view -> {
            //go profile ui
            startActivity(new Intent(DriverNavSettings.this, DriverProfile.class));
            finish();
        });

        mclickDCP.setOnClickListener(view -> {
            //go change password ui
            startActivity(new Intent(DriverNavSettings.this, DriverChangePW.class));
            finish();
        });

        mclickDSwitch.setOnClickListener(view -> {
            //go switch account ui
            String uID = spDrivme.getString(KEY_ID, null);
            checkTourist = FirebaseFirestore.getInstance();
            updateAcc = FirebaseFirestore.getInstance();

            checkTourist.collection("User Accounts").document(uID).get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            String accState = doc.getString("accountStatus");

                            if(Objects.requireNonNull(accState).equals("Suspended")){
                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                                alertDialogBuilder.setTitle("Account Suspended");
                                alertDialogBuilder
                                        .setMessage("Your account have been suspended!\nPlease check your email or contact Drivme support for further information.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (dialog, iD) -> {
                                            spDrivme.edit().clear().apply();

                                            startActivity(new Intent(getApplicationContext(), UserRole.class));
                                            dialog.dismiss();
                                            finishAffinity();
                                            finish();
                                        });

                                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                            else {
                                int touristAcc = Objects.requireNonNull(doc.getLong("Account Tourist")).intValue();

                                if (touristAcc == 0) {
                                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                                    alertDialogBuilder.setTitle("Activate Tourist Account");
                                    alertDialogBuilder
                                            .setMessage("Do you wish to activate Tourist Account?")
                                            .setCancelable(false)
                                            .setPositiveButton("Activate", (dialog, id) -> {
                                                SharedPreferences.Editor spEditor = spDrivme.edit();
                                                spEditor.putString(KEY_ROLE, "Tourist");
                                                spEditor.apply();

                                                Map<String, Object> acc = new HashMap<>();
                                                acc.put("accountStatus", "Tourist");
                                                acc.put("Account Tourist", 1);

                                                updateAcc.collection("User Accounts").document(uID)
                                                        .update(acc);

                                                startActivity(new Intent(DriverNavSettings.this, TouristInputCar.class));
                                                finishAffinity();
                                                finish();
                                            })
                                            .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                } else {
                                    SharedPreferences.Editor spEditor = spDrivme.edit();
                                    spEditor.putString(KEY_ROLE, "Tourist");
                                    spEditor.apply();

                                    Map<String, Object> acc = new HashMap<>();
                                    acc.put("accountStatus", "Tourist");

                                    updateAcc.collection("User Accounts").document(uID)
                                            .update(acc);

                                    startActivity(new Intent(DriverNavSettings.this, TouristNavHomepage.class));
                                    finishAffinity();
                                    finish();
                                }
                            }
                        }
                    });
        });

        mclickDAbout.setOnClickListener(view -> {
            //go about us ui
            startActivity(new Intent(DriverNavSettings.this, DriverAboutUs.class));
            finish();
        });

        mbtnDLogout.setOnClickListener(view -> {
            //logout
            String id = spDrivme.getString(KEY_ID, null);
            spDrivme.edit().clear().apply();

            FirebaseFirestore updateStatus = FirebaseFirestore.getInstance();
            Map<String,Object> noToken = new HashMap<>();
            noToken.put("accountStatus", "Offline");
            noToken.put("notificationToken", "-");

            updateStatus.collection("User Accounts").document(id)
                    .update(noToken);

            startActivity(new Intent(getApplicationContext(), UserRole.class));
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
                case R.id.rating:
                    startActivity(new Intent(getApplicationContext(), DriverNavRating.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), DriverNavHomepage.class));
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