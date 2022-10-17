package gr.hua.dit.assignmentapplicationtwo;

import androidx.fragment.app.FragmentActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //Const
    private final double DEFAULT_LAT = 37.983810;
    private final double DEFAULT_LON = 23.727539;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        //Access database of the other app
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://" + DbGeofenceInput.AUTHORITY+"/"+DbGeofenceInput.PATH);
        //Uri uri = Uri.parse("content:// + gr.hua.dit.assignmentapplicationone/GEOFENCEINPUT");
        Cursor cursor = resolver.query(uri, null,null,null, null);


        //Clear the Map
        mMap.clear();

        //For each row
        if(cursor.moveToFirst()) {
            do {
                //Get fields
                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);
                String action = cursor.getString(2);
                Timestamp t = Timestamp.valueOf(cursor.getString(3));
                Log.d("Geofence", "Lat: " +lat+" Lon: " + lon + " Action: " + action+" T: "+ t);

                //Create a marker & Put it on the map
                LatLng latLng = new LatLng(lat, lon);
                String markerTitle = action + " " + String.valueOf(t);

                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(markerTitle);
                mMap.addMarker(markerOptions);
            }while (cursor.moveToNext());
        }

    }
}