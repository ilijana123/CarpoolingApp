package ilijana.example.carpoolingapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, "app.db", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        MyDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "email TEXT UNIQUE, " +
                        "password TEXT, " +
                        "userType TEXT, " +
                        "name TEXT, " +
                        "surname TEXT)"
                );
                MyDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS drivers (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userId INTEGER, " +
                        "vehicleBrand TEXT, " +
                        "pricePerKm REAL, " +
                        "startTime TEXT, " +
                        "endTime TEXT, " +
                        "FOREIGN KEY(userId) REFERENCES users(id))"
                );
        MyDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS ride (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "passengerId INTEGER, " +
                        "driverId INTEGER, " +
                        "originLat REAL, " +
                        "originLng REAL, " +
                        "destinationLat REAL, " +
                        "destinationLng REAL, " +
                        "price REAL, " +
                        "distance REAL, " +
                        "driverRating REAL, "+
                        "passengerRating REAL, "+
                        "FOREIGN KEY(passengerId) REFERENCES users(id), " +
                        "FOREIGN KEY(driverId) REFERENCES users(id))"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
    }

    public Boolean insertData(String email, String password, String userType, String name, String surname) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("userType", userType);
        contentValues.put("name", name);
        contentValues.put("surname", surname);

        long result = MyDatabase.insert("users", null, contentValues);

        if(result != -1) {
            Log.d("DatabaseHelper", "User inserted successfully: " + email);
            return true;
        } else {
            Log.d("DatabaseHelper", "User insert failed");
            return false;
        }
    }
    public Boolean insertDriverData(int userId, String vehicleBrand, double pricePerKm, String startTime, String endTime) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", userId);
        contentValues.put("vehicleBrand", vehicleBrand);
        contentValues.put("pricePerKm", pricePerKm);
        contentValues.put("startTime", startTime);
        contentValues.put("endTime", endTime);

        long result = MyDatabase.insert("drivers", null, contentValues);

        if (result != -1) {
            Log.d("DatabaseHelper", "Driver inserted successfully: UserId=" + userId);
            return true;
        } else {
            Log.d("DatabaseHelper", "Driver insert failed");
            return false;
        }
    }

    public int insertRideData(int passengerId, int driverId, double originLat, double originLng,
                              double destinationLat, double destinationLng, double price, double distance) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("passengerId", passengerId);
        contentValues.put("driverId", driverId);
        contentValues.put("originLat", originLat);
        contentValues.put("originLng", originLng);
        contentValues.put("destinationLat", destinationLat);
        contentValues.put("destinationLng", destinationLng);
        contentValues.put("price", price);
        contentValues.put("distance", distance);
        contentValues.put("driverRating", 0);
        contentValues.put("passengerRating", 0);

        long result = MyDatabase.insert("ride", null, contentValues);

        if (result != -1) {
            Log.d("DatabaseHelper", "Ride data inserted successfully with price: " + price + " and distance: " + distance);
            return (int) result;
        } else {
            Log.e("DatabaseHelper", "Failed to insert ride data");
            return -1;
        }
    }



    public Boolean checkEmail(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public Boolean checkEmailPassword(String email, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ? and password = ?", new String[]{email, password});

        if (cursor.getCount() > 0) {
            return true;
        }else {
            return false;
        }
    }
    public int getPassengerRating(int passengerId) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor cursor = MyDatabase.rawQuery(
                "SELECT AVG(ratingPassenger) FROM ride WHERE passengerId = ?",
                new String[]{String.valueOf(passengerId)}
        );

        if (cursor.moveToFirst()) {
            int rating = cursor.getInt(0);
            cursor.close();
            return rating;
        }
        cursor.close();
        return 0;
    }

    public int getDriverRating(int driverId) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor cursor = MyDatabase.rawQuery(
                "SELECT AVG(driverRating) AS avgRating FROM ride WHERE driverId = ?",
                new String[]{String.valueOf(driverId)}
        );

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int rating = cursor.getInt(cursor.getColumnIndex("avgRating"));
            cursor.close();
            return rating;
        }
        if (cursor != null) {
            cursor.close();
        }
        return 0;
    }

    public List<Driver> getDrivers() {
        List<Driver> driverList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u.name, u.surname, d.* " +
                "FROM users u " +
                "JOIN drivers d ON u.id = d.userId ";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex("userId"));
                @SuppressLint("Range") String vehicleBrand = cursor.getString(cursor.getColumnIndex("vehicleBrand"));
                @SuppressLint("Range") double pricePerKm = cursor.getDouble(cursor.getColumnIndex("pricePerKm"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("endTime"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String surname = cursor.getString(cursor.getColumnIndex("surname"));
                Driver driver = new Driver(id,userId, vehicleBrand, pricePerKm ,startTime,endTime,name,surname);
                driverList.add(driver);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return driverList;
    }

    public double calculateDistance(double originLat, double originLng, double destinationLat, double destinationLng) {
        final double R = 6371;
        double latDistance = Math.toRadians(destinationLat - originLat);
        double lonDistance = Math.toRadians(destinationLng - originLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(originLat)) * Math.cos(Math.toRadians(destinationLat)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    public boolean updateDriverRating(int passengerId, int driverId, int rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("driverRating", rating);

        int rowsAffected = db.update("ride", contentValues, "passengerId = ? AND driverId = ?",
                new String[]{String.valueOf(passengerId), String.valueOf(driverId)});

        if (rowsAffected > 0) {
            Log.d("DatabaseHelper", "Ride rating updated successfully.");
            return true;
        } else {
            Log.e("DatabaseHelper", "Failed to update ride rating.");
            return false;
        }
    }

    public List<Ride> getRidesForPassenger(int passengerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Ride> rideList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT r.id AS rideId, r.driverId, r.originLat, r.originLng, r.destinationLat, r.destinationLng, " +
                    "r.price, r.distance, r.driverRating, r.passengerRating, " +
                    "u.name AS driverName, u.surname AS driverSurname " +
                    "FROM ride r " +
                    "JOIN users u ON r.driverId = u.id " +
                    "WHERE r.passengerId = ?";
            Log.d("DatabaseHelper", "Query: " + query);
            Log.d("DatabaseHelper", "Passenger ID: " + passengerId);

            cursor = db.rawQuery(query, new String[]{String.valueOf(passengerId)});

            if (cursor != null && cursor.moveToFirst()) {
                Log.d("DatabaseHelper", "Cursor Count: " + cursor.getCount());
                do {
                    @SuppressLint("Range") int rideId = cursor.getInt(cursor.getColumnIndex("rideId"));
                    @SuppressLint("Range") int driverId = cursor.getInt(cursor.getColumnIndex("driverId"));
                    @SuppressLint("Range") double originLat = cursor.getDouble(cursor.getColumnIndex("originLat"));
                    @SuppressLint("Range") double originLng = cursor.getDouble(cursor.getColumnIndex("originLng"));
                    @SuppressLint("Range") double destinationLat = cursor.getDouble(cursor.getColumnIndex("destinationLat"));
                    @SuppressLint("Range") double destinationLng = cursor.getDouble(cursor.getColumnIndex("destinationLng"));
                    @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex("price"));
                    @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex("distance"));
                    @SuppressLint("Range") float driverRating = cursor.getFloat(cursor.getColumnIndex("driverRating"));
                    @SuppressLint("Range") float passengerRating = cursor.getFloat(cursor.getColumnIndex("passengerRating"));
                    @SuppressLint("Range") String driverName = cursor.getString(cursor.getColumnIndex("driverName"));
                    @SuppressLint("Range") String driverSurname = cursor.getString(cursor.getColumnIndex("driverSurname"));

                    rideList.add(new Ride(
                            rideId, passengerId, driverId, originLat, originLng, destinationLat, destinationLng,
                            price, distance, driverName, driverSurname, driverRating, passengerRating
                    ));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching rides for passenger: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        Log.d("DatabaseHelper", "Total rides fetched: " + rideList.size());
        return rideList;
    }

    public boolean updateDriverRating(int rideId, int rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("driverRating", rating);

        int rowsAffected = db.update("ride", contentValues, "id = ?", new String[]{String.valueOf(rideId)});
        return rowsAffected > 0;
    }

    public boolean updatePassengerRating(int rideId, int rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("passengerRating", rating);

        int rowsAffected = db.update("ride", contentValues, "id = ?", new String[]{String.valueOf(rideId)});
        return rowsAffected > 0;
    }
    public boolean updateDriverData(int userId, String vehicleBrand, double pricePerKm, String startTime, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("vehicleBrand", vehicleBrand);
        contentValues.put("pricePerKm", pricePerKm);
        contentValues.put("startTime", startTime);
        contentValues.put("endTime", endTime);

        int rowsAffected = db.update("drivers", contentValues, "userId = ?", new String[]{String.valueOf(userId)});
        if (rowsAffected > 0) {
            Log.d("DatabaseHelper", "Driver data updated successfully for userId: " + userId);
            return true;
        } else {
            Log.d("DatabaseHelper", "No driver data found to update for userId: " + userId);
            return false;
        }
    }

    public Driver getDriverByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Driver driver = null;

        try {

            String query = "SELECT d.*, u.name, u.surname " +
                    "FROM drivers d " +
                    "JOIN users u ON d.userId = u.id " +
                    "WHERE d.userId = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String vehicleBrand = cursor.getString(cursor.getColumnIndex("vehicleBrand"));
                @SuppressLint("Range") double pricePerKm = cursor.getDouble(cursor.getColumnIndex("pricePerKm"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("endTime"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String surname = cursor.getString(cursor.getColumnIndex("surname"));

                driver = new Driver(id, userId, vehicleBrand, pricePerKm, startTime, endTime, name, surname);
                Log.d("DatabaseHelper", "Driver data retrieved successfully for userId: " + userId);
            } else {
                Log.d("DatabaseHelper", "No driver data found for userId: " + userId);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching driver data for userId: " + userId + ". " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return driver;
    }
    public List<Ride> getRidesForDriver(int driverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Ride> rideList = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT r.*, u.name AS passengerName, u.surname AS passengerSurname " +
                    "FROM ride r " +
                    "JOIN users u ON r.passengerId = u.id " +
                    "WHERE r.driverId = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(driverId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") int passengerId = cursor.getInt(cursor.getColumnIndex("passengerId"));
                    @SuppressLint("Range") double originLat = cursor.getDouble(cursor.getColumnIndex("originLat"));
                    @SuppressLint("Range") double originLng = cursor.getDouble(cursor.getColumnIndex("originLng"));
                    @SuppressLint("Range") double destinationLat = cursor.getDouble(cursor.getColumnIndex("destinationLat"));
                    @SuppressLint("Range") double destinationLng = cursor.getDouble(cursor.getColumnIndex("destinationLng"));
                    @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex("price"));
                    @SuppressLint("Range") double distance = cursor.getDouble(cursor.getColumnIndex("distance"));
                    @SuppressLint("Range") float passengerRating = cursor.getFloat(cursor.getColumnIndex("passengerRating"));
                    @SuppressLint("Range") String passengerName = cursor.getString(cursor.getColumnIndex("passengerName"));
                    @SuppressLint("Range") String passengerSurname = cursor.getString(cursor.getColumnIndex("passengerSurname"));

                    rideList.add(new Ride(id, passengerId, driverId, originLat, originLng,
                            destinationLat, destinationLng, price, distance, passengerName, passengerSurname, 0, passengerRating));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching rides for driver: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        Log.d("DatabaseHelper", "Total rides fetched: " + rideList.size());
        return rideList;
    }
}
