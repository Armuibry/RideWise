# Object Relationships & Design Patterns

## Overview

This document explains the relationships between objects in RideWise and the reasoning behind key design decisions.

---

## Relationship Types

### Association
A "uses" or "has" relationship where objects maintain independent lifecycles. Objects can exist separately and are managed independently.

### Composition
A "part-of" relationship where the child's lifecycle is bound to the parent. The child cannot exist without the parent.

### Dependency
One class uses another temporarily without storing a reference. This is the loosest form of coupling.

---

## Core Entity Relationships

### Rider and Ride (Association)

```java
public class Ride {
    private final Rider rider;  // Ride knows about Rider
}
```

**Type**: One-to-Many Association (unidirectional)

**Design Rationale**:
- Riders exist independently of rides
- Multiple rides can reference the same rider
- Rider doesn't store references to rides (loose coupling)
- RideService manages the relationship

**Example**:
```java
Rider alice = new Rider("RDR0001", "Alice", "Downtown");
// Alice exists without any rides

Ride ride1 = new Ride("RIDE0001", alice, 10.0, VehicleType.CAR);
Ride ride2 = new Ride("RIDE0002", alice, 15.5, VehicleType.BIKE);
// Multiple rides reference Alice
```

---

### Driver and Ride (Association)

```java
public class Ride {
    private Driver driver;  // Initially null, assigned later
}
```

**Type**: One-to-Many Association (unidirectional)

**Design Rationale**:
- Drivers exist independently
- Driver field is null when ride is REQUESTED
- Driver assigned when ride status changes to ASSIGNED
- Driver availability managed separately in DriverService

**State Evolution**:
```java
Ride ride = new Ride("RIDE0001", rider, 10.0, VehicleType.AUTO);
// driver is null (REQUESTED state)

rideService.assignDriver(ride, driver);
// driver is assigned (ASSIGNED state)
```

---

### Ride and FareReceipt (Composition)

```java
public class Ride {
    private FareReceipt fareReceipt;  // Part of Ride
    private final VehicleType vehicleType;
}

public class FareReceipt {
    private final String rideId;
    private final double amount;
}
```

**Type**: One-to-One Composition

**Design Rationale**:
- FareReceipt cannot exist without a Ride
- Created only when ride is completed
- Receipt is owned exclusively by the Ride
- Vehicle type from Ride is used to calculate fare amount

**Lifecycle**:
```java
// Ride created - no receipt yet
Ride ride = new Ride("RIDE0001", rider, 10.0, VehicleType.CAR);

// Ride completed - receipt generated with vehicle-based fare
double fare = fareStrategy.calculateFare(ride);  // Uses ride.getVehicleType()
FareReceipt receipt = new FareReceipt(ride.getId(), fare);
ride.setFareReceipt(receipt);
```

**Why Composition?**
The receipt is billing data that belongs to the ride. If the ride is deleted, the receipt should be deleted too. The fare amount is calculated based on the ride's vehicle type.

---

### RideService and Strategies (Composition)

```java
public class RideService {
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
}
```

**Type**: Composition via Dependency Injection

**Design Rationale**:
- Strategies define how the service behaves
- Injected at construction time
- Service is incomplete without strategies
- Strategies are immutable after creation

**Usage**:
```java
public Ride requestRide(Rider rider, double distance, VehicleType vehicleType) {
    Driver driver = matchingStrategy.findDriver(rider, drivers);
    // Strategy implements the algorithm
}

public void completeRide(String rideId) {
    double fare = fareStrategy.calculateFare(ride);
    // Strategy calculates based on vehicle type and distance
}
```

---

## Service Dependencies

### RideService and DriverService

```java
public class RideService {
    private final DriverService driverService;
    
    public Ride requestRide(Rider rider, double distance, VehicleType vehicleType) {
        List<Driver> drivers = driverService.getAllDrivers();
        Driver assigned = matchingStrategy.findDriver(rider, drivers);
    }
}
```

**Type**: Dependency (injected)

