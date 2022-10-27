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

import java.util.HashMap;
import java.util.Map;
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
                                        String accountState = docResult.getString("accountStatus");

                                        if(accountState.equals("Suspended")){
                                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
                                            alertDialogBuilder.setTitle("Account Suspended");
                                            alertDialogBuilder
                                                    .setMessage("Your account have been suspended!\nPlease check your email or contact Drivme support for further information.")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", (dialog, iD) -> {
                                                        spDrivme.edit().clear().commit();

                                                        startActivity(new Intent(getApplicationContext(), UserRole.class));
                                                        dialog.dismiss();
                                                        finishAffinity();
                                                        finish();
                                                    });

                                            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.show();
                                        }
                                        else {
                                            int loginStat = Objects.requireNonNull(docResult.getLong("loginStatusTourist")).intValue();

                                            if (loginStat == 0) {
                                                startActivity(new Intent(SplashActivity.this, UserWelcomeTo.class));
                                            } else {
                                                startActivity(new Intent(SplashActivity.this, UserWelcomeBack.class));
                                            }

                                            overridePendingTransition(0, 0);
                                            finishAffinity();
                                            finish();
                                        }

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
                                        int loginStat = Objects.requireNonNull(docResult.getLong("loginStatusDriver")).intValue();

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