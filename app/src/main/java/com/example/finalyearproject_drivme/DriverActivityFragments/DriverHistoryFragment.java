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

public class DriverHistoryFragment extends Fragment {
    //declare variables
    private ArrayList<ModelRequestList> historyOrderList;
    private AdapterOrderList olAdapter;
    private FirebaseFirestore historyOrderDB;
    private SwipeRefreshLayout mswipeRequest;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    public DriverHistoryFragment() {
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
        View historyView = inflater.inflate(R.layout.fragment_driver_request, container, false);
        InitializeRequestCardView(historyView);

        return historyView;
    }

    private void InitializeRequestCardView(View historyView) {
        //declare variables
        RecyclerView mrvDRequest = historyView.findViewById(R.id.rvDRequest);
        mswipeRequest = historyView.findViewById(R.id.swipeRequest);
        mrvDRequest.setLayoutManager(new LinearLayoutManager(historyView.getContext()));

        //initialize variables
        historyOrderDB = FirebaseFirestore.getInstance();
        historyOrderList = new ArrayList<>();

        //initialize adapter
        olAdapter = new AdapterOrderList(historyView.getContext(), historyOrderList);
        mrvDRequest.setAdapter(olAdapter);

        getRequestDetailsFromFirestore(historyView);

        //swipe down refresh
        mswipeRequest.setOnRefreshListener(() -> {
            getRequestDetailsFromFirestore(historyView);
            mswipeRequest.setRefreshing(false);
        });
    }

    /*check order status*/
    private void getRequestDetailsFromFirestore(View historyView) {
        SharedPreferences spDrivme = historyView.getContext().getSharedPreferences(SP_NAME, historyView.getContext().MODE_PRIVATE);
        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);

        //display category that belongs to history
        historyOrderDB.collection("Trip Details")
                .whereEqualTo("driverID", uID)
                .whereIn("orderStatus", Arrays.asList("Rejected by Driver", "Cancelled by Tourist", "Cancelled by Driver", "Trip Finished"))
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(historyView.getContext(), "Error Loading Request!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    historyOrderList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            historyOrderList.add(dc.getDocument().toObject(ModelRequestList.class));
                        }
                    }

                    //if no records found
                    if(historyOrderList.size() == 0){
                        Toast.makeText(historyView.getContext(), "No history records found!", Toast.LENGTH_SHORT).show();
                    }

                    olAdapter.notifyDataSetChanged();
                });
    }
}