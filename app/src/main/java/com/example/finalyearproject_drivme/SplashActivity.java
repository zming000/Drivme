package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    //declare variables
    Animation rotateAnim, topAnim, btmAnim;
    ImageView ivWheel;
    TextView mtvDriv, mtvSlogan;
    SharedPreferences spDrivme;
    FirebaseFirestore drivmeDB, updateToken;
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
            //get username from shared preference
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
                                            startActivity(new Intent(SplashActivity.this, WelcomeTo.class));
                                        }
                                        else{
                                            startActivity(new Intent(SplashActivity.this, WelcomeBack.class));
                                        }

//                                        FirebaseMessaging.getInstance().getToken()
//                                                .addOnCompleteListener(new OnCompleteListener<String>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<String> task) {
//                                                        if (!task.isSuccessful()) {
//                                                            return;
//                                                        }
//                                                        // Get new FCM registration token
//                                                        String token = task.getResult();
//                                                        updateToken = FirebaseFirestore.getInstance();
//
//                                                        Map<String,Object> noToken = new HashMap<>();
//                                                        noToken.put("notificationToken", token);
//
//                                                        updateToken.collection("User Accounts").document(uID)
//                                                                .update(noToken);
//                                                    }
//                                                });

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
                                            startActivity(new Intent(SplashActivity.this, WelcomeTo.class));
                                        }
                                        else{
                                            startActivity(new Intent(SplashActivity.this, WelcomeBack.class));
                                        }

                                        FirebaseMessaging.getInstance().getToken()
                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<String> task) {
                                                        if (!task.isSuccessful()) {
                                                            return;
                                                        }
                                                        // Get new FCM registration token
                                                        String token = task.getResult();
                                                        updateToken = FirebaseFirestore.getInstance();

                                                        Map<String,Object> noToken = new HashMap<>();
                                                        noToken.put("notificationToken", token);

                                                        updateToken.collection("User Accounts").document(uID)
                                                                .update(noToken);
                                                    }
                                                });

                                        overridePendingTransition(0, 0);
                                        finish();
                                    }
                                }
                            });
                }
            }else{
                startActivity(new Intent(SplashActivity.this, Role.class));
                overridePendingTransition(0, 0);
                finish();
            }
        },3000);
    }
}