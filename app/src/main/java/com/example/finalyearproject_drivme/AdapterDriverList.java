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

public class AdapterDriverList extends RecyclerView.Adapter<AdapterDriverList.DriverListViewHolder> {
    //declare variables
    Context driverContext;
    ArrayList<ModelDriverList> driverArrayList;

    //constructor
    public AdapterDriverList(Context driverContext, ArrayList<ModelDriverList> driverArrayList) {
        this.driverContext = driverContext;
        this.driverArrayList = driverArrayList;
    }

    @NonNull
    @Override
    public AdapterDriverList.DriverListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View driverView = LayoutInflater.from(driverContext).inflate(R.layout.item_driver, parent, false);

        return new DriverListViewHolder(driverView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDriverList.DriverListViewHolder holder, int position) {
        //get position
        ModelDriverList mdl = driverArrayList.get(position);
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
        holder.mtvPrice.setText(mdl.priceDay + " / Day");
        holder.mrbDriver.setRating(mdl.rating);

        //driver list -> driver more details
        holder.mcvDetails.setOnClickListener(view -> {
            Intent intent = new Intent(driverContext, DriverMoreDetails.class);
            intent.putExtra("driverID", mdl.userID);
            intent.putExtra("rating", mdl.rating);

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
