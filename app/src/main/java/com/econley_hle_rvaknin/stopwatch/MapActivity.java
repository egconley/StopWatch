package com.econley_hle_rvaknin.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import static android.provider.SettingsSlicesContract.KEY_LOCATION;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient fusedLocationClient;

    ///////////////////////////////////////////////////////////////
//    private SearchView searchView;
    private SupportMapFragment mapFragment;
    SearchView searchView;
    ///////////////////////////////////////////////////////////////


    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    double longitude;
    double latitude;
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    Geocoder geocoder;
    List<Address> addresses;
    LocationManager locationManager;
    Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.map_fragment);

        ActivityCompat.requestPermissions(MapActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.i("rvrv", "success!!");
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            System.out.println("longitude = " + longitude);
                            System.out.println("latitude = " + latitude);
                            System.out.println("location = " + location);
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                                TextView myLocation = findViewById(R.id.myLocation);
//                                myLocation.setText(addresses.get(0).getAddressLine(0));
                                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                mapFragment.getMapAsync(MapActivity.this);
                                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    Activity#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for Activity#requestPermissions for more details.
                                    return;
                                }
                                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                    locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            longitude = location.getLongitude();
                                            latitude = location.getLatitude();
                                            LatLng userLocation = new LatLng(latitude,longitude);
                                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                            try {
                                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                                String address = addresses.get(0).getAddressLine(0);
//                                                TextView myLocation = findViewById(R.id.myLocation);
//                                                myLocation.setText(address);
                                                marker.setPosition(userLocation);// add a marker on the map
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15)); // move camera to that location
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        @Override
                                        public void onStatusChanged(String provider, int status, Bundle extras) {
                                        }
                                        @Override
                                        public void onProviderEnabled(String provider) {
                                        }
                                        @Override
                                        public void onProviderDisabled(String provider) {
                                        }
                                    });
                                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            longitude = location.getLongitude();
                                            latitude = location.getLatitude();
                                            System.out.println("location = " + location);
                                            LatLng userLocation = new LatLng(latitude,longitude);
                                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                            try {
                                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                                String address = addresses.get(0).getAddressLine(0);
//                                                TextView myLocation = findViewById(R.id.myLocation);
//                                                myLocation.setText(address);
                                                marker.setPosition(userLocation);
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10.2f)); // move camera to that location
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        @Override
                                        public void onStatusChanged(String provider, int status, Bundle extras) {
                                        }
                                        @Override
                                        public void onProviderEnabled(String provider) {
                                        }
                                        @Override
                                        public void onProviderDisabled(String provider) {
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("rvrv", "failure... location is null!!");
                    }
                });


///////////////////////////////////////////////////////////////
//        searchView = findViewById(R.id.menu_search);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        Log.i("haitle16.MapActivity", "data from searchView: " +searchView);


//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                String location = searchView.getQuery().toString();
//                List<Address> destination = null;
//
//                if(location!= null || !location.equals("")) {
//                    Geocoder geocoder = new Geocoder(MapActivity.this);
//                    try {
//                        destination = geocoder.getFromLocationName(location,1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Address address = destination.get(0);
//                    LatLng latLng = new LatLng(address.getLatitude(), address.getLatitude());
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
//
//
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                return false;
//            }
//        });
        mapFragment.getMapAsync(this);
        ///////////////////////////////////////////////////////////////


    }

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
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        final MenuItem searchMenu = menu.findItem(R.id.menu_search);
//        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(searchMenu);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);

        Log.i("haitle16.MapActivity", "data from searchView: " +searchView);

        if(searchView != null) {
            Log.i("haitle16.MapActivity", "getting into searchView");

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String location = searchView.getQuery().toString();
                    List<Address> destination = null;

                    Log.i("haitle16.MapActivity", "getting into into into  searchView");

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        destination = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = destination.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLatitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                    Log.i("haitle16.MapActivity", "Data from latLng"+latLng);
                    Log.i("haitle16.MapActivity", "Data from address"+address);
                    // lat and long is under address.lat,long


                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    return false;
                }
            });
        }
//        Log.i("haitle16.MapActivity", "data from searchView: " +searchView2);




        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param //item The menu item to handle.
     * @return Boolean.
     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.option_get_place) {
//            showCurrentPlace();
//        }
//        return true;
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));
//        if (latitude != 0) {
//            map = googleMap;
            LatLng userLocation = new LatLng(latitude, longitude);
//            map.addMarker(new MarkerOptions().position(userLocation).title("User Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
//        }
    }
}
