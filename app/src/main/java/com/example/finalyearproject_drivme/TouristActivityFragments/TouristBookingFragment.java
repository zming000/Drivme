package com.example.finalyearproject_drivme.TouristActivityFragments;

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

public class TouristBookingFragment extends Fragment {
    //declare variables
    private ArrayList<ModelRequestList> pendingList;
    private AdapterOrderList plAdapter;
    private FirebaseFirestore pendingDB;
    private SwipeRefreshLayout mswipeRequest;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    public TouristBookingFragment() {
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
        View pendingView = inflater.inflate(R.layout.fragment_driver_request, container, false);
        InitializeRequestCardView(pendingView);

        return pendingView;
    }

    private void InitializeRequestCardView(View pendingView) {
        //assign variables
        mswipeRequest = pendingView.findViewById(R.id.swipeRequest);
        //declare variables
        RecyclerView mrvDRequest = pendingView.findViewById(R.id.rvDRequest);
        mrvDRequest.setLayoutManager(new LinearLayoutManager(pendingView.getContext()));

        //initialize variables
        pendingDB = FirebaseFirestore.getInstance();
        pendingList = new ArrayList<>();

        //initialize adapter
        plAdapter = new AdapterOrderList(pendingView.getContext(), pendingList);
        mrvDRequest.setAdapter(plAdapter);

        getRequestDetailsFromFirestore(pendingView);

        //swipe down refresh
        mswipeRequest.setOnRefreshListener(() -> {
            getRequestDetailsFromFirestore(pendingView);
            mswipeRequest.setRefreshing(false);
        });
    }

    /*check order status*/
    private void getRequestDetailsFromFirestore(View v) {
        SharedPreferences spDrivme = v.getContext().getSharedPreferences(SP_NAME, v.getContext().MODE_PRIVATE);
        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);

        //display category that belongs to pending
        pendingDB.collection("Trip Details")
                .whereEqualTo("touristID", uID)
                .whereIn("orderStatus", Arrays.asList("Pending Driver Accept", "Pending Tourist Payment"))
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(v.getContext(), "Error Loading Request!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    pendingList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){

                        if(dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            pendingList.add(dc.getDocument().toObject(ModelRequestList.class));
                        }
                    }

                    plAdapter.notifyDataSetChanged();
                });
    }
}