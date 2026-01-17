package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.exception.NoDriverAvailableException;
import java.util.List;

public class LeastActiveDriverStrategy implements RideMatchingStrategy {
    
    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) throws NoDriverAvailableException {
        Driver leastActiveDriver = null;
        int minRides = Integer.MAX_VALUE;

        for (Driver driver : drivers) {
            if (driver.isAvailable()) {
                if (driver.getRidesCompleted() < minRides) {
                    minRides = driver.getRidesCompleted();
                    leastActiveDriver = driver;
                }
            }
        }

        if (leastActiveDriver == null) {
            throw new NoDriverAvailableException("No available drivers found");
        }

        return leastActiveDriver;
    }
}