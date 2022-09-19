package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class ForgotPassword extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilFpwID, mtilFPWPhoneNumber;
    TextInputEditText metFpwID, metFPWPhoneNumber;
    Button mbtnGetOTP;
    String character;
    FirebaseFirestore userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //obtaining the View with specific ID
        mtilFpwID = findViewById(R.id.tilFpwID);
        mtilFPWPhoneNumber = findViewById(R.id.tilFPWPhoneNumber);
        metFpwID = findViewById(R.id.etFpwID);
        metFPWPhoneNumber = findViewById(R.id.etFPWPhoneNumber);
        mbtnGetOTP = findViewById(R.id.btnGetOTP);

        //return instance of the class
        userDB = FirebaseFirestore.getInstance();
        character = getIntent().getStringExtra("role");

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

                userDB.collection("User Accounts")
                        .document(id)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document != null) {
                                    //check the existence of document/tourist ID
                                    if (document.exists()) {
                                        if(character.equals("Tourist")) {
                                            int semTourist = Objects.requireNonNull(document.getLong("Account Tourist")).intValue();

                                            if (semTourist == 1) {
                                                String phText = document.getString("Phone Number");

                                                if(Objects.requireNonNull(phText).equals("+60" + metFPWPhoneNumber.getText().toString())) {
                                                    //proceed to verify otp
                                                    Intent intent = new Intent(ForgotPassword.this, FPWOtp.class);
                                                    intent.putExtra("id", id);
                                                    intent.putExtra("character", character);
                                                    intent.putExtra("phNum", "+60" + metFPWPhoneNumber.getText().toString());

                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                                else{
                                                    mtilFPWPhoneNumber.setError("Use Verified Phone Number!");
                                                }
                                            } else {
                                                Toast.makeText(ForgotPassword.this, "Tourist Role haven't activated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            int semDriver = Objects.requireNonNull(document.getLong("Account Driver")).intValue();

                                            if(semDriver == 1) {
                                                //proceed to verify otp
                                                Intent intent = new Intent(ForgotPassword.this, FPWOtp.class);
                                                intent.putExtra("id", id);
                                                intent.putExtra("character", character);
                                                intent.putExtra("phNum", "+60" + metFPWPhoneNumber.getText().toString());

                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(ForgotPassword.this, "Driver Role haven't activated!", Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(ForgotPassword.this, TouristLogin.class));
        }
        else{
            startActivity(new Intent(ForgotPassword.this, DriverLogin.class));
        }
        finish();
    }

    //forgot password -> login (tourist/driver)
    @Override
    public void onBackPressed() {
        if(character.equals("Tourist")) {
            startActivity(new Intent(ForgotPassword.this, TouristLogin.class));
        }
        else{
            startActivity(new Intent(ForgotPassword.this, DriverLogin.class));
        }
        finish();
    }
}