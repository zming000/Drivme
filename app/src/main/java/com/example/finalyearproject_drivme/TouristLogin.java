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

import java.util.Objects;

public class TouristLogin extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilLoginTouristID, mtilLoginTouristPW;
    TextInputEditText metLoginTouristID, metLoginTouristPW;
    Button mbtnTouristLogin;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_FNAME = "fName";
    private static final String KEY_ID = "userID";
    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_login);

        //obtaining the View with specific ID
        mtilLoginTouristID = findViewById(R.id.tilLoginTouristID);
        mtilLoginTouristPW = findViewById(R.id.tilLoginTouristPW);
        metLoginTouristID = findViewById(R.id.etLoginTouristID);
        metLoginTouristPW = findViewById(R.id.etLoginTouristPW);
        mbtnTouristLogin = findViewById(R.id.btnTouristLogin);

        metLoginTouristID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilLoginTouristID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metLoginTouristPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilLoginTouristPW.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnTouristLogin.setOnClickListener(v -> {
            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metLoginTouristID.getText()).toString().trim().isEmpty()){
                mtilLoginTouristID.setError("Field cannot be empty!");
            }
            else if(Objects.requireNonNull(metLoginTouristPW.getText()).toString().trim().isEmpty()){
                mtilLoginTouristPW.setError("Field cannot be empty!");
            }
            else{
                FirebaseFirestore drivmeDB = FirebaseFirestore.getInstance();
                String id = metLoginTouristID.getText().toString();
                String pw = metLoginTouristPW.getText().toString();

                drivmeDB.collection("User Accounts")
                        .document(id)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot docResult = task.getResult();

                                if (docResult != null) {
                                    //check the existence of ID
                                    if (docResult.exists()) {
                                            String pw2 = docResult.getString("Password");
                                            //check if the password matched
                                            if (pw.matches(Objects.requireNonNull(pw2))) {
                                                int value = Objects.requireNonNull(docResult.getLong("Account Tourist")).intValue();

                                                //check if id activated tourist role or not
                                                if(value == 1) {
                                                    int loginStat = Objects.requireNonNull(docResult.getLong("Login Status Tourist")).intValue();
                                                    String name = Objects.requireNonNull(docResult.getString("First Name"));

                                                    if (loginStat == 0){
                                                        startActivity(new Intent(TouristLogin.this, WelcomeTo.class));
                                                    }
                                                    else{
                                                        startActivity(new Intent(TouristLogin.this, WelcomeBack.class));
                                                    }

                                                    //save necessary data for later use
                                                    spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
                                                    SharedPreferences.Editor spEditor = spDrivme.edit();
                                                    spEditor.putString(KEY_FNAME, name);
                                                    spEditor.putString(KEY_ID, id);
                                                    spEditor.putString(KEY_ROLE, "Tourist");
                                                    spEditor.apply();

                                                    finish();
                                                }
                                                else{
                                                    Toast.makeText(TouristLogin.this, "Tourist Role haven't activated!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else {
                                                Toast.makeText(TouristLogin.this, "Wrong ID or Password!", Toast.LENGTH_SHORT).show();
                                            }

                                    }
                                    else{
                                        mtilLoginTouristID.setError("ID does not exist!");
                                    }
                                }
                            }
                        });
            }
        });
    }

    public void googleLogin(View view) {
    }

    //tourist login -> tourist sign up
    public void signupTourist(View view) {
        Intent intent = new Intent(TouristLogin.this, AgreementPolicy.class);
        intent.putExtra("role", "Tourist");
        startActivity(intent);
        finish();
    }

    //tourist login -> role
    @Override
    public void onBackPressed() {
        startActivity(new Intent(TouristLogin.this, Role.class));
        finish();
    }

    public void touristForgot(View view) {
        Intent intent = new Intent(TouristLogin.this, ForgotPassword.class);
        intent.putExtra("role", "Tourist");

        startActivity(intent);
        finish();
    }
}