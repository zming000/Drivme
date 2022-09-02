package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class TouristSignUp extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilTouristID, mtilTFName, mtilTLName, mtilTEmail, mtilTPassword, mtilTCPassword;
    TextInputEditText metTouristID, metTouristFName, metTouristLName, metTouristEmail, metTouristPassword, metTouristCPassword;
    Button mbtnTouristNext;
    Boolean statusID, statusFName, statusLName, statusEmail, statusPassword, statusCPassword, statusVerification;

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
        mbtnTouristNext = findViewById(R.id.btnTouristNext);

        //change error messages
        errorChangeOnEachFields();

        mbtnTouristNext.setOnClickListener(v -> {
            //validate each field
            statusVerification = validationOnEachFields();
            if(statusVerification) {
                checkID();
            }
            else{
                Toast.makeText(TouristSignUp.this, "Please ensure each field input correctly!", Toast.LENGTH_SHORT).show();
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

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields(){
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
                else if(Objects.requireNonNull(metTouristID.getText()).length() < 7){
                    mtilTouristID.setError("ID too short! At least total of 7 character(s) and number(s)");
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

    //Validations on each field to ensure correct input
    private boolean validationOnEachFields(){
        //check input conditions (no whitespace, with letters, with digit, no uppercase)
        statusID = !Objects.requireNonNull(metTouristID.getText()).toString().contains(" ") &&
                metTouristID.getText().toString().matches(".*[a-zA-Z]+.*") &&
                digitExist(metTouristID.getText().toString()) &&
                !uppercaseExist(metTouristID.getText().toString()) &&
                (Objects.requireNonNull(metTouristID.getText()).length() > 7);

        //check input condition (without digit)
        statusFName = !digitExist(Objects.requireNonNull(metTouristFName.getText()).toString());

        //check input condition (without digit)
        statusLName = !digitExist(Objects.requireNonNull(metTouristLName.getText()).toString().trim());

        //check input condition (valid format)
        statusEmail = Objects.requireNonNull(metTouristEmail.getText()).toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

        //check input conditions (no whitespace, strong password)
        statusPassword = !whitespaceExist(Objects.requireNonNull(metTouristPassword.getText()).toString()) &&
                metTouristPassword.getText().toString().length() >= 8 &&
                digitExist(metTouristPassword.getText().toString()) &&
                uppercaseExist(metTouristPassword.getText().toString());

        //check input condition (match password)
        statusCPassword = Objects.requireNonNull(metTouristCPassword.getText()).toString()
                .matches(Objects.requireNonNull(metTouristPassword.getText()).toString());

        return statusID && statusFName && statusLName && statusEmail && statusPassword && statusCPassword;
    }

    //check if ID existed
    private void checkID(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String value = Objects.requireNonNull(metTouristID.getText()).toString();

        db.collection("User Accounts").document(value).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        //check the existence of document ID
                        if(Objects.requireNonNull(document).exists()){
                            mtilTouristID.setError("ID have been used!");
                        }
                        else{
                            Intent intent = new Intent(TouristSignUp.this, TouristPhoneNumber.class);
                            intent.putExtra("tIDNext", metTouristID.getText().toString());
                            intent.putExtra("tFNameNext", getCapsSentences(Objects.requireNonNull(metTouristFName.getText()).toString()));
                            intent.putExtra("tLNameNext", getCapsSentences(Objects.requireNonNull(metTouristLName.getText()).toString()));
                            intent.putExtra("tEmailNext", Objects.requireNonNull(metTouristEmail.getText()).toString());
                            intent.putExtra("tPasswordNext", Objects.requireNonNull(metTouristPassword.getText()).toString());

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
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