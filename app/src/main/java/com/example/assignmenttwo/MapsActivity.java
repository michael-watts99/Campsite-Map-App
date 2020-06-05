package com.example.assignmenttwo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;
import java.util.Arrays;


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
    private Marker markers;

    String [] names;
    String[] latitude;
    String[] longitude;

    TypedArray images;
    String[] siteInformation;
    String[] siteFacilities;
    String[] siteAccessibility;
    String[] siteRestrictions;
    //Fragment variables
    public View siteFrag;
    public View favFrag;

    public FragmentManager fragManager = getSupportFragmentManager();

    //listarrays
    public ArrayList<String> namesList = new ArrayList<>();;
    ArrayList<String> latitudeList = new ArrayList<>();
    ArrayList<String> longitudeList = new ArrayList<>();
    ArrayList<TypedArray> imagesList= new ArrayList<>();
    ArrayList<String> siteInformationList = new ArrayList<>();
    ArrayList<String> siteFacilitiesList = new ArrayList<>();
    ArrayList<String> siteAccessibilityList = new ArrayList<>();
    ArrayList<String> siteRestrictionsList = new ArrayList<>();
    ArrayList<String> favs = new ArrayList<>();
    ArrayList<TypedArray> favsImage = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Resources res = getResources();

        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        favouritesFragment frag = new favouritesFragment();
        fragmentTransaction.attach(frag);
        fragmentTransaction.addToBackStack("FRAG");
        fragmentTransaction.add(R.id.favFrag, frag, "FRAG");
        fragmentTransaction.commit();

        names = res.getStringArray(R.array.names);
        latitude =  res.getStringArray(R.array.latitude);
        longitude = res.getStringArray(R.array.longitude);
        images = res.obtainTypedArray(R.array.images);

        siteInformation = res.getStringArray(R.array.information);
        siteFacilities = res.getStringArray(R.array.facilities);
        siteAccessibility = res.getStringArray(R.array.accessibility);
        siteRestrictions = res.getStringArray(R.array.restrictions);

        //Setting the fragment to a variable
        favFrag = findViewById(R.id.favFrag);
        siteFrag = findViewById(R.id.siteFrag);



        favFrag.setVisibility(View.INVISIBLE);
        siteFrag.setVisibility(View.INVISIBLE);




        namesList.addAll(Arrays.asList(names));


        latitudeList.addAll(Arrays.asList(latitude));


        longitudeList.addAll(Arrays.asList(longitude));


        for(int i = 0; i <images.length(); i++)
        {
            imagesList.add(i, images);
        }



        siteInformationList.addAll(Arrays.asList(siteInformation));

//        siteFacilitiesList = new ArrayList<>();
        siteFacilitiesList.addAll(Arrays.asList(siteFacilities));

//        siteAccessibilityList = new ArrayList<>();
        siteAccessibilityList.addAll(Arrays.asList(siteAccessibility));

