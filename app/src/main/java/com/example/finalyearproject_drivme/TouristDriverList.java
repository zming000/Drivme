package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class TouristDriverList extends AppCompatActivity {
    //declare variables
    RecyclerView mrvDriver;
    ArrayList<ModelDriverList> mdl;
    AdapterDriverList dlAdapter;
    FirebaseFirestore userDB;
    String driverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_driver_list);

        //assign variables
        mrvDriver = findViewById(R.id.rvDriver);
        mrvDriver.setLayoutManager(new LinearLayoutManager(this));

        //initialize varaibles
        userDB = FirebaseFirestore.getInstance();
        mdl = new ArrayList<>();
        dlAdapter = new AdapterDriverList(TouristDriverList.this, mdl);

        mrvDriver.setAdapter(dlAdapter);

        getDetailsFromFirestore();
    }

    /*check available driver that fulfill requirement*/
    private void getDetailsFromFirestore() {
        //declare variables
        int numDate = getIntent().getIntExtra("duration", 0);
        String startDate = getIntent().getStringExtra("dateID");

        //initialize
        ArrayList<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Calendar c = Calendar.getInstance();

        //add start date
        dates.add(startDate);

        //calculate and add dates into arraylist
        for(int i = 0; i < numDate; i++) {
            //calculate end date with duration
            try {
                c.setTime(Objects.requireNonNull(sdf.parse(dates.get(i))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //add 1 day
            c.add(Calendar.DATE, 1);

            Date resultDate = new Date(c.getTimeInMillis());
            dates.add(sdf.format(resultDate));
        }

        //search for driver that fulfill requirements with whereEqualTo
        userDB.collection("User Accounts")
                .whereEqualTo("Account Driver", 1)
                .whereEqualTo("Login Status Driver", 1)
                .whereEqualTo("accountStatus", "Driver")
                .whereEqualTo("state", getIntent().getStringExtra("state"))
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(TouristDriverList.this, "Error Finding Drivers!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        driverID = dc.getDocument().getId();

                        if(dc.getType() == DocumentChange.Type.ADDED) {
                            FirebaseFirestore dateDB = FirebaseFirestore.getInstance();

                            //check which driver match the requirements
                            dateDB.collection("User Accounts").document(driverID).collection("Not Available").get()
                                    .addOnCompleteListener(task -> {
                                        String checkStatus = "true";
                                        ArrayList<String> listID = new ArrayList<>();

                                        if (task.isSuccessful()) {
                                            //save id into arraylist
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                listID.add(document.getId());
                                            }
                                            //nested for loop to check availability of the dates calculated
                                            for (int i = 0; i < dates.size(); i++) {
                                                for (int j = 0; j < listID.size(); j++) {
                                                    //if false, break loop
                                                    if (dates.get(i).equals(listID.get(j))) {
                                                        checkStatus = "false";
                                                        break;
                                                    }
                                                }
                                                //if false, break loop
                                                if (checkStatus.equals("false")) {
                                                    break;
                                                }
                                            }

                                            //if true only allowed to set the details into model
                                            if (checkStatus.equals("true")) {
                                                mdl.add(dc.getDocument().toObject(ModelDriverList.class));
                                                dlAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}