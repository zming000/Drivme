package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class TouristReload extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilReload;
    TextInputEditText metReload;
    CardView mcv100, mcv300, mcv500, mcvCard, mcvOnline;
    TextView mtvCard, mtvOnline;
    Button mbtnReload;
    SharedPreferences spDrivme;
    FirebaseFirestore getPay, updatePay;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_reload);

        //assign variables
        mtilReload = findViewById(R.id.tilReload);
        metReload = findViewById(R.id.etReload);
        mcv100 = findViewById(R.id.cv100);
        mcv300 = findViewById(R.id.cv300);
        mcv500 = findViewById(R.id.cv500);
        mcvCard = findViewById(R.id.cvCard);
        mcvOnline = findViewById(R.id.cvOnline);
        mtvCard = findViewById(R.id.tvCard);
        mtvOnline = findViewById(R.id.tvOnline);
        mbtnReload = findViewById(R.id.btnReload);

        //initialize shared preferences
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        metReload.addTextChangedListener(new MoneyTextWatcher(metReload));
        metReload.setText("0.00");

        mcv100.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcv300.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcv500.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcvCard.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcvOnline.setCardBackgroundColor(Color.parseColor("#E6E6E6"));

        mcv100.setOnClickListener(view -> {
            metReload.setText("100.00");
        });

        mcv300.setOnClickListener(view -> {
            metReload.setText("300.00");
        });

        mcv500.setOnClickListener(view -> {
            metReload.setText("500.00");
        });

        mcvCard.setOnClickListener(view -> {
            //set card view color
            mcvCard.setCardBackgroundColor(Color.parseColor("#0F7D63"));
            mcvOnline.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            //set text color
            mtvCard.setTextColor(Color.WHITE);
            mtvOnline.setTextColor(Color.parseColor("#0F7D63"));
        });

        mcvOnline.setOnClickListener(view -> {
            //set card view color
            mcvCard.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            mcvOnline.setCardBackgroundColor(Color.parseColor("#0F7D63"));
            //set text color
            mtvCard.setTextColor(Color.parseColor("#0F7D63"));
            mtvOnline.setTextColor(Color.WHITE);
        });

        metReload.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilReload.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mbtnReload.setOnClickListener(view -> {
            String tID = spDrivme.getString(KEY_ID, null);
            float amount = Float.parseFloat(metReload.getText().toString());
            getPay = FirebaseFirestore.getInstance();
            updatePay = FirebaseFirestore.getInstance();

            if(amount >= 10){
                if(mtvCard.getCurrentTextColor() == -1){
                    getPay.collection("User Accounts").document(tID).get()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                   DocumentSnapshot doc = task.getResult();
                                   float pay = Float.parseFloat(doc.getString("drivPay"));
                                   float newPay = amount + pay;

                                    Map<String,Object> updateTotal = new HashMap<>();
                                    updateTotal.put("drivPay", String.format("%.2f", newPay));

                                    updatePay.collection("User Accounts").document(tID)
                                            .update(updateTotal)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(TouristReload.this, "Reloaded Successfully!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(TouristReload.this, TouristNavHomepage.class));
                                                finishAffinity();
                                                finish();
                                            });
                                }
                            });
                }
                else if(mtvOnline.getCurrentTextColor() == -1){
                    getPay.collection("User Accounts").document(tID).get()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    DocumentSnapshot doc = task.getResult();
                                    float pay = Float.parseFloat(doc.getString("drivPay"));
                                    float newPay = amount + pay;

                                    Map<String,Object> updateTotal = new HashMap<>();
                                    updateTotal.put("drivPay", String.format("%.2f", newPay));

                                    updatePay.collection("User Accounts").document(tID)
                                            .update(updateTotal)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(TouristReload.this, "Reloaded Successfully!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(TouristReload.this, TouristNavHomepage.class));
                                                finishAffinity();
                                                finish();
                                            });
                                }
                            });
                }
                else{
                    Toast.makeText(TouristReload.this, "Please choose a reload method!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                mtilReload.setError("Min reload amount is RM 10!");
            }
        });
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Discard Reload");
        alertDialogBuilder
                .setMessage("Are you sure to discard reload?")
                .setCancelable(false)
                .setPositiveButton("Discard", (dialog, id) -> {
                    startActivity(new Intent(TouristReload.this, TouristNavHomepage.class));
                    finishAffinity();
                    finish();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

