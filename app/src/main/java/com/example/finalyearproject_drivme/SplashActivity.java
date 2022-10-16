package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    //declare variables
    Animation rotateAnim, topAnim, btmAnim;
    ImageView ivWheel;
    TextView mtvDriv, mtvSlogan;
    FirebaseFirestore drivmeDB;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";
    private static final String KEY_ROLE = "role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //assign variables
        ivWheel = findViewById(R.id.wheelImg);
        mtvDriv = findViewById(R.id.tvDriv);
        mtvSlogan = findViewById(R.id.tvSlogan);

        //load animation
        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        btmAnim = AnimationUtils.loadAnimation(this, R.anim.btm_anim);

        //add animation into the widgets
        AnimationSet setAnim = new AnimationSet(false);//do not share interpolators if false
        setAnim.addAnimation(rotateAnim);
        setAnim.addAnimation(topAnim);
        ivWheel.startAnimation(setAnim);
        mtvDriv.startAnimation(btmAnim);
        mtvSlogan.startAnimation(btmAnim);

        //display a loading screen for 3 second
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            drivmeDB = FirebaseFirestore.getInstance();
            spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

            //check shared preference whether user logged out
            //get user id from shared preference
            String uID = spDrivme.getString(KEY_ID, null);
            String uRole = spDrivme.getString(KEY_ROLE, null);

            //check if user signed in or not
            if(uID != null){
                if(uRole.equals("Tourist")) {
                    drivmeDB.collection("User Accounts").document(uID).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot docResult = task.getResult();

                                    if (docResult != null) {
                                        int loginStat = Objects.requireNonNull(docResult.getLong("Login Status Tourist")).intValue();

                                        if (loginStat == 0){
                                            startActivity(new Intent(SplashActivity.this, UserWelcomeTo.class));
                                        }
                                        else{
                                            startActivity(new Intent(SplashActivity.this, UserWelcomeBack.class));
                                        }

                                        overridePendingTransition(0, 0);
                                        finish();
                                    }
                                }
                            });
                }
                else{
                    drivmeDB.collection("User Accounts").document(uID).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot docResult = task.getResult();

                                    if (docResult != null) {
                                        int loginStat = Objects.requireNonNull(docResult.getLong("Login Status Driver")).intValue();

                                        if (loginStat == 0){
                                            startActivity(new Intent(SplashActivity.this, UserWelcomeTo.class));
                                        }
                                        else{
                                            startActivity(new Intent(SplashActivity.this, UserWelcomeBack.class));
                                        }

                                        overridePendingTransition(0, 0);
                                        finish();
                                    }
                                }
                            });
                }
            }else{
                startActivity(new Intent(SplashActivity.this, UserRole.class));
                overridePendingTransition(0, 0);
                finish();
            }
        },3000);
    }
}