//        siteRestrictionsList = new ArrayList<>();
        siteRestrictionsList.addAll(Arrays.asList(siteRestrictions));


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        //Setting the location services client
//        locationClient = LocationServices.getFusedLocationProviderClient(this);
        //Checking if the location services are enabled
        isLocationOn();
        //If the location services are on the app will
        if (locationOn && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Getting location client


            //Getting last location
            getLastLocation();
            //Calling location request creator
            locationRequest();
            //Calling location callback method
            getLocationCallback();
        }

        mainButtons();












    }

    private void mainButtons()
    {
        ImageButton favButton = findViewById(R.id.star);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFavouritesButton();
            }
        });

        ImageButton mainButton = findViewById(R.id.home);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favFrag.getVisibility() == View.VISIBLE || siteFrag.getVisibility() == View.VISIBLE)
                {
                    favFrag.setVisibility(View.INVISIBLE);
                    siteFrag.setVisibility(View.INVISIBLE);
                }
                isLocationOn();

                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && locationOn) {

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
                else
                {
                    LatLng sydney = new LatLng(-33.8688,151.2093);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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

        addMarkers();
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

                    directions();
                }


            }

        }
        else if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || !locationOn)
        {
            if(mMap != null)
            {
                LatLng sydney = new LatLng(33.8688,151.2093);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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

    }

    public void getLocationCallback() {
        //Creating the call back to get live location
        locCallback = new LocationCallback() {
            @Override

            public void onLocationResult(LocationResult locationRes) {
                //Checking if the location result is null || This will be null if location services are turned off

                if (locationRes == null) {
                    return;
                }
                //Creating a loop to update the GUI everytime there is an update to location.

                for (Location loc : locationRes.getLocations()) {
                    //TODO Create logic AND UI for displaying the ongoing latitude and longitude to the user. Could a compass be pulled here?
                    TextView text = findViewById(R.id.latView);
                    text.setText("Lat: " + String.valueOf(loc.getLatitude()));
                    TextView longText = findViewById(R.id.longView);
                    longText.setText("Long: " + String.valueOf(loc.getLongitude()));
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
        if(locationOn && mMap != null && ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && siteFrag.getVisibility() != View.VISIBLE)
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
        else if(siteFrag.getVisibility() == View.VISIBLE)
        {
            return;
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
                    getLocationCallback();
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
                LatLng sydney = new LatLng(-34, 151);
                //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
               // mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
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

    public void addMarkers()
    {
        for(int i = 0; i < namesList.size(); i++ )
        {
            LatLng location = new LatLng(Double.parseDouble(latitudeList.get(i)), Double.parseDouble(longitudeList.get(i)));
            mMap.addMarker(new MarkerOptions().position(location).title(namesList.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.campsite_icon)));


            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public boolean onMarkerClick(final Marker marker) {


                    siteFrag.setVisibility(View.VISIBLE);

                    ImageView image = siteFrag.findViewById(R.id.siteImage);
                    TextView siteInfo = siteFrag.findViewById(R.id.siteDescription);
                    TextView siteTitle = siteFrag.findViewById(R.id.campsiteTitle);
                    TextView siteLat = siteFrag.findViewById(R.id.latitude);
                    TextView siteLong = siteFrag.findViewById(R.id.longitude);
                    TextView facilitiesInfo = siteFrag.findViewById(R.id.faciltiesInfo);
                    TextView accessibilityInfo = siteFrag.findViewById(R.id.accessibility);
                    TextView restrictionsInfo = siteFrag.findViewById(R.id.restrictionsInfo);

                    ImageButton star = siteFrag.findViewById(R.id.favButton);
                    if(favs.contains(marker.getTitle()))
                    {
                        star.setImageResource(R.drawable.star_filled);
                    }
                    else if (!favs.contains(marker.getTitle()))
                    {
                        star.setImageResource(R.drawable.star);
                    }
                    int index = namesList.indexOf(marker.getTitle());
                    //TODO REPLACE EACH INDIVIDUAL VIEW WITH A RECYCLERVIEW WITH CARDVIEW
                    getCampsiteInfo(image, siteInfo, siteTitle, siteLat, siteLong, facilitiesInfo, accessibilityInfo, restrictionsInfo, index);


                    final ImageView favButton = siteFrag.findViewById(R.id.favButton);
                    favButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!favs.contains(marker.getTitle()))

                            {
                                addFavouriteButton(marker);
                                favButton.setImageResource(R.drawable.star_filled);
                            }
                            else if(favs.contains(marker.getTitle()))
                            {
                                favButton.setImageResource(R.drawable.star);
                                favs.remove(marker.getTitle());
                                favsImage.remove(imagesList.get(namesList.indexOf(marker.getTitle())));
                            }

                        }
                    });



                    return false;
                }
            });
        }

    }

    @SuppressLint("ResourceType")
    public void getCampsiteInfo(ImageView image, TextView siteInfo, TextView siteTitle, TextView siteLat, TextView siteLong, TextView facilitiesInfo, TextView accessibilityInfo, TextView restrictionsInfo, int index)
    {

        if(imagesList.size() - 1 >= index)
        {

            image.setImageResource(imagesList.get(index).getResourceId(index, -1));


        }
        else
        {
            image.setImageResource(0);
        }
        if(namesList.size() - 1 >= index)
        {
            siteTitle.setText(namesList.get(index));
        }
        else
        {
            siteTitle.setText(null);
        }
        if(latitudeList.size() - 1 >= index)
        {
            siteLat.setText(latitudeList.get(index));
        }
        else
        {
            siteLat.setText(null);
        }
        if(longitudeList.size() - 1 >= index)
        {
            siteLong.setText(longitudeList.get(index));
        }
        else
        {
            siteLong.setText(null);
        }
        if(siteFacilitiesList.size() - 1 >= index)
        {
            facilitiesInfo.setText(siteFacilitiesList.get(index));
        }
        else
        {
            facilitiesInfo.setText(null);
        }
        if(siteAccessibilityList.size() - 1 >= index)
        {
            accessibilityInfo.setText(siteAccessibilityList.get(index));
        }
        else
        {
            accessibilityInfo.setText(null);
        }
        if(siteRestrictionsList.size() - 1 >= index)
        {
            restrictionsInfo.setText(siteRestrictionsList.get(index));
        }
        else
        {
            restrictionsInfo.setText(null);
        }

        if(siteInformationList.size() - 1 >= index)
        {
            siteInfo.setText(siteInformationList.get(index));
        }
        else
        {
            siteInfo.setText(null);
        }

    }
    @Override
    public void onBackPressed()
    {

        siteFrag.setVisibility(View.INVISIBLE);
        favFrag.setVisibility(View.INVISIBLE);
    }

    public void addFavouriteButton(Marker marker)
    {

        favs.add(marker.getTitle());
        favsImage.add(imagesList.get(namesList.indexOf(marker.getTitle())));
//        ArrayList<Integer> favsImage = new ArrayList<>();
//        favsImage.add(imagesList.get(namesList.indexOf(marker.getTitle())));



    }

    public void getFavouritesButton()
    {


        favFrag.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        favouritesFragment frag = new favouritesFragment();
        favouritesFragment fragment = (favouritesFragment) getSupportFragmentManager().findFragmentById(R.id.favFrag);
        favouritesAdapter favAdapter = new favouritesAdapter(frag.data, getBaseContext(), siteFrag, favFrag);
        for(int i = 0; i < favs.size(); i++)
        {

            String name = favs.get(i);
            Integer image = favsImage.get(i).getResourceId(namesList.indexOf(favs.get(i)), -1);
            com.example.assignmenttwo.favouritesData favouritesData = new com.example.assignmenttwo.favouritesData(name,i, image);
            frag.data.add(favouritesData);

            RecyclerView recy = favFrag.findViewById(R.id.favouritesRecycler);
            recy.setAdapter(favAdapter);
            fragment.favouritesAdapter.notifyDataSetChanged();



        }
        if(favs.isEmpty())
        {
//                favouritesFragment fragment = (favouritesFragment) getSupportFragmentManager().findFragmentById(R.id.favFrag);

//            favouritesFragment fragment = (favouritesFragment) getSupportFragmentManager().findFragmentById(R.id.favFrag);
            fragment.favouritesAdapter.notifyDataSetChanged();

        }


        fragmentTransaction.attach(frag);
        fragmentTransaction.addToBackStack("FRAG");
        fragmentTransaction.replace(R.id.favFrag, frag);
        fragmentTransaction.commit();






    }


    public void directions()
    {
        LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
        LatLng destination = new LatLng(Double.parseDouble(latitudeList.get(0)), Double.parseDouble(longitudeList.get(0)));

//        Polyline dirLine = new Polyline();
        mMap.addPolyline(new PolylineOptions().add(current, destination));



    }



}
