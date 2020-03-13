package com.econley_hle_rvaknin.stopwatch;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.os.Build;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, BottomNavigationView.OnNavigationItemReselectedListener {
    private static final String TAG = "egc." + MapActivity.class.getSimpleName();

    BottomNavigationView bottomNavigationView;

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    private Geofence geofence;

    // Search
    private SupportMapFragment mapFragment;
    SearchView searchView;
    LatLng destionationLatLng;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    private LinkedList<String> recentDestinations;

    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private String mLastKnownDestination;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    static String CHANNEL_ID = "101";

    // TODO: hook these up to help persist location data between the recycler view and the map activity
    public static void start(Context context,
                             String mLastKnownDestination,
                             Location mLastKnownLocation) {
        context.startActivity(makeIntent(context, mLastKnownDestination, mLastKnownLocation));
    }

    public static void start(Context context,
                             String mLastKnownDestination) {
        context.startActivity(makeIntent(context, mLastKnownDestination));
    }

    public static Intent makeIntent(Context context,
                                    String destination) {
        Intent myIntent = new Intent(context, MapActivity.class);
        myIntent.putExtra("newDestination", destination);
        return myIntent;
    }

    public static Intent makeIntent(Context context,
                                    String destination,
                                    Location mLastKnownLocation) {
        Intent myIntent = new Intent(context, MapActivity.class);
        myIntent.putExtra("newDestination", destination);
        return myIntent;
    }
    // ^TODO: hook these up to help persist location data between the recycler view and the map activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recentDestinations = loadRecents();
        System.out.println("size " + recentDestinations.size());

        for(String value: recentDestinations){
            System.out.println("value = " + value);
        }

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        // Retrieve the content view that renders the map.
        setContentView(R.layout.map_fragment);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bottomNavigationSetUp();
        nofificationChannelSetup();
        firebaseSetup();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Make sure Google Play Services is compatible with Firebase
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     *
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        final MenuItem searchMenu = menu.findItem(R.id.menu_search);
        final MenuItem settingMenu = menu.findItem(R.id.settingMenu);
        settingMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem MenuItem) {
                Intent tosettingpage = new Intent(MapActivity.this, SettingsActivity.class);
                startActivity(tosettingpage);
                return false;
            }
        });

        searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);
        Log.i("haitle16.MapActivity", "data from searchView: " + searchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String location = searchView.getQuery().toString();

                    setDestination(getApplicationContext(), location);

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {

                    return false;
                }
            });

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(final LatLng latLng) {
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> destination = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
                        final Address address = destination.get(0);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(address)));
                        userDialog(address);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.map_info_content,
                        (FrameLayout) findViewById(R.id.map), false);
                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());
                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }
        });
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }
    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                recentDestinations = new LinkedList<>();
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of     the device.
                            mLastKnownLocation = task.getResult();
                            // send notification if entry status is true
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setGeofence(double targetLat, double targetLong) {
        //// creating a geofence with lat long and radius
        geofence = new Geofence.Builder()
                .setRequestId("destination")
                .setCircularRegion(targetLat, targetLong, 300)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();

        // creating a request using the geofence we created in previous code block
        GeofencingRequest geofencingRequest = getGeofencingRequest(geofence);

        // create an intent and set it to be a pending intent.
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //add the geofence to map
        LocationServices.getGeofencingClient(this).addGeofences(geofencingRequest ,pendingIntent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "added geofences yeahh!!!");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "failed to add geofence :( ");
                        Log.e("ehr ERROR",e.toString());

                    }
                });

    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private void stopGeofenceMonitoring(){
        List<String> geofenceIds = new ArrayList<>();
        geofenceIds.add("destination");
        LocationServices.getGeofencingClient(this).removeGeofences(geofenceIds)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "removed geofences yeahh!!!");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to remove geofences :( ");
                    }
                });
    }

    public void passToGooglemap(LatLng latLng) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+mLastKnownLocation.getLatitude()+","+mLastKnownLocation.getLongitude()+"&daddr="+latLng.latitude+","+latLng.longitude));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        Log.i("erh", "gotten into passToGooglemap");
        startActivity(intent);
    }

    private void saveToRecents(String address){
        recentDestinations.addFirst(address);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recentDestinations);
        editor.putString("recent destination list",json);
        editor.apply();
    }

    private LinkedList<String> loadRecents() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString("recent destination list", null);
        Type type = new TypeToken<LinkedList<String>>() {}.getType();
        LinkedList<String> distinctRecentDestinations = new LinkedList<>();
        recentDestinations = gson.fromJson(json, type);

        if(recentDestinations == null){
            recentDestinations = new LinkedList<>();
            // dedup list of recent destinations
            HashSet<String> set = new HashSet<>();
            for (String address : recentDestinations) {
                set.add(address);
            }
            for (String address : set) {
                distinctRecentDestinations.add(address);
            }
        }
        return distinctRecentDestinations;
    }

    private void bottomNavigationSetUp() {
        // Event listener for bottom navigation menu
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // sets initial selected item to map
        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_map:
                        findViewById(R.id.map).bringToFront();
                        return true;
                    case R.id.favorite_routes:
                        System.out.println("MENU FAVORITE SELECTED!!!");
                        return false;
                    case R.id.recent_routes:
                        findViewById(R.id.recyclerView1).bringToFront();
                        return true;
                }
                return false;
            }
        });
    }

    private void nofificationChannelSetup() {
        //Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void firebaseSetup() {
        // Firebase
        // Make sure Google Play Services is compatible with Firebase
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("INSTANCE ID", msg);
                    }
                });
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem menuItem) {

    }

    public void setDestination(Context context, String location) {
        mLastKnownDestination = location;
        Context appContext = getApplicationContext();
        Geocoder geocoder = new Geocoder(appContext, Locale.getDefault());
        try {
            List<Address> destination = geocoder.getFromLocationName(location, 1);
            if(!destination.isEmpty()) {
                final Address address = destination.get(0);
                final LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                LatLng currentlatLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                builder.include(currentlatLng); // get user's location
                builder.include(latLng); // get marker's location and then zoom
                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
                searchView.onActionViewCollapsed();
                userDialog(address);
            }
            else {
                // else reload page with search clicked
                Toast toast = Toast.makeText(MapActivity.this,
                        "Search location is invalid, please specify location name and state!",
                        Toast.LENGTH_LONG);
                toast.show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void userDialog(Address address) {
        final Address selectedAddress = address;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(MapActivity.this);
                        dialogbuilder.setTitle("Set destination?");
                        final String destination = selectedAddress.getAddressLine(0);
                        final LatLng latLng = new LatLng(selectedAddress.getLatitude(), selectedAddress.getLongitude());
                        dialogbuilder.setMessage(destination);
                        dialogbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do something when user clicked the Yes button
                                destionationLatLng = latLng;
                                recentDestinations = loadRecents();
                                saveToRecents(destination);

                                setGeofence(destionationLatLng.latitude,destionationLatLng.longitude);
                                mMap.addCircle(new CircleOptions()
                                        .center(destionationLatLng)
                                        .strokeColor(Color.argb(100, 98, 0, 238))
                                        .fillColor(Color.argb(50, 98, 0, 238))
                                        .radius(300f));
                                SharedPreferences storage = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                                Boolean userGuidanceMode = storage.getBoolean("guidanceStatus",false);
                                if(userGuidanceMode == true) {
                                    passToGooglemap(latLng);
                                }
                            }
                        });

                        // Set the alert dialog no button click listener
                        dialogbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do something when no button has been clicked
                                Toast.makeText(getApplicationContext(),
                                        "You selected No, please search again.", Toast.LENGTH_LONG).show();
                            }
                        });

                        AlertDialog dialog = dialogbuilder.create();
                        // Display the alert dialog on interface
                        dialog.show();
                    }
                },
                800);
    }
}