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

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    // ...

    private static boolean withinRadius = false;
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
         System.out.println("geofenceTransition = " + geofenceTransition);

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            Log.e("ehr", "ENTERED THE DANGER ZONEEEEE");
            System.out.println("Welcome to the jungle!!!");

            // Send Notification
            withinRadius=true;
            sendNotification(context);

        } else {
            // Log the error.
            Log.e("ehr", "failed to enter geofence");
        }
    }

    public void sendNotification(Context context) {
        // Create an explicit intent for an Activity in your app
        System.out.println("SEND NOTIFICATION CALLEDDDDD!!!!!!!");
        Intent intent = new Intent(context, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Approaching Stop!")
                .setContentText("You are almost at your stop!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify((int)(Math.random() * 100.0), builder.build());
    }


}