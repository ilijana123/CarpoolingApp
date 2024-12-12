package ilijana.example.carpoolingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder> {

    private final List<Ride> rides;
    private final DatabaseHelper databaseHelper;

    public PassengerAdapter(List<Ride> rides, DatabaseHelper databaseHelper) {
        this.rides = rides;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public PassengerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_passenger, parent, false);
        return new PassengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerViewHolder holder, int position) {
        Ride ride = rides.get(position);
        String passengerInfo = (position + 1) + ". " + ride.getName() + " " + ride.getSurname();
        holder.passengerName.setText(passengerInfo);
        float currentRating = ride.getPassengerRating();
        holder.ratingBar.setRating(currentRating);
        holder.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                boolean success = databaseHelper.updatePassengerRating(ride.getRideId(), (int) rating);
                if (success) {
                    ride.setPassengerRating((int) rating);
                    Toast.makeText(holder.itemView.getContext(), "Passenger rated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Failed to rate passenger.", Toast.LENGTH_SHORT).show();
                }
            }
        });
}


    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class PassengerViewHolder extends RecyclerView.ViewHolder {
        TextView passengerName;
        RatingBar ratingBar;

        PassengerViewHolder(@NonNull View itemView) {
            super(itemView);
            passengerName = itemView.findViewById(R.id.passengerName);
            ratingBar = itemView.findViewById(R.id.passengerRatingBar);
        }
    }
}
