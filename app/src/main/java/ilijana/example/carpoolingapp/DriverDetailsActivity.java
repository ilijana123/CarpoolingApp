package ilijana.example.carpoolingapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DriverDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        Driver driver = (Driver) getIntent().getSerializableExtra("driver");
        if (driver == null) {
            Toast.makeText(this, "Driver data not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (savedInstanceState == null) {
            DriverDetailsFragment fragment = DriverDetailsFragment.newInstance(driver);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment2, fragment)
                    .commit();
        }
    }



}
