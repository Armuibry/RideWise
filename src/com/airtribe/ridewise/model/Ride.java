package com.airtribe.ridewise.model;

public class Ride {
    private final String id;
    private final Rider rider;
    private Driver driver;
    private final double distance;
    private RideStatus status;
    private FareReceipt fareReceipt;
    private final VehicleType vehicleType;

    public Ride(String id, Rider rider, double distance, VehicleType vehicleType) {
        this.id = id;
        this.rider = rider;
        this.distance = distance;
        this.vehicleType = vehicleType;
        this.status = RideStatus.REQUESTED;
    }

    public String getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public double getDistance() {
        return distance;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public FareReceipt getFareReceipt() {
        return fareReceipt;
    }

    public void setFareReceipt(FareReceipt fareReceipt) {
        this.fareReceipt = fareReceipt;
    }

    @Override
    public String toString() {
        return "Ride{id='" + id + "', rider=" + rider.getName() + 
               ", driver=" + (driver != null ? driver.getName() : "None") +
               ", distance=" + distance + "km, vehicle=" + vehicleType +
               ", status=" + status + "}";
    }
}