package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilFpwID;
    TextInputEditText metFpwID;
    CardView mviaEmail, mviaPhoneNumber;
    String character;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //obtaining the View with specific ID
        mtilFpwID = findViewById(R.id.tilFpwID);
        metFpwID = findViewById(R.id.etFpwID);
        mviaEmail = findViewById(R.id.viaEmail);
        mviaPhoneNumber = findViewById(R.id.viaPhoneNumber);

        db = FirebaseFirestore.getInstance();
        character = getIntent().getStringExtra("role");

        metFpwID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilFpwID.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mviaEmail.setOnClickListener(view -> {
            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metFpwID.getText()).toString().trim().isEmpty()){
                mtilFpwID.setError("Field cannot be empty!");
            }
            else{
                String id = Objects.requireNonNull(metFpwID.getText()).toString();

                if(character.equals("Tourist")) {
                    db.collection("User Accounts").document(id).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document != null) {
                                        //check the existence of document/tourist ID
                                        if (document.exists()) {
                                            Integer value = (Integer) document.get("Account Tourist");

                                            if(value == 1) {
                                                Intent intent = new Intent(ForgotPassword.this, FPWEmail.class);
                                                intent.putExtra("roleID", id);
                                                intent.putExtra("roleCharacter", character);

                                                startActivity(intent);
                                            }
                                            else{
                                                Toast.makeText(ForgotPassword.this, "Tourist Role haven't activated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            mtilFpwID.setError("ID does not exist!");
                                        }
                                    }
                                }
                            });
                }
                else{
                    db.collection("User Accounts")
                            .document(id)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document != null) {
                                        //check the existence of document/tourist ID
                                        if (document.exists()) {
                                            Integer value = (Integer) document.get("Account Driver");

                                            if(value == 1) {
                                                Intent intent = new Intent(ForgotPassword.this, FPWEmail.class);
                                                intent.putExtra("roleID", id);
                                                intent.putExtra("roleCharacter", character);

                                                startActivity(intent);
                                            }
                                            else{
                                                Toast.makeText(ForgotPassword.this, "Driver Role haven't activated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            mtilFpwID.setError("ID does not exist!");
                                        }
                                    }
                                }
                            });
                }
            }
        });

        mviaPhoneNumber.setOnClickListener(view -> {
            //check condition (fields not empty) before proceed to database
            if(Objects.requireNonNull(metFpwID.getText()).toString().trim().isEmpty()){
                mtilFpwID.setError("Field cannot be empty!");
            }
            else{
                String id = Objects.requireNonNull(metFpwID.getText()).toString();
                if(character.equals("Tourist")) {
                    db.collection("User Accounts")
                            .document(id)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document != null) {
                                        //check the existence of document/tourist ID
                                        if (document.exists()) {
                                            Integer value = (Integer) document.get("Account Tourist");

                                            if(value == 1) {
                                                Intent intent = new Intent(ForgotPassword.this, FPWPhoneNumber.class);
                                                intent.putExtra("roleID", id);
                                                intent.putExtra("roleCharacter", character);

                                                startActivity(intent);
                                            }
                                            else{
                                                Toast.makeText(ForgotPassword.this, "Tourist Role haven't activated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            mtilFpwID.setError("ID does not exist!");
                                        }
                                    }
                                }
                            });
                }
                else{
                    db.collection("User Accounts")
                            .document(id)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document != null) {
                                        //check the existence of document/tourist ID
                                        if (document.exists()) {
                                            Integer value = (Integer) document.get("Account Driver");

                                            if(value == 1) {
                                                Intent intent = new Intent(ForgotPassword.this, FPWPhoneNumber.class);
                                                intent.putExtra("roleID", id);
                                                intent.putExtra("roleCharacter", character);

                                                startActivity(intent);
                                            }
                                            else{
                                                Toast.makeText(ForgotPassword.this, "Driver Role haven't activated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            mtilFpwID.setError("ID does not exist!");
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    public void backToLogin(View view) {
        if(character.equals("Tourist")) {
            startActivity(new Intent(ForgotPassword.this, TouristLogin.class));
        }
        else{
            startActivity(new Intent(ForgotPassword.this, DriverLogin.class));
        }
        finish();
    }

    //tourist forgot password -> login
    @Override
    public void onBackPressed() {
        if(character.equals("Tourist")) {
            startActivity(new Intent(ForgotPassword.this, TouristLogin.class));
        }
        else{
            startActivity(new Intent(ForgotPassword.this, DriverLogin.class));
        }
        finish();
    }
}