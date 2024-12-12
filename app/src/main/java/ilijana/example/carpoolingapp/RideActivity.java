package ilijana.example.carpoolingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RideActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private int passengerId;
    private int currentRideId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        TextView driverName = findViewById(R.id.driver_name);
        TextView tripPrice = findViewById(R.id.trip_price);
        TextView originAddressText = findViewById(R.id.origin_address);
        TextView destinationAddressText = findViewById(R.id.destination_address);
        RatingBar ratingBar = findViewById(R.id.rating_bar);
        Button rateDriverButton = findViewById(R.id.rate_driver_button);
        Button myRidesButton = findViewById(R.id.my_rides_button);
        Button logoutButton = findViewById(R.id.logout_button);

        passengerId = getIntent().getIntExtra("userId", -1);
        int driverId = getIntent().getIntExtra("driverId", -1);
        String name = getIntent().getStringExtra("driverName");
        String surname = getIntent().getStringExtra("driverSurname");
        double pricePerKm = getIntent().getDoubleExtra("pricePerKm", 0);
        double originLat = getIntent().getDoubleExtra("originLat", 0);
        double originLng = getIntent().getDoubleExtra("originLng", 0);
        double destinationLat = getIntent().getDoubleExtra("destinationLat", 0);
        double destinationLng = getIntent().getDoubleExtra("destinationLng", 0);

        String originAddress = getAddressFromCoordinates(originLat, originLng);
        String destinationAddress = getAddressFromCoordinates(destinationLat, destinationLng);

        Log.d("RideActivity", "Origin Address: " + originAddress);
        Log.d("RideActivity", "Destination Address: " + destinationAddress);

        databaseHelper = new DatabaseHelper(this);
        double distance = databaseHelper.calculateDistance(originLat, originLng, destinationLat, destinationLng);
        double totalPrice = distance * pricePerKm;

        driverName.setText(getString(R.string.driver_label, name, surname));
        tripPrice.setText(getString(R.string.trip_price_label, totalPrice));
        originAddressText.setText(getString(R.string.origin_label, originAddress));
        destinationAddressText.setText(getString(R.string.destination_label, destinationAddress));
        TextView totalDistanceText = findViewById(R.id.total_distance);
        totalDistanceText.setText(getString(R.string.distance_label, distance));

        currentRideId = databaseHelper.insertRideData(passengerId, driverId, originLat, originLng, destinationLat, destinationLng, totalPrice, distance);

        if (currentRideId == -1) {
            Toast.makeText(this, "Failed to insert ride data.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("RideActivity", "New ride inserted with ID: " + currentRideId);
        }

        rateDriverButton.setOnClickListener(view -> {
            int rating = Math.round(ratingBar.getRating());
            if (rating > 0) {
                boolean isUpdated = databaseHelper.updateDriverRating(currentRideId, rating);
                if (isUpdated) {
                    Toast.makeText(this, "Thank you for rating!", Toast.LENGTH_SHORT).show();
                    rateDriverButton.setEnabled(false);
                } else {
                    Toast.makeText(this, "Failed to save rating. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please select a rating.", Toast.LENGTH_SHORT).show();
            }
        });


        myRidesButton.setOnClickListener(view -> {
            Intent intent = new Intent(RideActivity.this, MyRidesActivity.class);
            intent.putExtra("userId", passengerId);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(RideActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
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
