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

public class TouristPhoneNumber extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilTPhoneNumber;
    TextInputEditText metTPhoneNumber;
    Button mbtnTouristOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_phone_number);

        //obtaining the View with specific ID
        mtilTPhoneNumber = findViewById(R.id.tilSignUpTouristPhoneNumber);
        metTPhoneNumber = findViewById(R.id.etSignUpTouristPhoneNumber);
        mbtnTouristOTP = findViewById(R.id.btnTouristOTP);

        //change error message
        metTPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilTPhoneNumber.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnTouristOTP.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to next page
            if(Objects.requireNonNull(metTPhoneNumber.getText()).toString().trim().isEmpty()){
                mtilTPhoneNumber.setError("Field cannot be empty!");
            }
            else{
                //proceed to verify otp
                Intent intent = new Intent(TouristPhoneNumber.this, TouristOTP.class);
                intent.putExtra("tID", getIntent().getStringExtra("tIDNext"));
                intent.putExtra("tFName", getIntent().getStringExtra("tFNameNext"));
                intent.putExtra("tLName", getIntent().getStringExtra("tLNameNext"));
                intent.putExtra("tPhoneNumber", "+60" + metTPhoneNumber.getText().toString());
                intent.putExtra("tEmail", getIntent().getStringExtra("tEmailNext"));
                intent.putExtra("tPassword", getIntent().getStringExtra("tPasswordNext"));

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}