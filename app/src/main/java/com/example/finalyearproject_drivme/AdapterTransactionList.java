package com.example.finalyearproject_drivme;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterTransactionList extends RecyclerView.Adapter<AdapterTransactionList.TransactionListViewHolder> {
    //declare variables
    Context transactionContext;
    ArrayList<ModelTransactionList> transactionArrayList;

    public AdapterTransactionList(Context transactionContext, ArrayList<ModelTransactionList> transactionArrayList) {
        this.transactionContext = transactionContext;
        this.transactionArrayList = transactionArrayList;
    }

    @NonNull
    @Override
    public AdapterTransactionList.TransactionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View transView = LayoutInflater.from(transactionContext).inflate(R.layout.item_transaction, parent, false);

        return new TransactionListViewHolder(transView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterTransactionList.TransactionListViewHolder holder, int position) {
        //get position
        ModelTransactionList mtl = transactionArrayList.get(position);
        String type = mtl.transType;

        holder.mtvTransactionType.setText(mtl.transType);
        holder.mtvOrderID.setText(mtl.orderID);
        holder.mtvDateTime.setText(mtl.transDateTime);

        if(type.equals("Payment")) {
            holder.mtvAmount.setText("-RM" + mtl.transAmount);
            holder.mtvAmount.setTextColor(Color.parseColor("#FF0000"));
        }
        else{
            holder.mtvAmount.setText("+RM" + mtl.transAmount);
            holder.mtvAmount.setTextColor(Color.parseColor("#08F26E"));

        }
    }

    @Override
    public int getItemCount() {
        return transactionArrayList.size();
    }

    public static class TransactionListViewHolder extends RecyclerView.ViewHolder{
        //declare variables
        TextView mtvTransactionType, mtvDateTime, mtvAmount, mtvOrderID;

        public TransactionListViewHolder(@NonNull View itemView) {
            super(itemView);

            //assign variables
            mtvTransactionType = itemView.findViewById(R.id.tvTransactionType);
            mtvDateTime = itemView.findViewById(R.id.tvDateTime);
            mtvAmount = itemView.findViewById(R.id.tvAmount);
            mtvOrderID = itemView.findViewById(R.id.tvOrderID);
        }
    }
}
