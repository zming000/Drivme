package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class TouristCarDetails extends AppCompatActivity {
    //declare variables
    TextView mtvTCarPlate, mtvTCarModel, mtvTCarColour, mtvTCarTrans, mtvTPetrolCompany, mtvTPetrolType;
    Button mbtnDelete;
    FirebaseFirestore carDB, checkCar, deleteCar;
    ArrayList<String> carID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_car_details);

        //assign variables
        mtvTCarPlate = findViewById(R.id.tvTCarPlate);
        mtvTCarModel = findViewById(R.id.tvTCarModel);
        mtvTCarColour = findViewById(R.id.tvTCarColour);
        mtvTCarTrans = findViewById(R.id.tvTCarTrans);
        mtvTPetrolCompany = findViewById(R.id.tvTPetrolCompany);
        mtvTPetrolType = findViewById(R.id.tvTPetrolType);
        mbtnDelete = findViewById(R.id.btnDelete);

        //initialize
        carDB = FirebaseFirestore.getInstance();
        checkCar = FirebaseFirestore.getInstance();
        deleteCar = FirebaseFirestore.getInstance();

        String uID = getIntent().getStringExtra("id");
        String cP = getIntent().getStringExtra("carPlate");

        carDB.collection("User Accounts").document(uID).collection("Car Details").document(cP).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();

                        mtvTCarPlate.setText(cP);
                        mtvTCarModel.setText(doc.getString("carModel"));
                        mtvTCarColour.setText(doc.getString("carColour"));
                        mtvTCarTrans.setText(doc.getString("carTransmission"));
                        mtvTPetrolCompany.setText(doc.getString("petrolCompany"));
                        mtvTPetrolType.setText(doc.getString("petrolType"));
                    }
                });

        mbtnDelete.setOnClickListener(view -> {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristCarDetails.this);
            alertDialogBuilder.setTitle("Delete Car");
            alertDialogBuilder
                    .setMessage("Do you wish to delete this car?")
                    .setCancelable(false)
                    .setPositiveButton("Delete Car", (dialog, id) -> manageCar(uID, cP))
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        });
    }

    private void manageCar(String touristID, String carPlate) {
        checkCar.collection("Trip Details")
                .whereEqualTo("touristID", touristID)
                .whereIn("orderStatus", Arrays.asList("Pending Driver Accept", "Pending Tourist Payment", "Coming Soon", "Trip Ongoing"))
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(TouristCarDetails.this, "Error Loading Car!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    carID.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            carID.add(dc.getDocument().getId());
                        }
                    }

                    //if no records found
                    if(carID.size() == 0){
                        deleteCar.collection("User Accounts").document(touristID).collection("Car Details").document(carPlate)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(TouristCarDetails.this, "Car Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(TouristCarDetails.this, TouristNavCars.class));
                                    finishAffinity();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(TouristCarDetails.this, "Unable to delete car!", Toast.LENGTH_SHORT).show());
                    }
                    else{
                        Toast.makeText(TouristCarDetails.this, "Unable to delete car! Car in used!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}