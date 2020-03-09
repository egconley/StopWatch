package com.econley_hle_rvaknin.stopwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

    private boolean mLocationPermissionGranted;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);


        ActivityCompat.requestPermissions(MapActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        getLocationPermission();

        getDeviceLocation();


    }

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
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    private void getDeviceLocation() {

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
//                                                marker.setPosition(userLocation);// add a marker on the map
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
//                                                marker.setPosition(userLocation);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        getDeviceLocation();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        LatLng userLocation = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

//        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)));
//        if (latitude != 0) {`
//            map = googleMap;
//            map.addMarker(new MarkerOptions().position(userLocation).title("User Location"));

//        }
    }
}
