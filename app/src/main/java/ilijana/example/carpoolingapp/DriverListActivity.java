package ilijana.example.carpoolingapp;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class DriverListActivity extends AppCompatActivity implements DriverListFragment.OnDriverSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_list);

        if (savedInstanceState == null) {
            int currentUserId = getIntent().getIntExtra("userId", -1);
            double originLat = getIntent().getDoubleExtra("originLat",-1);
            double originLng = getIntent().getDoubleExtra("originLng",-1);
            double destinationLat = getIntent().getDoubleExtra("destinationLat",-1);
            double destinationLng = getIntent().getDoubleExtra("destinationLng",-1);

            DriverListFragment listFragment = new DriverListFragment();
            Bundle args = new Bundle();
            args.putInt("userId", currentUserId);
            args.putDouble("originLat",originLat);
            args.putDouble("originLng",originLng);
            args.putDouble("destinationLat",destinationLat);
            args.putDouble("destinationLng",destinationLng);
            listFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment1, listFragment)
                    .commit();
        }
    }

    @Override
    public void onDriverSelected(Driver driver) {
        if (findViewById(R.id.fragment2) != null) {
            DriverDetailsFragment detailsFragment = DriverDetailsFragment.newInstance(driver);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment2, detailsFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DriverDetailsActivity.class);
            intent.putExtra("driver", driver);
            startActivity(intent);
        }
    }
}

