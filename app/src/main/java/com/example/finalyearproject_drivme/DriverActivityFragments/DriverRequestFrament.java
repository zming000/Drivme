package com.example.finalyearproject_drivme.DriverActivityFragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalyearproject_drivme.AdapterOrderList;
import com.example.finalyearproject_drivme.ModelRequestList;
import com.example.finalyearproject_drivme.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DriverRequestFrament extends Fragment {
    //declare variables
    private ArrayList<ModelRequestList> requestList;
    private AdapterOrderList reqlAdapter;
    private FirebaseFirestore requestDB;
    private SwipeRefreshLayout mswipeRequest;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    public DriverRequestFrament() {
        //empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View requestView = inflater.inflate(R.layout.fragment_driver_request, container, false);
        InitializeRequestCardView(requestView);

        return requestView;
    }

    private void InitializeRequestCardView(View requestView) {
        //declare variables
        RecyclerView mrvDRequest = requestView.findViewById(R.id.rvDRequest);
        mswipeRequest = requestView.findViewById(R.id.swipeRequest);
        mrvDRequest.setLayoutManager(new LinearLayoutManager(requestView.getContext()));

        //initialize variables
        requestDB = FirebaseFirestore.getInstance();
        requestList = new ArrayList<>();

        //initialize adapter
        reqlAdapter = new AdapterOrderList(requestView.getContext(), requestList);
        mrvDRequest.setAdapter(reqlAdapter);

        getRequestDetailsFromFirestore(requestView);

        //swipe down refresh
        mswipeRequest.setOnRefreshListener(() -> {
            getRequestDetailsFromFirestore(requestView);
            mswipeRequest.setRefreshing(false);
        });
    }

    /*check order status*/
    private void getRequestDetailsFromFirestore(View v) {
        SharedPreferences spDrivme = v.getContext().getSharedPreferences(SP_NAME, v.getContext().MODE_PRIVATE);
        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);

        //display category that belongs to request
        requestDB.collection("Trip Details")
                .whereEqualTo("driverID", uID)
                .whereIn("orderStatus", Arrays.asList("Pending Driver Accept", "Pending Tourist Payment"))
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(v.getContext(), "Error Loading Request!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    requestList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){

                        if(dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            requestList.add(dc.getDocument().toObject(ModelRequestList.class));
                        }
                    }

                    //if no records found
                    if(requestList.size() == 0){
                        Toast.makeText(v.getContext(), "No request found!", Toast.LENGTH_SHORT).show();
                    }

                    reqlAdapter.notifyDataSetChanged();
                });
    }
}