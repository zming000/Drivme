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

public class TouristLogin extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilLoginTouristID, mtilLoginTouristPW;
    TextInputEditText metLoginTouristID, metLoginTouristPW;
    Button mbtnTouristLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_login);

        //obtaining the View with specific ID
        mtilLoginTouristID = findViewById(R.id.tilLoginTouristID);
        mtilLoginTouristPW = findViewById(R.id.tilLoginTouristPW);
        metLoginTouristID = findViewById(R.id.etLoginTouristID);
        metLoginTouristPW = findViewById(R.id.etLoginTouristPW);
        mbtnTouristLogin = findViewById(R.id.btnTouristLogin);

        metLoginTouristID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilLoginTouristID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metLoginTouristPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilLoginTouristPW.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnTouristLogin.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metLoginTouristID.getText()).toString().trim().isEmpty()){
                mtilLoginTouristID.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metLoginTouristPW.getText()).toString().trim().isEmpty()){
                mtilLoginTouristPW.setError("Field cannot be empty!");
            }
        });
    }

    public void googleLogin(View view) {
    }

    //tourist login -> tourist sign up
    public void signupTourist(View view) {
        startActivity(new Intent(TouristLogin.this, TouristSignUp.class));
        finish();
    }

    //tourist login -> role
    @Override
    public void onBackPressed() {
        startActivity(new Intent(TouristLogin.this, Role.class));
        finish();
    }
}