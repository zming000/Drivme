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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DriverLogin extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilLoginDID, mtilLoginDPW;
    TextInputEditText metLoginDID, metLoginDPW;
    Button mbtnDLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        //obtaining the View with specific ID
        mtilLoginDID = findViewById(R.id.tilLoginDID);
        mtilLoginDPW = findViewById(R.id.tilLoginDPW);
        metLoginDID = findViewById(R.id.etLoginDID);
        metLoginDPW = findViewById(R.id.etLoginDPW);
        mbtnDLogin = findViewById(R.id.btnDLogin);

        metLoginDID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilLoginDID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metLoginDPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilLoginDPW.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnDLogin.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metLoginDID.getText()).toString().trim().isEmpty()){
                mtilLoginDID.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metLoginDPW.getText()).toString().trim().isEmpty()){
                mtilLoginDPW.setError("Field cannot be empty!");
            }
            else{
                //return instance of the class
                FirebaseFirestore userDB = FirebaseFirestore.getInstance();
                String dID = metLoginDID.getText().toString();
                String dPW = metLoginDPW.getText().toString();

                userDB.collection("User Accounts")
                        .document(dID)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot docResult = task.getResult();

                                if (docResult != null) {
                                    //check the existence of ID
                                    if (docResult.exists()) {
                                            String docPW = docResult.getString("Password");
                                            //check if password matched
                                            if (dPW.matches(Objects.requireNonNull(docPW))) {
                                                int sem = Objects.requireNonNull(docResult.getLong("Account Driver")).intValue();

                                                if(sem == 1) {
                                                    int loginStat = Objects.requireNonNull(docResult.getLong("Login Status Driver")).intValue();

                                                    if (loginStat == 0){
                                                        Map<String,Object> userAcc = new HashMap<>();
                                                        userAcc.put("Login Status Driver", 1);

                                                        userDB.collection("User Accounts").document(dID)
                                                                .update(userAcc);

                                                        startActivity(new Intent(DriverLogin.this, WelcomeTo.class));
                                                    }
                                                    else{
                                                        startActivity(new Intent(DriverLogin.this, WelcomeBack.class));
                                                    }
                                                    finish();
                                                }
                                                else{
                                                    Toast.makeText(DriverLogin.this, "Driver Role haven't activated!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else {
                                                Toast.makeText(DriverLogin.this, "Wrong ID or Password!", Toast.LENGTH_SHORT).show();
                                            }

                                    }
                                    else{
                                        mtilLoginDID.setError("ID does not exist!");
                                    }
                                }
                            }
                        });
            }
        });
    }

    //driver login -> driver sign up
    public void signupDriver(View view) {
        startActivity(new Intent(DriverLogin.this, DriverSignUp.class));
        finish();
    }

    //driver login -> driver option
    @Override
    public void onBackPressed() {
        startActivity(new Intent(DriverLogin.this, DriverOption.class));
        finish();
    }

    //driver login -> forgot password
    public void driverForgot(View view) {
        Intent intent = new Intent(DriverLogin.this, ForgotPassword.class);
        intent.putExtra("role", "Driver");

        startActivity(intent);
        finish();
    }
}