package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class UserTransactionHistory extends AppCompatActivity {
    //declare variable
    RecyclerView mrvTransaction;
    ArrayList<ModelTransactionList> transactionList;
    AdapterTransactionList transactionAdapter;
    FirebaseFirestore transactionDB;
    SwipeRefreshLayout mswipeTransaction;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction_history);

        //assign variable
        mrvTransaction = findViewById(R.id.rvTransaction);
        mswipeTransaction = findViewById(R.id.swipeTransaction);
        mrvTransaction.setLayoutManager(new LinearLayoutManager(this));

        //initialize variables
        transactionDB = FirebaseFirestore.getInstance();
        transactionList = new ArrayList<>();

        //initialize adapter
        transactionAdapter = new AdapterTransactionList(this, transactionList);
        mrvTransaction.setAdapter(transactionAdapter);

        getTransactionDetailsFromFirestore();

        mswipeTransaction.setOnRefreshListener(() -> {
            getTransactionDetailsFromFirestore();
            mswipeTransaction.setRefreshing(false);
        });
    }

    private void getTransactionDetailsFromFirestore() {
        SharedPreferences spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);

        transactionDB.collection("User Accounts").document(uID).collection("Transaction History")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Toast.makeText(UserTransactionHistory.this, "Error Loading Transaction!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //clear list
                    transactionList.clear();

                    //use the id to check if the driver available within the duration requested
                    for(DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()){
                        if(dc.getType() == DocumentChange.Type.ADDED) {
                            transactionList.add(dc.getDocument().toObject(ModelTransactionList.class));
                        }
                    }

                    //if no records found
                    if(transactionList.size() == 0){
                        Toast.makeText(UserTransactionHistory.this, "No transaction!", Toast.LENGTH_SHORT).show();
                    }

                    transactionAdapter.notifyDataSetChanged();
                });
    }
}