package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.exception.NoDriverAvailableException;
import java.util.List;

public interface RideMatchingStrategy {
    Driver findDriver(Rider rider, List<Driver> drivers) throws NoDriverAvailableException;
}