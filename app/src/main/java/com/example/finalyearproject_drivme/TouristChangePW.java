package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TouristChangePW extends AppCompatActivity {
    TextInputLayout mtilTOld, mtilTNew, mtilTConfirm;
    TextInputEditText metTOld, metTNew, metTConfirm;
    Button mbtnChange;
    SharedPreferences spDrivme;
    Boolean statusRPassword, statusRCPassword;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_change_pw);

        mtilTOld = findViewById(R.id.tilTOld);
        mtilTNew = findViewById(R.id.tilTNew);
        mtilTConfirm = findViewById(R.id.tilTConfirm);
        metTOld = findViewById(R.id.etTOld);
        metTNew = findViewById(R.id.etTNew);
        metTConfirm = findViewById(R.id.etTConfirm);
        mbtnChange = findViewById(R.id.btnChange);

        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        errorChangeOnEachFields();

        mbtnChange.setOnClickListener(view -> {
            FirebaseFirestore checkPW = FirebaseFirestore.getInstance();
            String uID = spDrivme.getString(KEY_ID, null);

            checkPW.collection("User Accounts").document(uID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();

                            String old = doc.getString("password");
                            String inputOld = Objects.requireNonNull(metTOld.getText()).toString();

                            if(inputOld.equals(old)){
                                //validate each field
                                boolean statusRVerification = validationOnEachFields();
                                if(statusRVerification){
                                    updatePassword();
                                }
                                else{
                                    Toast.makeText(TouristChangePW.this, "Please ensure each field input correctly!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                mtilTOld.setError("Incorrect old password!");
                            }

                        }
                    });
        });
    }

    //Validations on each field to ensure correct input
    private boolean validationOnEachFields(){
        //check input conditions (no whitespace, strong password)
        statusRPassword = !Objects.requireNonNull(metTOld.getText()).toString()
                .matches(Objects.requireNonNull(metTNew.getText()).toString()) &&
                !whitespaceExist(Objects.requireNonNull(metTNew.getText()).toString()) &&
                metTNew.getText().toString().length() >= 8 &&
                digitExist(metTNew.getText().toString()) &&
                uppercaseExist(metTNew.getText().toString());

        //check input condition (match password)
        statusRCPassword = Objects.requireNonNull(metTNew.getText()).toString()
                .matches(Objects.requireNonNull(metTConfirm.getText()).toString());

        return statusRPassword && statusRCPassword;
    }

    //update password
    private void updatePassword(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uID = spDrivme.getString(KEY_ID, null);

        Map<String,Object> updatePW = new HashMap<>();
        updatePW.put("password", Objects.requireNonNull(metTNew.getText()).toString());

        db.collection("User Accounts").document(uID)
                .update(updatePW)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(TouristChangePW.this, "Password Change Successfully!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(TouristChangePW.this, TouristNavSettings.class));
                    finishAffinity();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(TouristChangePW.this, "Fail to change password!", Toast.LENGTH_SHORT).show());
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields(){
        metTOld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilTOld.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metTNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, strong password)
                if(Objects.requireNonNull(metTOld.getText()).toString().matches(Objects.requireNonNull(metTNew.getText()).toString())){
                    mtilTNew.setError("Cannot use old password!");
                }
                else if(whitespaceExist(Objects.requireNonNull(metTNew.getText()).toString())){
                    mtilTNew.setError("Whitespace(s) not allowed!");
                }
                else if(metTNew.getText().toString().length() < 8 || !digitExist(metTNew.getText().toString())
                        || !uppercaseExist(metTNew.getText().toString())){
                    mtilTNew.setError("Password not strong enough! At least 8 characters with digit(s) and uppercase(s)!");
                }
                else{
                    mtilTNew.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metTConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (match password)
                if(!Objects.requireNonNull(metTConfirm.getText()).toString()
                        .matches(Objects.requireNonNull(metTNew.getText()).toString())){
                    mtilTConfirm.setError("Password not match!");
                }
                else{
                    mtilTConfirm.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    public void forgotPW(View view) {
        Intent intent = new Intent(TouristChangePW.this, UserForgotPassword.class);
        intent.putExtra("role", "Tourist");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TouristChangePW.this, TouristNavSettings.class));
        finish();
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