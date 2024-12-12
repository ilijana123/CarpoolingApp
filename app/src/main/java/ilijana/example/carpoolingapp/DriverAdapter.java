package ilijana.example.carpoolingapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverViewHolder> {

    public interface OnDriverClickListener {
        void onDriverClick(Driver driver);
    }

    private List<Driver> driverList;
    private final OnDriverClickListener listener;
    private final DatabaseHelper databaseHelper;
    private final double originLat, originLng, destinationLat, destinationLng;
    private final int currentUserId;

    public DriverAdapter(
            List<Driver> driverList,
            int rowLayout,
            Context context,
            int currentUserId,
            double originLat,
            double originLng,
            double destinationLat,
            double destinationLng,
            OnDriverClickListener listener
    ) {
        this.driverList = driverList != null ? new ArrayList<>(driverList) : new ArrayList<>();
        this.currentUserId = currentUserId;
        this.originLat = originLat;
        this.originLng = originLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.listener = listener;
        this.databaseHelper = new DatabaseHelper(context); // Initialize DatabaseHelper
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row, parent, false);
        return new DriverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        Driver driver = driverList.get(position);

        if (driver == null) {
            return;
        }

        float driverRating = databaseHelper.getDriverRating(driver.getId());
        holder.fullName.setText(driver.getName() + " " + driver.getSurname());
        holder.vehicleBrand.setText("Vehicle Brand: " + driver.getVehicleBrand());
        holder.pricePerKm.setText(String.format("Price: $%.2f per km", driver.getPricePerKm()));
        holder.ratingBar.setRating(driverRating);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDriverClick(driver);
            }
        });

        holder.selectDriverButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), RideActivity.class);
            intent.putExtra("driverId", driver.getId());
            intent.putExtra("driverName", driver.getName());
            intent.putExtra("driverSurname", driver.getSurname());
            intent.putExtra("pricePerKm", driver.getPricePerKm());
            intent.putExtra("userId", currentUserId);
            intent.putExtra("originLat", originLat);
            intent.putExtra("originLng", originLng);
            intent.putExtra("destinationLat", destinationLat);
            intent.putExtra("destinationLng", destinationLng);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    public void updateDriverList(List<Driver> updatedDriverList) {
        driverList.clear();
        if (updatedDriverList != null) {
            driverList.addAll(updatedDriverList);
        }
        notifyDataSetChanged();
    }

    static class DriverViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, vehicleBrand, pricePerKm;
        RatingBar ratingBar;
        Button selectDriverButton;

        DriverViewHolder(View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.driver_full_name);
            vehicleBrand = itemView.findViewById(R.id.vehicleBrand);
            pricePerKm = itemView.findViewById(R.id.pricePerKm);
            ratingBar = itemView.findViewById(R.id.driver_rating_bar);
            selectDriverButton = itemView.findViewById(R.id.select_driver_button);
        }
    }
}
