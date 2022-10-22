package com.example.finalyearproject_drivme;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterHourDriverList extends RecyclerView.Adapter<AdapterHourDriverList.DriverListViewHolder> {
    //declare variables
    Context driverHourContext;
    ArrayList<ModelHourDriverList> driverArrayList;
    String orderID, touristID, date, startTime, endTime, carPlate, locality, address, comment, tripOption, dateID;
    int duration, hourStart;

    //constructor
    public AdapterHourDriverList(Context driverHourContext, ArrayList<ModelHourDriverList> driverArrayList, String orderID,
                                 String touristID, String date, String startTime, String endTime, String carPlate, String locality,
                                 String address, String comment, String tripOption, String dateID, int duration, int hourStart) {
        this.driverHourContext = driverHourContext;
        this.driverArrayList = driverArrayList;
        this.orderID = orderID;
        this.touristID = touristID;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.carPlate = carPlate;
        this.locality = locality;
        this.address = address;
        this.comment = comment;
        this.tripOption = tripOption;
        this.dateID = dateID;
        this.duration = duration;
        this.hourStart = hourStart;
    }

    @NonNull
    @Override
    public AdapterHourDriverList.DriverListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View driverView = LayoutInflater.from(driverHourContext).inflate(R.layout.item_driver, parent, false);

        return new DriverListViewHolder(driverView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHourDriverList.DriverListViewHolder holder, int position) {
        //get position
        ModelHourDriverList mhl = driverArrayList.get(position);
        String getGender = mhl.gender;

        //set values to display
        if(getGender.equals("Male")){
            holder.mivGender.setBackgroundResource(R.drawable.icon_male);
        }
        else{
            holder.mivGender.setBackgroundResource(R.drawable.icon_female);
        }

        holder.mtvName.setText(mhl.lastName + " " + mhl.firstName);
        holder.mtvRating.setText(String.valueOf(mhl.rating));
        holder.mtvPrice.setText("RM" + Math.round(mhl.priceHour) + " / Hour");
        holder.mrbDriver.setRating(mhl.rating);

        //driver list -> driver more details
        holder.mcvDetails.setOnClickListener(view -> {
            Intent intent = new Intent(driverHourContext, DriverHourMoreDetails.class);
            intent.putExtra("driverID", mhl.userID);
            intent.putExtra("rating", mhl.rating);
            intent.putExtra("priceHour", Math.round(mhl.priceHour));
            intent.putExtra("orderID", orderID);
            intent.putExtra("touristID", touristID);
            intent.putExtra("duration", duration);
            intent.putExtra("date", date);
            intent.putExtra("startTime", startTime);
            intent.putExtra("endTime", endTime);
            intent.putExtra("carPlate", carPlate);
            intent.putExtra("locality", locality);
            intent.putExtra("address", address);
            intent.putExtra("tripOption", tripOption);
            intent.putExtra("comment", comment);
            intent.putExtra("dateID", dateID);
            intent.putExtra("hourStart", hourStart);

            driverHourContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return driverArrayList.size();
    }

    public static class DriverListViewHolder extends RecyclerView.ViewHolder{
        //declare variables
        ImageView mivGender;
        TextView mtvName, mtvRating, mtvPrice;
        RatingBar mrbDriver;
        CardView mcvDetails;

        public DriverListViewHolder(@NonNull View itemView) {
            super(itemView);
            //assign variables
            mcvDetails = itemView.findViewById(R.id.cvDetails);
            mivGender = itemView.findViewById(R.id.ivGender);
            mtvName = itemView.findViewById(R.id.tvName);
            mtvRating = itemView.findViewById(R.id.tvRating);
            mtvPrice = itemView.findViewById(R.id.tvPrice);
            mrbDriver = itemView.findViewById(R.id.rbDriver);
        }
    }
}
