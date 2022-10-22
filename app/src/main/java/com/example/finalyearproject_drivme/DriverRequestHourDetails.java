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

public class DriverRequestHourDetails extends AppCompatActivity {
    //declare variables
    TextView mtvOrderID, mtvName, mtvContact, mtvTripOption, mtvMeetDate, mtvMeetTime, mtvStartTime,
            mtvEndTime, mtvDuration, mtvLocality, mtvAddress, mtvCarPlate, mtvCarModel, mtvCarColour,
            mtvCarTrans, mtvPetrolCompany, mtvPetrolType, mtvNotes, mtvPriceHour, mtvTotal;
    ImageView mivCopy;
    Button mbtnReject, mbtnAccept, mbtnCancel;
    String orderID;
    FirebaseFirestore touristDetails, tripDetails, carDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request_hour_details);

        //assign variables
        mtvOrderID = findViewById(R.id.tvOrderID);
        mtvName = findViewById(R.id.tvName);
        mtvContact = findViewById(R.id.tvContact);
        mtvTripOption = findViewById(R.id.tvTripOption);
        mtvMeetDate = findViewById(R.id.tvMeetDate);
        mtvMeetTime = findViewById(R.id.tvMeetTime);
        mtvStartTime = findViewById(R.id.tvStartTime);
        mtvEndTime = findViewById(R.id.tvEndTime);
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
        mtvPriceHour = findViewById(R.id.tvPriceHour);
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

                        if(Objects.requireNonNull(status).equals("Rejected by Driver") || status.equals("Cancelled by Tourist") || status.equals("Cancelled by Driver")
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
                        mtvMeetDate.setText(doc.getString("meetDate"));
                        mtvMeetTime.setText(doc.getString("meetTime"));
                        mtvStartTime.setText(doc.getString("meetTime"));
                        mtvEndTime.setText(doc.getString("endTime"));

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
                        mtvPriceHour.setText(String.valueOf(Objects.requireNonNull(doc.getLong("priceDriver")).intValue()));
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

            Toast.makeText(DriverRequestHourDetails.this, "Copied address to clipboard!", Toast.LENGTH_SHORT).show();
        });

        mbtnReject.setOnClickListener(view -> {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverRequestHourDetails.this);
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
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverRequestHourDetails.this);
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
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DriverRequestHourDetails.this);
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
                        getToken.collection("User Accounts").document(Objects.requireNonNull(tID)).get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot tourToken = task1.getResult();
                                        String token = tourToken.getString("notificationToken");

                                        UserFCMSend.pushNotification(
                                                DriverRequestHourDetails.this,
                                                token,
                                                "Booking Accepted",
                                                "Your request have been accepted by the driver!");

                                        Toast.makeText(DriverRequestHourDetails.this, "Request accepted successfully!", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(DriverRequestHourDetails.this, DriverNavActivity.class));
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
        FirebaseFirestore checkDriverTime = FirebaseFirestore.getInstance();
        FirebaseFirestore checkTouristTime = FirebaseFirestore.getInstance();
        FirebaseFirestore deleteDriverTime = FirebaseFirestore.getInstance();
        FirebaseFirestore deleteTouristTime = FirebaseFirestore.getInstance();

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

                        //calculate and add hour into arraylist
                        for(int i = 0; i < 19; i++) {
                            //add 1 hour
                            allTime.add(String.valueOf(5 + i));
                        }

                        //calculate and add hour into arraylist
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

                        /*send notification to tourist*/
                        getToken.collection("User Accounts").document(tID).get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot tourToken = task1.getResult();
                                        String token = tourToken.getString("notificationToken");

                                        UserFCMSend.pushNotification(
                                                DriverRequestHourDetails.this,
                                                token,
                                                "Request Rejected",
                                                "Your request have been rejected by the driver!");

                                        Toast.makeText(DriverRequestHourDetails.this, "Request rejected successfully!", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(DriverRequestHourDetails.this, DriverNavActivity.class));
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
        FirebaseFirestore checkDriverTime = FirebaseFirestore.getInstance();
        FirebaseFirestore checkTouristTime = FirebaseFirestore.getInstance();
        FirebaseFirestore deleteDriverTime = FirebaseFirestore.getInstance();
        FirebaseFirestore deleteTouristTime = FirebaseFirestore.getInstance();

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

                        //calculate and add hour into arraylist
                        for(int i = 0; i < 19; i++) {
                            //add 1 hour
                            allTime.add(String.valueOf(5 + i));
                        }

                        //calculate and add hour into arraylist
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

                        /*send notification to tourist*/
                        getToken.collection("User Accounts").document(Objects.requireNonNull(tID)).get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot tourToken = task1.getResult();
                                        String token = tourToken.getString("notificationToken");

                                        UserFCMSend.pushNotification(
                                                DriverRequestHourDetails.this,
                                                token,
                                                "Booking Cancelled by Driver",
                                                "Driver have cancelled the booking!");

                                        Toast.makeText(DriverRequestHourDetails.this, "Booking cancelled successfully!", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(DriverRequestHourDetails.this, DriverNavActivity.class));
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
            startActivity(new Intent(DriverRequestHourDetails.this, DriverNavRating.class));
        }
        else{
            startActivity(new Intent(DriverRequestHourDetails.this, DriverNavActivity.class));
        }
        finish();
    }
}