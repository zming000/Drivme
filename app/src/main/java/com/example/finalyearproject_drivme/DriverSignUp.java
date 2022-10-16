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

public class DriverSignUp extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilDriverID, mtilDFName, mtilDLName, mtilDEmail, mtilDPassword, mtilDCPassword, mtilDriverRC;
    TextInputEditText metDriverID, metDriverFName, metDriverLName, metDriverEmail, metDriverPassword, metDriverCPassword, metDriverRC;
    Button mbtnDriverSignUp;
    Boolean statusDID, statusDFName, statusDLName, statusDEmail, statusDPassword, statusDCPassword, statusDVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_up);

        //assign variables
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

        //change error messages
        errorChangeOnEachFields();

        mbtnDriverSignUp.setOnClickListener(v -> {
            //validate each field
            statusDVerification = validationOnEachFields();
            if(statusDVerification){
                checkID();
            }
            else{
                Toast.makeText(DriverSignUp.this, "Please ensure each field input correctly!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //driver sign up -> driver option
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverSignUp.this);
        alertDialogBuilder.setTitle("Discard Process");
        alertDialogBuilder
                .setMessage("Do you wish to discard and go back login?")
                .setCancelable(false)
                .setPositiveButton("DISCARD",
                        (dialog, id) -> {
                            startActivity(new Intent(DriverSignUp.this, DriverLogin.class));
                            finish();
                        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //driver sign up -> driver login
    public void loginDriver(View view) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverSignUp.this);
        alertDialogBuilder.setTitle("Discard Process");
        alertDialogBuilder
                .setMessage("Do you wish to discard and go back login?")
                .setCancelable(false)
                .setPositiveButton("DISCARD",
                        (dialog, id) -> {
                            startActivity(new Intent(DriverSignUp.this, DriverLogin.class));
                            finish();
                        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields(){
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
                else if(Objects.requireNonNull(metDriverID.getText()).length() < 7){
                    mtilDriverID.setError("ID too short! At least total of 7 character(s) and number(s)");
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
                        .matches(Objects.requireNonNull(metDriverPassword.getText()).toString())){
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

    //Validations on each field to ensure correct input
    private boolean validationOnEachFields(){
        //check input conditions (no whitespace, with letters, with digit, no uppercase)
        statusDID = !Objects.requireNonNull(metDriverID.getText()).toString().contains(" ") &&
                metDriverID.getText().toString().matches(".*[a-zA-Z]+.*") &&
                digitExist(metDriverID.getText().toString()) &&
                !uppercaseExist(metDriverID.getText().toString()) &&
                (Objects.requireNonNull(metDriverID.getText()).length() >= 7);

        //check input condition (without digit)
        statusDFName = !digitExist(Objects.requireNonNull(metDriverFName.getText()).toString());

        //check input condition (without digit)
        statusDLName = !digitExist(Objects.requireNonNull(metDriverLName.getText()).toString().trim());

        //check input condition (valid format)
        statusDEmail = Objects.requireNonNull(metDriverEmail.getText()).toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

        //check input conditions (no whitespace, strong password)
        statusDPassword = !whitespaceExist(Objects.requireNonNull(metDriverPassword.getText()).toString()) &&
                metDriverPassword.getText().toString().length() >= 8 &&
                digitExist(metDriverPassword.getText().toString()) &&
                uppercaseExist(metDriverPassword.getText().toString());

        //check input condition (match password)
        statusDCPassword = Objects.requireNonNull(metDriverCPassword.getText()).toString()
                .matches(Objects.requireNonNull(metDriverPassword.getText()).toString());

        return statusDID && statusDFName && statusDLName && statusDEmail && statusDPassword && statusDCPassword;
    }

    //check if ID existed
    private void checkID(){
        //return instance of the class
        FirebaseFirestore drivmeDB = FirebaseFirestore.getInstance();
        String driverID = Objects.requireNonNull(metDriverID.getText()).toString();
        String refCode = Objects.requireNonNull(metDriverRC.getText()).toString();

        drivmeDB.collection("User Accounts").document(driverID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        //check the existence of document ID
                        if(Objects.requireNonNull(document).exists()){
                            mtilDriverID.setError("ID have been used!");
                        }
                        else{
                            drivmeDB.collection("Reference Code Details").document(refCode).get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DocumentSnapshot document2 = task2.getResult();
                                            String codeStatus = Objects.requireNonNull(document2).getString("status");
                                            //check the existence of reference code
                                            if (!document2.exists()) {
                                                mtilDriverRC.setError("Invalid Reference Code!");
                                            }
                                            else if(Objects.requireNonNull(codeStatus).matches("N/A")){ //check if the reference code available
                                                mtilDriverRC.setError("Reference Code Not Available!");
                                            }
                                            else{
                                                Intent intent = new Intent(DriverSignUp.this, DriverPhoneNumber.class);
                                                intent.putExtra("dIDNext", metDriverID.getText().toString());
                                                intent.putExtra("dFNameNext", getCapsSentences(Objects.requireNonNull(metDriverFName.getText()).toString().toUpperCase()));
                                                intent.putExtra("dLNameNext", getCapsSentences(Objects.requireNonNull(metDriverLName.getText()).toString().toUpperCase()));
                                                intent.putExtra("dEmailNext", Objects.requireNonNull(metDriverEmail.getText()).toString());
                                                intent.putExtra("dPasswordNext", Objects.requireNonNull(metDriverPassword.getText()).toString());
                                                intent.putExtra("dRefCodeNext", Objects.requireNonNull(metDriverRC.getText()).toString().toUpperCase());

                                                startActivity(intent);
                                            }
                                        }
                                    });
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

    //change 1st letter into uppercase
    private String getCapsSentences(String tagName) {
        String[] splitWord = tagName.toLowerCase().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < splitWord.length; i++) {
            String word = splitWord[i];
            if (i > 0 && word.length() > 0) {
                sb.append(" ");
            }
            String cap = word.substring(0, 1).toUpperCase()
                    + word.substring(1);
            sb.append(cap);
        }
        return sb.toString();
    }
}