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

public class DriverChangePW extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilDOld, mtilDNew, mtilDConfirm;
    TextInputEditText metDOld, metDNew, metDConfirm;
    Button mbtnDChange;
    SharedPreferences spDrivme;
    Boolean statusRPassword, statusRCPassword;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_change_pw);

        //assign variables
        mtilDOld = findViewById(R.id.tilDOld);
        mtilDNew = findViewById(R.id.tilDNew);
        mtilDConfirm = findViewById(R.id.tilDConfirm);
        metDOld = findViewById(R.id.etDOld);
        metDNew = findViewById(R.id.etDNew);
        metDConfirm = findViewById(R.id.etDConfirm);
        mbtnDChange = findViewById(R.id.btnDChange);

        //initialize shared preference
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //error change
        errorChangeOnEachFields();

        mbtnDChange.setOnClickListener(view -> {
            FirebaseFirestore checkPW = FirebaseFirestore.getInstance();
            String uID = spDrivme.getString(KEY_ID, null);

            checkPW.collection("User Accounts").document(uID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();

                            String old = doc.getString("password");
                            String inputOld = Objects.requireNonNull(metDOld.getText()).toString();

                            if(inputOld.equals(old)){
                                //validate each field
                                boolean statusRVerification = validationOnEachFields();
                                if(statusRVerification){
                                    updatePassword();
                                }
                                else{
                                    Toast.makeText(DriverChangePW.this, "Please ensure each field input correctly!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                mtilDOld.setError("Incorrect old password!");
                            }

                        }
                    });
        });
    }

    //Validations on each field to ensure correct input
    private boolean validationOnEachFields(){
        //check input conditions (no whitespace, strong password)
        statusRPassword = !Objects.requireNonNull(metDOld.getText()).toString()
                .matches(Objects.requireNonNull(metDNew.getText()).toString()) &&
                !whitespaceExist(Objects.requireNonNull(metDNew.getText()).toString()) &&
                metDNew.getText().toString().length() >= 8 &&
                digitExist(metDNew.getText().toString()) &&
                uppercaseExist(metDNew.getText().toString());

        //check input condition (match password)
        statusRCPassword = Objects.requireNonNull(metDNew.getText()).toString()
                .matches(Objects.requireNonNull(metDConfirm.getText()).toString());

        return statusRPassword && statusRCPassword;
    }

    //update password
    private void updatePassword(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uID = spDrivme.getString(KEY_ID, null);

        Map<String,Object> updatePW = new HashMap<>();
        updatePW.put("password", Objects.requireNonNull(metDNew.getText()).toString());

        db.collection("User Accounts").document(uID)
                .update(updatePW)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(DriverChangePW.this, "Password Change Successfully!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(DriverChangePW.this, DriverNavSettings.class));
                    finishAffinity();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(DriverChangePW.this, "Fail to change password!", Toast.LENGTH_SHORT).show());
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields(){
        metDOld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDOld.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input conditions (no whitespace, strong password)
                if(Objects.requireNonNull(metDOld.getText()).toString().matches(Objects.requireNonNull(metDNew.getText()).toString())){
                    mtilDNew.setError("Cannot use old password!");
                }
                else if(whitespaceExist(Objects.requireNonNull(metDNew.getText()).toString())){
                    mtilDNew.setError("Whitespace(s) not allowed!");
                }
                else if(metDNew.getText().toString().length() < 8 || !digitExist(metDNew.getText().toString())
                        || !uppercaseExist(metDNew.getText().toString())){
                    mtilDNew.setError("Password not strong enough! At least 8 characters with digit(s) and uppercase(s)!");
                }
                else{
                    mtilDNew.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (match password)
                if(!Objects.requireNonNull(metDConfirm.getText()).toString()
                        .matches(Objects.requireNonNull(metDNew.getText()).toString())){
                    mtilDConfirm.setError("Password not match!");
                }
                else{
                    mtilDConfirm.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    public void forgotPassW(View view) {
        Intent intent = new Intent(DriverChangePW.this, UserForgotPassword.class);
        intent.putExtra("role", "Driver");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DriverChangePW.this, DriverNavSettings.class));
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