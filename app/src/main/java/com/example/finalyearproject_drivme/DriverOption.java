package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class DriverOption extends AppCompatActivity {
    //declare variables
    Button mbtnJoinUs, mbtnDriverLogin, mbtnDriverSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_option);

        //assign variables
        mbtnJoinUs = findViewById(R.id.btnJoinUs);
        mbtnDriverLogin = findViewById(R.id.btnDriverLogin);
        mbtnDriverSignup = findViewById(R.id.btnDriverSignUp);

        mbtnJoinUs.setOnClickListener(v -> {
            //go to google form
        });

        mbtnDriverLogin.setOnClickListener(v -> {
            //driver option -> driver login
            startActivity(new Intent(DriverOption.this, DriverLogin.class));
            finish();
        });

        mbtnDriverSignup.setOnClickListener(v -> {
            //driver option -> driver sign up
            startActivity(new Intent(DriverOption.this, DriverSignUp.class));
            finish();
        });
    }

    //driver option -> role
    @Override
    public void onBackPressed() {
        startActivity(new Intent(DriverOption.this, Role.class));
        finish();
    }
}