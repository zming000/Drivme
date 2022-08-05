package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TouristSignUp extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_sign_up);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TouristSignUp.this, TouristLogin.class));
        finish();
    }

    public void loginTourist(View view) {
        startActivity(new Intent(TouristSignUp.this, TouristLogin.class));
        finish();
    }
}