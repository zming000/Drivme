package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
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
    ArrayList<ModelDayDriverList> dayList;
    ArrayList<ModelHourDriverList> hourList;
    AdapterDayDriverList dlAdapter;
    AdapterHourDriverList hlAdapter;
    FirebaseFirestore userDB;
    String driverID, tripOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_driver_list);

        //assign variables
        mrvDriver = findViewById(R.id.rvDriver);
        mrvDriver.setLayoutManager(new LinearLayoutManager(this));

        //initialize variables
        userDB = FirebaseFirestore.getInstance();
        dayList = new ArrayList<>();
        hourList = new ArrayList<>();
        tripOption = getIntent().getStringExtra("tripOption");

        if(tripOption.equals("Day")) {
            dlAdapter = new AdapterDayDriverList(
                    TouristDriverList.this,
                    dayList,
                    getIntent().getStringExtra("orderID"),
                    getIntent().getStringExtra("touristID"),
                    getIntent().getIntExtra("duration", 0),
                    getIntent().getStringExtra("startDate"),
                    getIntent().getStringExtra("endDate"),
                    getIntent().getStringExtra("time"),
                    getIntent().getStringExtra("carPlate"),
                    getIntent().getStringExtra("locality"),
                    getIntent().getStringExtra("address"),
                    getIntent().getStringExtra("comment"),
                    getIntent().getStringExtra("tripOption"),
                    getIntent().getStringExtra("dateID"));

            mrvDriver.setAdapter(dlAdapter);
        }
        else{
            hlAdapter = new AdapterHourDriverList(
                    TouristDriverList.this,
                    hourList,
                    getIntent().getStringExtra("orderID"),
                    getIntent().getStringExtra("touristID"),
                    getIntent().getStringExtra("date"),
                    getIntent().getStringExtra("startTime"),
                    getIntent().getStringExtra("endTime"),
                    getIntent().getStringExtra("carPlate"),
                    getIntent().getStringExtra("locality"),
                    getIntent().getStringExtra("address"),
                    getIntent().getStringExtra("comment"),
                    getIntent().getStringExtra("tripOption"),
                    getIntent().getStringExtra("dateID"),
                    getIntent().getIntExtra("duration", 0),
                    getIntent().getIntExtra("hourStart", 0));

            mrvDriver.setAdapter(hlAdapter);
        }

        getDetailsFromFirestore();
    }

    /*check available driver that fulfill requirement*/
    private void getDetailsFromFirestore() {
        //declare variables
        int dur = getIntent().getIntExtra("duration", 0);
        String meetDate = getIntent().getStringExtra("dateID");
        int meetTime = getIntent().getIntExtra("hourStart", 0);

        if(tripOption.equals("Day")) {
            //initialize
            ArrayList<String> dates = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            Calendar c = Calendar.getInstance();

            //add start date
            dates.add(meetDate);

            //calculate and add dates into arraylist
            for (int i = 0; i < dur - 1; i++) {
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
                    .whereEqualTo("accountDriver", 1)
                    .whereEqualTo("loginStatusDriver", 1)
                    .whereEqualTo("accountStatus", "Driver")
                    .whereEqualTo("state", getIntent().getStringExtra("state"))
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Toast.makeText(TouristDriverList.this, "Error Finding Drivers!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //use the id to check if the driver available within the duration requested
                        for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
                            driverID = dc.getDocument().getId();

                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                FirebaseFirestore dateDB = FirebaseFirestore.getInstance();

                                //check which driver match the requirements
                                dateDB.collection("User Accounts").document(driverID).collection("Date Booked").get()
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
                                                    dayList.add(dc.getDocument().toObject(ModelDayDriverList.class));
                                                    dlAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
        else{
            //initialize
            ArrayList<String> time = new ArrayList<>();

            //calculate and add hour into arraylist
            for(int i = 0; i < dur; i++) {
                //add 1 hour
                time.add(String.valueOf(meetTime + i));
            }

            //search for driver that fulfill requirements with whereEqualTo
            userDB.collection("User Accounts")
                    .whereEqualTo("accountDriver", 1)
                    .whereEqualTo("loginStatusDriver", 1)
                    .whereEqualTo("accountStatus", "Driver")
                    .whereEqualTo("state", getIntent().getStringExtra("state"))
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Toast.makeText(TouristDriverList.this, "Error Finding Drivers!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //use the id to check if the driver available within the duration requested
                        for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
                            driverID = dc.getDocument().getId();

                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                FirebaseFirestore dateDB = FirebaseFirestore.getInstance();

                                //check which driver match the requirements
                                dateDB.collection("User Accounts").document(driverID).collection("Date Booked").document(meetDate).get()
                                        .addOnCompleteListener(task -> {
                                            String checkStatus = "true";
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();

                                                if(doc.exists()) {
                                                    String driverTripOpt = doc.getString("tripOption");

                                                    if (Objects.requireNonNull(driverTripOpt).equals("Hour")) {
                                                        for (int i = 0; i < time.size(); i++) {
                                                            String timeState = doc.getString(time.get(i));

                                                            if (Objects.requireNonNull(timeState).equals("Not Available")) {
                                                                checkStatus = "false";
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    else{
                                                        checkStatus = "false";
                                                    }

                                                    //if true only allowed to set the details into model
                                                    if (checkStatus.equals("true")) {
                                                        hourList.add(dc.getDocument().toObject(ModelHourDriverList.class));
                                                        hlAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                                else{
                                                    hourList.add(dc.getDocument().toObject(ModelHourDriverList.class));
                                                    hlAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }
}