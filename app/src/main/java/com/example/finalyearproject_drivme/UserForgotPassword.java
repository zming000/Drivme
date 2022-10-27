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

import java.util.Objects;

public class UserForgotPassword extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilFpwID, mtilFPWPhoneNumber;
    TextInputEditText metFpwID, metFPWPhoneNumber;
    Button mbtnGetOTP;
    String character;
    FirebaseFirestore userDB;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";
    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forgot_password);

        //assign variables
        mtilFpwID = findViewById(R.id.tilFpwID);
        mtilFPWPhoneNumber = findViewById(R.id.tilFPWPhoneNumber);
        metFpwID = findViewById(R.id.etFpwID);
        metFPWPhoneNumber = findViewById(R.id.etFPWPhoneNumber);
        mbtnGetOTP = findViewById(R.id.btnGetOTP);

        //initialize
        userDB = FirebaseFirestore.getInstance();
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        character = getIntent().getStringExtra("role");

        String uID = spDrivme.getString(KEY_ID, null);

        if(uID != null){
            metFpwID.setText(uID);
            metFpwID.setFocusable(false);
        }

        metFpwID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilFpwID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        //change error message
        metFPWPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilFPWPhoneNumber.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnGetOTP.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metFpwID.getText()).toString().trim().isEmpty()){
                mtilFpwID.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metFPWPhoneNumber.getText()).toString().trim().isEmpty()){
                mtilFPWPhoneNumber.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metFPWPhoneNumber.getText()).length() < 9){
                mtilFPWPhoneNumber.setError("Invalid length of phone number!");
            }
            else{
                String id = Objects.requireNonNull(metFpwID.getText()).toString();

                userDB.collection("User Accounts").document(id).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document != null) {
                                    //check the existence of document/tourist ID
                                    if (document.exists()) {
                                        if(character.equals("Tourist")) {
                                            int semTourist = Objects.requireNonNull(document.getLong("accountTourist")).intValue();

                                            if (semTourist == 1) {
                                                String phText = document.getString("phoneNumber");

                                                if(Objects.requireNonNull(phText).equals("+60" + metFPWPhoneNumber.getText().toString())) {
                                                    //proceed to verify otp
                                                    Intent intent = new Intent(UserForgotPassword.this, UserFPWOtp.class);
                                                    intent.putExtra("id", id);
                                                    intent.putExtra("character", character);
                                                    intent.putExtra("phNum", "+60" + metFPWPhoneNumber.getText().toString());

                                                    startActivity(intent);
                                                }
                                                else{
                                                    mtilFPWPhoneNumber.setError("Use Verified Phone Number!");
                                                }
                                            } else {
                                                Toast.makeText(UserForgotPassword.this, "Tourist Role haven't activated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            int semDriver = Objects.requireNonNull(document.getLong("accountDriver")).intValue();

                                            if(semDriver == 1) {
                                                String phText = document.getString("phoneNumber");

                                                if(Objects.requireNonNull(phText).equals("+60" + metFPWPhoneNumber.getText().toString())) {
                                                    //proceed to verify otp
                                                    Intent intent = new Intent(UserForgotPassword.this, UserFPWOtp.class);
                                                    intent.putExtra("id", id);
                                                    intent.putExtra("character", character);
                                                    intent.putExtra("phNum", "+60" + metFPWPhoneNumber.getText().toString());

                                                    startActivity(intent);
                                                }
                                                else{
                                                    mtilFPWPhoneNumber.setError("Use Verified Phone Number!");
                                                }
                                            }
                                            else{
                                                Toast.makeText(UserForgotPassword.this, "Driver Role haven't activated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    else {
                                        mtilFpwID.setError("ID does not exist!");
                                    }
                                }
                            }
                        });
            }
        });
    }

    //forgot password -> login (tourist/driver)
    public void backToLogin(View view) {
        if(character.equals("Tourist")) {
            startActivity(new Intent(UserForgotPassword.this, TouristLogin.class));
        }
        else{
            startActivity(new Intent(UserForgotPassword.this, DriverLogin.class));
        }
        finishAffinity();
        finish();
    }

    //forgot password -> login (tourist/driver)
    @Override
    public void onBackPressed() {
        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);
        String uRole = spDrivme.getString(KEY_ROLE, null);

        if(uID != null) {
            if (uRole.equals("Tourist")) {
                startActivity(new Intent(UserForgotPassword.this, TouristChangePW.class));
            }
            else{
                startActivity(new Intent(UserForgotPassword.this, DriverChangePW.class));
            }
        }
        else {
            if (character.equals("Tourist")) {
                startActivity(new Intent(UserForgotPassword.this, TouristLogin.class));
            }
            else {
                startActivity(new Intent(UserForgotPassword.this, DriverLogin.class));
            }
        }
        finishAffinity();
        finish();
    }
}