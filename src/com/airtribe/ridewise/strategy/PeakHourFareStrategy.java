package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Ride;

public class PeakHourFareStrategy implements FareStrategy {
    private static final double BASE_FARE_BIKE = 30.0;
    private static final double BASE_FARE_AUTO = 50.0;
    private static final double BASE_FARE_CAR = 80.0;
    
    private static final double PER_KM_RATE_BIKE = 8.0;
    private static final double PER_KM_RATE_AUTO = 12.0;
    private static final double PER_KM_RATE_CAR = 15.0;
    
    private static final double PEAK_MULTIPLIER = 1.5;

    @Override
    public double calculateFare(Ride ride) {
        double baseFare;
        double perKmRate;
        
        switch (ride.getVehicleType()) {
            case BIKE:
                baseFare = BASE_FARE_BIKE;
                perKmRate = PER_KM_RATE_BIKE;
                break;
            case AUTO:
                baseFare = BASE_FARE_AUTO;
                perKmRate = PER_KM_RATE_AUTO;
                break;
            case CAR:
                baseFare = BASE_FARE_CAR;
                perKmRate = PER_KM_RATE_CAR;
                break;
            default:
                baseFare = BASE_FARE_AUTO;
                perKmRate = PER_KM_RATE_AUTO;
        }
        
        double regularFare = baseFare + (ride.getDistance() * perKmRate);
        return regularFare * PEAK_MULTIPLIER;
    }
}