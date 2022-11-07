package com.example.finalyearproject_drivme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterOrderList extends RecyclerView.Adapter<AdapterOrderList.RequestListViewHolder> {
    //declare variables
    Context reqContext;
    ArrayList<ModelRequestList> reqArrayList;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ROLE = "role";

    //constructor
    public AdapterOrderList(Context reqContext, ArrayList<ModelRequestList> reqArrayList) {
        this.reqContext = reqContext;
        this.reqArrayList = reqArrayList;
    }

    @NonNull
    @Override
    public AdapterOrderList.RequestListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View reqView = LayoutInflater.from(reqContext).inflate(R.layout.item_activity_request, parent, false);

        return new RequestListViewHolder(reqView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderList.RequestListViewHolder holder, int position) {
        //get position
        ModelRequestList mrl = reqArrayList.get(position);
        //get user id and trip option
        String tid = mrl.touristID;
        String did = mrl.driverID;
        String tripOpt = mrl.tripOption;
        String textStatus = mrl.orderStatus;
        //get role
        spDrivme = reqContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String uRole = spDrivme.getString(KEY_ROLE, null);

        //set text based on the role
        if(uRole.equals("Driver")) {
            holder.mtvNameTitle.setText("Tourist Name:");
            FirebaseFirestore touristDB = FirebaseFirestore.getInstance();
            touristDB.collection("User Accounts").document(tid).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            holder.mtvName.setText(doc.getString("lastName") + " " + doc.getString("firstName"));
                        }
                    });
        }
        else{
            holder.mtvNameTitle.setText("Driver Name:");
            FirebaseFirestore driverDB = FirebaseFirestore.getInstance();
            driverDB.collection("User Accounts").document(did).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            holder.mtvName.setText(doc.getString("lastName") + " " + doc.getString("firstName"));
                        }
                    });
        }

        holder.mtvMeetDateTime.setText(mrl.meetDate + " " + mrl.meetTime);
        holder.mtvLocation.setText(mrl.locality);

        //change text color based on the status
        if(textStatus.equals("Pending Driver Accept")){
            holder.mtvStatus.setTextColor(Color.parseColor("#D35400"));
        }
        else if(textStatus.equals("Pending Tourist Payment")){
            holder.mtvStatus.setTextColor(Color.parseColor("#F1C40F"));
        }
        else if(textStatus.equals("Trip Finished")){
            holder.mtvStatus.setTextColor(Color.parseColor("#08F26E"));
        }
        else{
            holder.mtvStatus.setTextColor(Color.parseColor("#FF0000"));
        }

        holder.mtvStatus.setText(mrl.orderStatus);

        if(tripOpt.equals("Day")) {
            holder.mtvPrice.setText("RM" + Math.round(mrl.total) + " (RM" + Math.round(mrl.priceDriver) + " / day)");
        }
        else{
            holder.mtvPrice.setText("RM" + Math.round(mrl.total) + " (RM" + Math.round(mrl.priceDriver) + " / hour)");
        }


        holder.mcvReqDetails.setOnClickListener(view -> {
            Intent intent;
            if(uRole.equals("Tourist")) {
                if(tripOpt.equals("Day")) {
                    intent = new Intent(reqContext, TouristBookingDayDetails.class);
                }
                else{
                    intent = new Intent(reqContext, TouristBookingHourDetails.class);
                }
            }
            else {
                if(tripOpt.equals("Day")) {
                    intent = new Intent(reqContext, DriverRequestDayDetails.class);
                }
                else{
                    intent = new Intent(reqContext, DriverRequestHourDetails.class);
                }
                intent.putExtra("navRate", "Order");

            }
            intent.putExtra("orderID", mrl.orderID);
            intent.putExtra("orderStatus", mrl.orderStatus);
            reqContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reqArrayList.size();
    }

    public static class RequestListViewHolder extends RecyclerView.ViewHolder{
        //declare variables
        TextView mtvNameTitle, mtvName, mtvMeetDateTime, mtvLocation, mtvPrice, mtvStatus;
        CardView mcvReqDetails;

        public RequestListViewHolder(@NonNull View itemView) {
            super(itemView);

            //assign variables
            mtvNameTitle = itemView.findViewById(R.id.tvNameTitle);
            mtvName = itemView.findViewById(R.id.tvName);
            mtvMeetDateTime = itemView.findViewById(R.id.tvMeetDateTime);
            mtvLocation = itemView.findViewById(R.id.tvLocation);
            mtvPrice = itemView.findViewById(R.id.tvPrice);
            mtvStatus = itemView.findViewById(R.id.tvStatus);
            mcvReqDetails = itemView.findViewById(R.id.cvReqDetails);
        }
    }
}
