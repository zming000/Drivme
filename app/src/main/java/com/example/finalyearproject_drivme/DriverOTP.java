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

public class DriverOTP extends AppCompatActivity {
    //declare variables
    TextView mtvDPhoneText, mtvDriverResend, mtvTimer;
    PinView mpvDOTP;
    Button mbtnDVerify;
    FirebaseAuth driverOTP;
    String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_otp);

        //assign variables
        mtvDPhoneText = findViewById(R.id.tvDPhoneText);
        mtvDriverResend = findViewById(R.id.tvDResend);
        mtvTimer = findViewById(R.id.tvTimer);
        mpvDOTP = findViewById(R.id.pvDriverOTP);
        mbtnDVerify = findViewById(R.id.btnDVerify);

        mtvTimer.setVisibility(View.GONE);

        //return instance of the class
        driverOTP = FirebaseAuth.getInstance();

        //get driver phone number
        String phNumD = getIntent().getStringExtra("dPhoneNumber");
        mtvDPhoneText.setText(phNumD);

        //send otp
        sendOTPtoDriverPhone(phNumD);

        //resend otp
        resendDOTP();

        mbtnDVerify.setOnClickListener(v -> {
            String value = Objects.requireNonNull(mpvDOTP.getText()).toString();
            if(value.isEmpty()){
                Toast.makeText(DriverOTP.this, "Invalid OTP Code!", Toast.LENGTH_SHORT).show();
            }
            else {
                verifyOTP(value);
            }
        });
    }

    private void sendOTPtoDriverPhone(String phNum) {
        PhoneAuthOptions opts = PhoneAuthOptions.newBuilder(driverOTP)
                .setPhoneNumber(phNum)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(opts);
    }

    //callback functions that handle the results of the request
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential authCred) {
            String verificationCodeD = authCred.getSmsCode(); //auto get from sms
            mpvDOTP.setText(verificationCodeD);

            if(verificationCodeD != null){
                verifyOTP(verificationCodeD);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(DriverOTP.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verID,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(verID, token);
            Toast.makeText(DriverOTP.this, "OTP sent!", Toast.LENGTH_SHORT).show();
            verificationID = verID;
        }
    };

    private void verifyOTP(String cred) {
        PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(verificationID, cred);
        signInByCredentials(authCredential);
    }

    private void signInByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth signInCred = FirebaseAuth.getInstance();
        signInCred.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        addUserToFirestore();
                    }
                    else{
                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(DriverOTP.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //add driver details into firestore
    private void addUserToFirestore(){
        FirebaseFirestore drivmeDB = FirebaseFirestore.getInstance();
        String docID = getIntent().getStringExtra("dID");
        String referenceCode = getIntent().getStringExtra("dRefCode");

        //update reference code details and insert driver details into firestore
        Map<String,Object> refCode = new HashMap<>();
        refCode.put("refCode", referenceCode);
        refCode.put("driverID", docID);
        refCode.put("status", "N/A");
        refCode.put("driverName",getIntent().getStringExtra("dLName") + " " + getIntent().getStringExtra("dFName"));

        //driver details to insert into firestore
        Map<String,Object> driverAcc = new HashMap<>();
        driverAcc.put("userID", docID);
        driverAcc.put("firstName", getIntent().getStringExtra("dFName"));
        driverAcc.put("lastName", getIntent().getStringExtra("dLName"));
        driverAcc.put("phoneNumber", getIntent().getStringExtra("dPhoneNumber"));
        driverAcc.put("email", getIntent().getStringExtra("dEmail"));
        driverAcc.put("password", getIntent().getStringExtra("dPassword"));
        driverAcc.put("Login Status Tourist", 0);
        driverAcc.put("Login Status Driver", 0);
        driverAcc.put("Account Tourist", 0);
        driverAcc.put("Account Driver", 1);
        driverAcc.put("Agreement Check", 1);
        driverAcc.put("rating", 5);
        driverAcc.put("5 Stars", 1);
        driverAcc.put("4 Stars", 0);
        driverAcc.put("3 Stars", 0);
        driverAcc.put("2 Stars", 0);
        driverAcc.put("1 Star", 0);
        driverAcc.put("priceDay", 300);
        driverAcc.put("priceHour", 15);
        driverAcc.put("drivPay", "0.00");

        drivmeDB.collection("Reference Code Details").document(referenceCode)
                .update(refCode);

        drivmeDB.collection("User Accounts").document(docID)
                .set(driverAcc)
                .addOnSuccessListener(unused -> {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverOTP.this);
                    alertDialogBuilder.setTitle("Created Account Successfully!");
                    alertDialogBuilder
                            .setMessage("Let's try to login!")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                startActivity(new Intent(DriverOTP.this, DriverLogin.class));
                                finishAffinity();
                                finish();
                            });

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                })
                .addOnFailureListener(e -> {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverOTP.this);
                    alertDialogBuilder.setTitle("Fail to create account!");
                    alertDialogBuilder
                            .setMessage("Please try again!")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    (dialog, id) -> {
                                        startActivity(new Intent(DriverOTP.this, DriverSignUp.class));
                                        finishAffinity();
                                        finish();
                                    });

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                });
    }

    public void resendDOTP() {
        mtvDriverResend.setOnClickListener(view1 -> {
            String numD = getIntent().getStringExtra("dPhoneNumber");
            sendOTPtoDriverPhone(numD);
            mtvDriverResend.setVisibility(View.GONE);
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
                    mtvDriverResend.setVisibility(View.VISIBLE);
                }
            }.start();
        });
    }
}