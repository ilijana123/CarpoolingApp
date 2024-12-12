package ilijana.example.carpoolingapp;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PassengerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView passengerText;
    private LatLng selectedLocation;
    private DatabaseHelper databaseHelper;
    private int currentUserId;
    private static final int req_code = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);

        passengerText = findViewById(R.id.passengerText);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button navigateToDriverListButton = findViewById(R.id.navigate_to_driver_list_button);
        navigateToDriverListButton.setEnabled(false);
        Button myRidesButton = findViewById(R.id.my_rides_button);
        databaseHelper = new DatabaseHelper(this);
        String name = getIntent().getStringExtra("name");
        if (name != null) {
            passengerText.setText("Welcome, " + name + "!");
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }

        currentUserId = getIntent().getIntExtra("userId", -1);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng destination = place.getLatLng();
                    if (destination != null) {
                        LatLng origin=getMyLocation();
                        mMap.addMarker(new MarkerOptions().position(destination).title(getAddressFromCoordinates(destination.latitude,destination.longitude)));
                        if(origin!=null ){
                            mMap.addMarker(new MarkerOptions().position(origin).title(getAddressFromCoordinates(origin.latitude,origin.longitude)));
                        }
                        mMap.addPolyline((new PolylineOptions()).add(getMyLocation(),destination).
                                width(5)
                                .color(Color.RED)
                                .geodesic(true));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 10));

                        selectedLocation = destination;
                    }
                    if (selectedLocation != null) {
                        Log.d("PassengerActivity", "Selected Location: " + selectedLocation.toString());
                        navigateToDriverListButton.setEnabled(true);
                    } else {
                        Log.e("PassengerActivity", "Destination location is null.");
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.e("AutocompleteError", "Error: " + status.getStatusMessage());
                    Toast.makeText(PassengerActivity.this, "Error selecting place: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("AutocompleteFragment", "AutocompleteSupportFragment is null");
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        navigateToDriverListButton.setOnClickListener(view -> {
            if (selectedLocation != null) {
                LatLng origin = getMyLocation();
                if (origin != null) {
                    Log.d("PassengerActivity", "Passing userId: " + currentUserId);
                    Log.d("PassengerActivity", "Origin Lat: " + origin.latitude + ", Lng: " + origin.longitude);
                    Log.d("PassengerActivity", "Destination Lat: " + selectedLocation.latitude + ", Lng: " + selectedLocation.longitude);

                    Intent intent = new Intent(PassengerActivity.this, DriverListActivity.class);
                    intent.putExtra("userId", currentUserId);
                    intent.putExtra("originLat", origin.latitude);
                    intent.putExtra("originLng", origin.longitude);
                    intent.putExtra("destinationLat", selectedLocation.latitude);
                    intent.putExtra("destinationLng", selectedLocation.longitude);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Unable to fetch your current location", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select a location first", Toast.LENGTH_SHORT).show();
            }
        });

        myRidesButton.setOnClickListener(view -> {
            if (currentUserId != -1) {
                Intent intent = new Intent(PassengerActivity.this, MyRidesActivity.class);
                intent.putExtra("userId", currentUserId);
                intent.putExtra("passengerName",name);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(PassengerActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng originLocation = getMyLocation();
        if (originLocation != null) {
            mMap.addMarker(new MarkerOptions().position(originLocation).title(getAddressFromCoordinates(originLocation.latitude,originLocation.longitude)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 12));
            selectedLocation = originLocation;
        } else {
            Toast.makeText(this, "Unable to fetch your current location", Toast.LENGTH_LONG).show();
        }

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title(getAddressFromCoordinates(latLng.latitude,latLng.longitude)));
            selectedLocation = latLng;
            Log.d("PassengerActivity", "Destination set from Map Click: " + selectedLocation.toString());
        });

    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private LatLng getMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!isLocationEnabled()) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, req_code);
            return null;
        }

        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (loc == null) {
            loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        if (loc == null) {
            requestLocationUpdates(locationManager);
            return null;
        } else {
            return new LatLng(loc.getLatitude(), loc.getLongitude());
        }
    }
    private void requestLocationUpdates(LocationManager locationManager) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, req_code);
            return;
        }

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                selectedLocation = new LatLng(lat, lng);
                Log.d("PassengerActivity", "Location updated: " + lat + ", " + lng);
                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(selectedLocation).title(getAddressFromCoordinates(lat,lng)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 12));
                }
            }
        }, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == req_code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LatLng location = getMyLocation();
                if (location != null) {
                    mMap.addMarker(new MarkerOptions().position(location).title(getAddressFromCoordinates(location.latitude,location.longitude)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
                    selectedLocation = location;
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        currentUserId = getIntent().getIntExtra("userId", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PassengerActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        double destinationLat = getIntent().getDoubleExtra("destinationLat", 0);
        double destinationLng = getIntent().getDoubleExtra("destinationLng", 0);

        if (destinationLat != 0 && destinationLng != 0) {
            selectedLocation = new LatLng(destinationLat, destinationLng);

            if (mMap != null) {
                LatLng origin = getMyLocation();

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(selectedLocation).title(getAddressFromCoordinates(destinationLat, destinationLng)));
                if (origin != null) {
                    mMap.addMarker(new MarkerOptions().position(origin).title(getAddressFromCoordinates(origin.latitude, origin.longitude)));
                    mMap.addPolyline(new PolylineOptions()
                            .add(origin, selectedLocation)
                            .width(5)
                            .color(Color.RED)
                            .geodesic(true));
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 10));
            }
        }
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e("RideActivity", "Geocoder error: " + e.getMessage());
        }
        return "Unknown Location";
    }
}