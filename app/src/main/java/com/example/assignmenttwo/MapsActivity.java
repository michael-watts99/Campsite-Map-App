package com.example.assignmenttwo;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

//Using implementation "com.google.android.gms:play-services-location:17.0.0" in build.gradle file
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION = 1;
    //Global variable to store the location Client
    private FusedLocationProviderClient locationClient;
    private Location currentLoc;
    private LocationCallback locCallback;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Getting location client
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        //Getting last location
        getLastLocation();
        //Calling location request creator
        locationRequest();
        //Calling location callback method
        getLocationCallback();



        Button locationButton = findViewById(R.id.button);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = findViewById(R.id.textView);
                text.setText(String.valueOf(currentLoc.getLatitude()));
            }
        });





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


        //Asking for permission to access location
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        mMap.moveCamera( CameraUpdateFactory.zoomTo(10));


        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //Checking if location access has been granted. If it has then the location enabled option is set
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(mMap != null)
            {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                ImageButton myLocation = findViewById(R.id.myLocation);
                myLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                        CameraPosition currentLocationCameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
                        CameraUpdate currentLocationUpdate = CameraUpdateFactory.newCameraPosition(currentLocationCameraPosition);
                        mMap.animateCamera(currentLocationUpdate);
                    }
                });

            }
        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        //TODO Create new "my location" button to ensure it's movable.



    }

    @Override
    public boolean onMyLocationButtonClick()
    {
        return false;
    }

    @Override
    //is called when the user clicks on the location dot.
    public void onMyLocationClick(@NonNull Location location)
    {

        Toast.makeText(this, String.valueOf(location.getLatitude()), Toast.LENGTH_LONG).show();
    }

    //Setting on start method
    @Override
    protected void onStart()
    {
        super.onStart();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationUpdates();
        }


    }

    public void getLastLocation()
    {
        locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
                    //Setting the location to a global variable
                    currentLoc = location;

                }
            }
        });
    }

    public void getLocationCallback()
    {
        //Creating the call back to get live location
        locCallback = new LocationCallback()
        {
            @Override

            public void onLocationResult(LocationResult locationRes)
            {
                //Checking if the location result is null || This will be null if location services are turned off
                //TODO DECIDE IF THE APP WILL PROMPT THE USER TO TURN LOCATION SERVICES ON HERE OR ONLY WHEN THE LOCATION BUTTON IS PRESSED
                if(locationRes == null)
                {
                    return;
                }
                //Creating a loop to update the GUI everytime there is an update to location.

                for (Location loc : locationRes.getLocations())
                {
                    //TODO Create logic for displaying the ongoing latitude and longitude to the user. Could a compass be pulled here?
                    TextView text = findViewById(R.id.textView);
                    text.setText(String.valueOf(loc.getLatitude()));
                }
            }
        };
    }

    //Setting location request
    protected void locationRequest()
    {
        //Creating a location request
        locationRequest = LocationRequest.create();
        //Setting the standard interval to 10 seconds
        locationRequest.setInterval(10000);
        //setting the fastest possible interval to 5 seconds
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    //Setting a location update method
    private void locationUpdates()
    {
        locationClient.requestLocationUpdates(locationRequest, locCallback, Looper.getMainLooper());
    }


}
