package ilijana.example.carpoolingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DriverListFragment extends Fragment {

    private DriverAdapter driverAdapter;
    private OnDriverSelectedListener callback;
    private List<Driver> driverList;
    private int currentUserId;
    private double originLat, originLng, destinationLat, destinationLng;

    public interface OnDriverSelectedListener {
        void onDriverSelected(Driver driver);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDriverSelectedListener) {
            callback = (OnDriverSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnDriverSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            currentUserId = getArguments().getInt("userId", -1);
            String passengerName = getArguments().getString("passengerName", "");
            originLat = getArguments().getDouble("originLat", -1);
            originLng = getArguments().getDouble("originLng", -1);
            destinationLat = getArguments().getDouble("destinationLat", -1);
            destinationLng = getArguments().getDouble("destinationLng", -1);
        }

        RecyclerView recyclerView = view.findViewById(R.id.driver_recycler_view);
        EditText searchBar = view.findViewById(R.id.search_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        driverList = databaseHelper.getDrivers();

        if (driverList == null || driverList.isEmpty()) {
            Toast.makeText(requireContext(), "No drivers available", Toast.LENGTH_SHORT).show();
        } else {
            driverAdapter = new DriverAdapter(new ArrayList<>(driverList), R.layout.my_row, getContext(),currentUserId,originLat,originLng,destinationLat,destinationLng, driver -> {
                if (callback != null) {
                    callback.onDriverSelected(driver);
                }
            });

            recyclerView.setAdapter(driverAdapter);
        }

        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDrivers(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        Button backToPassengerButton = view.findViewById(R.id.back_to_passenger_button);
        backToPassengerButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PassengerActivity.class);
            intent.putExtra("userId", currentUserId);
            startActivity(intent);
        });
    }

    private void filterDrivers(String query) {
        List<Driver> filteredDriverList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredDriverList.addAll(driverList);
        } else {
            for (Driver driver : driverList) {
                if (driver != null && (
                        (driver.getName() != null && driver.getName().toLowerCase().contains(query.toLowerCase())) ||
                                (driver.getSurname() != null && driver.getSurname().toLowerCase().contains(query.toLowerCase())) ||
                                (driver.getVehicleBrand() != null && driver.getVehicleBrand().toLowerCase().contains(query.toLowerCase()))
                )) {
                    filteredDriverList.add(driver);
                }
            }
        }

        if (filteredDriverList.isEmpty()) {
            Toast.makeText(getContext(), "No matching drivers found.", Toast.LENGTH_SHORT).show();
        }

        driverAdapter.updateDriverList(filteredDriverList);
    }
}
