package com.example.assignmenttwo;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
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

    //TODO GET THE APP TO CHECK IF LOCATION SERVICES IS ENABLED WHEN THE LOCAITON BUTTON IS PRESSED. IF THEY ARE ENABLED THEN GET LOCATION.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        //Getting location client
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        //Getting last location
        getLastLocation();
        //Calling location request creator
        locationRequest();
        //Calling location callback method
        getLocationCallback();


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
        //Checking if location access has been granted. If it has then the location enabled option is set
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (mMap != null) {


                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                locationUpdates();
                getLastLocation();
                LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                CameraPosition currentLocationCameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
                CameraUpdate currentLocationUpdate = CameraUpdateFactory.newCameraPosition(currentLocationCameraPosition);
                mMap.animateCamera(currentLocationUpdate);




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

    //Setting on start method
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationUpdates();
//            LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
//            CameraPosition currentLocationCameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
//            CameraUpdate currentLocationUpdate = CameraUpdateFactory.newCameraPosition(currentLocationCameraPosition);
//            mMap.animateCamera(currentLocationUpdate);
        }


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
                    //TODO Create logic AND UI for displaying the ongoing latitude and longitude to the user. Could a compass be pulled here?
                    TextView text = findViewById(R.id.textView);
                    text.setText(String.valueOf(loc.getLatitude()));
                    currentLoc = loc;
                }
            }
        };
    }
    //Method to get the users location when the location button is pressed.
    private void getMyLocationButton()
    {
        ImageButton myLocation = findViewById(R.id.myLocation);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Checking if the user has provided permission to access their location
                if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    //if the permission weren't already granted then request those permissions
                    //Each request for this permission will load the permissions request callback
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


                }
                //If the permission has already been granted then the button will get the last known location and then move the map to that location.
                else if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    //Gets the last known location and sets it to the global location variable.
                    getLastLocation();


                    //TODO Find out how to make the loading of the map smoother after giving permissions during runtime


                    //Gets the latitude and longitude of the current location and then moves the camera there by panning over the map (To give it a smooth look)
                    //Sets the map zoom to 15 so the location area is visible at a local level.
                    LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                    CameraPosition currentLocationCameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
                    CameraUpdate currentLocationUpdate = CameraUpdateFactory.newCameraPosition(currentLocationCameraPosition);
                    mMap.animateCamera(currentLocationUpdate);
                }

            }
        });
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
                LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
                CameraPosition currentLocationCameraPosition = new CameraPosition.Builder().target(current).zoom(15).build();
                CameraUpdate currentLocationUpdate = CameraUpdateFactory.newCameraPosition(currentLocationCameraPosition);
                mMap.animateCamera(currentLocationUpdate);
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

    private void isLocationOn()
    {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        //If location services are on
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try
                {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                }
                catch(ApiException exception)
                {
                    switch(exception.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            //Location services need to be turned on
                            try
                            {
                                ResolvableApiException resolveable = (ResolvableApiException) exception;

                                resolveable.startResolutionForResult(getParent(), 1);
                            }
                            catch(IntentSender.SendIntentException e)
                            {
                                //ignore
                            }
                            catch(ClassCastException e)
                            {
                                //ignore
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //No way to change the settings
                            break;
                    }


                }
            }
        });

    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent i)
//    {
//
//        super.onActivityResult(requestCode, resultCode, i);
//        final LocationSettingsStates states = LocationSettingsStates.fromIntent(i);
//        if(requestCode == REQUEST_CHECK_SETTINGS)
//        switch(requestCode)
//        {
//            case LocationSettingsStatusCodes.SUCCESS:
//                switch(resultCode)
//                {
//                    case Activity.RESULT_OK:
//                        //All changes were successful
//                        getLastLocation();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        //User said no
//                        break;
//                    default:
//                        break;
//                }
//                break;
//
//        }
//    }

}
