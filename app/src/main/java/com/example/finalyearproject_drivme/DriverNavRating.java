package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DriverNavRating extends AppCompatActivity {
    //declare variable
    RecyclerView mrvDRating;
    ArrayList<ModelRatingList> rateList;
    AdapterRatingList rateAdapter;
    FirebaseFirestore rateDB;
    SwipeRefreshLayout mswipeRating;
    BottomNavigationView mbtmDNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_nav_rating);

        //assign variable
        mrvDRating = findViewById(R.id.rvDRating);
        mswipeRating = findViewById(R.id.swipeRating);
        mbtmDNav = findViewById(R.id.btmDNav);
        mrvDRating.setLayoutManager(new LinearLayoutManager(this));

        //initialize varaibles
        rateDB = FirebaseFirestore.getInstance();
        rateList = new ArrayList<>();

        //initialize adapter
        rateAdapter = new AdapterRatingList(this, rateList);
        mrvDRating.setAdapter(rateAdapter);

        getOrderDetailsFromFirestore();
        navSelection();

        mswipeRating.setOnRefreshListener(() -> {
            getOrderDetailsFromFirestore();
            mswipeRating.setRefreshing(false);
        });
    }

    private void getOrderDetailsFromFirestore() {
        rateDB.collection("Trip Details")
                .whereEqualTo("orderStatus", "Trip Finished")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(DriverNavRating.this, "Error Loading Request!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    rateList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            rateList.add(dc.getDocument().toObject(ModelRatingList.class));
                        }
                    }
                    rateAdapter.notifyDataSetChanged();
                });
    }

    private void navSelection() {
        //set homepage selected
        mbtmDNav.setSelectedItemId(R.id.rating);

        //perform listener
        mbtmDNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.activity:
                    startActivity(new Intent(getApplicationContext(), DriverNavActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.rating:
                    return true;
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), DriverNavHomepage.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), DriverNavSettings.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
            }
            return false;
        });
    }

    //quit application
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Leaving Drivme?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    finishAffinity();
                    finish();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}