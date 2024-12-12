package ilijana.example.carpoolingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DriverDetailsFragment extends Fragment {

    private static final String ARG_DRIVER = "driver";
    private Driver driver;
    private DatabaseHelper databaseHelper;

    public static DriverDetailsFragment newInstance(Driver driver) {
        DriverDetailsFragment fragment = new DriverDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DRIVER, driver);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());

        Bundle args = getArguments();
        if (args != null) {
            driver = (Driver) args.getSerializable(ARG_DRIVER);
        }

        if (driver != null) {
            TextView fullName = view.findViewById(R.id.driver_full_name);
            TextView vehicleBrandView = view.findViewById(R.id.vehicle_brand);
            TextView startTime = view.findViewById(R.id.start_time);
            TextView endTime = view.findViewById(R.id.end_time);
            TextView pricePerKm = view.findViewById(R.id.price_per_km);
            RatingBar ratingBar = view.findViewById(R.id.driver_rating_bar);
            fullName.setText(driver.getName() + " " + driver.getSurname());
            vehicleBrandView.setText("Vehicle: " + driver.getVehicleBrand());
            startTime.setText("Start Time: " + driver.getStartTime());
            endTime.setText("End Time: " + driver.getEndTime());
            pricePerKm.setText("Price per km" + driver.getPricePerKm());
            float driverRating = (float) databaseHelper.getDriverRating(driver.getId());
            ratingBar.setRating(driverRating);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
