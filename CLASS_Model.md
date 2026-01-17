# Class Model & Architecture

## System Architecture

RideWise follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│            (Main.java)                  │
│     Console UI & Menu Handling          │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│          Service Layer                  │
│   RiderService, DriverService,          │
│        RideService                      │
│   (Business Logic & Orchestration)      │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│         Strategy Layer                  │
│   RideMatchingStrategy, FareStrategy    │
│   (Pluggable Algorithms)                │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│          Domain Layer                   │
│   Rider, Driver, Ride, FareReceipt      │
│   (Core Business Entities)              │
└─────────────────────────────────────────┘
```

---

## Core Domain Model

### Entity Classes

#### 1. Rider
```java
class Rider {
    - id: String
    - name: String
    - location: String
    
    + Rider(id: String, name: String, location: String)
    + getId(): String
    + getName(): String
    + getLocation(): String
    + setLocation(location: String): void
}
```

**Responsibilities**:
- Represents users who request rides
- Stores rider identification and current location
- Immutable ID, mutable location

**Relationships**:
- Has many `Ride` objects (Association)

---

#### 2. Driver
```java
class Driver {
    - id: String
    - name: String
    - currentLocation: String
    - available: boolean
    - ridesCompleted: int
    
    + Driver(id: String, name: String, location: String)
    + getId(): String
    + getName(): String
    + getCurrentLocation(): String
    + setCurrentLocation(location: String): void
    + isAvailable(): boolean
    + setAvailable(available: boolean): void
    + getRidesCompleted(): int
    + incrementRidesCompleted(): void
}
```

**Responsibilities**:
- Represents drivers who accept rides
- Tracks availability status
- Maintains ride completion count for analytics

**Relationships**:
- Has many `Ride` objects (Association)

---

#### 3. Ride
```java
class Ride {
    - id: String
    - rider: Rider
    - driver: Driver
    - distance: double
    - status: RideStatus
    - fareReceipt: FareReceipt
    
    + Ride(id: String, rider: Rider, distance: double)
    + getId(): String
    + getRider(): Rider
    + getDriver(): Driver
    + setDriver(driver: Driver): void
    + getDistance(): double
    + getStatus(): RideStatus
    + setStatus(status: RideStatus): void
    + getFareReceipt(): FareReceipt
    + setFareReceipt(receipt: FareReceipt): void
}
```

**Responsibilities**:
- Central entity representing a trip
- Connects rider and driver
- Tracks ride lifecycle through statuses

**Relationships**:
- Associates with one `Rider`
- Associates with one `Driver` (when assigned)
- Composes one `FareReceipt` (when completed)

**State Transitions**:
```
REQUESTED → ASSIGNED → COMPLETED
    ↓           ↓
CANCELLED   CANCELLED
```

---

#### 4. FareReceipt
```java
class FareReceipt {
    - rideId: String
    - amount: double
    - generatedAt: LocalDateTime
    
    + FareReceipt(rideId: String, amount: double)
    + getRideId(): String
    + getAmount(): double
    + getGeneratedAt(): LocalDateTime
}
```

**Responsibilities**:
- Stores billing information
- Timestamps fare calculation
- Immutable once created

**Relationships**:
- Composed by `Ride` (composition, not association)
- Cannot exist without parent `Ride`

---

### Enumerations

#### RideStatus
```java
enum RideStatus {
    REQUESTED,   // Initial state when ride created
    ASSIGNED,    // Driver matched and assigned
    COMPLETED,   // Ride finished, fare calculated
    CANCELLED    // Ride cancelled by rider or system
}
```

#### VehicleType
```java
enum VehicleType {
    BIKE,   // Two-wheeler
    AUTO,   // Three-wheeler
    CAR     // Four-wheeler
}
```

---

## Service Layer

### 1. RiderService

```java
class RiderService {
    - riders: Map<String, Rider>
    
    + registerRider(name: String, location: String): Rider
    + getRiderById(id: String): Rider
    + getAllRiders(): Map<String, Rider>
}
```

**Purpose**: Manages rider registry  
**Pattern**: Repository pattern (in-memory)  
**Dependencies**: None

---

### 2. DriverService

```java
class DriverService {
    - drivers: Map<String, Driver>
    
    + registerDriver(name: String, location: String): Driver
    + updateAvailability(driverId: String, available: boolean): void
    + getAvailableDrivers(): List<Driver>
    + getAllDrivers(): List<Driver>
    + getDriverById(id: String): Driver
}
```

**Purpose**: Manages driver registry and availability  
**Pattern**: Repository pattern (in-memory)  
**Dependencies**: None

---

### 3. RideService

```java
class RideService {
    - rides: Map<String, Ride>
    - matchingStrategy: RideMatchingStrategy
    - fareStrategy: FareStrategy
    - driverService: DriverService
    
