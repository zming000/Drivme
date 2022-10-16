package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TouristCarDetails extends AppCompatActivity {
    //declare variables
    TextView mtvTCarPlate, mtvTCarStatus, mtvTCarModel, mtvTCarColour, mtvTCarTrans, mtvTPetrolCompany, mtvTPetrolType;
    Button mbtnDelete;
    FirebaseFirestore carDB, deleteCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_car_details);

        mtvTCarPlate = findViewById(R.id.tvTCarPlate);
        mtvTCarStatus = findViewById(R.id.tvTCarStatus);
        mtvTCarModel = findViewById(R.id.tvTCarModel);
        mtvTCarColour = findViewById(R.id.tvTCarColour);
        mtvTCarTrans = findViewById(R.id.tvTCarTrans);
        mtvTPetrolCompany = findViewById(R.id.tvTPetrolCompany);
        mtvTPetrolType = findViewById(R.id.tvTPetrolType);
        mbtnDelete = findViewById(R.id.btnDelete);

        carDB = FirebaseFirestore.getInstance();
        deleteCar = FirebaseFirestore.getInstance();

        String uID = getIntent().getStringExtra("id");
        String cP = getIntent().getStringExtra("carPlate");

        carDB.collection("User Accounts").document(uID).collection("Car Details").document(cP).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();

                        mtvTCarPlate.setText(cP);
                        mtvTCarStatus.setText(doc.getString("carStatus"));
                        mtvTCarModel.setText(doc.getString("carModel"));
                        mtvTCarColour.setText(doc.getString("carColour"));
                        mtvTCarTrans.setText(doc.getString("carTransmission"));
                        mtvTPetrolCompany.setText(doc.getString("petrolCompany"));
                        mtvTPetrolType.setText(doc.getString("petrolType"));
                    }
                });

        mbtnDelete.setOnClickListener(view -> {
            if(mtvTCarStatus.getText().toString().equals("N/A")){
                Toast.makeText(TouristCarDetails.this, "Car is not available to delete!", Toast.LENGTH_SHORT).show();
            }
            else {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristCarDetails.this);
                alertDialogBuilder.setTitle("Delete Car");
                alertDialogBuilder
                        .setMessage("Do you wish to delete this car?")
                        .setCancelable(false)
                        .setPositiveButton("Delete Car", (dialog, id) -> {
                            deleteCar.collection("User Accounts").document(uID).collection("Car Details").document(cP)
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(TouristCarDetails.this, "Car Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(TouristCarDetails.this, TouristNavCars.class));
                                        finishAffinity();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(TouristCarDetails.this, "Unable to delete car!", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }
}