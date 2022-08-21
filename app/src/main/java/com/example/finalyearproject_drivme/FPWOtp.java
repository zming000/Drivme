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

import java.util.concurrent.TimeUnit;

public class FPWOtp extends AppCompatActivity {
    //declare variables
    TextView mtvFPWPhoneText;
    PinView mpvFPWOtp;
    Button mbtnFPWVerify;
    FirebaseAuth mAuth;
    String verificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpw_otp);

        //obtaining the View with specific ID
        mtvFPWPhoneText = findViewById(R.id.tvFPWPhoneText);
        mpvFPWOtp = findViewById(R.id.pvFPWOtp);
        mbtnFPWVerify = findViewById(R.id.btnFPWVerify);

        //return instance of the class
        mAuth = FirebaseAuth.getInstance();

        //get phone number
        String phNum = getIntent().getStringExtra("phNum");
        mtvFPWPhoneText.setText(phNum);

        //send otp
        sendOTP(phNum);

        mbtnFPWVerify.setOnClickListener(v -> {
            String value = mpvFPWOtp.getText().toString();
            if(value.isEmpty()){
                Toast.makeText(FPWOtp.this, "Invalid OTP Code!", Toast.LENGTH_SHORT).show();
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
            mpvFPWOtp.setText(code);

            if(code != null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(FPWOtp.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            Toast.makeText(FPWOtp.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(FPWOtp.this, ResetPW.class);
                        intent.putExtra("ID", getIntent().getStringExtra("id"));
                        intent.putExtra("Character", getIntent().getStringExtra("character"));

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(FPWOtp.this, "Verification Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void resendFPWOtp(View view) {
        String num = getIntent().getStringExtra("phNum");
        sendOTP(num);
    }
}