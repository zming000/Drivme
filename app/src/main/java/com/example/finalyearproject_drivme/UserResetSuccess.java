package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserResetSuccess extends AppCompatActivity {
    //declare variable
    Button mbtnGotoLogin, mbtnGotoSetting;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";
    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reset_success);

        //assign variable
        mbtnGotoLogin = findViewById(R.id.btnGotoLogin);
        mbtnGotoSetting = findViewById(R.id.btnGotoSetting);

        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);
        String uRole = spDrivme.getString(KEY_ROLE, null);

        if(uID != null){
            mbtnGotoSetting.setVisibility(View.VISIBLE);
            mbtnGotoLogin.setVisibility(View.GONE);
        }
        else{
            mbtnGotoSetting.setVisibility(View.GONE);
            mbtnGotoLogin.setVisibility(View.VISIBLE);
        }

        mbtnGotoSetting.setOnClickListener(view -> {
            if (uRole.equals("Tourist")) {
                startActivity(new Intent(UserResetSuccess.this, TouristNavSettings.class));
            }
            else{
                startActivity(new Intent(UserResetSuccess.this, DriverNavSettings.class));
            }
            finishAffinity();
            finish();
        });

        mbtnGotoLogin.setOnClickListener(view -> {
            String role = getIntent().getStringExtra("role");

            //check condition on the role
            if(role.equals("Tourist")){
                startActivity(new Intent(UserResetSuccess.this, TouristLogin.class));
            } else{
                startActivity(new Intent(UserResetSuccess.this, DriverLogin.class));
            }
            finishAffinity();
            finish();
        });
    }

    //Reset Password -> login
    @Override
    public void onBackPressed() {
        String role = getIntent().getStringExtra("role");
        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);
        String uRole = spDrivme.getString(KEY_ROLE, null);

        if(uID != null) {
            if (uRole.equals("Tourist")) {
                startActivity(new Intent(UserResetSuccess.this, TouristNavSettings.class));
            }
            else{
                startActivity(new Intent(UserResetSuccess.this, DriverNavSettings.class));
            }
        }
        else {
            //check condition on the role
            if(role.equals("Tourist")){
                startActivity(new Intent(UserResetSuccess.this, TouristLogin.class));
            } else{
                startActivity(new Intent(UserResetSuccess.this, DriverLogin.class));
            }
        }
        finishAffinity();
        finish();
    }
}