package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TouristNavSettings extends AppCompatActivity {
    //declare variables
    BottomNavigationView mbtmTNav;
    ConstraintLayout mclickTProfile, mclickTCP, mclickTSwitch, mclickTAbout;
    Button mbtnTLogout;
    SharedPreferences spDrivme;
    FirebaseFirestore checkDriver, rcDB, updateRC, updateAcc, getName;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";
    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_nav_settings);

        //assign variables
        mclickTProfile = findViewById(R.id.clickTProfile);
        mclickTCP = findViewById(R.id.clickTCP);
        mclickTSwitch = findViewById(R.id.clickTSwitch);
        mclickTAbout = findViewById(R.id.clickTAbout);
        mbtnTLogout = findViewById(R.id.btnTLogout);
        mbtmTNav = findViewById(R.id.btmTNav);
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        navSelection();

        mclickTProfile.setOnClickListener(view -> {
            //go profile ui
            startActivity(new Intent(TouristNavSettings.this, TouristProfile.class));
            finish();
        });

        mclickTCP.setOnClickListener(view -> {
            //go change password ui
            startActivity(new Intent(TouristNavSettings.this, TouristChangePW.class));
            finish();
        });

        mclickTSwitch.setOnClickListener(view -> {
            //go switch account ui
            String uID = spDrivme.getString(KEY_ID, null);
            checkDriver = FirebaseFirestore.getInstance();
            rcDB = FirebaseFirestore.getInstance();
            updateRC = FirebaseFirestore.getInstance();
            updateAcc = FirebaseFirestore.getInstance();
            getName = FirebaseFirestore.getInstance();

            checkDriver.collection("User Accounts").document(uID).get()
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
                                int driverAcc = Objects.requireNonNull(doc.getLong("accountDriver")).intValue();

                                if (driverAcc == 0) {
                                    //initialize layout
                                    LayoutInflater dialogInflater = getLayoutInflater();
                                    View switchView = dialogInflater.inflate(R.layout.activity_reference_code, null);

                                    //initialize dialog builder
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
                                    AlertDialog rcDialog = dialogBuilder.setView(switchView).create();

                                    //assign variables
                                    TextInputLayout mtilRC = switchView.findViewById(R.id.tilRC);
                                    TextInputEditText metRC = switchView.findViewById(R.id.etRC);
                                    Button mbtnDriver = switchView.findViewById(R.id.btnDriver);

                                    metRC.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                            //Nothing
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            mtilRC.setErrorEnabled(false);
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            //Nothing
                                        }
                                    });

                                    mbtnDriver.setOnClickListener(view12 -> {
                                        String refCode = Objects.requireNonNull(metRC.getText()).toString();

                                        if (!refCode.isEmpty()) {
                                            rcDB.collection("Reference Code Details").document(refCode).get()
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()) {
                                                            DocumentSnapshot document2 = task2.getResult();
                                                            String codeStatus = Objects.requireNonNull(document2).getString("status");
                                                            //check the existence of reference code
                                                            if (!document2.exists()) {
                                                                mtilRC.setError("Invalid Reference Code!");
                                                            } else if (Objects.requireNonNull(codeStatus).matches("N/A")) { //check if the reference code available
                                                                mtilRC.setError("Reference Code Not Available!");
                                                            } else {
                                                                SharedPreferences.Editor spEditor = spDrivme.edit();
                                                                spEditor.putString(KEY_ROLE, "Driver");
                                                                spEditor.apply();

                                                                getName.collection("User Accounts").document(uID).get()
                                                                        .addOnCompleteListener(task1 -> {
                                                                            if (task1.isSuccessful()) {
                                                                                DocumentSnapshot name = task1.getResult();

                                                                                Map<String, Object> rc = new HashMap<>();
                                                                                rc.put("refCode", refCode);
                                                                                rc.put("driverID", uID);
                                                                                rc.put("status", "N/A");
                                                                                rc.put("driverName", name.getString("lastName") + " " + name.getString("firstName"));

                                                                                Map<String, Object> acc = new HashMap<>();
                                                                                acc.put("accountStatus", "Driver");
                                                                                acc.put("accountDriver", 1);
                                                                                acc.put("rating", 5);
                                                                                acc.put("5 Stars", 1);
                                                                                acc.put("4 Stars", 0);
                                                                                acc.put("3 Stars", 0);
                                                                                acc.put("2 Stars", 0);
                                                                                acc.put("1 Star", 0);
                                                                                acc.put("priceDay", 300);
                                                                                acc.put("priceHour", 15);

                                                                                updateRC.collection("Reference Code Details").document(refCode)
                                                                                        .update(rc);

                                                                                updateAcc.collection("User Accounts").document(uID)
                                                                                        .update(acc);
                                                                            }
                                                                        });

                                                                startActivity(new Intent(TouristNavSettings.this, DriverDrivingDetails.class));
                                                                finishAffinity();
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            mtilRC.setError("Please input reference code!");
                                        }
                                    });

                                    //display dialog with suitable size
                                    rcDialog.show();
                                    rcDialog.getWindow().setLayout(650, 550);
                                } else {
                                    SharedPreferences.Editor spEditor = spDrivme.edit();
                                    spEditor.putString(KEY_ROLE, "Driver");
                                    spEditor.apply();

                                    Map<String, Object> acc = new HashMap<>();
                                    acc.put("accountStatus", "Driver");

                                    updateAcc.collection("User Accounts").document(uID)
                                            .update(acc);

                                    startActivity(new Intent(TouristNavSettings.this, DriverNavHomepage.class));
                                    finishAffinity();
                                    finish();
                                }
                            }
                        }
                    });
        });

        mclickTAbout.setOnClickListener(view -> {
            //go about us ui
            startActivity(new Intent(TouristNavSettings.this, TouristAboutUs.class));
            finish();
        });

        mbtnTLogout.setOnClickListener(view -> {
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
        mbtmTNav.setSelectedItemId(R.id.settings);

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
                    startActivity(new Intent(getApplicationContext(), TouristNavHomepage.class));
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