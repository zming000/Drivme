package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

public class UserAgreementPolicy extends AppCompatActivity {
    //declare variables
    Button mbtnAgree;
    CheckBox mcbTNC;
    String character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement_policy);

        //assign variables
        mcbTNC = findViewById(R.id.cbTNC);
        mbtnAgree = findViewById(R.id.btnAgree);

        mbtnAgree.setEnabled(false);

        //enable button when checked
        mcbTNC.setOnClickListener(view -> mbtnAgree.setEnabled(mcbTNC.isChecked()));

        mbtnAgree.setOnClickListener(view -> {
            character = getIntent().getStringExtra("role");
            //agreement -> signup (based on the role from login)
            if(character.equals("Tourist")) {
                startActivity(new Intent(UserAgreementPolicy.this, TouristSignUp.class));
            }
            else{
                startActivity(new Intent(UserAgreementPolicy.this, DriverSignUp.class));
            }
            finish();
        });
    }

    //user agreement dialog
    public void toAgreement(View agreementView) {
        //initialize layout
        LayoutInflater dialogInflater = getLayoutInflater();
        agreementView = dialogInflater.inflate(R.layout.activity_user_agreement_popup, null);

        //initialize dialog builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
        AlertDialog agreementDialog = dialogBuilder.setView(agreementView).create();

        //assign variables
        ImageView mivClose = agreementView.findViewById(R.id.ivClose);

        mivClose.setOnClickListener(view1 -> agreementDialog.dismiss());

        //display dialog with suitable size
        agreementDialog.show();
        agreementDialog.getWindow().setLayout(650, 1100);
    }

    //privacy policy dialog
    public void toPolicy(View policyView) {
        //initialize layout
        LayoutInflater dialogInflater = getLayoutInflater();
        policyView = dialogInflater.inflate(R.layout.activity_privacy_policy_popout, null);

        //initialize dialog builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
        AlertDialog policyDialog = dialogBuilder.setView(policyView).create();

        //assign variables
        ImageView mivCloseP = policyView.findViewById(R.id.ivCloseP);

        mivCloseP.setOnClickListener(view1 -> policyDialog.dismiss());

        //display dialog with suitable size
        policyDialog.show();
        policyDialog.getWindow().setLayout(650, 1100);
    }
}