package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DriverOTP extends AppCompatActivity {
    //declare variables
    TextView mtvDriverPhoneText;
    PinView mpvDOTP;
    Button mbtnDriverVerify;
    FirebaseAuth mAuth;
    String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_otp);

        //obtaining the View with specific ID
        mtvDriverPhoneText = findViewById(R.id.tvDriverPhoneText);
        mpvDOTP = findViewById(R.id.pvDriverOTP);
        mbtnDriverVerify = findViewById(R.id.btnDriverVerify);

        //return instance of the class
        mAuth = FirebaseAuth.getInstance();

        //get driver phone number
        String phNumD = getIntent().getStringExtra("dPhoneNumber");
        mtvDriverPhoneText.setText(phNumD);

        //send otp
        sendOTP(phNumD);

        mbtnDriverVerify.setOnClickListener(v -> {
            String value = mpvDOTP.getText().toString();
            if(value.isEmpty()){
                Toast.makeText(DriverOTP.this, "Invalid OTP Code!", Toast.LENGTH_SHORT).show();
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
            String codeD = credential.getSmsCode();
            mpvDOTP.setText(codeD);

            if(codeD != null){
                verifyCode(codeD);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(DriverOTP.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            Toast.makeText(DriverOTP.this, "OTP sent!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(DriverOTP.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //add driver details into firestore
    private void addInfoToFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String value = getIntent().getStringExtra("dID");
        String code = getIntent().getStringExtra("dRefCode");

        //update reference code details and insert driver details into firestore
        Map<String,Object> refCode = new HashMap<>();
        refCode.put("Driver ID", value);
        refCode.put("Status", "N/A");

        //driver details to insert into firestore
        Map<String,Object> driverAcc = new HashMap<>();
        driverAcc.put("User ID", value);
        driverAcc.put("First Name", getIntent().getStringExtra("dFName"));
        driverAcc.put("Last Name", getIntent().getStringExtra("dLName"));
        driverAcc.put("Phone Number", getIntent().getStringExtra("dPhoneNumber"));
        driverAcc.put("Email", getIntent().getStringExtra("dEmail"));
        driverAcc.put("Password", getIntent().getStringExtra("dPassword"));
        driverAcc.put("Login Status Tourist", 0);
        driverAcc.put("Login Status Driver", 0);
        driverAcc.put("Account Tourist", 0);
        driverAcc.put("Account Driver", 1);

        db.collection("Reference Code Details").document(code)
                .update(refCode);

        db.collection("User Accounts").document(value)
                .set(driverAcc)
                .addOnSuccessListener(unused -> {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverOTP.this);
                    alertDialogBuilder.setTitle("Created Account Successfully!");
                    alertDialogBuilder
                            .setMessage("Let's try to login!")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                startActivity(new Intent(DriverOTP.this, DriverLogin.class));
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
                                        finish();
                                    });

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                });
    }

    public void resendDOTP(View view) {
        String numD = getIntent().getStringExtra("dPhoneNumber");
        sendOTP(numD);
    }
}