**Design Rationale**:
- RideService needs driver data but doesn't manage drivers
- DriverService owns driver state and availability
- Clear separation of concerns

---

## Strategy Pattern Implementation

### Matching Strategies

```
RideMatchingStrategy (interface)
    |
    |-- NearestDriverStrategy
    |-- LeastActiveDriverStrategy
```

Both strategies implement the same interface, making them interchangeable:

```java
RideMatchingStrategy strategy1 = new NearestDriverStrategy();
RideMatchingStrategy strategy2 = new LeastActiveDriverStrategy();
// Both work with RideService
```

### Fare Strategies

```
FareStrategy (interface)
    |
    |-- DefaultFareStrategy
    |-- PeakHourFareStrategy
```

Both strategies calculate fares differently based on vehicle type:

**DefaultFareStrategy**:
- BIKE: Base 30 + 8 per km
- AUTO: Base 50 + 12 per km
- CAR: Base 80 + 15 per km

**PeakHourFareStrategy**:
- Same rates multiplied by 1.5x

The vehicle type is retrieved from the Ride object during fare calculation.

---

## System Dependency Graph

```
Main
 |-- RiderService
 |-- DriverService
 |-- RideService
      |-- RideMatchingStrategy (interface)
      |    |-- NearestDriverStrategy
      |    |-- LeastActiveDriverStrategy
      |-- FareStrategy (interface)
      |    |-- DefaultFareStrategy
      |    |-- PeakHourFareStrategy
      |-- DriverService
```

Dependencies flow from concrete to abstract (Dependency Inversion Principle).

---

## Design Principles Applied

### Composition Over Inheritance

Instead of extending classes, we compose behavior:

```java
// Good: Composition
public class RideService {
    private final FareStrategy fareStrategy;
}

// Avoided: Inheritance
public class RideService extends FareCalculator { }
```

### Program to Interfaces

```java
// Good: Interface reference
private final RideMatchingStrategy strategy;

// Avoided: Concrete reference
private final NearestDriverStrategy strategy;
```

### Dependency Injection

```java
// Good: Constructor injection
public RideService(RideMatchingStrategy strategy, ...) {
    this.matchingStrategy = strategy;
}

// Avoided: Hard-coded creation
public RideService() {
    this.matchingStrategy = new NearestDriverStrategy();
}
```

---

## Anti-Patterns Avoided

### God Object
We avoided putting all logic in one class. Instead, we separated into focused services: RiderService, DriverService, and RideService.

### Circular Dependencies
We avoided circular references between Rider and Ride. The relationship is unidirectional and managed by services.

### Tight Coupling
We avoided depending on concrete classes. Services depend on strategy interfaces, allowing runtime flexibility.

---

## Key Design Decisions Summary

| Relationship | Type | Why |
|--------------|------|-----|
| Rider to Ride | Association | Independent lifecycles |
| Driver to Ride | Association | Managed separately |
| Ride to FareReceipt | Composition | Receipt tied to ride lifecycle |
| Ride to VehicleType | Composition | Vehicle type determines fare |
| RideService to Strategies | Composition | Defines behavior |
| RideService to DriverService | Dependency | Separation of concerns |

---

## Extensibility Example

Adding driver ratings requires no changes to existing relationships:

```java
// Add to Driver model
public class Driver {
    private double rating;
}

// Create new strategy
public class HighestRatedDriverStrategy implements RideMatchingStrategy {
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        return drivers.stream()
            .filter(Driver::isAvailable)
            .max(Comparator.comparing(Driver::getRating))
            .orElseThrow();
    }
}

// No changes needed to RideService
```

This demonstrates the Open/Closed Principle in action.

---

## Conclusion

The relationship design provides:
- Low coupling between components
- High cohesion within classes
- Easy extensibility for new features
- Clear separation of responsibilities
- Testable, maintainable code structure

Vehicle type integration shows how composition allows different parts of the system to work together without tight coupling. The Ride owns the vehicle type, and strategies use it through the Ride interface to calculate appropriate fares.