package com.example.finalyearproject_drivme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterCarList extends RecyclerView.Adapter<AdapterCarList.CarListViewHolder> {
    //declare variables
    Context carContext;
    ArrayList<String> carArrayList;
    SharedPreferences spDrivme;

    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    public AdapterCarList(Context carContext, ArrayList<String> carArrayList) {
        this.carContext = carContext;
        this.carArrayList = carArrayList;
    }

    @NonNull
    @Override
    public AdapterCarList.CarListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View carView = LayoutInflater.from(carContext).inflate(R.layout.item_car, parent, false);

        return new CarListViewHolder(carView);    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCarList.CarListViewHolder holder, int position) {
        String carPlate = carArrayList.get(position);
        spDrivme = carContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String uID = spDrivme.getString(KEY_ID, null);

        holder.mtvCarPlate.setText(carPlate);
        holder.mcvCarDetails.setOnClickListener(view -> {
            Intent intent = new Intent(carContext, TouristCarDetails.class);
            intent.putExtra("id", uID);
            intent.putExtra("carPlate", carPlate);

            carContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return carArrayList.size();
    }

    public static class CarListViewHolder extends RecyclerView.ViewHolder{
        //declare variables
        CardView mcvCarDetails;
        TextView mtvCarPlate;

        public CarListViewHolder(@NonNull View itemView) {
            super(itemView);

            //assign variables
            mtvCarPlate = itemView.findViewById(R.id.tvCarPlate);
            mcvCarDetails = itemView.findViewById(R.id.cvCarDetails);
        }
    }
}