    + RideService(matchingStrategy: RideMatchingStrategy,
                  fareStrategy: FareStrategy,
                  driverService: DriverService)
    + requestRide(rider: Rider, distance: double): Ride
    + completeRide(rideId: String): void
    + cancelRide(rideId: String): void
    + getRideById(rideId: String): Ride
    + getAllRides(): List<Ride>
}
```

**Purpose**: Orchestrates ride lifecycle  
**Pattern**: Service pattern with Strategy composition  
**Dependencies**: 
- `RideMatchingStrategy` (interface)
- `FareStrategy` (interface)
- `DriverService` (concrete)

---

## Strategy Pattern Implementation

### 1. RideMatchingStrategy

```java
<<interface>> RideMatchingStrategy {
    + findDriver(rider: Rider, drivers: List<Driver>): Driver
      throws NoDriverAvailableException
}
```

**Implementations**:

#### NearestDriverStrategy
```java
class NearestDriverStrategy implements RideMatchingStrategy {
    + findDriver(rider: Rider, drivers: List<Driver>): Driver
    - calculateDistance(loc1: String, loc2: String): double
}
```
**Algorithm**: Finds geographically closest available driver

#### LeastActiveDriverStrategy
```java
class LeastActiveDriverStrategy implements RideMatchingStrategy {
    + findDriver(rider: Rider, drivers: List<Driver>): Driver
}
```
**Algorithm**: Selects driver with fewest completed rides (load balancing)

---

### 2. FareStrategy

```java
<<interface>> FareStrategy {
    + calculateFare(ride: Ride): double
}
```

**Implementations**:

#### DefaultFareStrategy
```java
class DefaultFareStrategy implements FareStrategy {
    - BASE_FARE: double = 50.0
    - PER_KM_RATE: double = 12.0
    
    + calculateFare(ride: Ride): double
}
```
**Formula**: `fare = BASE_FARE + (distance × PER_KM_RATE)`

#### PeakHourFareStrategy
```java
class PeakHourFareStrategy implements FareStrategy {
    - BASE_FARE: double = 50.0
    - PER_KM_RATE: double = 12.0
    - PEAK_MULTIPLIER: double = 1.5
    
    + calculateFare(ride: Ride): double
}
```
**Formula**: `fare = (BASE_FARE + distance × PER_KM_RATE) × PEAK_MULTIPLIER`

---

## Utility Classes

### IdGenerator

```java
class IdGenerator {
    - riderCounter: AtomicInteger
    - driverCounter: AtomicInteger
    - rideCounter: AtomicInteger
    
    + generateRiderId(): String
    + generateDriverId(): String
    + generateRideId(): String
}
```

**Purpose**: Thread-safe unique ID generation  
**Pattern**: Utility class (static methods)  
**Format**:
- Rider: `RDR0001`, `RDR0002`, ...
- Driver: `DRV0001`, `DRV0002`, ...
- Ride: `RIDE0001`, `RIDE0002`, ...

---

## Exception Hierarchy

```java
class NoDriverAvailableException extends Exception {
    + NoDriverAvailableException(message: String)
}
```

**Usage**: Thrown when no drivers match ride request criteria

---

## Object Relationships

### Association vs Composition

#### Association (Weak Relationship)
- `Rider` ←→ `Ride`: Rider can exist without rides
- `Driver` ←→ `Ride`: Driver can exist without rides

#### Composition (Strong Relationship)
- `Ride` ◆→ `FareReceipt`: Receipt cannot exist without ride
- `RideService` ◆→ `Strategies`: Service owns strategy instances

### Dependency Relationships

```
RideService ─depends on→ RideMatchingStrategy
RideService ─depends on→ FareStrategy
RideService ─depends on→ DriverService

Main ─depends on→ All Services
Main ─depends on→ All Strategies
```

---

## Class Diagram (UML)

```
┌──────────────────┐
│     Rider        │
├──────────────────┤
│ - id: String     │
│ - name: String   │
│ - location: Str  │
└──────────────────┘
         △
         │ associates
         │
┌──────────────────┐      ┌──────────────────────┐
│     Ride         │◆────→│   FareReceipt        │
├──────────────────┤      ├──────────────────────┤
│ - id: String     │      │ - rideId: String     │
│ - rider: Rider   │      │ - amount: double     │
│ - driver: Driver │      │ - generatedAt: Date  │
│ - distance: dbl  │      └──────────────────────┘
│ - status: Enum   │              (composition)
└──────────────────┘
         │ associates
         ▽
┌──────────────────┐
│     Driver       │
├──────────────────┤
│ - id: String     │
│ - name: String   │
│ - available: bool│
└──────────────────┘
```

---

## Design Decisions

### Why Composition for FareReceipt?
- Receipt has no meaning without parent ride
- Lifecycle bound to ride
- Deleted when ride deleted

### Why Association for Rider/Driver?
- Entities have independent lifecycles
- Can exist before/after rides
- Managed by separate services

### Why Strategy Pattern?
- Multiple algorithms needed (matching, pricing)
- Algorithms change independently
- Easy to add new strategies
- Runtime selection of behavior

### Why Service Layer?
- Separates business logic from presentation
- Coordinates between entities
- Enforces business rules
- Single point of control

---

## Extensibility Points

### Adding New Entities
1. Create entity class in `model/` package
2. Add to appropriate service
3. Update relationships

### Adding New Strategies
1. Create class implementing strategy interface
2. No changes to existing code
3. Inject in `Main.java`

### Adding New Services
1. Create service in `service/` package
2. Follow SRP
3. Inject dependencies via constructor

---

## Conclusion

The class model demonstrates:
- **Clear entity boundaries**
- **Appropriate relationship types**
- **Separation of concerns**
- **Extensible design**
- **SOLID compliance**

This architecture supports growth while maintaining simplicity and clarity.