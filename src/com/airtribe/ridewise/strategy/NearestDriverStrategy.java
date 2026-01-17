package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.exception.NoDriverAvailableException;
import java.util.List;

public class NearestDriverStrategy implements RideMatchingStrategy {
    
    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) throws NoDriverAvailableException {
        Driver nearestDriver = null;
        double minDistance = Double.MAX_VALUE;

        for (Driver driver : drivers) {
            if (driver.isAvailable()) {
                double distance = calculateDistance(rider.getLocation(), driver.getCurrentLocation());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestDriver = driver;
                }
            }
        }

        if (nearestDriver == null) {
            throw new NoDriverAvailableException("No available drivers found nearby");
        }

        return nearestDriver;
    }

    private double calculateDistance(String location1, String location2) {
        // Simplified distance calculation based on location string similarity
        // In real-world, this would use GPS coordinates
        return Math.abs(location1.hashCode() - location2.hashCode()) % 100;
    }
}