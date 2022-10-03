package com.example.finalyearproject_drivme;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {
    @SuppressLint("NewApi")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String noTitle = message.getNotification().getTitle();
        String noText = message.getNotification().getBody();
        String CHANNEL_ID = "MESSAGE";

        NotificationChannel noChannel = new NotificationChannel(
                CHANNEL_ID,
                "Message Notification",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(noChannel);

        Notification.Builder noBuilder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(noTitle)
                .setContentText(noText)
                .setSmallIcon(R.drawable.app_logo_wheel)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1, noBuilder.build());
        super.onMessageReceived(message);
    }
}
