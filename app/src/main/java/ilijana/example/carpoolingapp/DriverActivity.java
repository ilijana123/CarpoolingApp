package ilijana.example.carpoolingapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

public class DriverActivity extends AppCompatActivity {

    private EditText startTimeEditText, endTimeEditText, otherBrandEditText, pricePerKmEditText;
    private Spinner vehicleBrandSpinner;
    private Button saveButton, logoutButton, viewPassengersButton;
    private TextView ratingTextView, welcomeText;
    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        databaseHelper = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        startTimeEditText = findViewById(R.id.startTimeEditText);
        endTimeEditText = findViewById(R.id.endTimeEditText);
        vehicleBrandSpinner = findViewById(R.id.vehicleBrandSpinner);
        otherBrandEditText = findViewById(R.id.otherBrandEditText);
        pricePerKmEditText = findViewById(R.id.pricePerKmText);
        saveButton = findViewById(R.id.saveButton);
        logoutButton = findViewById(R.id.logoutButton);
        viewPassengersButton = findViewById(R.id.viewPassengersButton);
        ratingTextView = findViewById(R.id.ratingTextView);
        welcomeText = findViewById(R.id.welcomeText);

        setupVehicleBrandSpinner();
        loadDriverData();
        setupButtons();
    }

    private void setupVehicleBrandSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.brands, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleBrandSpinner.setAdapter(adapter);

        vehicleBrandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBrand = parent.getItemAtPosition(position).toString();
                otherBrandEditText.setVisibility("Other".equalsIgnoreCase(selectedBrand) ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                otherBrandEditText.setVisibility(View.GONE);
            }
        });
    }

    private void loadDriverData() {
        Driver driver = databaseHelper.getDriverByUserId(userId);

        if (driver != null) {
            try {
                String welcomeMessage = getString(R.string.welcome_message, driver.getName(), driver.getSurname());
                welcomeText.setText(welcomeMessage);

                String vehicleBrand = driver.getVehicleBrand();
                if ("Other".equalsIgnoreCase(vehicleBrand)) {
                    vehicleBrandSpinner.setSelection(vehicleBrandSpinner.getAdapter().getCount() - 1);
                    otherBrandEditText.setText(vehicleBrand);
                    otherBrandEditText.setVisibility(View.VISIBLE);
                } else {
                    int spinnerPosition = ((ArrayAdapter) vehicleBrandSpinner.getAdapter()).getPosition(vehicleBrand);
                    if (spinnerPosition >= 0) vehicleBrandSpinner.setSelection(spinnerPosition);
                }

                pricePerKmEditText.setText(String.valueOf(driver.getPricePerKm()));
                startTimeEditText.setText(driver.getStartTime());
                endTimeEditText.setText(driver.getEndTime());

                float rating = databaseHelper.getDriverRating(userId);
                ratingTextView.setText(getString(R.string.driver_rating_label, rating));
            } catch (Exception e) {
                Log.e("DriverActivity", "Error loading driver data: " + e.getMessage(), e);
            }
        } else {
            Toast.makeText(this, "No driver data found. Please enter your details.", Toast.LENGTH_SHORT).show();
            welcomeText.setText(getString(R.string.welcome_message));
            vehicleBrandSpinner.setSelection(0);
            otherBrandEditText.setVisibility(View.GONE);
            pricePerKmEditText.setText("");
            startTimeEditText.setText("");
            endTimeEditText.setText("");
            ratingTextView.setText(getString(R.string.driver_rating_label, 0.0f));
        }
    }

    private void setupButtons() {
        saveButton.setOnClickListener(v -> saveDriverData());
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        viewPassengersButton.setOnClickListener(v -> handleViewPassengersClick());

        startTimeEditText.setOnClickListener(v -> showTimePickerDialog(startTimeEditText));
        endTimeEditText.setOnClickListener(v -> showTimePickerDialog(endTimeEditText));
    }

    private void handleViewPassengersClick() {
        Intent intent = new Intent(this, PassengerListActivity.class);
        intent.putExtra("driverId", userId);
        Log.d("DriverActivity", "Driver ID passed: " + userId);
        startActivity(intent);
    }

    private void saveDriverData() {
        String selectedBrand = vehicleBrandSpinner.getSelectedItem().toString();
        String vehicleBrand = "Other".equalsIgnoreCase(selectedBrand) ? otherBrandEditText.getText().toString().trim() : selectedBrand;

        if (vehicleBrand.isEmpty() || startTimeEditText.getText().toString().isEmpty() ||
                endTimeEditText.getText().toString().isEmpty() || pricePerKmEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double pricePerKm;
        try {
            pricePerKm = Double.parseDouble(pricePerKmEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price per km.", Toast.LENGTH_SHORT).show();
            return;
        }

        Driver driver = databaseHelper.getDriverByUserId(userId);
        boolean success;

        if (driver == null) {
            success = databaseHelper.insertDriverData(userId, vehicleBrand, pricePerKm,
                    startTimeEditText.getText().toString(), endTimeEditText.getText().toString());
            Toast.makeText(this, success ? "Driver data saved successfully." : "Failed to save driver data.", Toast.LENGTH_SHORT).show();
        } else {
            success = databaseHelper.updateDriverData(userId, vehicleBrand, pricePerKm,
                    startTimeEditText.getText().toString(), endTimeEditText.getText().toString());
            Toast.makeText(this, success ? "Driver data updated successfully." : "Failed to update driver data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimePickerDialog(final EditText timeEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            Locale locale = Locale.getDefault();
            timeEditText.setText(String.format(locale, "%02d:%02d", hourOfDay, minuteOfHour));
        }, hour, minute, false).show();
    }

}
