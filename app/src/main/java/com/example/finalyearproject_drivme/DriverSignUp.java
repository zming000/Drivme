package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DriverSignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_up);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(DriverSignUp.this, DriverOption.class));
        finish();
    }

    public void loginDriver(View view) {
        startActivity(new Intent(DriverSignUp.this, DriverLogin.class));
        finish();
    }
}