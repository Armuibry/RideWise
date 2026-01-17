package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.*;
import com.airtribe.ridewise.strategy.FareStrategy;
import com.airtribe.ridewise.strategy.RideMatchingStrategy;
import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.util.IdGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RideService {
    private final Map<String, Ride> rides = new HashMap<>();
    private final RideMatchingStrategy matchingStrategy;
    private final FareStrategy fareStrategy;
    private final DriverService driverService;

    public RideService(RideMatchingStrategy matchingStrategy, 
                      FareStrategy fareStrategy,
                      DriverService driverService) {
        this.matchingStrategy = matchingStrategy;
        this.fareStrategy = fareStrategy;
        this.driverService = driverService;
    }

    public Ride requestRide(Rider rider, double distance, VehicleType vehicleType) throws NoDriverAvailableException {
        String rideId = IdGenerator.generateRideId();
        Ride ride = new Ride(rideId, rider, distance, vehicleType);
        
        Driver assignedDriver = matchingStrategy.findDriver(rider, driverService.getAllDrivers());
        
        ride.setDriver(assignedDriver);
        ride.setStatus(RideStatus.ASSIGNED);
        assignedDriver.setAvailable(false);
        
        rides.put(rideId, ride);
        return ride;
    }

    public void completeRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null && ride.getStatus() == RideStatus.ASSIGNED) {
            double fare = fareStrategy.calculateFare(ride);
            FareReceipt receipt = new FareReceipt(rideId, fare);
            
            ride.setFareReceipt(receipt);
            ride.setStatus(RideStatus.COMPLETED);
            
            Driver driver = ride.getDriver();
            driver.setAvailable(true);
            driver.incrementRidesCompleted();
        }
    }

    public void cancelRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null && ride.getStatus() != RideStatus.COMPLETED) {
            ride.setStatus(RideStatus.CANCELLED);
            if (ride.getDriver() != null) {
                ride.getDriver().setAvailable(true);
            }
        }
    }

    public Ride getRideById(String rideId) {
        return rides.get(rideId);
    }

    public List<Ride> getAllRides() {
        return new ArrayList<>(rides.values());
    }
}