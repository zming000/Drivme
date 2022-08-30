package com.example.finalyearproject_drivme;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TouristIntroAdapter extends RecyclerView.Adapter<TouristIntroAdapter.TouristIntroViewHolder>{

    @NonNull
    @Override
    public TouristIntroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TouristIntroViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class TouristIntroViewHolder extends RecyclerView.ViewHolder{
        TouristIntroViewHolder(@NonNull View itemView){
            super(itemView);
        }
    }
}
