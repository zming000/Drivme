package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TouristOTP extends AppCompatActivity {
    TextView mtvTouristPhoneText;
    PinView mpvOTP;
    Button mbtnTouristVerify;
    FirebaseAuth mAuth;
    String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_otp);

        //obtaining the View with specific ID
        mtvTouristPhoneText = findViewById(R.id.tvTouristPhoneText);
        mpvOTP = findViewById(R.id.pvOTP);
        mbtnTouristVerify = findViewById(R.id.btnTouristVerify);

        //return instance of the class
        mAuth = FirebaseAuth.getInstance();

        //set phone number
        String phNum = getIntent().getStringExtra("tPhoneNumber");
        mtvTouristPhoneText.setText(phNum);

        //send otp
        sendOTP(phNum);

        mbtnTouristVerify.setOnClickListener(v -> {
            String value = mpvOTP.getText().toString();
            if(value.isEmpty()){
                Toast.makeText(TouristOTP.this, "Invalid OTP Code!", Toast.LENGTH_SHORT).show();
            }
            else {
                verifyCode(value);
            }
        });
    }

    private void sendOTP(String phNum) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phNum)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //callback functions that handle the results of the request
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            String code = credential.getSmsCode();
            if(code != null){
                mpvOTP.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(TouristOTP.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificationID = s;
        }
    };

    private void verifyCode(String c) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, c);
        signInByCredentials(credential);
    }

    private void signInByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        addInfoToFirestore();
                    }
                    else{
                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(TouristOTP.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //add tourists details into firestore
    private void addInfoToFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String value = getIntent().getStringExtra("tID");

        db.collection("Tourists Account Details")
                .document(value)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        //insert tourist details into firestore
                        Map<String,Object> touristAcc = new HashMap<>();
                        touristAcc.put("Tourist ID", value);
                        touristAcc.put("Tourist First Name", getIntent().getStringExtra("tFName"));
                        touristAcc.put("Tourist Last Name", getIntent().getStringExtra("tLName"));
                        touristAcc.put("Tourist Phone Number", getIntent().getStringExtra("tPhoneNumber"));
                        touristAcc.put("Tourist Email", getIntent().getStringExtra("tEmail"));
                        touristAcc.put("Tourist Password", getIntent().getStringExtra("tPassword"));

                        db.collection("Tourists Account Details")
                                .document(value)
                                .set(touristAcc)
                                .addOnSuccessListener(unused -> {
                                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristOTP.this);
                                    alertDialogBuilder.setTitle("Created Account Successfully!");
                                    alertDialogBuilder
                                            .setMessage("Let's try to login!")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", (dialog, id) -> {
                                                startActivity(new Intent(TouristOTP.this, TouristLogin.class));
                                                finish();
                                            });

                                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                })
                                .addOnFailureListener(e -> {
                                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristOTP.this);
                                    alertDialogBuilder.setTitle("Fail to create account!");
                                    alertDialogBuilder
                                            .setMessage("Please try again!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK",
                                                    (dialog, id) -> {
                                                        startActivity(new Intent(TouristOTP.this, TouristSignUp.class));
                                                        finish();
                                                    });

                                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                });
                    }
                });
    }
}