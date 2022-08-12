package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
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

public class DriverSignUp extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilDriverID, mtilDFName, mtilDLName, mtilDEmail, mtilDPassword, mtilDCPassword, mtilDriverRC;
    TextInputEditText metDriverID, metDriverFName, metDriverLName, metDriverEmail, metDriverPassword, metDriverCPassword, metDriverRC;
    Button mbtnDriverSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_up);

        //obtaining the View with specific ID
        mtilDriverID = findViewById(R.id.tilSignUpDriverID);
        mtilDFName = findViewById(R.id.tilSignUpDriverFName);
        mtilDLName = findViewById(R.id.tilSignUpDriverLName);
        mtilDEmail = findViewById(R.id.tilSignUpDriverEmail);
        mtilDPassword = findViewById(R.id.tilSignUpDriverPassword);
        mtilDCPassword = findViewById(R.id.tilSignUpDriverCPassword);
        mtilDriverRC = findViewById(R.id.tilSignUpDriverReferenceCode);
        metDriverID = findViewById(R.id.etSignUpDriverID);
        metDriverFName = findViewById(R.id.etSignUpDriverFName);
        metDriverLName = findViewById(R.id.etSignUpDriverLName);
        metDriverEmail = findViewById(R.id.etSignUpDriverEmail);
        metDriverPassword = findViewById(R.id.etSignUpDriverPassword);
        metDriverCPassword = findViewById(R.id.etSignUpDriverCPassword);
        metDriverRC = findViewById(R.id.etSignUpDriverReferenceCode);
        mbtnDriverSignUp = findViewById(R.id.btnDriverSignUp);

        validationOnEachFields();

        mbtnDriverSignUp.setOnClickListener(v -> {

            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metDriverID.getText()).toString().trim().isEmpty()){
                mtilDriverID.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metDriverFName.getText()).toString().trim().isEmpty()){
                mtilDFName.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metDriverLName.getText()).toString().trim().isEmpty()){
                mtilDLName.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metDriverEmail.getText()).toString().trim().isEmpty()){
                mtilDEmail.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metDriverPassword.getText()).toString().trim().isEmpty()){
                mtilDPassword.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metDriverCPassword.getText()).toString().trim().isEmpty()){
                mtilDCPassword.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metDriverRC.getText()).toString().trim().isEmpty()){
                mtilDriverRC.setError("Field cannot be empty!");
            }
            else{
                addInfoToFirestore();
            }
        });
    }

    //driver sign up -> driver option
    @Override
    public void onBackPressed() {
        startActivity(new Intent(DriverSignUp.this, DriverOption.class));
        finish();
    }

    //driver sign up -> driver login
    public void loginDriver(View view) {
        startActivity(new Intent(DriverSignUp.this, DriverLogin.class));
        finish();
    }

    private void validationOnEachFields(){
        //Validations on each field to ensure correct input
        metDriverID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, with letters, with digit, no uppercase)
                if(Objects.requireNonNull(metDriverID.getText()).toString().contains(" ")){
                    mtilDriverID.setError("ID cannot contain spaces!");
                }
                else if(!metDriverID.getText().toString().matches(".*[a-zA-Z]+.*")){
                    mtilDriverID.setError("Please contain letter(s) in ID!");
                }
                else if(!digitExist(metDriverID.getText().toString())){
                    mtilDriverID.setError("Please contain number(s) in ID!");
                }
                else if(uppercaseExist(metDriverID.getText().toString())){
                    mtilDriverID.setError("ID cannot contain uppercase!");
                }
                else{
                    mtilDriverID.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDriverFName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (without digit)
                if(digitExist(Objects.requireNonNull(metDriverFName.getText()).toString())){
                    mtilDFName.setError("First Name cannot contain number(s)!");
                }
                else{
                    mtilDFName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDriverLName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (without digit)
                if(digitExist(Objects.requireNonNull(metDriverLName.getText()).toString().trim())){
                    mtilDLName.setError("Last Name cannot contain number(s)!");
                }
                else{
                    mtilDLName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDriverEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (valid format)
                if(!Objects.requireNonNull(metDriverEmail.getText()).toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                    mtilDEmail.setError("Invalid email format!");
                }
                else{
                    mtilDEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDriverPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, strong password)
                if(whitespaceExist(Objects.requireNonNull(metDriverPassword.getText()).toString())){
                    mtilDPassword.setError("Whitespace(s) not allowed!");
                }
                else if(metDriverPassword.getText().toString().length() < 8 || !digitExist(metDriverPassword.getText().toString())
                        || !uppercaseExist(metDriverPassword.getText().toString())){
                    mtilDPassword.setError("Password not strong enough! At least 8 characters with digit(s) and uppercase(s)!");
                }
                else{
                    mtilDPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDriverCPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (match password)
                if(!Objects.requireNonNull(metDriverCPassword.getText()).toString()
                        .matches(Objects.requireNonNull(metDriverCPassword.getText()).toString())){
                    mtilDCPassword.setError("Password not match!");
                }
                else{
                    mtilDCPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDriverRC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDriverRC.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    //add driver details into firestore
    private void addInfoToFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String value = Objects.requireNonNull(metDriverID.getText()).toString();
        String code = Objects.requireNonNull(metDriverRC.getText()).toString();

        db.collection("Drivers Account Details").document(value).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document != null) {
                            //check the existence of document ID
                            if(document.exists()){
                                mtilDriverID.setError("ID have been used!");
                            }
                            else{
                                db.collection("Reference Code Details").document(code).get()
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                DocumentSnapshot document2 = task2.getResult();
                                                String codeStatus = document2.getString("Status");
                                                //check the existence of reference code
                                                if (!document2.exists()) {
                                                    mtilDriverRC.setError("Invalid Reference Code!");
                                                }
                                                else if(codeStatus.matches("N/A")){ //check if the reference code available
                                                    mtilDriverRC.setError("Reference Code Not Available!");
                                                }
                                                else{
                                                    //update reference code details and insert driver details into firestore
                                                    Map<String,Object> refCode = new HashMap<>();
                                                    refCode.put("Driver ID", metDriverID.getText().toString());
                                                    refCode.put("Status", "N/A");

                                                    Map<String,Object> driverAcc = new HashMap<>();
                                                    driverAcc.put("Driver ID", metDriverID.getText().toString());
                                                    driverAcc.put("Driver First Name", Objects.requireNonNull(metDriverFName.getText()).toString().toUpperCase());
                                                    driverAcc.put("Driver Last Name", Objects.requireNonNull(metDriverLName.getText()).toString().toUpperCase());
                                                    driverAcc.put("Driver Email", Objects.requireNonNull(metDriverEmail.getText()).toString());
                                                    driverAcc.put("Driver Password", Objects.requireNonNull(metDriverPassword.getText()).toString());

                                                    db.collection("Reference Code Details").document(code)
                                                            .update(refCode);

                                                    db.collection("Drivers Account Details").document(value)
                                                            .set(driverAcc)
                                                            .addOnSuccessListener(unused -> {
                                                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverSignUp.this);
                                                                alertDialogBuilder.setTitle("Created Account Successfully!");
                                                                alertDialogBuilder
                                                                        .setMessage("Let's try to login!")
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("Yes", (dialog, id) -> {
                                                                            startActivity(new Intent(DriverSignUp.this, DriverLogin.class));
                                                                            finish();
                                                                        });

                                                                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                                                alertDialog.show();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverSignUp.this);
                                                                alertDialogBuilder.setTitle("Fail to create account!");
                                                                alertDialogBuilder
                                                                        .setMessage("Please try again!")
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("OK",
                                                                                (dialog, id) -> {
                                                                                    metDriverID.getText().clear();
                                                                                    metDriverFName.getText().clear();
                                                                                    metDriverLName.getText().clear();
                                                                                    metDriverEmail.getText().clear();
                                                                                    metDriverPassword.getText().clear();
                                                                                    metDriverCPassword.getText().clear();
                                                                                    metDriverRC.getText().clear();
                                                                                });

                                                                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                                                alertDialog.show();
                                                            });
                                                }
                                            }
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