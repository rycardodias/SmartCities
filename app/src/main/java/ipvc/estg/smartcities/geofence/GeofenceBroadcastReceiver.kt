package com.example.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import ipvc.estg.smartcities.Maps
import ipvc.estg.smartcities.R
import ipvc.estg.smartcities.geofence.NotificationHelper

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // an Intent broadcast.
//        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();
        val notificationHelper = NotificationHelper(context)
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...")
            return
        }

        val geofenceList = geofencingEvent.triggeringGeofences
        for (geofence in geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.requestId)
        }

        val transitionType = geofencingEvent.geofenceTransition
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                notificationHelper.sendHighPriorityNotification(
                        context.getString(R.string.danger_zone),
                        context.getString(R.string.entered_danger_zone), Maps::class.java)
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", Maps::class.java)
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                notificationHelper.sendHighPriorityNotification(
                        context.getString(R.string.danger_zone),
                        context.getString(R.string.out_danger_zone), Maps::class.java)
            }
        }
    }
    private val TAG = "GeofenceBroadcastReceiver"

}