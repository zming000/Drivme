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

import com.example.finalyearproject_drivme.AdapterOngoingList;
import com.example.finalyearproject_drivme.ModelOngoingList;
import com.example.finalyearproject_drivme.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class TouristOngoingFragment extends Fragment {
    //declare variables
    private ArrayList<ModelOngoingList> ongoingList;
    private AdapterOngoingList olAdapter;
    private FirebaseFirestore ongoingDB;
    private SwipeRefreshLayout mswipeOngoing;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    public TouristOngoingFragment() {
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
        View ongoingOrderView = inflater.inflate(R.layout.fragment_tourist_ongoing, container, false);
        InitializeRequestCardView(ongoingOrderView);

        return ongoingOrderView;
    }

    private void InitializeRequestCardView(View ongoingView) {
        //declare variables
        RecyclerView mrvTOngoing = ongoingView.findViewById(R.id.rvTOngoing);
        mswipeOngoing = ongoingView.findViewById(R.id.swipeOngoing);
        mrvTOngoing.setLayoutManager(new LinearLayoutManager(ongoingView.getContext()));

        //initialize variables
        ongoingDB = FirebaseFirestore.getInstance();
        ongoingList = new ArrayList<>();

        //initialize adapter
        olAdapter = new AdapterOngoingList(ongoingView.getContext(), ongoingList);
        mrvTOngoing.setAdapter(olAdapter);

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
        ongoingDB.collection("Trip Details")
                .whereEqualTo("touristID", uID)
                .whereIn("orderStatus", Arrays.asList("Coming Soon", "Trip Ongoing"))
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(ongoingView.getContext(), "Error Loading Request!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    ongoingList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            ongoingList.add(dc.getDocument().toObject(ModelOngoingList.class));
                        }
                    }

                    olAdapter.notifyDataSetChanged();
                });
    }
}