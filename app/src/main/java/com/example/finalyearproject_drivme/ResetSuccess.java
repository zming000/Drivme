package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ResetSuccess extends AppCompatActivity {
    //declare variable
    Button mbtnGotoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_success);

        //assign variable
        mbtnGotoLogin = findViewById(R.id.btnGotoLogin);

        mbtnGotoLogin.setOnClickListener(view -> {
            String role = getIntent().getStringExtra("role");

            //check condition on the role
            if(role.equals("Tourist")){
                startActivity(new Intent(ResetSuccess.this, TouristLogin.class));
            } else{
                startActivity(new Intent(ResetSuccess.this, DriverLogin.class));
            }
            finishAffinity();
            finish();
        });
    }

    //Reset Password -> login
    @Override
    public void onBackPressed() {
        String role = getIntent().getStringExtra("role");

        //check condition on the role
        if(role.equals("Tourist")){
            startActivity(new Intent(ResetSuccess.this, TouristLogin.class));
        } else{
            startActivity(new Intent(ResetSuccess.this, DriverLogin.class));
        }
        finishAffinity();
        finish();
    }
}