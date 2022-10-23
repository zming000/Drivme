package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class TouristNavCars extends AppCompatActivity {
    //declare variable
    FloatingActionButton mbtnAddCar;
    RecyclerView mrvTCars;
    ArrayList<String> carList;
    AdapterCarList carAdapter;
    FirebaseFirestore carDB;
    SwipeRefreshLayout mswipeCars;
    BottomNavigationView mbtmTNav;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_nav_cars);

        //assign variable
        mbtnAddCar = findViewById(R.id.btnAddCar);
        mrvTCars = findViewById(R.id.rvTCars);
        mswipeCars = findViewById(R.id.swipeCars);
        mbtmTNav = findViewById(R.id.btmTNav);
        mrvTCars.setLayoutManager(new LinearLayoutManager(this));

        //initialize varaibles
        carDB = FirebaseFirestore.getInstance();
        carList = new ArrayList<>();

        //initialize adapter
        carAdapter = new AdapterCarList(this, carList);
        mrvTCars.setAdapter(carAdapter);

        getCarDetailsFromFirestore();
        navSelection();

        //go to add car ui
        mbtnAddCar.setOnClickListener(view -> startActivity(new Intent(TouristNavCars.this, TouristInputCar.class)));

        //swipe down refresh
        mswipeCars.setOnRefreshListener(() -> {
            getCarDetailsFromFirestore();
            mswipeCars.setRefreshing(false);
        });
    }

    private void getCarDetailsFromFirestore() {
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);

        carDB.collection("User Accounts").document(uID).collection("Car Details")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(TouristNavCars.this, "Error Loading Request!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    carList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED) {
                            carList.add(dc.getDocument().getId());
                        }
                    }

                    carAdapter.notifyDataSetChanged();
                });
    }

    private void navSelection() {
        //set homepage selected
        mbtmTNav.setSelectedItemId(R.id.cars);

        //perform listener
        mbtmTNav.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.activity:
                    startActivity(new Intent(getApplicationContext(), TouristNavActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.cars:
                    return true;
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), TouristNavHomepage.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                case R.id.settings:
                    startActivity(new Intent(getApplicationContext(), TouristNavSettings.class));
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