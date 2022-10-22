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

import com.example.finalyearproject_drivme.AdapterOngoingList;
import com.example.finalyearproject_drivme.ModelOngoingList;
import com.example.finalyearproject_drivme.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DriverOngoingFragment extends Fragment {
    //declare variables
    private ArrayList<ModelOngoingList> ongoingOrderList;
    private AdapterOngoingList onlAdapter;
    private FirebaseFirestore ongoingOrderDB;
    private SwipeRefreshLayout mswipeOngoing;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    public DriverOngoingFragment() {
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
        View ongoingView = inflater.inflate(R.layout.fragment_driver_ongoing, container, false);
        InitializeRequestCardView(ongoingView);

        return ongoingView;
    }

    private void InitializeRequestCardView(View ongoingView) {
        //declare variables
        RecyclerView mrvDOngoing = ongoingView.findViewById(R.id.rvDOngoing);
        mswipeOngoing = ongoingView.findViewById(R.id.swipeOngoing);
        mrvDOngoing.setLayoutManager(new LinearLayoutManager(ongoingView.getContext()));

        //initialize variables
        ongoingOrderDB = FirebaseFirestore.getInstance();
        ongoingOrderList = new ArrayList<>();

        //initialize adapter
        onlAdapter = new AdapterOngoingList(ongoingView.getContext(), ongoingOrderList);
        mrvDOngoing.setAdapter(onlAdapter);

        getOrderDetailsFromFirestore(ongoingView);

        //swipe down refresh
        mswipeOngoing.setOnRefreshListener(() -> {
            getOrderDetailsFromFirestore(ongoingView);
            mswipeOngoing.setRefreshing(false);
        });
    }

    /*check order status*/
    private void getOrderDetailsFromFirestore(View ongoingView) {
        SharedPreferences spDrivme = ongoingView.getContext().getSharedPreferences(SP_NAME, ongoingView.getContext().MODE_PRIVATE);
        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);

        //display category that belongs to ongoing
        ongoingOrderDB.collection("Trip Details")
                .whereEqualTo("driverID", uID)
                .whereIn("orderStatus", Arrays.asList("Coming Soon", "Trip Ongoing"))
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(ongoingView.getContext(), "Error Loading Request!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    ongoingOrderList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            ongoingOrderList.add(dc.getDocument().toObject(ModelOngoingList.class));
                        }
                    }

                    //if no records found
                    if(ongoingOrderList.size() == 0){
                        Toast.makeText(ongoingView.getContext(), "No ongoing orders!", Toast.LENGTH_SHORT).show();
                    }
                    
                    onlAdapter.notifyDataSetChanged();
                });
    }
}