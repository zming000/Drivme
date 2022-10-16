package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TouristPayment extends AppCompatActivity {
    //declare variables
    CardView mcvDrivPay, mcvCard, mcvOnline;
    TextView mtvDrivPay, mtvCard, mtvOnline, mtvPTripOption, mtvPStartDate, mtvPEndDate, mtvPDuration, mtvPPriceDay, mtvPTotal;
    Button mbtnPayNow;
    FirebaseFirestore getOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_payment);

        //assign variables
        mcvDrivPay = findViewById(R.id.cvDrivPay);
        mcvCard = findViewById(R.id.cvCard);
        mcvOnline = findViewById(R.id.cvOnline);
        mtvDrivPay = findViewById(R.id.tvDrivPay);
        mtvCard = findViewById(R.id.tvCard);
        mtvOnline = findViewById(R.id.tvOnline);
        mtvPTripOption = findViewById(R.id.tvPTripOption);
        mtvPStartDate = findViewById(R.id.tvPStartDate);
        mtvPEndDate = findViewById(R.id.tvPEndDate);
        mtvPDuration = findViewById(R.id.tvPDuration);
        mtvPPriceDay = findViewById(R.id.tvPPriceDay);
        mtvPTotal = findViewById(R.id.tvPTotal);
        mbtnPayNow = findViewById(R.id.btnPayNow);

        //initialize
        getOrder = FirebaseFirestore.getInstance();
        mcvDrivPay.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcvCard.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
        mcvOnline.setCardBackgroundColor(Color.parseColor("#E6E6E6"));

        //get order ID
        String orderID = getIntent().getStringExtra("orderID");

        //set text
        getOrder.collection("Trip Details").document(orderID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();

                        mtvPTripOption.setText("By " + doc.getString("tripOption"));
                        mtvPStartDate.setText(doc.getString("startDate"));
                        mtvPEndDate.setText(doc.getString("endDate"));
                        int numDay = Objects.requireNonNull(doc.getLong("duration")).intValue();

                        if(numDay > 1) {
                            mtvPDuration.setText(numDay + " days");
                        }
                        else{
                            mtvPDuration.setText(numDay + " day");
                        }
                        mtvPPriceDay.setText("RM " + Objects.requireNonNull(doc.getLong("priceDay")).intValue());
                        mtvPTotal.setText("Total RM " + Objects.requireNonNull(doc.getLong("total")).intValue());
                    }
                });

        /*set card view and text color when clicked*/
        mcvDrivPay.setOnClickListener(view -> {
            //set card view color
            mcvDrivPay.setCardBackgroundColor(Color.parseColor("#0F7D63"));
            mcvCard.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            mcvOnline.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            //set text color
            mtvDrivPay.setTextColor(Color.WHITE);
            mtvCard.setTextColor(Color.parseColor("#0F7D63"));
            mtvOnline.setTextColor(Color.parseColor("#0F7D63"));
        });

        mcvCard.setOnClickListener(view -> {
            //set card view color
            mcvDrivPay.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            mcvCard.setCardBackgroundColor(Color.parseColor("#0F7D63"));
            mcvOnline.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            //set text color
            mtvDrivPay.setTextColor(Color.parseColor("#0F7D63"));
            mtvCard.setTextColor(Color.WHITE);
            mtvOnline.setTextColor(Color.parseColor("#0F7D63"));
        });

        mcvOnline.setOnClickListener(view -> {
            //set card view color
            mcvDrivPay.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            mcvCard.setCardBackgroundColor(Color.parseColor("#E6E6E6"));
            mcvOnline.setCardBackgroundColor(Color.parseColor("#0F7D63"));
            //set text color
            mtvDrivPay.setTextColor(Color.parseColor("#0F7D63"));
            mtvCard.setTextColor(Color.parseColor("#0F7D63"));
            mtvOnline.setTextColor(Color.WHITE);
        });

        mbtnPayNow.setOnClickListener(view -> {
            FirebaseFirestore getID = FirebaseFirestore.getInstance();
            FirebaseFirestore getDrivPay = FirebaseFirestore.getInstance();
            FirebaseFirestore addDays = FirebaseFirestore.getInstance();
            FirebaseFirestore updateOrderStatus = FirebaseFirestore.getInstance();
            FirebaseFirestore updatePay = FirebaseFirestore.getInstance();

            //initialize
            ArrayList<String> dates = new ArrayList<>();
            SimpleDateFormat dateStart = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
            Calendar c = Calendar.getInstance();

            //update order status
            Map<String,Object> order = new HashMap<>();
            order.put("orderStatus", "Coming Soon");

            //add button check
            Map<String, Object> orderDate = new HashMap<>();
            orderDate.put("driverCheck", 0);
            orderDate.put("touristCheck", 0);

            getID.collection("Trip Details").document(orderID).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();

                            String tourID = doc.getString("touristID");
                            String driID = doc.getString("driverID");
                            String price = String.valueOf(doc.getLong("total").intValue());
                            String startDate = doc.getString("startDate");
                            float duration = doc.getLong("duration");
                            int days = Math.round(duration);

                            if(mtvDrivPay.getCurrentTextColor() == -1){
                                getDrivPay.collection("User Accounts").document(tourID).get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot snap = task1.getResult();

                                                double total = Double.parseDouble(price);
                                                double drivpay = Double.parseDouble(snap.getString("drivPay"));

                                                //check drivpay balance
                                                if(drivpay < total){
                                                    Toast.makeText(TouristPayment.this, "Insufficient Balance!", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    //update drivpay
                                                    double left = drivpay - total;
                                                    Map<String, Object> touristPay = new HashMap<>();
                                                    touristPay.put("drivPay", String.format("%.2f", left));

                                                    updatePay.collection("User Accounts").document(tourID).update(touristPay);

                                                    //update order status
                                                    updateOrderStatus.collection("Trip Details").document(orderID)
                                                            .update(order);

                                                    //add start date
                                                    try {
                                                        dates.add(sdf.format((Objects.requireNonNull(dateStart.parse(Objects.requireNonNull(startDate))))));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }

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

                                                    //create firestore documents for each date
                                                    for (int i = 0; i < dates.size(); i++) {
                                                        orderDate.put("numDay", (i + 1));

                                                        String dateID = dates.get(i);
                                                        addDays.collection("Trip Details").document(orderID).collection("Days").document(dateID)
                                                                .set(orderDate);
                                                    }

                                                    sendNotificationToTourist(driID, orderID);
                                                }
                                            }
                                        });
                            }
                            else if(mtvCard.getCurrentTextColor() == -1){
                                //update order status
                                updateOrderStatus.collection("Trip Details").document(orderID)
                                        .update(order);

                                //add start date
                                try {
                                    dates.add(sdf.format((Objects.requireNonNull(dateStart.parse(Objects.requireNonNull(startDate))))));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

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

                                //create firestore documents for each date
                                for (int i = 0; i < dates.size(); i++) {
                                    orderDate.put("numDay", (i + 1));

                                    String dateID = dates.get(i);
                                    addDays.collection("Trip Details").document(orderID).collection("Days").document(dateID)
                                            .set(orderDate);
                                }

                                sendNotificationToTourist(driID, orderID);
                            }
                            else if(mtvOnline.getCurrentTextColor() == -1){
                                //update order status
                                updateOrderStatus.collection("Trip Details").document(orderID)
                                        .update(order);

                                //add start date
                                try {
                                    dates.add(sdf.format((Objects.requireNonNull(dateStart.parse(Objects.requireNonNull(startDate))))));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

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

                                //create firestore documents for each date
                                for (int i = 0; i < dates.size(); i++) {
                                    orderDate.put("numDay", (i + 1));

                                    String dateID = dates.get(i);
                                    addDays.collection("Trip Details").document(orderID).collection("Days").document(dateID)
                                            .set(orderDate);
                                }

                                sendNotificationToTourist(driID, orderID);
                            }
                            else{
                                Toast.makeText(TouristPayment.this, "Please choose a payment method!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });
    }

    private void sendNotificationToTourist(String dID, String oID) {
        FirebaseFirestore getToken = FirebaseFirestore.getInstance();

        /*send notification to tourist*/
        getToken.collection("User Accounts").document(dID).get()
                .addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        DocumentSnapshot tourToken = task2.getResult();
                        String token = tourToken.getString("notificationToken");

                        UserFCMSend.pushNotification(
                                TouristPayment.this,
                                token,
                                "Booking Paid",
                                "Tourist paid for the booking!",
                                oID);

                        Toast.makeText(TouristPayment.this, "Paid Successfully!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(TouristPayment.this, TouristNavActivity.class));
                        finishAffinity();
                        finish();
                    }
                });
    }
}