package gr.hua.dit.assignmentapplicationone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.sql.Timestamp;
import java.util.Date;


public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()){
            Log.e("Geofence Error", "BroadcastReceiver Error");
            return ;
        }

        Location triggeringLocation = geofencingEvent.getTriggeringLocation();

        double lat = triggeringLocation.getLatitude();
        double lon = triggeringLocation.getLongitude();
        String action = null;
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType) {
            case Geofence
                    .GEOFENCE_TRANSITION_ENTER:
                action = "ENTER";
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                action = "EXIT";
                break;
        }

        GeofenceInput geofenceInput = new GeofenceInput(lat, lon, action, timestamp, context);
        if(geofenceInput.persist() != 0) {
            Log.d("Geofence", "Lat: " +lat+" Lon: " + lon + " Action: " + action+" T: "+ timestamp);
        };
    }
}
