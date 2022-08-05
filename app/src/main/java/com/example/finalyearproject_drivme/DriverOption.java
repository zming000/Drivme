package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DriverOption extends AppCompatActivity {

    Button mbtnJoinUs, mbtnDriverLogin, mbtnDriverSignup;
//    Animation topAnim, btmAnim;
//    ImageView mSideLogo, mDriverLogo;
//    TextView mtvSchedule, mtvDriverLogin, mtvDriverSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_option);

        mbtnJoinUs = findViewById(R.id.btnJoinUs);
        mbtnDriverLogin = findViewById(R.id.btnDriverLogin);
        mbtnDriverSignup = findViewById(R.id.btnDriverSignUp);
//        mSideLogo = findViewById(R.id.ivSideLogo);
//        mDriverLogo = findViewById(R.id.ivDriver);
//        mtvSchedule = findViewById(R.id.tvSchedule);
//        mtvDriverLogin = findViewById(R.id.tvLogin);
//        mtvDriverSignup = findViewById(R.id.tvSignUp);

//        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
//        btmAnim = AnimationUtils.loadAnimation(this, R.anim.btm_anim);

//        mSideLogo.setAnimation(topAnim);
//        mDriverLogo.setAnimation(topAnim);
//        mbtnJoinUs.setAnimation(btmAnim);
//        mbtnDriverLogin.setAnimation(btmAnim);
//        mbtnDriverSignup.setAnimation(btmAnim);
//        mtvSchedule.setAnimation(btmAnim);
//        mtvDriverLogin.setAnimation(btmAnim);
//        mtvDriverSignup.setAnimation(btmAnim);

        mbtnJoinUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to google form
            }
        });

        mbtnDriverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to driver login
                startActivity(new Intent(DriverOption.this, DriverLogin.class));
                finish();
            }
        });

        mbtnDriverSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to driver sign up
                startActivity(new Intent(DriverOption.this, DriverSignUp.class));
                finish();
            }
        });
    }

    //back to driver option
    @Override
    public void onBackPressed() {
        startActivity(new Intent(DriverOption.this, Role.class));
        finish();
    }
}