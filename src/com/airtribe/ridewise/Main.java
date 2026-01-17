package com.airtribe.ridewise;

import com.airtribe.ridewise.model.*;
import com.airtribe.ridewise.service.*;
import com.airtribe.ridewise.strategy.*;
import com.airtribe.ridewise.exception.NoDriverAvailableException;
import java.util.Scanner;

public class Main {
    private static RiderService riderService;
    private static DriverService driverService;
    private static RideService rideService;
    private static Scanner scanner;

    public static void main(String[] args) {
        initializeServices();
        scanner = new Scanner(System.in);
        
        System.out.println("========================================");
        System.out.println("   Welcome to RideWise Ride-Sharing");
        System.out.println("========================================\n");

        boolean running = true;
        while (running) {
            displayMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                running = handleMenuChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.\n");
            }
        }

        scanner.close();
        System.out.println("Thank you for using RideWise!");
    }

    private static void initializeServices() {
        riderService = new RiderService();
        driverService = new DriverService();
        
        RideMatchingStrategy matchingStrategy = new NearestDriverStrategy();
        FareStrategy fareStrategy = new DefaultFareStrategy();
        
        rideService = new RideService(matchingStrategy, fareStrategy, driverService);
    }

    private static void displayMenu() {
        System.out.println("========== Main Menu ==========");
        System.out.println("1. Add Rider");
        System.out.println("2. Add Driver");
        System.out.println("3. View Available Drivers");
        System.out.println("4. Request Ride");
        System.out.println("5. Complete Ride");
        System.out.println("6. View Rides");
        System.out.println("7. Exit");
        System.out.println("================================");
        System.out.print("Enter your choice: ");
    }

    private static boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                addRider();
                break;
            case 2:
                addDriver();
                break;
            case 3:
                viewAvailableDrivers();
                break;
            case 4:
                requestRide();
                break;
            case 5:
                completeRide();
                break;
            case 6:
                viewRides();
                break;
            case 7:
                return false;
            default:
                System.out.println("Invalid choice! Please try again.\n");
        }
        return true;
    }

    private static void addRider() {
        System.out.println("\n--- Add Rider ---");
        System.out.print("Enter rider name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter location: ");
        String location = scanner.nextLine().trim();
        
        if (name.isEmpty() || location.isEmpty()) {
            System.out.println("Error: Name and location cannot be empty!\n");
            return;
        }
        
        Rider rider = riderService.registerRider(name, location);
        System.out.println("Rider registered successfully!");
        System.out.println(rider + "\n");
    }

    private static void addDriver() {
        System.out.println("\n--- Add Driver ---");
        System.out.print("Enter driver name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter location: ");
        String location = scanner.nextLine().trim();
        
        if (name.isEmpty() || location.isEmpty()) {
            System.out.println("Error: Name and location cannot be empty!\n");
            return;
        }
        
        Driver driver = driverService.registerDriver(name, location);
        System.out.println("Driver registered successfully!");
        System.out.println(driver + "\n");
    }

    private static void viewAvailableDrivers() {
        System.out.println("\n--- Available Drivers ---");
        var drivers = driverService.getAvailableDrivers();
        
        if (drivers.isEmpty()) {
            System.out.println("No available drivers at the moment.\n");
            return;
        }
        
        for (Driver driver : drivers) {
            System.out.println(driver);
        }
        System.out.println();
    }

    private static void requestRide() {
        System.out.println("\n--- Request Ride ---");
        
        // Display all riders
        var allRiders = riderService.getAllRiders();
        if (allRiders.isEmpty()) {
            System.out.println("No riders registered! Please add a rider first.\n");
            return;
        }
        
        System.out.println("Available Riders:");
        for (Rider r : allRiders.values()) {
            System.out.println("  " + r);
        }
        
        System.out.print("\nEnter rider ID: ");
        String riderId = scanner.nextLine().trim();
        
        Rider rider = riderService.getRiderById(riderId);
        if (rider == null) {
            System.out.println("Error: Rider not found!\n");
            return;
        }
        
        System.out.print("Enter distance (km): ");
        try {
            double distance = Double.parseDouble(scanner.nextLine().trim());
            
            if (distance <= 0) {
                System.out.println("Error: Distance must be positive!\n");
                return;
            }
            
            // Select vehicle type
            System.out.println("\nSelect Vehicle Type:");
            System.out.println("1. BIKE");
            System.out.println("2. AUTO");
            System.out.println("3. CAR");
            System.out.print("Enter choice (1-3): ");
            
            int vehicleChoice = Integer.parseInt(scanner.nextLine().trim());
            VehicleType vehicleType;
            
            switch (vehicleChoice) {
                case 1:
                    vehicleType = VehicleType.BIKE;
                    break;
                case 2:
                    vehicleType = VehicleType.AUTO;
                    break;
                case 3:
                    vehicleType = VehicleType.CAR;
                    break;
                default:
                    System.out.println("Invalid choice! Defaulting to CAR.\n");
                    vehicleType = VehicleType.CAR;
            }
            
            Ride ride = rideService.requestRide(rider, distance, vehicleType);
            System.out.println("\nRide requested successfully!");
            System.out.println(ride);
            System.out.println("Driver assigned: " + ride.getDriver().getName() + "\n");
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input format!\n");
        } catch (NoDriverAvailableException e) {
            System.out.println("Error: " + e.getMessage() + "\n");
        }
    }

    private static void completeRide() {
        System.out.println("\n--- Complete Ride ---");
        System.out.print("Enter ride ID: ");
        String rideId = scanner.nextLine().trim();
        
        Ride ride = rideService.getRideById(rideId);
        if (ride == null) {
            System.out.println("Error: Ride not found!\n");
            return;
        }
        
        if (ride.getStatus() != RideStatus.ASSIGNED) {
            System.out.println("Error: Ride cannot be completed. Current status: " + 
                             ride.getStatus() + "\n");
            return;
        }
        
        rideService.completeRide(rideId);
        System.out.println("Ride completed successfully!");
        System.out.println(ride);
        System.out.println(ride.getFareReceipt() + "\n");
    }

    private static void viewRides() {
        System.out.println("\n--- All Rides ---");
        var rides = rideService.getAllRides();
        
        if (rides.isEmpty()) {
            System.out.println("No rides found.\n");
            return;
        }
        
        for (Ride ride : rides) {
            System.out.println(ride);
            if (ride.getFareReceipt() != null) {
                System.out.println("  " + ride.getFareReceipt());
            }
        }
        System.out.println();
    }
}