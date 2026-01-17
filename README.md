# RideWise - Modular Ride-Sharing System

A console-based ride-sharing application demonstrating **Low-Level Design (LLD)** principles, **SOLID** design patterns, and clean architecture.

## Project Overview

RideWise is a simplified Uber/Ola style ride-sharing system that allows:
- **Riders** to book rides
- **Drivers** to accept and complete rides
- **Multiple strategies** for driver matching and fare calculation
- **Lifecycle tracking** of rides from request to completion

## Architecture & Design Principles

### SOLID Principles Implementation

1. **Single Responsibility Principle (SRP)**
   - `RiderService`: Handles only rider-related operations
   - `DriverService`: Manages driver registration and availability
   - `RideService`: Orchestrates ride lifecycle
   - Each class has one reason to change

2. **Open/Closed Principle (OCP)**
   - New matching strategies can be added without modifying `RideService`
   - New fare strategies can be added without changing core logic
   - Extension points through interfaces

3. **Liskov Substitution Principle (LSP)**
   - Any `RideMatchingStrategy` implementation is interchangeable
   - Any `FareStrategy` implementation can replace another
   - Polymorphic behavior guaranteed

4. **Interface Segregation Principle (ISP)**
   - Small, focused interfaces: `RideMatchingStrategy`, `FareStrategy`
   - Clients depend only on methods they use

5. **Dependency Inversion Principle (DIP)**
   - Services depend on abstractions (interfaces), not concrete classes
   - Strategies injected via constructor

### Design Patterns

- **Strategy Pattern**: Driver matching and fare calculation
- **Composition over Inheritance**: Services compose strategies
- **Factory Pattern**: ID generation for entities

### Design Principles

- **DRY (Don't Repeat Yourself)**: Eliminates code duplication
- **KISS (Keep It Simple, Stupid)**: Simple entity modeling
- **YAGNI (You Aren't Gonna Need It)**: MVP before feature explosion
- **Law of Demeter**: Services avoid deep method chains

## Project Structure

```
src/
└── com/
    └── airtribe/
        └── ridewise/
            ├── Main.java
            ├── model/
            │   ├── Rider.java
            │   ├── Driver.java
            │   ├── Ride.java
            │   ├── FareReceipt.java
            │   ├── RideStatus.java
            │   └── VehicleType.java
            ├── strategy/
            │   ├── RideMatchingStrategy.java
            │   ├── NearestDriverStrategy.java
            │   ├── LeastActiveDriverStrategy.java
            │   ├── FareStrategy.java
            │   ├── DefaultFareStrategy.java
            │   └── PeakHourFareStrategy.java
            ├── service/
            │   ├── RiderService.java
            │   ├── DriverService.java
            │   └── RideService.java
            ├── exception/
            │   └── NoDriverAvailableException.java
            └── util/
                └── IdGenerator.java
```

## Getting Started

### Prerequisites

- Java 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code) or terminal with `javac`

### Installation

1. Clone the repository:
```bash
git clone <your-repository-url>
cd ridewise
```

2. Compile the project:
```bash
javac -d bin src/com/airtribe/ridewise/**/*.java
```

3. Run the application:
```bash
java -cp bin com.airtribe.ridewise.Main
```

### Quick Start

1. **Add Drivers** (Option 2)
   - Register at least one driver before requesting rides
   
2. **Add Riders** (Option 1)
   - Register riders who will request rides

3. **Request Ride** (Option 4)
   - Enter rider ID and distance
   - System automatically assigns nearest available driver

4. **Complete Ride** (Option 5)
   - Enter ride ID to complete and calculate fare

## 📋 Features

### Functional Requirements

- Register riders and drivers  
- Show available drivers  
- Request rides with automatic driver matching  
- Multiple ride matching strategies  
- Flexible fare calculation  
- Track ride status (REQUESTED, ASSIGNED, COMPLETED, CANCELLED)

### Non-Functional Requirements

- Easily extendable pricing algorithms  
- Pluggable driver matching logic  
- Low coupling between services  
- Maintainable and readable code  
- Exception handling

## Usage Example

```
========== Main Menu ==========
1. Add Rider
2. Add Driver
3. View Available Drivers
4. Request Ride
5. Complete Ride
6. View Rides
7. Exit
================================

> 2
Enter driver name: John Doe
Enter location: Downtown
Driver registered successfully!
Driver{id='DRV0001', name='John Doe', location='Downtown', available=true, rides=0}

> 1
Enter rider name: Alice Smith
Enter location: Uptown
Rider registered successfully!
Rider{id='RDR0001', name='Alice Smith', location='Uptown'}

> 4
Enter rider ID: RDR0001
Enter distance (km): 15.5
Select Vehicle Type:
1. BIKE
2. AUTO
3. CAR
Enter choice (1-3): 1
Ride requested successfully!
Ride{id='RIDE0001', rider=Asad, driver=Kadir, distance=30.0km, vehicle=BIKE, status=ASSIGNED}
Driver assigned: Kadir

> 5
Enter ride ID: RIDE0001
Ride completed successfully!
FareReceipt{rideId='RIDE0001', amount=236.0, generatedAt=2025-01-17T...}
```

## Extensibility

### Adding New Matching Strategies

Create a new class implementing `RideMatchingStrategy`:

```java
public class HighestRatedDriverStrategy implements RideMatchingStrategy {
    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) 
            throws NoDriverAvailableException {
        // Your implementation
    }
}
```

### Adding New Fare Strategies

Create a new class implementing `FareStrategy`:

```java
public class WeekendFareStrategy implements FareStrategy {
    @Override
    public double calculateFare(Ride ride) {
        // Your implementation
    }
}
```

Update `Main.java` to inject your new strategy:

```java
RideMatchingStrategy matchingStrategy = new HighestRatedDriverStrategy();
FareStrategy fareStrategy = new WeekendFareStrategy();
```

## Domain Model

### Core Entities

- **Rider**: Represents users requesting rides
- **Driver**: Represents drivers accepting rides
- **Ride**: Represents a trip from request to completion
- **FareReceipt**: Contains billing information

### Relationships

- `Rider` → `Ride`: Association (one-to-many)
- `Driver` → `Ride`: Association (one-to-many)
- `Ride` → `FareReceipt`: Composition (one-to-one)
- `RideService` → `Strategy Interfaces`: Composition

## Technology Stack

- **Language**: Java 8+
- **Paradigm**: Object-Oriented Programming
- **Design**: SOLID Principles, Strategy Pattern
- **Build**: Manual compilation (no build tools required)

## Documentation

Additional documentation can be found in the `docs/` folder:
- `Requirements.md`: Detailed functional and non-functional requirements
- `Class_Model.md`: UML diagrams and class relationships
- `SOLID_Reflection.md`: Analysis of SOLID principle application
- `Object_Relationships.md`: Entity relationships and design decisions

## Testing

To test the application:
1. Register multiple drivers with different locations
2. Register riders
3. Request rides and observe driver matching
4. Complete rides and verify fare calculation
5. Check driver availability updates

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement your changes following SOLID principles
4. Submit a pull request

## License

This project is for educational purposes demonstrating LLD and design patterns.

## Author

Developed as part of Airtribe's LLD assignment showcasing:
- Clean code principles
- Design pattern implementation
- Modular architecture
- Separation of concerns