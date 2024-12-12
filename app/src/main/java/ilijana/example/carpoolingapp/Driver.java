package ilijana.example.carpoolingapp;

import java.io.Serializable;

public class Driver implements Serializable {
    private int id;
    private int userId;
    private double pricePerKm;
    private String vehicleBrand;
    private String startTime;
    private String endTime;
    private String name;
    private String surname;

    public Driver(int id, int userId,String vehicleBrand, double pricePerKm, String startTime, String endTime,String name, String surname) {
        this.id=id;
        this.userId=userId;
        this.pricePerKm = pricePerKm;
        this.vehicleBrand = vehicleBrand;
        this.startTime=startTime;
        this.endTime=endTime;
        this.name=name;
        this.surname=surname;
    }
    public int getId(){
        return id;
    }
    public int getUserId(){
        return userId;
    }
    public double getPricePerKm() {
        return this.pricePerKm;
    }
    public String getVehicleBrand() {
        return vehicleBrand;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

}
