package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.util.IdGenerator;
import java.util.HashMap;
import java.util.Map;

public class RiderService {
    private final Map<String, Rider> riders = new HashMap<>();

    public Rider registerRider(String name, String location) {
        String id = IdGenerator.generateRiderId();
        Rider rider = new Rider(id, name, location);
        riders.put(id, rider);
        return rider;
    }

    public Rider getRiderById(String id) {
        return riders.get(id);
    }

    public Map<String, Rider> getAllRiders() {
        return new HashMap<>(riders);
    }
}