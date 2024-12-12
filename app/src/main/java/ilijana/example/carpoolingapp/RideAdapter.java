package ilijana.example.carpoolingapp;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import android.location.Address;
import android.location.Geocoder;
import android.widget.RatingBar;
import java.io.IOException;
import java.util.Locale;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<Ride> rideList;
    private Context context;

    public RideAdapter(List<Ride> rideList, Context context) {
        this.rideList = rideList;
        this.context = context;
        Log.d("RideAdapter", "Ride list size: " + (rideList != null ? rideList.size() : "null"));
    }


    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        Log.d("RideAdapter", "Binding ride at position: " + position);

        holder.driverName.setText(String.format("Driver: %s %s",
                ride.getName() != null ? ride.getName() : "N/A",
                ride.getSurname() != null ? ride.getSurname() : "N/A"));

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        numberFormat.setMaximumFractionDigits(2);

        holder.price.setText("Price: $" + numberFormat.format(ride.getPrice()));
        holder.distance.setText("Distance: " + numberFormat.format(ride.getDistance()) + " km");

        String originAddress = getAddressFromCoordinates(ride.getOriginLat(), ride.getOriginLng());
        holder.origin.setText("Origin: " + (originAddress != null ? originAddress : "Unknown Location"));

        String destinationAddress = getAddressFromCoordinates(ride.getDestinationLat(), ride.getDestinationLng());
        holder.destination.setText("Destination: " + (destinationAddress != null ? destinationAddress : "Unknown Location"));

        holder.ratingBar.setRating(ride.getDriverRating() > 0 ? ride.getDriverRating() : 0);
    }



    @Override
    public int getItemCount() {
        return rideList.size();
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e("RideAdapter", "Geocoder failed: " + e.getMessage());
        }
        return "Unknown Location";
    }


    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView driverName, price, distance, origin, destination;
        RatingBar ratingBar;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driver_name);
            price = itemView.findViewById(R.id.price);
            distance = itemView.findViewById(R.id.distance);
            origin = itemView.findViewById(R.id.origin);
            destination = itemView.findViewById(R.id.destination);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            ratingBar.setIsIndicator(true);
        }
    }
}

