package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TouristOTP extends AppCompatActivity {
    //declare variables
    TextView mtvTouristPhoneText, mtvTouristResend, mtvTimer;
    PinView mpvTOTP;
    Button mbtnTouristVerify;
    FirebaseAuth mAuth;
    String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_otp);

        //assign variable
        mtvTouristPhoneText = findViewById(R.id.tvTouristPhoneText);
        mtvTouristResend = findViewById(R.id.tvTouristResend);
        mtvTimer = findViewById(R.id.tvTimer);
        mpvTOTP = findViewById(R.id.pvOTP);
        mbtnTouristVerify = findViewById(R.id.btnTouristVerify);

        //return instance of the class
        mAuth = FirebaseAuth.getInstance();

        //get tourist phone number
        String phNumT = getIntent().getStringExtra("tPhoneNumber");
        mtvTouristPhoneText.setText(phNumT);

        //send otp
        sendOTPtoTouristPhone(phNumT);

        //resend otp
        resendTOTP();

        mbtnTouristVerify.setOnClickListener(v -> {
            String value = Objects.requireNonNull(mpvTOTP.getText()).toString();
            if(value.isEmpty()){
                Toast.makeText(TouristOTP.this, "Invalid OTP Code!", Toast.LENGTH_SHORT).show();
            }
            else {
                verifyCode(value);
            }
        });
    }

    private void sendOTPtoTouristPhone(String phNum) {
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
        public void onVerificationCompleted(@NonNull PhoneAuthCredential authCred) {
            if(!mpvTOTP.getText().toString().isEmpty()){
                verifyCode(mpvTOTP.getText().toString());
            }
            else{
                Toast.makeText(TouristOTP.this, "Please input code!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(TouristOTP.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verID,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verID, token);
            Toast.makeText(TouristOTP.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
            verificationID = verID;
        }
    };

    private void verifyCode(String cred) {
        PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(verificationID, cred);
        signInByCredentials(authCredential);
    }

    private void signInByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth signInCred = FirebaseAuth.getInstance();
        signInCred.signInWithCredential(credential)
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

    //add tourist details into firestore
    private void addInfoToFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String value = getIntent().getStringExtra("tID");

        //tourist details to insert into firestore
        Map<String,Object> touristAcc = new HashMap<>();
        touristAcc.put("userID", value);
        touristAcc.put("firstName", getIntent().getStringExtra("tFName"));
        touristAcc.put("lastName", getIntent().getStringExtra("tLName"));
        touristAcc.put("phoneNumber", getIntent().getStringExtra("tPhoneNumber"));
        touristAcc.put("email", getIntent().getStringExtra("tEmail"));
        touristAcc.put("password", getIntent().getStringExtra("tPassword"));
        touristAcc.put("Login Status Tourist", 0);
        touristAcc.put("Login Status Driver", 0);
        touristAcc.put("Account Tourist", 1);
        touristAcc.put("Account Driver", 0);
        touristAcc.put("Agreement Check", 1);
        touristAcc.put("drivPay", "0.00");

        db.collection("User Accounts").document(value)
                .set(touristAcc)
                .addOnSuccessListener(unused -> {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristOTP.this);
                    alertDialogBuilder.setTitle("Created Account Successfully!");
                    alertDialogBuilder
                            .setMessage("Let's try to login!")
                            .setCancelable(false)
                            .setPositiveButton("Login", (dialog, id) -> {
                                startActivity(new Intent(TouristOTP.this, TouristLogin.class));
                                finishAffinity();
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
                            .setPositiveButton("Try Again", (dialog, id) -> {
                                        startActivity(new Intent(TouristOTP.this, TouristSignUp.class));
                                        finishAffinity();
                                        finish();
                                    });

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                });
    }

    public void resendTOTP() {
        mtvTouristResend.setOnClickListener(view1 -> {
            String num = getIntent().getStringExtra("tPhoneNumber");
            sendOTPtoTouristPhone(num);
            mtvTouristResend.setVisibility(View.GONE);
            mtvTimer.setVisibility(View.VISIBLE);

            //countdown timer 60 seconds
            //initialize timer duration
            long duration = TimeUnit.MINUTES.toMillis(1);

            //Initialize countdown timer
            new CountDownTimer(duration, 1000) {
                @Override
                public void onTick(long l) {
                    String secDuration =  String.format(Locale.ENGLISH, "%02d : %02d",
                            TimeUnit.MILLISECONDS.toMinutes(l),
                            TimeUnit.MILLISECONDS.toSeconds(l) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(1)));
                    //Set converted string
                    mtvTimer.setText(secDuration);
                }

                @Override
                public void onFinish() {
                    mtvTimer.setVisibility(View.GONE);
                    mtvTouristResend.setVisibility(View.VISIBLE);
                }
            }.start();
        });
    }
}