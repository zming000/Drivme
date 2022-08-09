package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class DriverLogin extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilLoginDriverID, mtilLoginDriverPW;
    TextInputEditText metLoginDriverID, metLoginDriverPW;
    Button mbtnDriverLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        //obtaining the View with specific ID
        mtilLoginDriverID = findViewById(R.id.tilLoginDriverID);
        mtilLoginDriverPW = findViewById(R.id.tilLoginDriverPW);
        metLoginDriverID = findViewById(R.id.etLoginDriverID);
        metLoginDriverPW = findViewById(R.id.etLoginDriverPW);
        mbtnDriverLogin = findViewById(R.id.btnDriverLogin);

        metLoginDriverID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilLoginDriverID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metLoginDriverPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilLoginDriverPW.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnDriverLogin.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metLoginDriverID.getText()).toString().trim().isEmpty()){
                mtilLoginDriverID.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metLoginDriverPW.getText()).toString().trim().isEmpty()){
                mtilLoginDriverPW.setError("Field cannot be empty!");
            }
            else{
                //database
            }
        });
    }

    //driver login -> driver sign up
    public void signupDriver(View view) {
        startActivity(new Intent(DriverLogin.this, DriverSignUp.class));
        finish();
    }

    //driver login -> driver option
    @Override
    public void onBackPressed() {
        startActivity(new Intent(DriverLogin.this, DriverOption.class));
        finish();
    }
}