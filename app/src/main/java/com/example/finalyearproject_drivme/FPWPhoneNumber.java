package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class FPWPhoneNumber extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilFPWPhoneNumber;
    TextInputEditText metFPWPhoneNumber;
    Button mbtnGetOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpw_phone_number);

        //obtaining the View with specific ID
        mtilFPWPhoneNumber = findViewById(R.id.tilFPWPhoneNumber);
        metFPWPhoneNumber = findViewById(R.id.etFPWPhoneNumber);
        mbtnGetOTP = findViewById(R.id.btnGetOTP);

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
            //check condition (fields not empty) before proceed to next page
            if(Objects.requireNonNull(metFPWPhoneNumber.getText()).toString().trim().isEmpty()){
                mtilFPWPhoneNumber.setError("Field cannot be empty!");
            } else{
                //proceed to verify otp
                Intent intent = new Intent(FPWPhoneNumber.this, FPWOtp.class);
                intent.putExtra("id", getIntent().getStringExtra("roleID"));
                intent.putExtra("character", getIntent().getStringExtra("roleCharacter"));
                intent.putExtra("phNum", metFPWPhoneNumber.getText().toString());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}