package com.airtribe.ridewise.model;

public class Driver {
    private final String id;
    private String name;
    private String currentLocation;
    private boolean available;
    private int ridesCompleted;

    public Driver(String id, String name, String currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        this.available = true;
        this.ridesCompleted = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String location) {
        this.currentLocation = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getRidesCompleted() {
        return ridesCompleted;
    }

    public void incrementRidesCompleted() {
        this.ridesCompleted++;
    }

    @Override
    public String toString() {
        return "Driver{id='" + id + "', name='" + name + "', location='" + currentLocation + 
               "', available=" + available + ", rides=" + ridesCompleted + "}";
    }
}