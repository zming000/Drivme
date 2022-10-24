package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TouristRating extends AppCompatActivity {
    //declare variables
    RatingBar mrateBar;
    CardView mcvService, mcvConvo, mcvNavi, mcvPersonality;
    TextView mtvService, mtvConvo, mtvNavi, mtvPersonality, mtvTouristSignUp;
    TextInputEditText metComment;
    Button mbtnRate;
    ArrayList<String> complimentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_rating);

        //assign variables
        mrateBar = findViewById(R.id.rateBar);
        mcvService = findViewById(R.id.cvService);
        mcvConvo = findViewById(R.id.cvConvo);
        mcvNavi = findViewById(R.id.cvNavi);
        mcvPersonality = findViewById(R.id.cvPersonality);
        mtvService = findViewById(R.id.tvService);
        mtvConvo = findViewById(R.id.tvConvo);
        mtvNavi = findViewById(R.id.tvNavi);
        mtvPersonality = findViewById(R.id.tvPersonality);
        mtvTouristSignUp = findViewById(R.id.tvTouristSignUp);
        metComment = findViewById(R.id.etComment);
        mbtnRate = findViewById(R.id.btnRate);

        //initialize array list
        complimentList = new ArrayList<>();

        //initialize card view color
        mcvService.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcvConvo.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcvNavi.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcvPersonality.setCardBackgroundColor(Color.parseColor("#E6E6E6"));

        /*set card view and text color when clicked*/
        mcvService.setOnClickListener(view -> {
            if(mtvService.getCurrentTextColor() == -1){
                mcvService.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mtvService.setTextColor(Color.parseColor("#0F7D63"));
                complimentList.remove(mtvService.getText().toString());
            }
            else{
                mcvService.setCardBackgroundColor(Color.parseColor("#0F7D63"));
                mtvService.setTextColor(Color.WHITE);
                complimentList.add(mtvService.getText().toString());
            }
        });

        mcvConvo.setOnClickListener(view -> {
            if(mtvConvo.getCurrentTextColor() == -1){
                mcvConvo.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mtvConvo.setTextColor(Color.parseColor("#0F7D63"));
                complimentList.remove(mtvConvo.getText().toString());
            }
            else{
                mcvConvo.setCardBackgroundColor(Color.parseColor("#0F7D63"));
                mtvConvo.setTextColor(Color.WHITE);
                complimentList.add(mtvConvo.getText().toString());
            }
        });

        mcvNavi.setOnClickListener(view -> {
            if(mtvNavi.getCurrentTextColor() == -1){
                mcvNavi.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mtvNavi.setTextColor(Color.parseColor("#0F7D63"));
                complimentList.remove(mtvNavi.getText().toString());
            }
            else{
                mcvNavi.setCardBackgroundColor(Color.parseColor("#0F7D63"));
                mtvNavi.setTextColor(Color.WHITE);
                complimentList.add(mtvNavi.getText().toString());
            }
        });

        mcvPersonality.setOnClickListener(view -> {
            if(mtvPersonality.getCurrentTextColor() == -1){
                mcvPersonality.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
                mtvPersonality.setTextColor(Color.parseColor("#0F7D63"));
                complimentList.remove(mtvPersonality.getText().toString());
            }
            else{
                mcvPersonality.setCardBackgroundColor(Color.parseColor("#0F7D63"));
                mtvPersonality.setTextColor(Color.WHITE);
                complimentList.add(mtvPersonality.getText().toString());
            }
        });

        mbtnRate.setOnClickListener(view -> {
            FirebaseFirestore updateOrderRating = FirebaseFirestore.getInstance();
            FirebaseFirestore getDriverRating = FirebaseFirestore.getInstance();
            FirebaseFirestore updateDriverRating = FirebaseFirestore.getInstance();
            FirebaseFirestore getToken = FirebaseFirestore.getInstance();
            FirebaseFirestore updateHistory = FirebaseFirestore.getInstance();

            String orderID = getIntent().getStringExtra("orderID");
            String driverID = getIntent().getStringExtra("driverID");
            String numRate = String.valueOf(mrateBar.getRating());

            if(!numRate.equals("0.0")) {
                //initialize string builder
                StringBuilder typeSB = new StringBuilder();

                Map<String, Object> orderRate = new HashMap<>();
                orderRate.put("rateStar", mrateBar.getRating());
                orderRate.put("orderStatus", "Trip Finished");

                if (complimentList.size() > 0) {
                    for (int j = 0; j < complimentList.size(); j++) {
                        //concat array value
                        typeSB.append(complimentList.get(j));

                        if (j != complimentList.size() - 1) {
                            typeSB.append(", ");
                        }
                    }
                    orderRate.put("rateCompliment", typeSB.toString());
                } else {
                    orderRate.put("rateCompliment", "No compliment");
                }

                if (Objects.requireNonNull(metComment.getText()).toString().trim().isEmpty()) {
                    orderRate.put("rateComment", "No comment.");
                } else {
                    orderRate.put("rateComment", metComment.getText().toString());
                }

                updateOrderRating.collection("Trip Details").document(orderID)
                        .update(orderRate);

                getDriverRating.collection("User Accounts").document(driverID).get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                DocumentSnapshot doc = task.getResult();
                                float oneStar = doc.getLong("1 star");
                                float twoStar = doc.getLong("2 stars");
                                float threeStar = doc.getLong("3 stars");
                                float fourStar = doc.getLong("4 stars");
                                float fiveStar = doc.getLong("5 stars");
                                float multiply = 0;
                                float sum = 0;
                                Map<String, Object> rating = new HashMap<>();

                                switch (numRate) {
                                    case "1.0":
                                        rating.put("1 star", oneStar + 1);
                                        multiply = (oneStar + 1) + (twoStar * 2) + (threeStar * 3) + (fourStar * 4) + (fiveStar * 5);
                                        sum = oneStar + twoStar + threeStar + fourStar + fiveStar + 1;
                                        break;
                                    case "2.0":
                                        rating.put("2 stars", twoStar + 1);
                                        multiply = oneStar + ((twoStar + 1) * 2) + (threeStar * 3) + (fourStar * 4) + (fiveStar * 5);
                                        sum = oneStar + twoStar + threeStar + fourStar + fiveStar + 1;
                                        break;
                                    case "3.0":
                                        rating.put("3 stars", threeStar + 1);
                                        multiply = oneStar + (twoStar * 2) + ((threeStar + 1) * 3) + (fourStar * 4) + (fiveStar * 5);
                                        sum = oneStar + twoStar + threeStar + fourStar + fiveStar + 1;
                                        break;
                                    case "4.0":
                                        rating.put("4 stars", fourStar + 1);
                                        multiply = oneStar + (twoStar * 2) + (threeStar * 3) + ((fourStar + 1) * 4) + (fiveStar * 5);
                                        sum = oneStar + twoStar + threeStar + fourStar + fiveStar + 1;
                                        break;
                                    case "5.0":
                                        rating.put("5 stars", fiveStar + 1);
                                        multiply = oneStar + (twoStar * 2) + (threeStar * 3) + (fourStar * 4) + ((fiveStar + 1) * 5);
                                        sum = oneStar + twoStar + threeStar + fourStar + fiveStar + 1;
                                        break;
                                }

                                float total = multiply / sum;
                                double drivpay = Double.parseDouble(doc.getString("drivPay"));
                                double earn = Double.parseDouble(String.valueOf(getIntent().getFloatExtra("total", 0)));
                                double totalAmount = drivpay + earn;

                                rating.put("rating", Math.round(total * 10.0) / 10.0);
                                rating.put("drivPay", String.format("%.2f", totalAmount));

                                updateDriverRating.collection("User Accounts").document(driverID)
                                        .update(rating);

                                /*send notification to driver*/
                                getToken.collection("User Accounts").document(driverID).get()
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                DocumentSnapshot tourToken = task2.getResult();
                                                String token = tourToken.getString("notificationToken");

                                                UserFCMSend.pushNotification(
                                                        TouristRating.this,
                                                        token,
                                                        "Trip Ended",
                                                        "Tourist rated you! We are thankful for your services!");


                                                DateFormat docID = new SimpleDateFormat("ddMMyyyyHHmmss"); //record time of button clicked
                                                DateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm"); //record time of button clicked

                                                //get current date time for id
                                                String transID = docID.format(Calendar.getInstance().getTime());
                                                //get current date time
                                                String dateTime = fullFormat.format(Calendar.getInstance().getTime());

                                                Map<String,Object> updateTrans = new HashMap<>();
                                                updateTrans.put("transType", "Trip Finished");
                                                updateTrans.put("transAmount", String.format("%.2f", earn));
                                                updateTrans.put("transDateTime", dateTime);
                                                updateTrans.put("orderID", orderID);

                                                updateHistory.collection("User Accounts").document(driverID).collection("Transaction History").document(transID)
                                                        .set(updateTrans)
                                                        .addOnSuccessListener(unused1 -> {
                                                            Toast.makeText(TouristRating.this, "Rated Successfully!", Toast.LENGTH_SHORT).show();

                                                            startActivity(new Intent(TouristRating.this, TouristNavHomepage.class));
                                                            finishAffinity();
                                                            finish();
                                                        });
                                            }
                                        });
                            }
                        });
            }
            else{
                Toast.makeText(TouristRating.this, "Please rate the rating bar!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}