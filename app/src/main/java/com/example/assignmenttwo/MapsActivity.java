package com.example.assignmenttwo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.Map;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

//Using implementation "com.google.android.gms:play-services-location:17.0.0" in build.gradle file
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION = 1;
    //Global variable to store the location Client
    private FusedLocationProviderClient locationClient;
    private Location currentLoc = new Location("DUMMY");
    private LocationCallback locCallback;
    private LocationRequest locationRequest;
    private boolean locationOn;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleApiClient mGoogleApiClient;

    //TODO GET THE APP TO CHECK IF LOCATION SERVICES IS ENABLED WHEN THE LOCATION BUTTON IS PRESSED. IF THEY ARE ENABLED THEN GET LOCATION. NEED TO CHECK AND THEN CALL THE METHOD THAT LOADS THE LOCATION, SIMILAR TO HOW IT CHECKS FOR PERMISSIONS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        isLocationOn();
        if (locationOn) {
            //Getting location client


            //Getting last location
            getLastLocation();
            //Calling location request creator
            locationRequest();
            //Calling location callback method
            getLocationCallback();
        }


        //TODO REMOVE THIS BUTTON WHEN NO LONGER NEEDED.
        Button locationButton = findViewById(R.id.button);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    TextView text = findViewById(R.id.textView);
                    text.setText(String.valueOf(currentLoc.getLatitude()));
                }

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


        //Set zoom to 10
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));


        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //isLocationOn();
        //Checking if location access has been granted. If it has then the location enabled option is set
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && locationOn) {

            if (mMap != null) {


                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                locationUpdates();
                getLastLocation();
                if (currentLoc != null) {
                    LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                    CameraPosition currentLocationCameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
                    CameraUpdate currentLocationUpdate = CameraUpdateFactory.newCameraPosition(currentLocationCameraPosition);
                    mMap.animateCamera(currentLocationUpdate);
                }


            }

        }


        mMap.setOnMyLocationClickListener(this);


        getMyLocationButton();


    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    //is called when the user clicks on the location dot.
    public void onMyLocationClick(@NonNull Location location) {

        Toast.makeText(this, String.valueOf(location.getLatitude()), Toast.LENGTH_LONG).show();
    }


    public void getLastLocation() {
//        currentLoc = locationClient.getLastLocation();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria crit = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String provider = locationManager.getBestProvider(crit, true);
        Location location = locationManager.getLastKnownLocation(provider);
        currentLoc = location;
//        locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
////                if(location != null)
////                {
//                    //Setting the location to a global variable
//                    currentLoc = location;
//
//                }
////            }
//        });
    }

    public void getLocationCallback() {
        //Creating the call back to get live location
        locCallback = new LocationCallback() {
            @Override

            public void onLocationResult(LocationResult locationRes) {
                //Checking if the location result is null || This will be null if location services are turned off
                //TODO DECIDE IF THE APP WILL PROMPT THE USER TO TURN LOCATION SERVICES ON HERE OR ONLY WHEN THE LOCATION BUTTON IS PRESSED
                if (locationRes == null) {
                    return;
                }
                //Creating a loop to update the GUI everytime there is an update to location.

                for (Location loc : locationRes.getLocations()) {
                    //TODO Create logic AND UI for displaying the ongoing latitude and longitude to the user. Could a compass be pulled here?
                    TextView text = findViewById(R.id.textView);
                    text.setText(String.valueOf(loc.getLatitude()));
                    currentLoc = loc;
                }
            }
        };
    }

    //Method to get the users location when the location button is pressed.
    private void getMyLocationButton() {
        ImageButton myLocation = findViewById(R.id.myLocation);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocationOn();
                //Checking if the user has provided permission to access their location

                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //if the permission weren't already granted then request those permissions
                    //Each request for this permission will load the permissions request callback
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


                }
                else if (!locationOn)
                {
                    turnLocationOn();

                    //TODO GET THE APP TO WAIT FOR THIS TO FINISH BEFORE CARRYING ON


                }

                //If the permission has already been granted then the button will get the last known location and then move the map to that location.
                else if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && locationOn) {

                    //Gets the last known location and sets it to the global location variable.
                    getLastLocation();


                    //TODO Find out how to make the loading of the map smoother after giving permissions during runtime


                    //Gets the latitude and longitude of the current location and then moves the camera there by panning over the map (To give it a smooth look)
                    //Sets the map zoom to 15 so the location area is visible at a local level.

                    if (currentLoc != null) {
                        LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                        CameraPosition currentLocationCameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
                        CameraUpdate currentLocationUpdate = CameraUpdateFactory.newCameraPosition(currentLocationCameraPosition);
                        mMap.animateCamera(currentLocationUpdate);
                    }

                }


            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();

        isLocationOn();
        if(locationOn && mMap != null && ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationClient = LocationServices.getFusedLocationProviderClient(this);
            //Setting the location enabled setting to be true. This shows the default location button and the location marker
            mMap.setMyLocationEnabled(true);
            //hiding the default location button to ensure the custom button is being used.
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


            locationRequest();
            getLocationCallback();
            getLastLocation();
            locationUpdates();



            if (currentLoc != null) {
                LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                CameraPosition currentLocationCameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
                CameraUpdate currentLocationUpdate = CameraUpdateFactory.newCameraPosition(currentLocationCameraPosition);
                mMap.animateCamera(currentLocationUpdate);
            }
        }
        else if(!locationOn)
        {
            Toast.makeText(this, "Without access to your location you will not be able to see where you are on the map or get directions.", Toast.LENGTH_LONG).show();
        }

    }


    //Method to handle the users response to the permissions request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Checks if the request code is the location request. An else if could be used to add a second permission
        if(requestCode == REQUEST_LOCATION)
        {
            //Checking if the first result in the package manager (this should be location) is granted
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                isLocationOn();

                if(locationOn)
                {
                    //Setting the location enabled setting to be true. This shows the default location button and the location marker
                    mMap.setMyLocationEnabled(true);
                    //hiding the default location button to ensure the custom button is being used.
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    //Getting the last known location
                    getLastLocation();
                    //Starting location updates for location tracking
                    locationUpdates();

                    //Gets the latitude and longitude of the current location and then moves the camera there by panning over the map (To give it a smooth look)
                    //Sets the map zoom to 15 so the location area is visible at a local level.
                    if(currentLoc != null)
                    {
                        LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                        CameraPosition currentLocationCameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
                        CameraUpdate currentLocationUpdate = CameraUpdateFactory.newCameraPosition(currentLocationCameraPosition);
                        mMap.animateCamera(currentLocationUpdate);
                    }

                }
                else if(!locationOn)
                {
                    turnLocationOn();
                }

            }
            else
            {
                Toast.makeText(this, "Without access to your location you will not be able to see where you are on the map or get directions.", Toast.LENGTH_LONG).show();
            }
        }
    }
    //Setting location request
    protected void locationRequest()
    {
        //Creating a location request
        locationRequest = LocationRequest.create();
        //Setting the standard interval to 10 seconds
        locationRequest.setInterval(1000);
        //setting the fastest possible interval to 5 seconds
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    //Setting a location update method
    private void locationUpdates()
    {

        locationClient.requestLocationUpdates(locationRequest, locCallback, Looper.getMainLooper());
    }

    private void isLocationOn()
    {
        LocationManager manager;
        manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            locationOn = true;
            //Gets the last known location and sets it to the global location variable.
//            locationUpdates();
//            getLastLocation();


            //TODO Find out how to make the loading of the map smoother after giving permissions during runtime


//            //Gets the latitude and longitude of the current location and then moves the camera there by panning over the map (To give it a smooth look)
//            //Sets the map zoom to 15 so the location area is visible at a local level.
//            LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
//            CameraPosition currentLocationCameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
//            CameraUpdate currentLocationUpdate = CameraUpdateFactory.newCameraPosition(currentLocationCameraPosition);
//            mMap.animateCamera(currentLocationUpdate);
        }
        else
        {
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            MapsActivity.this.startActivity(intent);
//
//            getLastLocation();


        }
    }
    //Method to turn location on if the location is not already on
    public void turnLocationOn()
    {
        //Creates a pop up giving the user the option to continue or cancel the action
        new AlertDialog.Builder(this).setTitle("Enable location services").setMessage("Do you want to enable location services?").setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Takes the user to the settings page to turn location services on
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MapsActivity.this.startActivity(intent);
                //Displays a message for the user.
                Toast.makeText(MapsActivity.this, "Please enable location settings and then press the back button to return", Toast.LENGTH_LONG).show();
                onPause();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Showing rationale for location access if the location services aren't turned on
                Toast.makeText(MapsActivity.this, "Without access to your location you will not be able to see where you are on the map or get directions.", Toast.LENGTH_LONG).show();

            }
        }).create().show();

    }


}
