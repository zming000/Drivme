package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DriverMoreDetails extends AppCompatActivity {
    //declare variables
    ImageView mivGender;
    TextView mtvFName, mtvLName, mtvRating, mtvAge, mtvRace, mtvDrivExp, mtvLanguages, mtvState, mtvAreas, mtvPriceDay;
    RatingBar mrbDriver;
    RatingReviews mrrDriver;
    Button mbtnBack, mbtnSelect;
    FirebaseFirestore detailsDB;
    ArrayList<String> languages, areas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_more_details);

        //assign variables
        mivGender = findViewById(R.id.ivGender);
        mtvFName = findViewById(R.id.tvFName);
        mtvLName = findViewById(R.id.tvLName);
        mtvRating = findViewById(R.id.tvRating);
        mrbDriver = findViewById(R.id.rbDriver);
        mtvAge = findViewById(R.id.tvAge);
        mtvRace = findViewById(R.id.tvRace);
        mtvDrivExp = findViewById(R.id.tvDrivExp);
        mtvLanguages = findViewById(R.id.tvLanguages);
        mtvState = findViewById(R.id.tvState);
        mtvAreas = findViewById(R.id.tvAreas);
        mrrDriver = findViewById(R.id.rrDriver);
        mtvPriceDay = findViewById(R.id.tvPriceDay);
        mbtnBack = findViewById(R.id.btnBack);
        mbtnSelect = findViewById(R.id.btnSelect);

        //initialize firestore
        detailsDB = FirebaseFirestore.getInstance();

        String driverID = getIntent().getStringExtra("driverID");

        detailsDB.collection("User Accounts").document(driverID).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();

                        String gender = doc.getString("gender");
                        float ratings = getIntent().getFloatExtra("rating", 0);
                        languages = (ArrayList<String>) doc.get("languages");
                        areas = (ArrayList<String>) doc.get("familiarAreas");

                        //set values to display
                        if(Objects.requireNonNull(gender).equals("Male")){
                            mivGender.setBackgroundResource(R.drawable.icon_male);
                        }
                        else{
                            mivGender.setBackgroundResource(R.drawable.icon_female);
                        }
                        mtvFName.setText(doc.getString("firstName"));
                        mtvLName.setText(doc.getString("lastName"));
                        mtvRating.setText(String.valueOf(ratings));
                        mrbDriver.setRating(ratings);
                        mtvAge.setText(doc.getString("age") + " Years Old");
                        mtvRace.setText(doc.getString("race"));
                        mtvDrivExp.setText(doc.getString("drivingExperience") + " Years Of Driving Experience");
                        mtvState.setText(doc.getString("state"));
                        mtvPriceDay.setText("RM" + getIntent().getIntExtra("priceDay", 0) + " / day");

                        //initialize string builder
                        StringBuilder lanSB = new StringBuilder();

                        for(int j = 0; j < Objects.requireNonNull(languages).size(); j++){
                            //concat array value
                            lanSB.append(languages.get(j));

                            if(j != languages.size()-1){
                                lanSB.append(", ");
                            }
                        }
                        mtvLanguages.setText("[" + lanSB + "]");

                        //initialize string builder
                        StringBuilder areaSB = new StringBuilder();

                        for(int j = 0; j < Objects.requireNonNull(areas).size(); j++){
                            //concat array value
                            areaSB.append(areas.get(j));

                            if(j != areas.size()-1){
                                areaSB.append(", ");
                            }
                        }
                        mtvAreas.setText("[" + areaSB + "]");

                        //rating and reviews
                        Pair[] colors = new Pair[]{
                                new Pair<>(Color.parseColor("#0c96c7"), Color.parseColor("#00fe77")),
                                new Pair<>(Color.parseColor("#7b0ab4"), Color.parseColor("#ff069c")),
                                new Pair<>(Color.parseColor("#fe6522"), Color.parseColor("#fdd116")),
                                new Pair<>(Color.parseColor("#104bff"), Color.parseColor("#67cef6")),
                                new Pair<>(Color.parseColor("#ff5d9b"), Color.parseColor("#ffaa69"))
                        };

                        int[] raters = new int[]{
                                Objects.requireNonNull(doc.getLong("5 stars")).intValue(),
                                Objects.requireNonNull(doc.getLong("4 stars")).intValue(),
                                Objects.requireNonNull(doc.getLong("3 stars")).intValue(),
                                Objects.requireNonNull(doc.getLong("2 stars")).intValue(),
                                Objects.requireNonNull(doc.getLong("1 star")).intValue()
                        };

                        mrrDriver.createRatingBars(100, BarLabels.STYPE3, colors, raters);
                    }
                });

        mbtnBack.setOnClickListener(view -> finish());

        mbtnSelect.setOnClickListener(view -> {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverMoreDetails.this);
            alertDialogBuilder.setTitle("Selecting Driver");
            alertDialogBuilder
                    .setMessage("Do you wish to book him/her?")
                    .setCancelable(false)
                    .setPositiveButton("Book", (dialog, id) -> addOrder())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    public void addOrder(){
        FirebaseFirestore getToken = FirebaseFirestore.getInstance();
        FirebaseFirestore addOrder = FirebaseFirestore.getInstance();
        FirebaseFirestore updateDriver = FirebaseFirestore.getInstance();
        FirebaseFirestore updateTourist = FirebaseFirestore.getInstance();
        FirebaseFirestore updateTouristCar = FirebaseFirestore.getInstance();
        String driID = getIntent().getStringExtra("driverID");
        String orderID = getIntent().getStringExtra("orderID");
        String tourID = getIntent().getStringExtra("touristID");
        String tripOpt = getIntent().getStringExtra("tripOption");
        String carP = getIntent().getStringExtra("carPlate");
        float price = getIntent().getIntExtra("priceDay", 0);
        int dur = getIntent().getIntExtra("duration", 0);

        int total = Math.round(price) * dur;

        /* add trip details */
        Map<String, Object> tripDetails = new HashMap<>();
        if(tripOpt.equals("Day")) {
            tripDetails.put("driverID", getIntent().getStringExtra("driverID"));
            tripDetails.put("orderID", getIntent().getStringExtra("orderID"));
            tripDetails.put("touristID", getIntent().getStringExtra("touristID"));
            tripDetails.put("duration", getIntent().getIntExtra("duration", 0));
            tripDetails.put("startDate", getIntent().getStringExtra("startDate"));
            tripDetails.put("endDate", getIntent().getStringExtra("endDate"));
            tripDetails.put("time", getIntent().getStringExtra("time"));
            tripDetails.put("carPlate", getIntent().getStringExtra("carPlate"));
            tripDetails.put("locality", getIntent().getStringExtra("locality"));
            tripDetails.put("address", getIntent().getStringExtra("address"));
            tripDetails.put("tripOption", getIntent().getStringExtra("tripOption"));
            tripDetails.put("note", getIntent().getStringExtra("comment"));
            tripDetails.put("priceDay", price);
            tripDetails.put("total", total);
            tripDetails.put("orderStatus", "Pending Driver Accept");
            tripDetails.put("tripStart", 0);
            tripDetails.put("tripEnd", 0);
            tripDetails.put("rateStatus", 0);
        }else{
            //slot
        }

        addOrder.collection("Trip Details").document(orderID)
                .set(tripDetails);

        /* calculate dates */
        String startDate = getIntent().getStringExtra("dateID");
        int days = getIntent().getIntExtra("duration", 0);

        //initialize
        ArrayList<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Calendar c = Calendar.getInstance();

        //add start date
        dates.add(startDate);

        //calculate and add dates into arraylist
        for(int i = 0; i < days - 1; i++) {
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

        /* update driver */
        Map<String, Object> driverDate = new HashMap<>();
        driverDate.put("tripOption", getIntent().getStringExtra("tripOption"));

        if(tripOpt.equals("Slot")) {
            //checkboxes
        }
        else{
            for (int i = 0; i < dates.size(); i++) {
                String dateID = dates.get(i);
                updateDriver.collection("User Accounts").document(driID).collection("Date Booked").document(dateID)
                        .set(driverDate);
            }
        }

        /* update tourist */
        Map<String, Object> touristDate = new HashMap<>();
        touristDate.put("tripOption", getIntent().getStringExtra("tripOption"));

        if(tripOpt.equals("Slot")) {
            //checkboxes
        }
        else{
            for (int i = 0; i < dates.size(); i++) {
                String dateID = dates.get(i);
                updateTourist.collection("User Accounts").document(tourID).collection("Date Booked").document(dateID)
                        .set(touristDate);
            }
        }

        /* update tourist car */
        Map<String, Object> touristCar = new HashMap<>();
        touristCar.put("carStatus", "N/A");

        updateTouristCar.collection("User Accounts").document(tourID).collection("Car Details").document(carP)
                .update(touristCar);

        getToken.collection("User Accounts").document(driID).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        String token = doc.getString("notificationToken");

                        UserFCMSend.pushNotification(
                                DriverMoreDetails.this,
                                token,
                                "New Request",
                                "You have received a request from a customer!",
                                getIntent().getStringExtra("orderID"));

                        startActivity(new Intent(DriverMoreDetails.this, TouristBookSuccess.class));
                        finish();
                    }
                });
    }
}