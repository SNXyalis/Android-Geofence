package gr.hua.dit.assignmentapplicationone;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //DEFAULT
    private GoogleMap mMap;

    //Additions
    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList;
    private LocationManager locationManager;
    private PendingIntent geofencePendingIntent;
    private DbHelper dbHelper;

    //Flags
    private int request_flag = 1; //During the first installation showLocation()
    //doesnt work immediately so we call it again but every other time the app runs
    //flag is set to 0 so that it wont be called 2 times

    //Const
    private final double DEFAULT_LAT = 37.983810;
    private final double DEFAULT_LON = 23.727539;
    private final int ACTION_FINE_LOCATION_REQUEST_CODE = 7;
    private final int ACTION_BACKGROUND_LOCATION_REQUEST_CODE = 8;
    private final int GEOFENCE_PENDING_INTENT_REQUEST_CODE = 9;
    private final double GEOFENCE_RADIUS = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Additions
        //init geofencingclient
        geofencingClient = LocationServices.getGeofencingClient(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        dbHelper = new DbHelper(MapsActivity.this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng greece = new LatLng(DEFAULT_LAT, DEFAULT_LON);
        mMap.addMarker(new MarkerOptions().position(greece).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(greece, 12));

        showLocation();

        //On click, set a Geofence
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {

                if(request_flag == 1) {
                    showLocation();
                }

                //Add Geofence
                if(Build.VERSION.SDK_INT >= 29) {
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, ACTION_BACKGROUND_LOCATION_REQUEST_CODE);
                        return;
                    }
                }

                //Remove Previous Options
                mMap.clear();
                geofenceList = null;

                //Set a marker
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Geofence");
                mMap.addMarker(markerOptions);

                //Mark area
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latLng);
                circleOptions.radius(GEOFENCE_RADIUS);
                circleOptions.strokeColor(Color.argb(255, 0, 255, 0));
                circleOptions.fillColor(Color.argb(50, 0, 255, 0));
                mMap.addCircle(circleOptions);

                addGeofence(latLng);

                /*geofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId("GeofenceObject1")

                        .setCircularRegion(
                                latLng.latitude,
                                latLng.longitude,
                                (float) GEOFENCE_RADIUS
                        )
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());*/

            }
        });
    }


    //GEOFENCE*********************************************************************************
    //Implementation of GeofencingRequest
    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        /*builder.addGeofences(geofenceList);*/
        builder.addGeofence(geofence);
        return builder.build();
    }


    //Implementation of PendingIntent
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, GEOFENCE_PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private void addGeofence(LatLng latLng) {

        //Get the geofence
        Geofence geofence = new Geofence.Builder()
                .setRequestId("GeofenceObject1")

                .setCircularRegion(
                        latLng.latitude,
                        latLng.longitude,
                        (float) GEOFENCE_RADIUS
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        //Get the request
        GeofencingRequest geofencingRequest = getGeofencingRequest(geofence);

        //Get the pending intent
        PendingIntent pendingIntent = getGeofencePendingIntent();

        //Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACTION_FINE_LOCATION_REQUEST_CODE);
            return;
        }


        //Add geofence to client(last step)
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("addGeofences", " Geofence added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("addGeofences", e.getLocalizedMessage());
                    }
                });
    }
    //*******************************************************************************************

    //Location tracking for debugging************************************************************
    private void showLocation() {

        //Check & request permissions if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACTION_FINE_LOCATION_REQUEST_CODE);
            return;
        }

        //Execute Function
        request_flag = 0;
        mMap.setMyLocationEnabled(true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("LOCATION", location.toString());
            }
        });
    }
    //*******************************************************************************************

    //Permissions********************************************************************************
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACTION_FINE_LOCATION_REQUEST_CODE:
                //Check if user accepted/declined permissions and handle
                for(int i =0; i <permissions.length;i++) {
                    if(permissions[i] == Manifest.permission.ACCESS_FINE_LOCATION){
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            showLocation();
                        }
                    }
                }
                break;
            case ACTION_BACKGROUND_LOCATION_REQUEST_CODE:
                for(int i =0; i <permissions.length;i++) {
                    if(permissions[i] == Manifest.permission.ACCESS_BACKGROUND_LOCATION){
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                            //Set Default Geofence
                            LatLng latLng =  new LatLng(DEFAULT_LAT, DEFAULT_LON);


                            //Remove Previous Options
                            mMap.clear();
                            geofenceList = null;

                            //Set a marker
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Geofence");
                            mMap.addMarker(markerOptions);

                            //Mark area
                            CircleOptions circleOptions = new CircleOptions();
                            circleOptions.center(latLng);
                            circleOptions.radius(GEOFENCE_RADIUS);
                            circleOptions.strokeColor(Color.argb(255, 0, 255, 0));
                            circleOptions.fillColor(Color.argb(50, 0, 255, 0));
                            mMap.addCircle(circleOptions);
                            addGeofence(latLng);
                        }
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }
    //*******************************************************************************************

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}