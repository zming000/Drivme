package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TouristBookingHourDetails extends AppCompatActivity {
    //declare variables
    TextView mtvBOrderID, mtvBName, mtvBContact, mtvBTripOption, mtvBMeetDate, mtvBMeetTime, mtvBStartTime,
            mtvBEndTime, mtvBDuration, mtvBLocality, mtvBAddress, mtvBCarPlate, mtvBCarModel, mtvBCarColour,
            mtvBCarTrans, mtvBPetrolCompany, mtvBPetrolType, mtvBNotes, mtvBPriceDay, mtvBTotal;
    ImageView mivCopy;
    Button mbtnCancel, mbtnPayment;
    String orderID;
    FirebaseFirestore driverDetails, tripDetails, carDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_booking_hour_details);

        //assign variables
        mtvBOrderID = findViewById(R.id.tvBOrderID);
        mtvBName = findViewById(R.id.tvBName);
        mtvBContact = findViewById(R.id.tvBContact);
        mtvBTripOption = findViewById(R.id.tvBTripOption);
        mtvBMeetDate = findViewById(R.id.tvBMeetDate);
        mtvBMeetTime = findViewById(R.id.tvBMeetTime);
        mtvBStartTime = findViewById(R.id.tvBStartTime);
        mtvBEndTime = findViewById(R.id.tvBEndTime);
        mtvBDuration = findViewById(R.id.tvBDuration);
        mtvBLocality = findViewById(R.id.tvBLocality);
        mtvBAddress = findViewById(R.id.tvBAddress);
        mtvBCarPlate = findViewById(R.id.tvBCarPlate);
        mtvBCarModel = findViewById(R.id.tvBCarModel);
        mtvBCarColour = findViewById(R.id.tvBCarColour);
        mtvBCarTrans = findViewById(R.id.tvBCarTrans);
        mtvBPetrolCompany = findViewById(R.id.tvBPetrolCompany);
        mtvBPetrolType = findViewById(R.id.tvBPetrolType);
        mtvBNotes = findViewById(R.id.tvBNotes);
        mtvBPriceDay = findViewById(R.id.tvBPriceDay);
        mtvBTotal = findViewById(R.id.tvBTotal);
        mivCopy = findViewById(R.id.ivCopy);
        mbtnCancel = findViewById(R.id.btnCancel);
        mbtnPayment = findViewById(R.id.btnPayment);

        orderID = getIntent().getStringExtra("orderID");

        //set text
        /*order id*/
        mtvBOrderID.setText(orderID);

        /*trip details*/
        tripDetails = FirebaseFirestore.getInstance();
        tripDetails.collection("Trip Details").document(orderID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        String status = doc.getString("orderStatus");

                        if(Objects.requireNonNull(status).equals("Rejected by Driver") || status.equals("Cancelled by Tourist") || status.equals("Cancelled by Driver") || status.equals("Trip Ongoing") || status.equals("Trip Finished")){
                            mbtnCancel.setVisibility(View.GONE);
                        }
                        else if(status.equals("Pending Tourist Payment")){
                            mbtnPayment.setVisibility(View.VISIBLE);
                        }

                        mtvBTripOption.setText("By " + doc.getString("tripOption"));
                        mtvBMeetDate.setText(doc.getString("meetDate"));
                        mtvBMeetTime.setText(doc.getString("meetTime"));
                        mtvBStartTime.setText(doc.getString("meetTime"));
                        mtvBEndTime.setText(doc.getString("endTime"));

                        int num = Objects.requireNonNull(doc.getLong("duration")).intValue();

                        if(num > 1) {
                            mtvBDuration.setText(num + " hours");
                        }
                        else{
                            mtvBDuration.setText(num + " hour");
                        }

                        mtvBLocality.setText(doc.getString("locality"));
                        mtvBAddress.setText(doc.getString("address"));
                        mtvBNotes.setText(doc.getString("note"));
                        mtvBPriceDay.setText(String.valueOf(Objects.requireNonNull(doc.getLong("priceDriver")).intValue()));
                        mtvBTotal.setText(String.valueOf(Objects.requireNonNull(doc.getLong("total")).intValue()));

                        String touristID = doc.getString("touristID");
                        String driverID = doc.getString("driverID");
                        String carPlate = doc.getString("carPlate");

                        /*driver details*/
                        driverDetails = FirebaseFirestore.getInstance();
                        driverDetails.collection("User Accounts").document(Objects.requireNonNull(driverID)).get()
                                .addOnCompleteListener(task1 -> {
                                    DocumentSnapshot driver = task1.getResult();

                                    mtvBName.setText(driver.getString("lastName") + " " + driver.getString("firstName"));
                                    mtvBContact.setText(driver.getString("phoneNumber"));
                                });

                        /*car details*/
                        carDetails = FirebaseFirestore.getInstance();
                        carDetails.collection("User Accounts").document(Objects.requireNonNull(touristID)).collection("Car Details").document(Objects.requireNonNull(carPlate)).get()
                                .addOnCompleteListener(task2 -> {
                                    DocumentSnapshot car = task2.getResult();

                                    mtvBCarPlate.setText(car.getString("carPlate"));
                                    mtvBCarModel.setText(car.getString("carModel"));
                                    mtvBCarColour.setText(car.getString("carColour"));
                                    mtvBCarTrans.setText(car.getString("carTransmission"));
                                    mtvBPetrolCompany.setText(car.getString("petrolCompany"));
                                    mtvBPetrolType.setText(car.getString("petrolType"));
                                });
                    }
                });

        mivCopy.setOnClickListener(view -> {
            ClipboardManager copyAddress = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData address = ClipData.newPlainText("address", mtvBAddress.getText().toString());
            copyAddress.setPrimaryClip(address);

            Toast.makeText(TouristBookingHourDetails.this, "Copied address to clipboard!", Toast.LENGTH_SHORT).show();
        });

        mbtnPayment.setOnClickListener(view -> {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristBookingHourDetails.this);
            alertDialogBuilder.setTitle("Booking Payment");
            alertDialogBuilder
                    .setMessage("Do you wish to pay for booking?")
                    .setCancelable(false)
                    .setPositiveButton("Do Payment", (dialog, id) -> bookingPayment())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        mbtnCancel.setOnClickListener(view -> {
            String orderStatus = getIntent().getStringExtra("orderStatus");
            if(orderStatus.equals("Coming Soon")){
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristBookingHourDetails.this);
                alertDialogBuilder.setTitle("Cancel Booking");
                alertDialogBuilder
                        .setMessage("You can only refund 90% of total price paid! Do you still wish to cancel and refund the booking?")
                        .setCancelable(false)
                        .setPositiveButton("Cancel Booking", (dialog, id) -> cancelBooking())
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristBookingHourDetails.this);
                alertDialogBuilder.setTitle("Cancel Booking");
                alertDialogBuilder
                        .setMessage("Do you wish to cancel the booking?")
                        .setCancelable(false)
                        .setPositiveButton("Cancel Booking", (dialog, id) -> cancelBooking())
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void bookingPayment() {
        Intent booking = new Intent(TouristBookingHourDetails.this, TouristPayment.class);
        booking.putExtra("orderID", orderID);

        startActivity(booking);
    }

    private void cancelBooking() {
        FirebaseFirestore getToken = FirebaseFirestore.getInstance();
        FirebaseFirestore getAdminToken = FirebaseFirestore.getInstance();
        FirebaseFirestore getTouristID = FirebaseFirestore.getInstance();
        FirebaseFirestore updateOrderStatus = FirebaseFirestore.getInstance();
        FirebaseFirestore updateDriver = FirebaseFirestore.getInstance();
        FirebaseFirestore updateTourist = FirebaseFirestore.getInstance();
        FirebaseFirestore checkDriverTime = FirebaseFirestore.getInstance();
        FirebaseFirestore checkTouristTime = FirebaseFirestore.getInstance();
        FirebaseFirestore deleteDriverTime = FirebaseFirestore.getInstance();
        FirebaseFirestore deleteTouristTime = FirebaseFirestore.getInstance();
        String orderStatus = getIntent().getStringExtra("orderStatus");

        //delete date booked
        getTouristID.collection("Trip Details").document(orderID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        //initialize variables
                        String tID = doc.getString("touristID");
                        String dID = doc.getString("driverID");
                        SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                        String meetDate = "";
                        String hour;
                        float duration = doc.getLong("duration");
                        int dur = Math.round(duration);

                        //change date format into date id
                        try {
                            meetDate = sdf.format(Objects.requireNonNull(input.parse(Objects.requireNonNull(doc.getString("meetDate")))));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //get hour
                        String[] splitTime = Objects.requireNonNull(doc.getString("meetTime")).split(":");
                        //get first character
                        char firstNum = splitTime[0].charAt(0);

                        //if first character is 0, remove it
                        if(String.valueOf(firstNum).equals("0")) {
                            hour = splitTime[0].substring(1);
                        }
                        else{
                            hour = splitTime[0];
                        }

                        /* calculate time */
                        //initialize
                        ArrayList<String> time = new ArrayList<>();
                        ArrayList<String> allTime = new ArrayList<>();

                        //calculate and add hour into arraylist (whole day)
                        for(int i = 0; i < 19; i++) {
                            //add 1 hour
                            allTime.add(String.valueOf(5 + i));
                        }

                        //calculate and add hour into arraylist (booked)
                        for(int i = 0; i < dur; i++) {
                            //add 1 hour
                            time.add(String.valueOf(Integer.parseInt(hour) + i));
                        }

                        Map<String, Object> driverDate = new HashMap<>();

                        /*update driver date booked*/
                        for (int i = 0; i < time.size(); i++) {
                            String dateID = time.get(i);
                            driverDate.put(dateID, "Available");
                        }

                        String finalMeetDate = meetDate;
                        updateDriver.collection("User Accounts").document(Objects.requireNonNull(dID)).collection("Date Booked").document(meetDate)
                                .update(driverDate)
                                .addOnSuccessListener(unused -> checkDriverTime.collection("User Accounts").document(dID).collection("Date Booked").document(finalMeetDate).get()
                                        .addOnCompleteListener(task12 -> {
                                            if(task12.isSuccessful()){
                                                DocumentSnapshot doc1 = task12.getResult();
                                                String status = "true";

                                                for (int i = 0; i < allTime.size(); i++) {
                                                    String getTimeStatus = doc1.getString(allTime.get(i));

                                                    //check if there is time that N/A
                                                    if(Objects.requireNonNull(getTimeStatus).equals("Not Available")){
                                                        status = "false";
                                                        break;
                                                    }
                                                }

                                                //if all time available, delete it
                                                if(status.equals("true")){
                                                    deleteDriverTime.collection("User Accounts").document(Objects.requireNonNull(dID)).collection("Date Booked").document(finalMeetDate)
                                                            .delete();
                                                }
                                            }
                                        }));

                        Map<String, Object> touristDate = new HashMap<>();

                        /*update tourist date booked*/
                        for (int i = 0; i < time.size(); i++) {
                            String dateID = time.get(i);
                            touristDate.put(dateID, "Available");
                        }

                        updateTourist.collection("User Accounts").document(Objects.requireNonNull(tID)).collection("Date Booked").document(meetDate)
                                .update(touristDate)
                                .addOnSuccessListener(unused -> checkTouristTime.collection("User Accounts").document(tID).collection("Date Booked").document(finalMeetDate).get()
                                        .addOnCompleteListener(task12 -> {
                                            if(task12.isSuccessful()){
                                                DocumentSnapshot doc1 = task12.getResult();
                                                String status = "true";

                                                for (int i = 0; i < allTime.size(); i++) {
                                                    String getTimeStatus = doc1.getString(allTime.get(i));

                                                    //check if there is time that N/A
                                                    if(Objects.requireNonNull(getTimeStatus).equals("Not Available")){
                                                        status = "false";
                                                        break;
                                                    }
                                                }

                                                //if all time available, delete it
                                                if(status.equals("true")){
                                                    deleteTouristTime.collection("User Accounts").document(Objects.requireNonNull(tID)).collection("Date Booked").document(finalMeetDate)
                                                            .delete();
                                                }
                                            }
                                        }));

                        /*send notification to driver*/
                        getToken.collection("User Accounts").document(Objects.requireNonNull(dID)).get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot tourToken = task1.getResult();
                                        String token = tourToken.getString("notificationToken");

                                        UserFCMSend.pushNotification(
                                                TouristBookingHourDetails.this,
                                                token,
                                                "Booking Cancelled by Tourist",
                                                "Tourist have cancelled the booking!");

                                        //update order status
                                        Map<String,Object> order = new HashMap<>();
                                        order.put("orderStatus", "Cancelled by Tourist");

                                        if(orderStatus.equals("Coming Soon")){
                                            order.put("refundStatus", "Refund Needed");

                                            /*send notification to admin*/
                                            getAdminToken.collection("User Accounts").document("admin001").get()
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()) {
                                                            DocumentSnapshot doc2 = task2.getResult();
                                                            String adminToken = doc2.getString("notificationToken");

                                                            UserFCMSend.pushNotification(
                                                                    TouristBookingHourDetails.this,
                                                                    adminToken,
                                                                    "Refund Needed",
                                                                    "Tourist have cancelled a booking and requested a refund! Please review the order at the refund list!");
                                                        }
                                                    });

                                            Toast.makeText(TouristBookingHourDetails.this, "Booking cancelled successfully! Refund will be done in 5 working days!", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(TouristBookingHourDetails.this, "Booking cancelled successfully!", Toast.LENGTH_SHORT).show();
                                        }

                                        updateOrderStatus.collection("Trip Details").document(orderID)
                                                .update(order);

                                        startActivity(new Intent(TouristBookingHourDetails.this, TouristNavActivity.class));
                                        finishAffinity();
                                        finish();
                                    }
                                });
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TouristBookingHourDetails.this, TouristNavActivity.class));
        finish();
    }
}