package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
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
    TextView mtvDrivPay, mtvCard, mtvOnline, mtvPTripOption, mtvMeet, mtvPMeet, mtvStart, mtvPStart,
            mtvEnd, mtvPEnd, mtvPDuration, mtvPrice, mtvPPrice, mtvPTotal, mtvDriverDay, mtvMeal;
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
        mtvMeet = findViewById(R.id.tvMeet);
        mtvPMeet = findViewById(R.id.tvPMeet);
        mtvStart = findViewById(R.id.tvStart);
        mtvPStart = findViewById(R.id.tvPStart);
        mtvEnd = findViewById(R.id.tvEnd);
        mtvPEnd = findViewById(R.id.tvPEnd);
        mtvPDuration = findViewById(R.id.tvPDuration);
        mtvPrice = findViewById(R.id.tvPrice);
        mtvPPrice = findViewById(R.id.tvPPrice);
        mtvDriverDay = findViewById(R.id.tvDriverDay);
        mtvMeal = findViewById(R.id.tvMeal);
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
                        String tripOption = doc.getString("tripOption");
                        int numDay = Objects.requireNonNull(doc.getLong("duration")).intValue();

                        mtvPTripOption.setText("By " + tripOption);
                        mtvPPrice.setText("RM " + Objects.requireNonNull(doc.getLong("priceDriver")).intValue());
                        mtvPTotal.setText("Total RM " + Objects.requireNonNull(doc.getLong("total")).intValue());

                        if(Objects.requireNonNull(tripOption).equals("Day")){
                            mtvMeet.setText("Meet Time:");
                            mtvPMeet.setText(doc.getString("meetTime"));
                            mtvStart.setText("Start Date:");
                            mtvPStart.setText(doc.getString("meetDate"));
                            mtvEnd.setText("End Date:");
                            mtvPEnd.setText(doc.getString("endDate"));

                            if(numDay > 1) {
                                mtvPDuration.setText(numDay + " days");
                            }
                            else{
                                mtvPDuration.setText(numDay + " day");
                            }

                            mtvPrice.setText("Price per Day:");
                            mtvDriverDay.setText("- Driver's fee per day trip(Services, etc.)");
                            mtvMeal.setVisibility(View.VISIBLE);
                        }
                        else{
                            mtvMeet.setText("Meet Date:");
                            mtvPMeet.setText(doc.getString("meetDate"));
                            mtvStart.setText("Start Time:");
                            mtvPStart.setText(doc.getString("meetTime"));
                            mtvEnd.setText("End Time:");
                            mtvPEnd.setText(doc.getString("endTime"));

                            if(numDay > 1) {
                                mtvPDuration.setText(numDay + " hours");
                            }
                            else{
                                mtvPDuration.setText(numDay + " hour");
                            }

                            mtvPrice.setText("Price per Hour:");
                            mtvDriverDay.setText("- Driver's fee per hour trip(Services, etc.)");
                            mtvMeal.setVisibility(View.GONE);
                        }
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
            FirebaseFirestore updateHistory = FirebaseFirestore.getInstance();

            //initialize
            ArrayList<String> dates = new ArrayList<>();
            SimpleDateFormat dateStart = new SimpleDateFormat("dd/MM/yyyy"); //date format
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy"); //date id
            DateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm"); //date time
            DateFormat docID = new SimpleDateFormat("ddMMyyyyHHmmss"); //record time of button clicked
            Calendar c = Calendar.getInstance();
            Calendar oneDayBefore = Calendar.getInstance();

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
                            String price = String.valueOf(Objects.requireNonNull(doc.getLong("total")).intValue());
                            String tripOption = doc.getString("tripOption");
                            float totalPayment = doc.getLong("total");
                            float duration = doc.getLong("duration");
                            int days = Math.round(duration);

                            //get current date time for id
                            String transID = docID.format(Calendar.getInstance().getTime());
                            //get current date time
                            String dateTime = fullFormat.format(Calendar.getInstance().getTime());

                            //update transaction history
                            Map<String,Object> updateTrans = new HashMap<>();
                            updateTrans.put("transType", "Payment");
                            updateTrans.put("transAmount", String.format("%.2f", totalPayment));
                            updateTrans.put("transDateTime", dateTime);
                            updateTrans.put("orderID", orderID);

                            if(mtvDrivPay.getCurrentTextColor() == -1){
                                getDrivPay.collection("User Accounts").document(Objects.requireNonNull(tourID)).get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                DocumentSnapshot snap = task1.getResult();

                                                double total = Double.parseDouble(price);
                                                double drivpay = Double.parseDouble(Objects.requireNonNull(snap.getString("drivPay")));

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
                                                        dates.add(sdf.format((Objects.requireNonNull(dateStart.parse(Objects.requireNonNull(doc.getString("meetDate")))))));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }

                                                    if(Objects.requireNonNull(tripOption).equals("Day")) {
                                                        //calculate and add dates into arraylist
                                                        for (int i = 0; i < days - 1; i++) {
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
                                                    }

                                                    //create firestore documents for each date
                                                    for (int i = 0; i < dates.size(); i++) {
                                                        orderDate.put("numDay", (i + 1));

                                                        String dateID = dates.get(i);
                                                        addDays.collection("Trip Details").document(orderID).collection("Days").document(dateID)
                                                                .set(orderDate);
                                                    }

                                                    //calculate one day before start date
                                                    try {
                                                        oneDayBefore.setTime(Objects.requireNonNull(fullFormat.parse(doc.getString("meetDate") + " " + doc.getString("meetTime"))));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }

                                                    //deduct 1 day
                                                    oneDayBefore.add(Calendar.DAY_OF_MONTH, -1);

                                                    sendNotificationToTourist(driID, tourID, oneDayBefore);

                                                    updateHistory.collection("User Accounts").document(tourID).collection("Transaction History").document(transID)
                                                            .set(updateTrans)
                                                            .addOnSuccessListener(unused1 -> {
                                                                Toast.makeText(TouristPayment.this, "Paid Successfully!", Toast.LENGTH_SHORT).show();

                                                                startActivity(new Intent(TouristPayment.this, TouristNavActivity.class));
                                                                finishAffinity();
                                                                finish();
                                                            });
                                                }
                                            }
                                        });
                            }
                            else if(mtvCard.getCurrentTextColor() == -1 || mtvOnline.getCurrentTextColor() == -1){
                                //update order status
                                updateOrderStatus.collection("Trip Details").document(orderID)
                                        .update(order);

                                //add start date
                                try {
                                    dates.add(sdf.format((Objects.requireNonNull(dateStart.parse(Objects.requireNonNull(doc.getString("meetDate")))))));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if(Objects.requireNonNull(tripOption).equals("Day")) {
                                    //calculate and add dates into arraylist
                                    for (int i = 0; i < days - 1; i++) {
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
                                }

                                //create firestore documents for each date
                                for (int i = 0; i < dates.size(); i++) {
                                    orderDate.put("numDay", (i + 1));

                                    String dateID = dates.get(i);
                                    addDays.collection("Trip Details").document(orderID).collection("Days").document(dateID)
                                            .set(orderDate);
                                }

                                //calculate one day before start date
                                try {
                                    oneDayBefore.setTime(Objects.requireNonNull(fullFormat.parse(doc.getString("meetDate") + " " + doc.getString("meetTime"))));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                //deduct 1 day
                                oneDayBefore.add(Calendar.DAY_OF_MONTH, -1);

                                sendNotificationToTourist(driID, tourID, oneDayBefore);

                                updateHistory.collection("User Accounts").document(tourID).collection("Transaction History").document(transID)
                                        .set(updateTrans)
                                        .addOnSuccessListener(unused1 -> {
                                            Toast.makeText(TouristPayment.this, "Paid Successfully!", Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(TouristPayment.this, TouristNavActivity.class));
                                            finishAffinity();
                                            finish();
                                        });
                            }
                            else{
                                Toast.makeText(TouristPayment.this, "Please choose a payment method!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });
    }

    private void sendNotificationToTourist(String dID, String tID, Calendar day) {
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
                                "Tourist paid for the booking!");

                        //schedule reminder
                        Intent intent = new Intent(this, UserReminderReceiver.class);
                        intent.putExtra("driverID", dID);
                        intent.putExtra("touristID", tID);

                        sendBroadcast(intent);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                this.getApplicationContext(), 234, intent, 0);
                        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                        am.setExact(AlarmManager.RTC_WAKEUP, day.getTimeInMillis(), pendingIntent);
                    }
                });
    }
}