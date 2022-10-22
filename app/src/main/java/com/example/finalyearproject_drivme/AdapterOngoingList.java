package com.example.finalyearproject_drivme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdapterOngoingList extends RecyclerView.Adapter<AdapterOngoingList.OngoingListViewHolder>{
    //declare variables
    Context ongoingContext;
    ArrayList<ModelOngoingList> ongoingArrayList;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ROLE = "role";

    //constructor
    public AdapterOngoingList(Context ongoingContext, ArrayList<ModelOngoingList> ongoingArrayList) {
        this.ongoingContext = ongoingContext;
        this.ongoingArrayList = ongoingArrayList;
    }

    @NonNull
    @Override
    public AdapterOngoingList.OngoingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ongoingView = LayoutInflater.from(ongoingContext).inflate(R.layout.item_activity_ongoing, parent, false);

        return new OngoingListViewHolder(ongoingView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOngoingList.OngoingListViewHolder holder, int position) {
        //get position
        ModelOngoingList mol = ongoingArrayList.get(position);
        //get user id and trip option
        String tid = mol.touristID;
        String did = mol.driverID;
        String oid = mol.orderID;
        String tripOption = mol.tripOption;
        //get role
        spDrivme = ongoingContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String uRole = spDrivme.getString(KEY_ROLE, null);

        DateFormat slashFormat = new SimpleDateFormat("dd/MM/yyyy"); //convert string to date format
        DateFormat stickFormat = new SimpleDateFormat("ddMMyyyy"); //convert into date ID
        DateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm"); //record time of button clicked

        //set text based on the role
        if(uRole.equals("Driver")) {
            holder.mtvONameTitle.setText("Tourist Name:");
            FirebaseFirestore touristDB = FirebaseFirestore.getInstance();
            touristDB.collection("User Accounts").document(tid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            holder.mtvOName.setText(doc.getString("lastName") + " " + doc.getString("firstName"));
                        }
                    });
        }
        else{
            holder.mtvONameTitle.setText("Driver Name:");
            FirebaseFirestore driverDB = FirebaseFirestore.getInstance();
            driverDB.collection("User Accounts").document(did).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            holder.mtvOName.setText(doc.getString("lastName") + " " + doc.getString("firstName"));
                        }
                    });
        }
        holder.mtvOMeetDateTime.setText(mol.meetDate + " " + mol.meetTime);
        holder.mtvOLocation.setText(mol.locality);
        holder.mtvOStatus.setText(mol.orderStatus);

        FirebaseFirestore orderDB = FirebaseFirestore.getInstance();

        if(tripOption.equals("Day")) { //by day order
            orderDB.collection("Trip Details").document(oid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            int startStat = Objects.requireNonNull(doc.getLong("tripStart")).intValue();
                            int endStat = Objects.requireNonNull(doc.getLong("tripEnd")).intValue();

                            //get current date
                            String date = slashFormat.format(Calendar.getInstance().getTime());
                            String dateID = stickFormat.format(Calendar.getInstance().getTime());

                            if (startStat == 0) {
                                //if current date is start date
                                if (date.equals(mol.meetDate)) {
                                    Map<String, Object> start = new HashMap<>();
                                    start.put("tripStart", 1);
                                    start.put("orderStatus", "Trip Ongoing");

                                    FirebaseFirestore updateOrderStatus = FirebaseFirestore.getInstance();
                                    updateOrderStatus.collection("Trip Details").document(oid)
                                            .update(start);

                                    FirebaseFirestore setDay = FirebaseFirestore.getInstance();
                                    setDay.collection("Trip Details").document(oid).collection("Days").document(dateID).get()
                                            .addOnCompleteListener(task1 -> {
                                                DocumentSnapshot ds = task1.getResult();
                                                int dCheck = Objects.requireNonNull(ds.getLong("driverCheck")).intValue();
                                                int tCheck = Objects.requireNonNull(ds.getLong("touristCheck")).intValue();

                                                holder.mtvODay.setText(String.valueOf(Objects.requireNonNull(ds.getLong("numDay")).intValue()));

                                                //manage button for record
                                                if (dCheck == 0 && uRole.equals("Driver")) {
                                                    holder.mbtnHere.setVisibility(View.VISIBLE);
                                                } else if (dCheck == 1 && tCheck == 0 && uRole.equals("Tourist")) {
                                                    holder.mbtnStart.setVisibility(View.VISIBLE);
                                                }
                                            });
                                } else {
                                    holder.mtvODay.setText("-");
                                }
                            } else if (startStat == 1 && endStat == 0) {
                                FirebaseFirestore getDay = FirebaseFirestore.getInstance();
                                getDay.collection("Trip Details").document(oid).collection("Days").document(dateID).get()
                                        .addOnCompleteListener(task1 -> {
                                            DocumentSnapshot ds = task1.getResult();
                                            int dCheck = Objects.requireNonNull(ds.getLong("driverCheck")).intValue();
                                            int tCheck = Objects.requireNonNull(ds.getLong("touristCheck")).intValue();

                                            holder.mtvODay.setText(String.valueOf(Objects.requireNonNull(ds.getLong("numDay")).intValue()));

                                            if (dCheck == 0 && uRole.equals("Driver")) {
                                                holder.mbtnHere.setVisibility(View.VISIBLE);
                                            } else if (dCheck == 1 && tCheck == 0 && uRole.equals("Tourist")) {
                                                holder.mbtnStart.setVisibility(View.VISIBLE);
                                            } else if (dCheck == 1 && tCheck == 1 && uRole.equals("Tourist") && date.equals(mol.endDate)) { //if current date is end date
                                                holder.mbtnEndTrip.setVisibility(View.VISIBLE);
                                            }
                                        });
                            }
                        }
                    });
        }
        else{ //by hour order
            orderDB.collection("Trip Details").document(oid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            int startStat = Objects.requireNonNull(doc.getLong("tripStart")).intValue();

                            //get current date
                            String date = slashFormat.format(Calendar.getInstance().getTime());
                            String dateID = stickFormat.format(Calendar.getInstance().getTime());

                            if (startStat == 0) {
                                if (date.equals(mol.meetDate)) {
                                    Map<String, Object> start = new HashMap<>();
                                    start.put("tripStart", 1);
                                    start.put("orderStatus", "Trip Ongoing");

                                    FirebaseFirestore updateOrderStatus = FirebaseFirestore.getInstance();
                                    updateOrderStatus.collection("Trip Details").document(oid)
                                            .update(start);

                                    FirebaseFirestore getDay = FirebaseFirestore.getInstance();
                                    getDay.collection("Trip Details").document(oid).collection("Days").document(dateID).get()
                                            .addOnCompleteListener(task1 -> {
                                                DocumentSnapshot ds = task1.getResult();
                                                int dCheck = Objects.requireNonNull(ds.getLong("driverCheck")).intValue();
                                                int tCheck = Objects.requireNonNull(ds.getLong("touristCheck")).intValue();

                                                holder.mtvODay.setText(String.valueOf(Objects.requireNonNull(ds.getLong("numDay")).intValue()));

                                                if (dCheck == 0 && uRole.equals("Driver")) {
                                                    holder.mbtnHere.setVisibility(View.VISIBLE);
                                                } else if (dCheck == 1 && tCheck == 0 && uRole.equals("Tourist")) {
                                                    holder.mbtnStart.setVisibility(View.VISIBLE);
                                                } else if (dCheck == 1 && tCheck == 1 && uRole.equals("Tourist")) {
                                                    holder.mbtnEndTrip.setVisibility(View.VISIBLE);
                                                }
                                            });
                                } else {
                                    holder.mtvODay.setText("-");
                                }
                            }
                        }
                    });
        }

        //to notify tourist driver is here
        holder.mbtnHere.setOnClickListener(view -> {
            holder.mbtnHere.setVisibility(View.GONE);

            //get current date id
            String dateID = stickFormat.format(Calendar.getInstance().getTime());
            //get current date time
            String dateTime = fullFormat.format(Calendar.getInstance().getTime());

            Map<String, Object> checkDriver = new HashMap<>();
            checkDriver.put("driverCheck", 1);
            checkDriver.put("driverCheckTime", dateTime);

            //update driver status in the order
            FirebaseFirestore updateDCheck = FirebaseFirestore.getInstance();
            updateDCheck.collection("Trip Details").document(oid).collection("Days").document(dateID)
                    .update(checkDriver);

            /*send notification to tourist*/
            FirebaseFirestore getToken = FirebaseFirestore.getInstance();
            getToken.collection("User Accounts").document(tid).get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            DocumentSnapshot tourToken = task2.getResult();
                            String token = tourToken.getString("notificationToken");

                            UserFCMSend.pushNotification(
                                    ongoingContext,
                                    token,
                                    "I'm Here",
                                    "Hi, I'm already here at the meet-up location!");

                            Toast.makeText(ongoingContext, "Notified Tourist!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        //to ensure tourist meet the driver
        holder.mbtnStart.setOnClickListener(view -> {
            holder.mbtnStart.setVisibility(View.GONE);

            //get current date id
            String dateID = stickFormat.format(Calendar.getInstance().getTime());
            //get current date time
            String dateTime = fullFormat.format(Calendar.getInstance().getTime());

            Map<String, Object> checkTourist = new HashMap<>();
            checkTourist.put("touristCheck", 1);
            checkTourist.put("touristCheckTime", dateTime);

            FirebaseFirestore updateDCheck = FirebaseFirestore.getInstance();
            updateDCheck.collection("Trip Details").document(oid).collection("Days").document(dateID)
                    .update(checkTourist);

            /*send notification to driver*/
            FirebaseFirestore getToken = FirebaseFirestore.getInstance();
            getToken.collection("User Accounts").document(did).get()
                    .addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            DocumentSnapshot tourToken = task2.getResult();
                            String token = tourToken.getString("notificationToken");

                            UserFCMSend.pushNotification(
                                    ongoingContext,
                                    token,
                                    "Trip Started",
                                    "Tourist checked in today's trip! Enjoy the trip!");

                            Toast.makeText(ongoingContext, "Trip starts now! Enjoy the trip!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        //end the trip and go to rating page
        holder.mbtnEndTrip.setOnClickListener(view -> {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ongoingContext);
            alertDialogBuilder.setTitle("End Trip");
            alertDialogBuilder
                    .setMessage("Do you wish to end the trip right now?")
                    .setCancelable(false)
                    .setPositiveButton("End Trip", (dialog, id) -> {
                        Intent rating = new Intent(ongoingContext, TouristRating.class);
                        rating.putExtra("orderID", oid);
                        rating.putExtra("driverID", did);
                        rating.putExtra("total", mol.total);

                        ongoingContext.startActivity(rating);
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());

            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        holder.mcvTripDetails.setOnClickListener(view -> {
            Intent intent;
            if(uRole.equals("Tourist")) {
                if(tripOption.equals("Day")) {
                    intent = new Intent(ongoingContext, TouristBookingDayDetails.class);
                }
                else{
                    intent = new Intent(ongoingContext, TouristBookingHourDetails.class);
                }
            }
            else {
                if(tripOption.equals("Day")) {
                    intent = new Intent(ongoingContext, DriverRequestDayDetails.class);
                }
                else{
                    intent = new Intent(ongoingContext, DriverRequestHourDetails.class);
                }
                intent.putExtra("navRate", "Request");

            }
            intent.putExtra("orderID", mol.orderID);
            intent.putExtra("orderStatus", mol.orderStatus);
            ongoingContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ongoingArrayList.size();
    }

    public static class OngoingListViewHolder extends RecyclerView.ViewHolder{
        //declare variables
        TextView mtvONameTitle, mtvOName, mtvOMeetDateTime, mtvOLocation, mtvODay, mtvOStatus;
        CardView mcvTripDetails;
        Button mbtnHere, mbtnStart, mbtnEndTrip;

        public OngoingListViewHolder(@NonNull View itemView) {
            super(itemView);

            //assign variables
            mtvONameTitle = itemView.findViewById(R.id.tvONameTitle);
            mtvOName = itemView.findViewById(R.id.tvOName);
            mtvOMeetDateTime = itemView.findViewById(R.id.tvOMeetDateTime);
            mtvOLocation = itemView.findViewById(R.id.tvOLocation);
            mtvODay = itemView.findViewById(R.id.tvODay);
            mtvOStatus = itemView.findViewById(R.id.tvOStatus);
            mcvTripDetails = itemView.findViewById(R.id.cvTripDetails);
            mbtnHere = itemView.findViewById(R.id.btnHere);
            mbtnStart = itemView.findViewById(R.id.btnStart);
            mbtnEndTrip = itemView.findViewById(R.id.btnEndTrip);
        }
    }
}
