package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AgreementPolicy extends AppCompatActivity {
    Dialog agreementDialog, policyDialog;
    Button mbtnAgree;
    CheckBox mcbTNC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_policy);

        agreementDialog = new Dialog(this);
        policyDialog = new Dialog(this);

        mcbTNC = findViewById(R.id.cbTNC);
        mbtnAgree = findViewById(R.id.btnAgree);

        mbtnAgree.setEnabled(false);

        mcbTNC.setOnClickListener(view -> {
            if(mcbTNC.isChecked()){
                mbtnAgree.setEnabled(true);
            }
            else{
                mbtnAgree.setEnabled(false);
            }
        });


        mbtnAgree.setOnClickListener(view -> {
            //database

        });
    }

    public void toAgreement(View view) {
        agreementDialog.setContentView(R.layout.activity_user_agreement_popup);

        ImageView mivClose = agreementDialog.findViewById(R.id.ivClose);

        mivClose.setOnClickListener(view1 -> {
            agreementDialog.dismiss();
        });

        agreementDialog.show();
    }

    public void toPolicy(View view) {
        policyDialog.setContentView(R.layout.activity_privacy_policy_popout);

        ImageView mivCloseP = policyDialog.findViewById(R.id.ivCloseP);

        mivCloseP.setOnClickListener(view1 -> {
            policyDialog.dismiss();
        });

        policyDialog.show();
    }
}