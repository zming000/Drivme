package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserResetPW extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilResetPW, mtilResetCPW;
    TextInputEditText metResetPW, metResetCPW;
    Button mbtnResetPW;
    Boolean statusRPassword, statusRCPassword, statusRVerification;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";
    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reset_pw);

        //assign variables
        mtilResetPW = findViewById(R.id.tilResetPW);
        mtilResetCPW = findViewById(R.id.tilResetCPW);
        metResetPW = findViewById(R.id.etResetPW);
        metResetCPW = findViewById(R.id.etResetCPW);
        mbtnResetPW = findViewById(R.id.btnResetPW);

        //change error messages
        errorChangeOnEachFields();

        mbtnResetPW.setOnClickListener(v -> {
            //validate each field
            statusRVerification = validationOnEachFields();
            if(statusRVerification){
                updatePassword();
            }
            else{
                Toast.makeText(UserResetPW.this, "Please ensure each field input correctly!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields(){
        metResetPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, strong password)
                if(whitespaceExist(Objects.requireNonNull(metResetPW.getText()).toString())){
                    mtilResetPW.setError("Whitespace(s) not allowed!");
                }
                else if(metResetPW.getText().toString().length() < 8 || !digitExist(metResetPW.getText().toString())
                        || !uppercaseExist(metResetPW.getText().toString())){
                    mtilResetPW.setError("Password not strong enough! At least 8 characters with digit(s) and uppercase(s)!");
                }
                else{
                    mtilResetPW.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metResetCPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (match password)
                if(!Objects.requireNonNull(metResetCPW.getText()).toString()
                        .matches(Objects.requireNonNull(metResetPW.getText()).toString())){
                    mtilResetCPW.setError("Password not match!");
                }
                else{
                    mtilResetCPW.setErrorEnabled(false);
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
        //check input conditions (no whitespace, strong password)
        statusRPassword = !whitespaceExist(Objects.requireNonNull(metResetPW.getText()).toString()) &&
                metResetPW.getText().toString().length() >= 8 &&
                digitExist(metResetPW.getText().toString()) &&
                uppercaseExist(metResetPW.getText().toString());

        //check input condition (match password)
        statusRCPassword = Objects.requireNonNull(metResetPW.getText()).toString()
                .matches(Objects.requireNonNull(metResetCPW.getText()).toString());

        return statusRPassword && statusRCPassword;
    }

    //update password
    private void updatePassword(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = getIntent().getStringExtra("ID");

        db.collection("User Accounts").document(id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        //check the existence of document ID
                        if(Objects.requireNonNull(document).exists()){
                            Map<String,Object> userAcc = new HashMap<>();
                            userAcc.put("password", Objects.requireNonNull(metResetPW.getText()).toString());

                            db.collection("User Accounts").document(id)
                                    .update(userAcc)
                                    .addOnSuccessListener(unused -> {
                                        Intent intent = new Intent(UserResetPW.this, UserResetSuccess.class);
                                        intent.putExtra("role", getIntent().getStringExtra("Character"));

                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(UserResetPW.this, "Fail to change password!", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
    }

    //Reset Password -> login
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        String character = getIntent().getStringExtra("Character");
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);
        String uRole = spDrivme.getString(KEY_ROLE, null);

        if(uID != null) {
            alertDialogBuilder.setTitle("Remember old password?");
            alertDialogBuilder
                    .setMessage("Click yes to change password!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            (dialog, id) -> {
                                if (uRole.equals("Tourist")) {
                                    startActivity(new Intent(UserResetPW.this, TouristChangePW.class));
                                }
                                else{
                                    startActivity(new Intent(UserResetPW.this, DriverChangePW.class));
                                }
                                finishAffinity();
                                finish();
                            })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        }
        else {
            alertDialogBuilder.setTitle("Remember password?");
            alertDialogBuilder
                    .setMessage("Click yes to login!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            (dialog, id) -> {
                                if (character.equals("Tourist")) {
                                    startActivity(new Intent(UserResetPW.this, TouristLogin.class));
                                } else {
                                    startActivity(new Intent(UserResetPW.this, DriverLogin.class));
                                }
                                finishAffinity();
                                finish();
                            })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        }

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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