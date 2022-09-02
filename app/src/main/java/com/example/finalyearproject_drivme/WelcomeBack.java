package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class WelcomeBack extends AppCompatActivity {
    //declare variables
    TextView mtvHello;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_FNAME = "fName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_back);

        //obtaining the View with specific ID
        mtvHello = findViewById(R.id.tvHello);

        //initialize shared preference
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //get data from shared preference
        String getName = spDrivme.getString(KEY_FNAME, null);

        //set name
        if(getName != null){
            mtvHello.setText("Hello, " + getName);
        }

        //display a loading screen for 2 second
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //go to main
            startActivity(new Intent(WelcomeBack.this, Role.class));
            finish();
        },2000);
    }
}