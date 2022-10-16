package com.example.finalyearproject_drivme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterRatingList extends RecyclerView.Adapter<AdapterRatingList.RatingListViewHolder> {
    //declare variables
    Context rateContext;
    ArrayList<ModelRatingList> rateArrayList;

    public AdapterRatingList(Context rateContext, ArrayList<ModelRatingList> rateArrayList) {
        this.rateContext = rateContext;
        this.rateArrayList = rateArrayList;
    }

    @NonNull
    @Override
    public AdapterRatingList.RatingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rateView = LayoutInflater.from(rateContext).inflate(R.layout.item_activity_rating, parent, false);

        return new RatingListViewHolder(rateView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRatingList.RatingListViewHolder holder, int position) {
        //get position
        ModelRatingList mrl = rateArrayList.get(position);

        holder.morderRating.setRating(mrl.rateStar);
        holder.mtvCompliment.setText(mrl.rateCompliment);
        holder.mtvCNW.setText(mrl.rateComment);
        holder.mtvEarning.setText("RM " + String.format("%.2f", mrl.total));

        holder.mcvRatingDetails.setOnClickListener(view -> {
            Intent intent = new Intent(rateContext, DriverRequestDetails.class);
            intent.putExtra("orderID", mrl.orderID);
            intent.putExtra("navRate", "Rate");
            rateContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return rateArrayList.size();
    }

    public static class RatingListViewHolder extends RecyclerView.ViewHolder{
        //declare variables
        CardView mcvRatingDetails;
        RatingBar morderRating;
        TextView mtvCompliment, mtvCNW, mtvEarning;

        public RatingListViewHolder(@NonNull View itemView) {
            super(itemView);

            //assign variables
            mcvRatingDetails = itemView.findViewById(R.id.cvRatingDetails);
            morderRating = itemView.findViewById(R.id.orderRating);
            mtvCompliment = itemView.findViewById(R.id.tvCompliment);
            mtvCNW = itemView.findViewById(R.id.tvCNW);
            mtvEarning = itemView.findViewById(R.id.tvEarning);
        }
    }
}
