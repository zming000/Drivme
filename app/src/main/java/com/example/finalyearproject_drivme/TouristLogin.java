package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TouristLogin extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_login);
    }

    public void googleLogin(View view) {
    }

    public void signupTourist(View view) {
        startActivity(new Intent(TouristLogin.this, TouristSignUp.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TouristLogin.this, Role.class));
        finish();
    }
}