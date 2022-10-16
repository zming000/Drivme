package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class TouristBookSuccess extends AppCompatActivity {
    Button mbtnGotoActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_book_success);

        mbtnGotoActivity = findViewById(R.id.btnGotoActivity);

        mbtnGotoActivity.setOnClickListener(view -> {
            startActivity(new Intent(TouristBookSuccess.this, TouristNavActivity.class));
            finishAffinity();
            finish();
        });
    }

    //Book Success -> Activity
    @Override
    public void onBackPressed() {
        startActivity(new Intent(TouristBookSuccess.this, TouristNavActivity.class));
        finishAffinity();
        finish();
    }
}