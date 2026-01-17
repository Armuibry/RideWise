package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.util.IdGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverService {
    private final Map<String, Driver> drivers = new HashMap<>();

    public Driver registerDriver(String name, String location) {
        String id = IdGenerator.generateDriverId();
        Driver driver = new Driver(id, name, location);
        drivers.put(id, driver);
        return driver;
    }

    public void updateAvailability(String driverId, boolean available) {
        Driver driver = drivers.get(driverId);
        if (driver != null) {
            driver.setAvailable(available);
        }
    }

    public List<Driver> getAvailableDrivers() {
        List<Driver> availableDrivers = new ArrayList<>();
        for (Driver driver : drivers.values()) {
            if (driver.isAvailable()) {
                availableDrivers.add(driver);
            }
        }
        return availableDrivers;
    }

    public List<Driver> getAllDrivers() {
        return new ArrayList<>(drivers.values());
    }

    public Driver getDriverById(String id) {
        return drivers.get(id);
    }
}