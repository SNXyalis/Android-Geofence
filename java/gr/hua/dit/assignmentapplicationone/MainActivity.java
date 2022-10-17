package gr.hua.dit.assignmentapplicationone;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private GeofencingClient geofencingClient;


    @RequiresApi(api = 31)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        geofencingClient = LocationServices.getGeofencingClient(this);



        //ASK FOR PERMISSION DURING THE FIRST LAUNCH OF THE APP***************
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result ->{
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean backgroundLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION, false);

                            if (fineLocationGranted != null && fineLocationGranted
                            && backgroundLocationGranted != null && backgroundLocationGranted) {

                            }
                });

        //TODO
        // CHECK IF APP ALREADY HAS PERMISSIONS

        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        });
        //*******************************************************************


        findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLocation();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 7) {
            for(int i =0; i <permissions.length;i++) {
                if(permissions[i] == Manifest.permission.ACCESS_FINE_LOCATION){
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        showLocation();
                    }
                }
            }
        }
    }

    private void showLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 7);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("LOCATION", location.toString());
                Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}