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

public class AdapterDayDriverList extends RecyclerView.Adapter<AdapterDayDriverList.DriverListViewHolder> {
    //declare variables
    Context driverContext;
    ArrayList<ModelDayDriverList> driverArrayList;
    String orderID, touristID, meetDate, endDate, meetTime, carPlate, locality, address, comment, tripOption, dateID;
    int duration;

    //constructor
    public AdapterDayDriverList(Context driverContext, ArrayList<ModelDayDriverList> driverArrayList, String orderID, String touristID, int duration,
                                String meetDate, String endDate, String meetTime, String carPlate, String locality, String address, String comment,
                                String tripOption, String dateID) {
        this.driverContext = driverContext;
        this.driverArrayList = driverArrayList;
        this.orderID = orderID;
        this.touristID = touristID;
        this.duration = duration;
        this.meetDate = meetDate;
        this.endDate = endDate;
        this.meetTime = meetTime;
        this.carPlate = carPlate;
        this.locality = locality;
        this.address = address;
        this.comment = comment;
        this.tripOption = tripOption;
        this.dateID = dateID;
    }

    @NonNull
    @Override
    public AdapterDayDriverList.DriverListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View driverView = LayoutInflater.from(driverContext).inflate(R.layout.item_driver, parent, false);

        return new DriverListViewHolder(driverView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDayDriverList.DriverListViewHolder holder, int position) {
        //get position
        ModelDayDriverList mdl = driverArrayList.get(position);
        String getGender = mdl.gender;

        //set values to display
        if(getGender.equals("Male")){
            holder.mivGender.setBackgroundResource(R.drawable.icon_male);
        }
        else{
            holder.mivGender.setBackgroundResource(R.drawable.icon_female);
        }

        holder.mtvName.setText(mdl.lastName + " " + mdl.firstName);
        holder.mtvRating.setText(String.valueOf(mdl.rating));
        holder.mtvPrice.setText("RM" + Math.round(mdl.priceDay) + " / Day");
        holder.mrbDriver.setRating(mdl.rating);

        //driver list -> driver more details
        holder.mcvDetails.setOnClickListener(view -> {
            Intent intent = new Intent(driverContext, DriverDayMoreDetails.class);
            intent.putExtra("driverID", mdl.userID);
            intent.putExtra("rating", mdl.rating);
            intent.putExtra("priceDay", Math.round(mdl.priceDay));
            intent.putExtra("orderID", orderID);
            intent.putExtra("touristID", touristID);
            intent.putExtra("duration", duration);
            intent.putExtra("meetDate", meetDate);
            intent.putExtra("endDate", endDate);
            intent.putExtra("meetTime", meetTime);
            intent.putExtra("carPlate", carPlate);
            intent.putExtra("locality", locality);
            intent.putExtra("address", address);
            intent.putExtra("tripOption", tripOption);
            intent.putExtra("comment", comment);
            intent.putExtra("dateID", dateID);

            driverContext.startActivity(intent);
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
