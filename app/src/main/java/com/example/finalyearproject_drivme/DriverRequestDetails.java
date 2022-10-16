package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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

public class DriverRequestDetails extends AppCompatActivity {
    //declare variables
    TextView mtvOrderID, mtvName, mtvContact, mtvTripOption, mtvMeetDate, mtvMeetTime, mtvStartDate,
            mtvEndDate, mtvDuration, mtvLocality, mtvAddress, mtvCarPlate, mtvCarModel, mtvCarColour,
            mtvCarTrans, mtvPetrolCompany, mtvPetrolType, mtvNotes, mtvPriceDay, mtvTotal;
    ImageView mivCopy;
    Button mbtnReject, mbtnAccept, mbtnCancel;
    String orderID;
    FirebaseFirestore touristDetails, tripDetails, carDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request_details);

        //assign variables
        mtvOrderID = findViewById(R.id.tvOrderID);
        mtvName = findViewById(R.id.tvName);
        mtvContact = findViewById(R.id.tvContact);
        mtvTripOption = findViewById(R.id.tvTripOption);
        mtvMeetDate = findViewById(R.id.tvMeetDate);
        mtvMeetTime = findViewById(R.id.tvMeetTime);
        mtvStartDate = findViewById(R.id.tvStartDate);
        mtvEndDate = findViewById(R.id.tvEndDate);
        mtvDuration = findViewById(R.id.tvDuration);
        mtvLocality = findViewById(R.id.tvLocality);
        mtvAddress = findViewById(R.id.tvAddress);
        mtvCarPlate = findViewById(R.id.tvCarPlate);
        mtvCarModel = findViewById(R.id.tvCarModel);
        mtvCarColour = findViewById(R.id.tvCarColour);
        mtvCarTrans = findViewById(R.id.tvCarTrans);
        mtvPetrolCompany = findViewById(R.id.tvPetrolCompany);
        mtvPetrolType = findViewById(R.id.tvPetrolType);
        mtvNotes = findViewById(R.id.tvNotes);
        mtvPriceDay = findViewById(R.id.tvPriceDay);
        mtvTotal = findViewById(R.id.tvTotal);
        mivCopy = findViewById(R.id.ivCopy);
        mbtnReject = findViewById(R.id.btnReject);
        mbtnAccept = findViewById(R.id.btnAccept);
        mbtnCancel = findViewById(R.id.btnCancel);

        orderID = getIntent().getStringExtra("orderID");

        //set text
        /*order id*/
        mtvOrderID.setText(orderID);

        /*trip details*/
        tripDetails = FirebaseFirestore.getInstance();
        tripDetails.collection("Trip Details").document(orderID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        String status = doc.getString("orderStatus");

                        if(status.equals("Rejected by Driver") || status.equals("Cancelled by Tourist") || status.equals("Cancelled by Driver")
                                || status.equals("Coming Soon") || status.equals("Trip Ongoing") || status.equals("Trip Finished")){
                            mbtnReject.setVisibility(View.GONE);
                            mbtnAccept.setVisibility(View.GONE);
                            mbtnCancel.setVisibility(View.GONE);
                        }
                        else if(status.equals("Pending Tourist Payment")){
                            mbtnReject.setVisibility(View.GONE);
                            mbtnAccept.setVisibility(View.GONE);
                            mbtnCancel.setVisibility(View.VISIBLE);
                        }

                        mtvTripOption.setText("By " + doc.getString("tripOption"));
                        mtvMeetDate.setText(doc.getString("startDate"));
                        mtvMeetTime.setText(doc.getString("time"));
                        mtvStartDate.setText(doc.getString("startDate"));
                        mtvEndDate.setText(doc.getString("endDate"));

                        int num = Objects.requireNonNull(doc.getLong("duration")).intValue();

                        if(num > 1) {
                            mtvDuration.setText(num + " days");
                        }
                        else{
                            mtvDuration.setText(num + " day");
                        }

                        mtvLocality.setText(doc.getString("locality"));
                        mtvAddress.setText(doc.getString("address"));
                        mtvNotes.setText(doc.getString("note"));
                        mtvPriceDay.setText(String.valueOf(Objects.requireNonNull(doc.getLong("priceDay")).intValue()));
                        mtvTotal.setText(String.valueOf(Objects.requireNonNull(doc.getLong("total")).intValue()));

                        String touristID = doc.getString("touristID");
                        String carPlate = doc.getString("carPlate");

                        /*tourist details*/
                        touristDetails = FirebaseFirestore.getInstance();
                        touristDetails.collection("User Accounts").document(Objects.requireNonNull(touristID)).get()
                                .addOnCompleteListener(task1 -> {
                                    DocumentSnapshot tour = task1.getResult();

                                    String fName = tour.getString("firstName");
                                    String lName = tour.getString("lastName");
                                    String fullName = lName + " " + fName;

                                    mtvName.setText(fullName);
                                    mtvContact.setText(tour.getString("phoneNumber"));
                                });

                        /*car details*/
                        carDetails = FirebaseFirestore.getInstance();
                        carDetails.collection("User Accounts").document(touristID).collection("Car Details").document(Objects.requireNonNull(carPlate)).get()
                                .addOnCompleteListener(task2 -> {
                                    DocumentSnapshot car = task2.getResult();

                                    mtvCarPlate.setText(car.getString("carPlate"));
                                    mtvCarModel.setText(car.getString("carModel"));
                                    mtvCarColour.setText(car.getString("carColour"));
                                    mtvCarTrans.setText(car.getString("carTransmission"));
                                    mtvPetrolCompany.setText(car.getString("petrolCompany"));
                                    mtvPetrolType.setText(car.getString("petrolType"));
                                });
                    }
                });

        mivCopy.setOnClickListener(view -> {
            ClipboardManager copyAddress = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData address = ClipData.newPlainText("address", mtvAddress.getText().toString());
            copyAddress.setPrimaryClip(address);

            Toast.makeText(DriverRequestDetails.this, "Copied address to clipboard!", Toast.LENGTH_SHORT).show();
        });

        mbtnReject.setOnClickListener(view -> {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverRequestDetails.this);
            alertDialogBuilder.setTitle("Reject Tourist");
            alertDialogBuilder
                    .setMessage("Do you wish to reject him/her?")
                    .setCancelable(false)
                    .setPositiveButton("Reject Booking", (dialog, id) -> rejectRequest())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        mbtnAccept.setOnClickListener(view -> {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverRequestDetails.this);
            alertDialogBuilder.setTitle("Accept Booking");
            alertDialogBuilder
                    .setMessage("Do you wish to accept the booking?")
                    .setCancelable(false)
                    .setPositiveButton("Accept Booking", (dialog, id) -> acceptRequest())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        mbtnCancel.setOnClickListener(view -> {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverRequestDetails.this);
            alertDialogBuilder.setTitle("Cancel Booking");
            alertDialogBuilder
                    .setMessage("Do you wish to cancel the booking?")
                    .setCancelable(false)
                    .setPositiveButton("Cancel Booking", (dialog, id) -> cancelBooking())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    private void acceptRequest() {
        FirebaseFirestore getToken = FirebaseFirestore.getInstance();
        FirebaseFirestore getTouristID = FirebaseFirestore.getInstance();
        FirebaseFirestore updateOrderStatus = FirebaseFirestore.getInstance();
        FirebaseFirestore addDays = FirebaseFirestore.getInstance();

        //update order status
        Map<String,Object> order = new HashMap<>();
        order.put("orderStatus", "Pending Tourist Payment");

        updateOrderStatus.collection("Trip Details").document(orderID)
                .update(order);

        //delete date booked
        getTouristID.collection("Trip Details").document(orderID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        String tID = doc.getString("touristID");

                        /*send notification to tourist*/
                        getToken.collection("User Accounts").document(tID).get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot tourToken = task1.getResult();
                                        String token = tourToken.getString("notificationToken");

                                        UserFCMSend.pushNotification(
                                                DriverRequestDetails.this,
                                                token,
                                                "Booking Accepted",
                                                "Your request have been accepted by the driver!",
                                                orderID);

                                        Toast.makeText(DriverRequestDetails.this, "Request accepted successfully!", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(DriverRequestDetails.this, DriverNavActivity.class));
                                        finishAffinity();
                                        finish();
                                    }
                                });
                    }
                });
    }

    private void rejectRequest() {
        FirebaseFirestore getToken = FirebaseFirestore.getInstance();
        FirebaseFirestore getTouristID = FirebaseFirestore.getInstance();
        FirebaseFirestore updateOrderStatus = FirebaseFirestore.getInstance();
        FirebaseFirestore updateDriver = FirebaseFirestore.getInstance();
        FirebaseFirestore updateTourist = FirebaseFirestore.getInstance();
        FirebaseFirestore updateTouristCar = FirebaseFirestore.getInstance();

        //update order status
        Map<String,Object> order = new HashMap<>();
        order.put("orderStatus", "Rejected by Driver");

        updateOrderStatus.collection("Trip Details").document(orderID)
                .update(order);

        //delete date booked
        getTouristID.collection("Trip Details").document(orderID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        String tID = doc.getString("touristID");
                        String dID = doc.getString("driverID");
                        String cp = doc.getString("carPlate");
                        String startDate = doc.getString("startDate");
                        float duration = doc.getLong("duration");
                        int days = Math.round(duration);

                        /* calculate dates */
                        //initialize
                        ArrayList<String> dates = new ArrayList<>();
                        SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                        Calendar c = Calendar.getInstance();

                        //add start date
                        try {
                            dates.add(sdf.format((Objects.requireNonNull(input.parse(Objects.requireNonNull(startDate))))));
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

                        /*update driver date booked*/
                        for (int i = 0; i < dates.size(); i++) {
                            String dateID = dates.get(i);
                            updateDriver.collection("User Accounts").document(Objects.requireNonNull(dID)).collection("Date Booked").document(dateID)
                                    .delete();
                        }

                        /*update tourist date booked*/
                        for (int i = 0; i < dates.size(); i++) {
                            String dateID = dates.get(i);
                            updateTourist.collection("User Accounts").document(Objects.requireNonNull(tID)).collection("Date Booked").document(dateID)
                                    .delete();
                        }

                        /* update tourist car */
                        Map<String, Object> touristCar = new HashMap<>();
                        touristCar.put("carStatus", "Available");

                        updateTouristCar.collection("User Accounts").document(Objects.requireNonNull(tID)).collection("Car Details").document(Objects.requireNonNull(cp))
                                .update(touristCar);

                        /*send notification to tourist*/
                        getToken.collection("User Accounts").document(tID).get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot tourToken = task1.getResult();
                                        String token = tourToken.getString("notificationToken");

                                        UserFCMSend.pushNotification(
                                                DriverRequestDetails.this,
                                                token,
                                                "Request Rejected",
                                                "Your request have been rejected by the driver!",
                                                orderID);

                                        Toast.makeText(DriverRequestDetails.this, "Request rejected successfully!", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(DriverRequestDetails.this, DriverNavActivity.class));
                                        finishAffinity();
                                        finish();
                                    }
                                });
                    }
                });
    }

    private void cancelBooking() {
        FirebaseFirestore getToken = FirebaseFirestore.getInstance();
        FirebaseFirestore getTouristID = FirebaseFirestore.getInstance();
        FirebaseFirestore updateOrderStatus = FirebaseFirestore.getInstance();
        FirebaseFirestore updateDriver = FirebaseFirestore.getInstance();
        FirebaseFirestore updateTourist = FirebaseFirestore.getInstance();
        FirebaseFirestore updateTouristCar = FirebaseFirestore.getInstance();

        //update order status
        Map<String,Object> order = new HashMap<>();
        order.put("orderStatus", "Cancelled by Driver");

        updateOrderStatus.collection("Trip Details").document(orderID)
                .update(order);

        //delete date booked
        getTouristID.collection("Trip Details").document(orderID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        String tID = doc.getString("touristID");
                        String dID = doc.getString("driverID");
                        String cp = doc.getString("carPlate");
                        String startDate = doc.getString("startDate");
                        float duration = doc.getLong("duration");
                        int days = Math.round(duration);

                        /* calculate dates */
                        //initialize
                        ArrayList<String> dates = new ArrayList<>();
                        SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                        Calendar c = Calendar.getInstance();

                        //add start date
                        try {
                            dates.add(sdf.format((Objects.requireNonNull(input.parse(Objects.requireNonNull(startDate))))));
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

                        /*update driver date booked*/
                        for (int i = 0; i < dates.size(); i++) {
                            String dateID = dates.get(i);
                            updateDriver.collection("User Accounts").document(Objects.requireNonNull(dID)).collection("Date Booked").document(dateID)
                                    .delete();
                        }

                        /*update tourist date booked*/
                        for (int i = 0; i < dates.size(); i++) {
                            String dateID = dates.get(i);
                            updateTourist.collection("User Accounts").document(Objects.requireNonNull(tID)).collection("Date Booked").document(dateID)
                                    .delete();
                        }

                        /* update tourist car */
                        Map<String, Object> touristCar = new HashMap<>();
                        touristCar.put("carStatus", "Available");

                        updateTouristCar.collection("User Accounts").document(Objects.requireNonNull(tID)).collection("Car Details").document(Objects.requireNonNull(cp))
                                .update(touristCar);

                        /*send notification to tourist*/
                        getToken.collection("User Accounts").document(Objects.requireNonNull(tID)).get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot tourToken = task1.getResult();
                                        String token = tourToken.getString("notificationToken");

                                        UserFCMSend.pushNotification(
                                                DriverRequestDetails.this,
                                                token,
                                                "Booking Cancelled by Driver",
                                                "Driver have cancelled the booking!",
                                                orderID);

                                        Toast.makeText(DriverRequestDetails.this, "Booking cancelled successfully!", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(DriverRequestDetails.this, TouristNavActivity.class));
                                        finishAffinity();
                                        finish();
                                    }
                                });
                    }
                });
    }

    @Override
    public void onBackPressed() {
        String nav = getIntent().getStringExtra("navRate");

        if(nav.equals("Rate")){
            startActivity(new Intent(DriverRequestDetails.this, DriverNavRating.class));
        }
        else{
            startActivity(new Intent(DriverRequestDetails.this, DriverNavActivity.class));
        }
        finish();
    }
}