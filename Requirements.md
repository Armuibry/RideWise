# Requirements

## Learning Objectives

### OOP & SOLID

**SRP (Single Responsibility Principle)**
- Single-purpose classes (RideService, DriverService, FareCalculator)

**OCP (Open/Closed Principle)**
- Adding new ride selection or pricing strategies without modifying core logic

**LSP (Liskov Substitution Principle)**
- Any strategy implementation must remain interchangeable

**ISP (Interface Segregation Principle)**
- Small focused interfaces (RideMatchingStrategy, FareStrategy)

**DIP (Dependency Inversion Principle)**
- Services depend on interfaces — not concrete classes

### Design Principles

**DRY**
- Eliminate duplication in ride allocation logic

**KISS**
- Simple entity modeling

**YAGNI**
- MVP before features explosion

**Law of Demeter**
- Services communicate with collaborators directly — no deep method chains

### LLD Skills

- Modeling entities & relationships
- Separating concerns into layers
- Designing scalable extension points

---

## Project Requirements

### A. Functional Requirements

1. Register Riders
2. Register Drivers
3. Show available drivers
4. Request a ride
5. Match ride to driver using a strategy
6. Calculate fare using a pricing strategy
7. Track ride status:
   - REQUESTED
   - ASSIGNED
   - COMPLETED
   - CANCELLED

### B. Non-Functional Requirements

- Easily extendable pricing algorithm
- Easily change driver matching logic
- Low coupling between services
- Maintainable and readable code

### C. Domain Entities

**Package → model/**

**Core Classes**

**Rider**
- id
- name
- location

**Driver**
- id
- name
- currentLocation
- available (boolean)

**Ride**
- id
- rider
- driver
- distance
- status

**FareReceipt**
- rideId
- amount
- generatedAt

**Enums**
- RideStatus
- VehicleType (BIKE, AUTO, CAR)

### D. Strategy & Composition Design

**1. Ride Matching Strategy**

```java
public interface RideMatchingStrategy {
    Driver findDriver(Rider rider, List<Driver> drivers);
}
```

**Implementations:**
- NearestDriverStrategy
- LeastActiveDriverStrategy

**2. Fare Calculation Strategy**

```java
public interface FareStrategy {
    double calculateFare(Ride ride);
}
```

**Implementations:**
- DefaultFareStrategy
- PeakHourFareStrategy

These strategies are injected into the RideService constructor.

This ensures:
- DIP compliance
- OCP compliance
- Composition over inheritance

### E. Service Layer

**Package → service/**

**RiderService**
- Register riders
- Get rider by ID

**DriverService**
- Register drivers
- Update availability
- List available drivers

**RideService**
- Request a ride
- Assign driver using RideMatchingStrategy
- Calculate fare using FareStrategy
- Complete ride

### F. Console Application (Menu)

**Main menu in Main.java:**

1. Add Rider
2. Add Driver
3. View Available Drivers
4. Request Ride
5. Complete Ride
6. View Rides
7. Exit

**Each option must:**
- Use service-layer only
- Catch invalid input
- Avoid tightly-coupled logic