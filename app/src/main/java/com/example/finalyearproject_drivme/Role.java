package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Role extends AppCompatActivity {

//    Animation topAnim, btmAnim;
//    CardView mcvDriver, mcvTourist;
//    ImageView mivLogo;
//    TextView mtvChoose;
    Button mbtnDriver, mbtnTourist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

//        mivLogo = findViewById(R.id.logo);
//        mtvChoose = findViewById(R.id.tvChoose);
//        mcvDriver = findViewById(R.id.cvDriver);
//        mcvTourist = findViewById(R.id.cvTourist);
        mbtnDriver = findViewById(R.id.btnDriver);
        mbtnTourist = findViewById(R.id.btnTourist);

//        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
//        btmAnim = AnimationUtils.loadAnimation(this, R.anim.btm_anim);
//
//        mivLogo.startAnimation(topAnim);
//        mtvChoose.startAnimation(topAnim);
//        mcvDriver.startAnimation(btmAnim);
//        mcvTourist.startAnimation(btmAnim);

        mbtnDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Role.this, DriverOption.class));
                finish();
            }
        });

        mbtnTourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Role.this, TouristLogin.class));
                finish();
            }
        });
    }

    //ask before closing application
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Leaving Drivme?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}