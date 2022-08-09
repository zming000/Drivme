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
import com.google.firebase.firestore.FirebaseFirestore;

public class TouristSignUp extends AppCompatActivity {
    //declare variable
    TextInputLayout mtilTouristID, mtilTFName, mtilTLName, mtilTEmail, mtilTPassword, mtilTCPassword;
    TextInputEditText mTouristID, mTFName, mTLName, mTEmail, mTPw, mTConfirmPw;
    Button mTouristSignUp;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_sign_up);

        //obtaining the View with specific ID
        mtilTouristID = findViewById(R.id.tilSignUpTouristID);
        mtilTFName = findViewById(R.id.tilSignUpTouristFName);
        mtilTLName = findViewById(R.id.tilSignUpTouristLName);
        mtilTEmail = findViewById(R.id.tilSignUpTouristEmail);
        mtilTPassword = findViewById(R.id.tilSignUpTouristPassword);
        mtilTCPassword = findViewById(R.id.tilSignUpTouristCPassword);
        mTouristID = findViewById(R.id.etSignUpTouristID);
        mTFName = findViewById(R.id.etSignUpTouristFName);
        mTLName = findViewById(R.id.etSignUpTouristLName);
        mTEmail = findViewById(R.id.etSignUpTouristEmail);
        mTPw = findViewById(R.id.etSignUpTouristPassword);
        mTConfirmPw = findViewById(R.id.etSignUpTouristCPassword);
        mTouristSignUp = findViewById(R.id.btnTouristSignUp);

        //Validations on each field to ensure correct input
        mTouristID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, with letters, with digit, no uppercase)
                if(mTouristID.getText().toString().contains(" ")){
                    mtilTouristID.setError("ID cannot contain spaces!");
                }
                else if(!mTouristID.getText().toString().matches(".*[a-zA-Z]+.*")){
                    mtilTouristID.setError("Please contain letter(s) in ID!");
                }
                else if(!digitExist(mTouristID.getText().toString())){
                    mtilTouristID.setError("Please contain number(s) in ID!");
                }
                else if(uppercaseExist(mTouristID.getText().toString())){
                    mtilTouristID.setError("ID cannot contain uppercase!");
                }
                else{
                    mtilTouristID.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mTFName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (without digit)
                if(digitExist(mTFName.getText().toString())){
                    mtilTFName.setError("First Name cannot contain number(s)!");
                }
                else{
                    mtilTFName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mTLName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (without digit)
                if(digitExist(mTLName.getText().toString().trim())){
                    mtilTLName.setError("Last Name cannot contain number(s)!");
                }
                else{
                    mtilTLName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mTEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (valid format)
                if(!mTEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                    mtilTEmail.setError("Invalid email format!");
                }
                else{
                    mtilTEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mTPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, strong password)
                if(whitespaceExist(mTPw.getText().toString())){
                    mtilTPassword.setError("Whitespace(s) not allowed!");
                }
                else if(mTPw.getText().toString().length() < 8 || !digitExist(mTPw.getText().toString()) || !uppercaseExist(mTPw.getText().toString())){
                    mtilTPassword.setError("Password not strong enough! At least 8 characters with digit(s) and uppercase(s)!");
                }
                else{
                    mtilTPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mTConfirmPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (match password)
                if(!mTConfirmPw.getText().toString().matches(mTPw.getText().toString())){
                    mtilTCPassword.setError("Password not match!");
                }
                else{
                    mtilTCPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mTouristSignUp.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to database
            if(mTouristID.getText().toString().trim().isEmpty()){
                mtilTouristID.setError("Field cannot be empty!");
            }
            else if(mTFName.getText().toString().trim().isEmpty()){
                mtilTFName.setError("Field cannot be empty!");
            }
            else if(mTLName.getText().toString().trim().isEmpty()){
                mtilTLName.setError("Field cannot be empty!");
            }
            else if(mTEmail.getText().toString().trim().isEmpty()){
                mtilTEmail.setError("Field cannot be empty!");
            }
            else if(mTPw.getText().toString().trim().isEmpty()){
                mtilTPassword.setError("Field cannot be empty!");
            }
            else if(mTConfirmPw.getText().toString().trim().isEmpty()){
                mtilTCPassword.setError("Field cannot be empty!");
            }
            else{
                //database
            }
        });
    }

    //sign up page -> login page
    @Override
    public void onBackPressed() {
        startActivity(new Intent(TouristSignUp.this, TouristLogin.class));
        finish();
    }

    //login text -> login page
    public void loginTourist(View view) {
        startActivity(new Intent(TouristSignUp.this, TouristLogin.class));
        finish();
    }

    //check digit
    private boolean digitExist(String text){
        return text.matches(".*\\d.*");
    }

    //check uppercase
    private boolean uppercaseExist(String text){
        for(int i = 0; i < text.length(); i++) {
            if (Character.isUpperCase(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    //check whitespace
    private boolean whitespaceExist(String text){
        for(int i = 0; i < text.length(); i++){
            if(Character.isWhitespace(text.charAt(i))){
                return true;
            }
        }
        return false;
    }
}