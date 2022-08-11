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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TouristSignUp extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilTouristID, mtilTFName, mtilTLName, mtilTEmail, mtilTPassword, mtilTCPassword;
    TextInputEditText metTouristID, metTouristFName, metTouristLName, metTouristEmail, metTouristPassword, metTouristCPassword;
    Button mbtnTouristSignUp;



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
        metTouristID = findViewById(R.id.etSignUpTouristID);
        metTouristFName = findViewById(R.id.etSignUpTouristFName);
        metTouristLName = findViewById(R.id.etSignUpTouristLName);
        metTouristEmail = findViewById(R.id.etSignUpTouristEmail);
        metTouristPassword = findViewById(R.id.etSignUpTouristPassword);
        metTouristCPassword = findViewById(R.id.etSignUpTouristCPassword);
        mbtnTouristSignUp = findViewById(R.id.btnTouristSignUp);

        validationOnEachFields();

        mbtnTouristSignUp.setOnClickListener(v -> {

            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metTouristID.getText()).toString().trim().isEmpty()){
                mtilTouristID.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metTouristFName.getText()).toString().trim().isEmpty()){
                mtilTFName.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metTouristLName.getText()).toString().trim().isEmpty()){
                mtilTLName.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metTouristEmail.getText()).toString().trim().isEmpty()){
                mtilTEmail.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metTouristPassword.getText()).toString().trim().isEmpty()){
                mtilTPassword.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metTouristCPassword.getText()).toString().trim().isEmpty()){
                mtilTCPassword.setError("Field cannot be empty!");
            }
            else{
                addInfoToFirestore();
            }
        });
    }

    //tourist sign up -> tourist login
    @Override
    public void onBackPressed() {
        startActivity(new Intent(TouristSignUp.this, TouristLogin.class));
        finish();
    }

    //tourist login text -> tourist login
    public void loginTourist(View view) {
        startActivity(new Intent(TouristSignUp.this, TouristLogin.class));
        finish();
    }

    private void validationOnEachFields(){
        //Validations on each field to ensure correct input
        metTouristID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, with letters, with digit, no uppercase)
                if(Objects.requireNonNull(metTouristID.getText()).toString().contains(" ")){
                    mtilTouristID.setError("ID cannot contain spaces!");
                }
                else if(!metTouristID.getText().toString().matches(".*[a-zA-Z]+.*")){
                    mtilTouristID.setError("Please contain letter(s) in ID!");
                }
                else if(!digitExist(metTouristID.getText().toString())){
                    mtilTouristID.setError("Please contain number(s) in ID!");
                }
                else if(uppercaseExist(metTouristID.getText().toString())){
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

        metTouristFName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (without digit)
                if(digitExist(Objects.requireNonNull(metTouristFName.getText()).toString())){
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

        metTouristLName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (without digit)
                if(digitExist(Objects.requireNonNull(metTouristLName.getText()).toString().trim())){
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

        metTouristEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (valid format)
                if(!Objects.requireNonNull(metTouristEmail.getText()).toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
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

        metTouristPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, strong password)
                if(whitespaceExist(Objects.requireNonNull(metTouristPassword.getText()).toString())){
                    mtilTPassword.setError("Whitespace(s) not allowed!");
                }
                else if(metTouristPassword.getText().toString().length() < 8 || !digitExist(metTouristPassword.getText().toString())
                        || !uppercaseExist(metTouristPassword.getText().toString())){
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

        metTouristCPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (match password)
                if(!Objects.requireNonNull(metTouristCPassword.getText()).toString()
                        .matches(Objects.requireNonNull(metTouristPassword.getText()).toString())){
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
    }

    //add tourists details into firestore
    private void addInfoToFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String value = Objects.requireNonNull(metTouristID.getText()).toString();

        db.collection("Tourists Account Details")
                .document(value)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document != null) {
                            //check the existence of document ID
                            if(document.exists()){
                                mtilTouristID.setError("ID have been used!");
                            }
                            else{
                                Map<String,Object> touristAcc = new HashMap<>();
                                touristAcc.put("Tourist ID", metTouristID.getText().toString());
                                touristAcc.put("Tourist First Name", Objects.requireNonNull(metTouristFName.getText()).toString().toUpperCase());
                                touristAcc.put("Tourist Last Name", Objects.requireNonNull(metTouristLName.getText()).toString().toUpperCase());
                                touristAcc.put("Tourist Email", Objects.requireNonNull(metTouristEmail.getText()).toString());
                                touristAcc.put("Tourist Password", Objects.requireNonNull(metTouristPassword.getText()).toString());

                                db.collection("Tourists Account Details")
                                        .document(value)
                                        .set(touristAcc)
                                        .addOnSuccessListener(unused -> {
                                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristSignUp.this);
                                            alertDialogBuilder.setTitle("Created Account Successfully!");
                                            alertDialogBuilder
                                                    .setMessage("Let's try to login!")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Yes", (dialog, id) -> {
                                                        startActivity(new Intent(TouristSignUp.this, TouristLogin.class));
                                                        finish();
                                                    });

                                            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();
                                        })
                                        .addOnFailureListener(e -> {
                                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristSignUp.this);
                                            alertDialogBuilder.setTitle("Fail to create account!");
                                            alertDialogBuilder
                                                    .setMessage("Please try again!")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK",
                                                            (dialog, id) -> {
                                                                metTouristID.getText().clear();
                                                                metTouristFName.getText().clear();
                                                                metTouristLName.getText().clear();
                                                                metTouristEmail.getText().clear();
                                                                metTouristPassword.getText().clear();
                                                                metTouristCPassword.getText().clear();
                                                            });

                                            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();
                                        });
                            }
                        }
                    }
                });
    }

    //validation checking
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