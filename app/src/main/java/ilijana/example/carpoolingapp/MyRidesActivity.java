package ilijana.example.carpoolingapp;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyRidesActivity extends AppCompatActivity {

    private int currentUserId;
    private EditText searchBar;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Button logoutButton = findViewById(R.id.logout_button);
        Button backToMapButton = findViewById(R.id.back_to_map_button);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        currentUserId = getIntent().getIntExtra("userId", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<Ride> rideList = databaseHelper.getRidesForPassenger(currentUserId);

        if (rideList == null || rideList.isEmpty()) {
            Toast.makeText(this, "No rides found.", Toast.LENGTH_SHORT).show();
        } else {
            RideAdapter rideAdapter = new RideAdapter(rideList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(rideAdapter);
            rideAdapter.notifyDataSetChanged();
        }

        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(MyRidesActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        backToMapButton.setOnClickListener(view -> {
            Intent intent = new Intent(MyRidesActivity.this, PassengerActivity.class);
            intent.putExtra("userId", currentUserId);
            startActivity(intent);
            finish();
        });

    }
}
