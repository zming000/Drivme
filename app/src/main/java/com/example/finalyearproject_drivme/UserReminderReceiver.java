package com.example.finalyearproject_drivme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserReminderReceiver extends BroadcastReceiver {
    public UserReminderReceiver() {/*empty constructor*/}

    @Override
    public void onReceive(Context context, Intent intent) {
        String tid = intent.getStringExtra("touristID");
        String did = intent.getStringExtra("driverID");

        /*send notification to tourist*/
        FirebaseFirestore getTouristToken = FirebaseFirestore.getInstance();
        getTouristToken.collection("User Accounts").document(tid).get()
                .addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        DocumentSnapshot tourToken = task2.getResult();
                        String token = tourToken.getString("notificationToken");

                        UserFCMSend.pushNotification(
                                context,
                                token,
                                "Tourist Trip Reminder",
                                "Hope you enjoy tomorrow's trip!");
                    }
                });

        /*send notification to driver*/
        FirebaseFirestore getDriverToken = FirebaseFirestore.getInstance();
        getDriverToken.collection("User Accounts").document(did).get()
                .addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        DocumentSnapshot driToken = task2.getResult();
                        String token = driToken.getString("notificationToken");

                        UserFCMSend.pushNotification(
                                context,
                                token,
                                "Driver Trip Reminder",
                                "Hope you enjoy tomorrow's trip!");
                    }
                });
    }
}
