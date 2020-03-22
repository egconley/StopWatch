package com.econley_hle_rvaknin.stopwatch;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

// resource: https://developer.android.com/training/location/geofencing
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    static String CHANNEL_ID = "101";

    public void onReceive(Context context, Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("ehr", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            Log.e("ehr", "Entered Notification Zone");

            // Send Notification
            sendNotification(context);

        } else {
            // Log the error.
            Log.e("ehr", "Failed to cross Geofence");
        }
    }

    public void sendNotification(Context context) {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Approaching Stop!")
                .setContentText("You are almost at your stop!")
                .setVibrate(new long[]{300,300})
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify((int)(Math.random() * 100.0), builder.build());
    }

}