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

public class DriverPhoneNumber extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilDPhoneNumber;
    TextInputEditText metDPhoneNumber;
    Button mbtnDriverOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_phone_number);

        //obtaining the View with specific ID
        mtilDPhoneNumber = findViewById(R.id.tilSignUpDPhoneNumber);
        metDPhoneNumber = findViewById(R.id.etSignUpDPhoneNumber);
        mbtnDriverOTP = findViewById(R.id.btnGetOTP);

        //change error message
        metDPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDPhoneNumber.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnDriverOTP.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to next page
            if(Objects.requireNonNull(metDPhoneNumber.getText()).toString().trim().isEmpty()){
                mtilDPhoneNumber.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metDPhoneNumber.getText()).length() < 9){
                mtilDPhoneNumber.setError("Invalid length of phone number!");
            }
            else{
                //proceed to verify otp
                Intent intent = new Intent(DriverPhoneNumber.this, DriverOTP.class);
                intent.putExtra("dID", getIntent().getStringExtra("dIDNext"));
                intent.putExtra("dFName", getIntent().getStringExtra("dFNameNext"));
                intent.putExtra("dLName", getIntent().getStringExtra("dLNameNext"));
                intent.putExtra("dPhoneNumber", "+60" + metDPhoneNumber.getText().toString());
                intent.putExtra("dEmail", getIntent().getStringExtra("dEmailNext"));
                intent.putExtra("dPassword", getIntent().getStringExtra("dPasswordNext"));
                intent.putExtra("dRefCode", getIntent().getStringExtra("dRefCodeNext"));

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    //driver phone number -> driver login
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverPhoneNumber.this);
        alertDialogBuilder.setTitle("Discard Process");
        alertDialogBuilder
                .setMessage("Do you wish to discard and go back login?")
                .setCancelable(false)
                .setPositiveButton("DISCARD",
                        (dialog, id) -> {
                            startActivity(new Intent(DriverPhoneNumber.this, DriverLogin.class));
                            finish();
                        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}