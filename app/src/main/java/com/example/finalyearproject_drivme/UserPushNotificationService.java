package com.example.finalyearproject_drivme;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class UserPushNotificationService extends FirebaseMessagingService {
    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String noTitle = Objects.requireNonNull(message.getNotification()).getTitle();
        String noText = message.getNotification().getBody();
        String CHANNEL_ID = "MESSAGE";

        NotificationChannel noChannel = new NotificationChannel(
                CHANNEL_ID,
                "Message Notification",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(noChannel);

        Notification.Builder noBuilder = new Notification.Builder(this, CHANNEL_ID);

        if(Objects.requireNonNull(noTitle).equals("New Request") ||
                Objects.requireNonNull(noTitle).equals("Booking Cancelled by Tourist") ||
                Objects.requireNonNull(noTitle).equals("Booking Paid") ||
                Objects.requireNonNull(noTitle).equals("Trip Started") ||
                Objects.requireNonNull(noTitle).equals("Trip Ended") ||
                Objects.requireNonNull(noTitle).equals("Driver Trip Reminder")) {
            Intent reqIntent = new Intent(getApplicationContext(), DriverNavActivity.class);
            reqIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent orderPending = PendingIntent.getActivity(getApplicationContext(), 1, reqIntent, PendingIntent.FLAG_ONE_SHOT);

            noBuilder.setContentTitle(noTitle);
            noBuilder.setContentText(noText);
            noBuilder.setSmallIcon(R.drawable.app_logo_wheel);
            noBuilder.setAutoCancel(true);
            noBuilder.setContentIntent(orderPending);
        }
        else if(Objects.requireNonNull(noTitle).equals("Request Rejected") ||
                Objects.requireNonNull(noTitle).equals("Booking Cancelled by Driver") ||
                Objects.requireNonNull(noTitle).equals("Booking Accepted") ||
                Objects.requireNonNull(noTitle).equals("I'm Here") ||
                Objects.requireNonNull(noTitle).equals("Tourist Trip Reminder")){
            Intent reqIntent = new Intent(getApplicationContext(), TouristNavActivity.class);
            reqIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent orderPending = PendingIntent.getActivity(getApplicationContext(), 1, reqIntent, PendingIntent.FLAG_ONE_SHOT);

            noBuilder.setContentTitle(noTitle);
            noBuilder.setContentText(noText);
            noBuilder.setSmallIcon(R.drawable.app_logo_wheel);
            noBuilder.setAutoCancel(true);
            noBuilder.setContentIntent(orderPending);
        }
        else if(Objects.requireNonNull(noTitle).equals("Cancellation Fee")){
            Intent reqIntent = new Intent(getApplicationContext(), DriverNavHomepage.class);
            reqIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent orderPending = PendingIntent.getActivity(getApplicationContext(), 1, reqIntent, PendingIntent.FLAG_ONE_SHOT);

            noBuilder.setContentTitle(noTitle);
            noBuilder.setContentText(noText);
            noBuilder.setSmallIcon(R.drawable.app_logo_wheel);
            noBuilder.setAutoCancel(true);
            noBuilder.setContentIntent(orderPending);
        }
        else if(Objects.requireNonNull(noTitle).equals("Refunded Successfully")){
            Intent reqIntent = new Intent(getApplicationContext(), TouristNavHomepage.class);
            reqIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent orderPending = PendingIntent.getActivity(getApplicationContext(), 1, reqIntent, PendingIntent.FLAG_ONE_SHOT);

            noBuilder.setContentTitle(noTitle);
            noBuilder.setContentText(noText);
            noBuilder.setSmallIcon(R.drawable.app_logo_wheel);
            noBuilder.setAutoCancel(true);
            noBuilder.setContentIntent(orderPending);
        }

        NotificationManagerCompat.from(this).notify(1, noBuilder.build());
        super.onMessageReceived(message);
    }
}
