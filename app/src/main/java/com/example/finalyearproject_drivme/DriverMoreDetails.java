package com.example.finalyearproject_drivme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.taufiqrahman.reviewratings.BarLabels;
import com.taufiqrahman.reviewratings.RatingReviews;

import java.util.ArrayList;
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
                        areas = (ArrayList<String>) doc.get("areas");

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
                        mtvPriceDay.setText(doc.getString("priceDay") + " / day");

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
            FirebaseFirestore getToken = FirebaseFirestore.getInstance();
            getToken.collection("User Accounts").document(driverID).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();

                                String token = doc.getString("notificationToken");

                                FCMSend.pushNotification(
                                        DriverMoreDetails.this,
                                        token,
                                        "New Request",
                                        "You have received a request from a customer!");
                            }
                        }
                    });
        });
    }
}