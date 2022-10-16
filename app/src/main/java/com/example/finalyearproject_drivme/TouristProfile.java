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

public class TouristProfile extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilPEmail, mtilPPhoneNumber;
    TextInputEditText metPID, metPFName, metPLName, metPEmail, metPPhoneNumber;
    Button mbtnEdit;
    FirebaseFirestore getDetails, checkDetails, editDetails;
    SharedPreferences spDrivme;
    boolean statusEmail, statusPhoneNum;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_profile);

        getDetails = FirebaseFirestore.getInstance();
        checkDetails = FirebaseFirestore.getInstance();
        editDetails = FirebaseFirestore.getInstance();
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        String uID = spDrivme.getString(KEY_ID, null);

        mtilPEmail = findViewById(R.id.tilPEmail);
        mtilPPhoneNumber = findViewById(R.id.tilPPhoneNumber);
        metPID = findViewById(R.id.etPID);
        metPFName = findViewById(R.id.etPFName);
        metPLName = findViewById(R.id.etPLName);
        metPEmail = findViewById(R.id.etPEmail);
        metPPhoneNumber = findViewById(R.id.etPPhoneNumber);
        mbtnEdit = findViewById(R.id.btnEdit);

        //change error messages
        errorChangeOnEachFields();
        metPID.setText(uID);

        getDetails.collection("User Accounts").document(uID).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();

                        // Creating a StringBuilder object
                        StringBuilder sb = new StringBuilder(Objects.requireNonNull(doc.getString("phoneNumber")));
                        //delete "+60"
                        for(int i = 0; i < 3; i++) {
                            sb.deleteCharAt(0);
                        }

                        metPFName.setText(doc.getString("firstName"));
                        metPLName.setText(doc.getString("lastName"));
                        metPEmail.setText(doc.getString("email"));
                        metPPhoneNumber.setText(sb.toString());
                    }
                });

        mbtnEdit.setOnClickListener(view -> {
            //validate each field
            boolean statusVerification = validationOnEachFields();
            if(statusVerification) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristProfile.this);
                alertDialogBuilder.setTitle("Discard Process");
                alertDialogBuilder
                        .setMessage("Do you wish to edit your details?")
                        .setCancelable(false)
                        .setPositiveButton("Edit",
                                (dialog, id) -> checkDetails.collection("User Accounts").document(uID).get()
                                        .addOnCompleteListener(task -> {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot doc = task.getResult();

                                                if(Objects.requireNonNull(metPEmail.getText()).toString().equals(doc.getString("email"))
                                                        && ("+60" + Objects.requireNonNull(metPPhoneNumber.getText())).equals(doc.getString("phoneNumber"))){
                                                    Toast.makeText(TouristProfile.this, "Nothing to edit! Please make some changes to edit!", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Map<String,Object> editAcc = new HashMap<>();
                                                    editAcc.put("email", metPEmail.getText().toString());
                                                    editAcc.put("phoneNumber", "+60" + Objects.requireNonNull(metPPhoneNumber.getText()));

                                                    editDetails.collection("User Accounts").document(uID)
                                                            .update(editAcc);

                                                    Toast.makeText(TouristProfile.this, "Edit Successfully!", Toast.LENGTH_SHORT).show();

                                                    startActivity(new Intent(TouristProfile.this, TouristNavSettings.class));
                                                    finishAffinity();
                                                    finish();
                                                }
                                            }
                                        }))
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else{
                Toast.makeText(TouristProfile.this, "Please ensure each field input correctly!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Validations on each field to ensure correct input
    private boolean validationOnEachFields(){
        //check input condition (valid format)
        statusEmail = Objects.requireNonNull(metPEmail.getText()).toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");

        //check input phone number length
        statusPhoneNum = Objects.requireNonNull(metPPhoneNumber.getText()).length() >= 9;

        return statusEmail && statusPhoneNum;
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields(){
        metPEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check input condition (valid format)
                if(!Objects.requireNonNull(metPEmail.getText()).toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                    mtilPEmail.setError("Invalid email format!");
                }
                else{
                    mtilPEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metPPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Objects.requireNonNull(metPPhoneNumber.getText()).length() < 9){
                    metPPhoneNumber.setError("Invalid length of phone number!");
                }
                else {
                    mtilPPhoneNumber.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TouristProfile.this, TouristNavSettings.class));
        finishAffinity();
        finish();
    }
}