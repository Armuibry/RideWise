package com.airtribe.ridewise.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger riderCounter = new AtomicInteger(1);
    private static final AtomicInteger driverCounter = new AtomicInteger(1);
    private static final AtomicInteger rideCounter = new AtomicInteger(1);

    public static String generateRiderId() {
        return "RDR" + String.format("%04d", riderCounter.getAndIncrement());
    }

    public static String generateDriverId() {
        return "DRV" + String.format("%04d", driverCounter.getAndIncrement());
    }

    public static String generateRideId() {
        return "RIDE" + String.format("%04d", rideCounter.getAndIncrement());
    }
}