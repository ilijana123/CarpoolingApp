package ilijana.example.carpoolingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PassengerListActivity extends AppCompatActivity {


    private RecyclerView passengerRecyclerView;
    private DatabaseHelper databaseHelper;
    private int driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_list);

        driverId = getIntent().getIntExtra("driverId", -1);

        if (driverId == -1) {
            Log.e("PassengerActivity", "No driver ID passed!");
            Toast.makeText(this, "No driver ID provided.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            Log.d("PassengerActivity", "Driver ID: " + driverId);
        }

        databaseHelper = new DatabaseHelper(this);
        passengerRecyclerView = findViewById(R.id.passengerRecyclerView);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button backButton = findViewById(R.id.backButton);
        passengerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadPassengers();
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DriverActivity.class);
            intent.putExtra("userId", driverId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(PassengerListActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadPassengers() {
        List<Ride> rides = databaseHelper.getRidesForDriver(driverId);
        Log.d("PassengerActivity", "Total rides fetched: " + (rides != null ? rides.size() : 0));

        if (rides != null && !rides.isEmpty()) {
            PassengerAdapter adapter = new PassengerAdapter(rides, databaseHelper);
            passengerRecyclerView.setAdapter(adapter);
            Log.d("PassengerActivity", "RecyclerView populated with rides.");
        } else {
            Log.e("PassengerActivity", "No passengers found for this driver.");
            Toast.makeText(this, "No passengers found for this driver.", Toast.LENGTH_SHORT).show();
        }
    }
}
