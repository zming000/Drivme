package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DriverLogin extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilLoginDID, mtilLoginDPW;
    TextInputEditText metLoginDID, metLoginDPW;
    Button mbtnDLogin;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_FNAME = "fName";
    private static final String KEY_ID = "userID";
    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        //assign variables
        mtilLoginDID = findViewById(R.id.tilLoginDID);
        mtilLoginDPW = findViewById(R.id.tilLoginDPW);
        metLoginDID = findViewById(R.id.etLoginDID);
        metLoginDPW = findViewById(R.id.etLoginDPW);
        mbtnDLogin = findViewById(R.id.btnDLogin);

        metLoginDID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilLoginDID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metLoginDPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilLoginDPW.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnDLogin.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metLoginDID.getText()).toString().trim().isEmpty()){
                mtilLoginDID.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metLoginDPW.getText()).toString().trim().isEmpty()){
                mtilLoginDPW.setError("Field cannot be empty!");
            }
            else{
                //return instance of the class
                FirebaseFirestore userDB = FirebaseFirestore.getInstance();
                String dID = metLoginDID.getText().toString();
                String dPW = metLoginDPW.getText().toString();

                userDB.collection("User Accounts")
                        .document(dID)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot docResult = task.getResult();

                                if (docResult != null) {
                                    //check the existence of ID
                                    if (docResult.exists()) {
                                            String docPW = docResult.getString("password");
                                            //check if password matched
                                            if (dPW.matches(Objects.requireNonNull(docPW))) {
                                                String accStatus = docResult.getString("accountStatus");

                                                if(!accStatus.equals("Suspended")) {
                                                    int sem = Objects.requireNonNull(docResult.getLong("Account Driver")).intValue();

                                                    //check if id activated driver role or not
                                                    if (sem == 1) {
                                                        int loginStat = Objects.requireNonNull(docResult.getLong("Login Status Driver")).intValue();
                                                        String name = Objects.requireNonNull(docResult.getString("firstName"));

                                                        if (loginStat == 0) {
                                                            startActivity(new Intent(DriverLogin.this, UserWelcomeTo.class));
                                                        } else {
                                                            startActivity(new Intent(DriverLogin.this, UserWelcomeBack.class));
                                                        }

                                                        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
                                                        SharedPreferences.Editor spEditor = spDrivme.edit();
                                                        spEditor.putString(KEY_FNAME, name);
                                                        spEditor.putString(KEY_ID, dID);
                                                        spEditor.putString(KEY_ROLE, "Driver");
                                                        spEditor.apply();

                                                        FirebaseMessaging.getInstance().getToken()
                                                                .addOnCompleteListener(task1 -> {
                                                                    if (!task1.isSuccessful()) {
                                                                        return;
                                                                    }

                                                                    // Get new FCM registration token
                                                                    String token = task1.getResult();
                                                                    FirebaseFirestore updateToken = FirebaseFirestore.getInstance();

                                                                    Map<String, Object> noToken = new HashMap<>();
                                                                    noToken.put("notificationToken", token);
                                                                    noToken.put("accountStatus", "Driver");

                                                                    updateToken.collection("User Accounts").document(dID)
                                                                            .update(noToken);
                                                                });

                                                        finish();
                                                    } else {
                                                        Toast.makeText(DriverLogin.this, "Driver Role haven't activated!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                else{
                                                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                                                    alertDialogBuilder.setTitle("Account Suspended");
                                                    alertDialogBuilder
                                                            .setMessage("Your account have been suspended!\nPlease check your email or contact Drivme support for further information.")
                                                            .setCancelable(false)
                                                            .setPositiveButton("OK", (dialog, id) -> {
                                                                dialog.cancel();
                                                            });

                                                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                                    alertDialog.show();
                                                }
                                            }
                                            else {
                                                Toast.makeText(DriverLogin.this, "Wrong ID or Password!", Toast.LENGTH_SHORT).show();
                                            }
                                    }
                                    else{
                                        mtilLoginDID.setError("ID does not exist!");
                                    }
                                }
                            }
                        });
            }
        });
    }

    //driver login -> driver sign up
    public void signupDriver(View view) {
        Intent intent = new Intent(DriverLogin.this, UserAgreementPolicy.class);
        intent.putExtra("role", "Driver");
        startActivity(intent);
        finish();
    }

    //driver login -> driver option
    @Override
    public void onBackPressed() {
        startActivity(new Intent(DriverLogin.this, DriverOption.class));
        finish();
    }

    //driver login -> forgot password
    public void driverForgot(View view) {
        Intent intent = new Intent(DriverLogin.this, UserForgotPassword.class);
        intent.putExtra("role", "Driver");

        startActivity(intent);
        finish();
    }
}