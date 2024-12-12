package ilijana.example.carpoolingapp;

public class Ride {
    private int rideId, passengerId,driverId;
    private double originLat, originLng, destinationLat, destinationLng, price, distance;
    private String name, surname;
    private float driverRating, passengerRating;

    public Ride(int rideId, int passengerId,int driverId, double originLat, double originLng, double destinationLat, double destinationLng,
                double price, double distance, String name, String surname, float driverRating, float passengerRating) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.originLat = originLat;
        this.originLng = originLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.price = price;
        this.distance = distance;
        this.name = name;
        this.surname = surname;
        this.driverRating = driverRating;
        this.passengerRating = passengerRating;
    }

    public int getRideId() { return this.rideId; }
    public int getPassengerId() { return this.passengerId; }
    public int getDriverId() {return this.driverId; }
    public double getOriginLat() { return this.originLat; }
    public double getOriginLng() { return this.originLng; }
    public double getDestinationLat() { return this.destinationLat; }
    public double getDestinationLng() { return this.destinationLng; }
    public double getPrice() { return this.price; }
    public double getDistance() { return this.distance; }
    public String getName() { return this.name; }
    public String getSurname() { return this.surname; }
    public float getDriverRating() { return this.driverRating; }
    public float getPassengerRating() { return this.passengerRating; }

    public void setPassengerRating(int rating) {
        this.passengerRating=rating;
    }
}

