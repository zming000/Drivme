package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

public class AgreementPolicy extends AppCompatActivity {
    //declare variables
    Dialog agreementDialog, policyDialog;
    Button mbtnAgree;
    CheckBox mcbTNC;
    String character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_policy);

        //Initialize dialog
        agreementDialog = new Dialog(this);
        policyDialog = new Dialog(this);

        //obtaining the View with specific ID
        mcbTNC = findViewById(R.id.cbTNC);
        mbtnAgree = findViewById(R.id.btnAgree);

        mbtnAgree.setEnabled(false);

        //enable button when checked
        mcbTNC.setOnClickListener(view -> mbtnAgree.setEnabled(mcbTNC.isChecked()));

        mbtnAgree.setOnClickListener(view -> {
            character = getIntent().getStringExtra("role");
            //agreement -> signup (based on the role from login)
            if(character.equals("Tourist")) {
                startActivity(new Intent(AgreementPolicy.this, TouristSignUp.class));
            }
            else{
                startActivity(new Intent(AgreementPolicy.this, DriverSignUp.class));
            }
            finish();
        });
    }

    //user agreement dialog
    public void toAgreement(View view) {
        agreementDialog.setContentView(R.layout.activity_user_agreement_popup);

        ImageView mivClose = agreementDialog.findViewById(R.id.ivClose);

        mivClose.setOnClickListener(view1 -> agreementDialog.dismiss());

        agreementDialog.show();
    }

    //privacy policy dialog
    public void toPolicy(View view) {
        policyDialog.setContentView(R.layout.activity_privacy_policy_popout);

        ImageView mivCloseP = policyDialog.findViewById(R.id.ivCloseP);

        mivCloseP.setOnClickListener(view1 -> policyDialog.dismiss());

        policyDialog.show();
    }
}