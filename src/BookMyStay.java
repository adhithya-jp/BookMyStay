/**
 * ================================================================
 * MAIN CLASS – BookMyStay
 * ================================================================
 *
 * Use Case 1: Application Entry & Welcome Message
 * Use Case 2: Basic Room Types & Static Availability
 * Use Case 3: Centralized Room Inventory Management
 * Use Case 4: Room Search & Availability Check
 * Use Case 5: Booking Request (First-Come-First-Served)
 * Use Case 6: Reservation Confirmation & Room Allocation
 * Use Case 7: Add-On Service Selection
 *
 * @author Developer
 * @version 7.0
 */

import java.util.*;

abstract class Room {
    protected int numberOfBeds;
    protected int squareFeet;
    protected double pricePerNight;

    public Room(int numberOfBeds, int squareFeet, double pricePerNight) {
        this.numberOfBeds = numberOfBeds;
        this.squareFeet = squareFeet;
        this.pricePerNight = pricePerNight;
    }

    public void displayRoomDetails() {
        System.out.println("Beds: " + numberOfBeds);
        System.out.println("Size: " + squareFeet + " sqft");
        System.out.println("Price per night: " + pricePerNight);
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super(1, 250, 1500.0); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 400, 2500.0); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 750, 5000.0); }
}

class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}

class RoomSearchService {
    public void searchAvailableRooms(RoomInventory inventory,
                                     Room singleRoom,
                                     Room doubleRoom,
                                     Room suiteRoom) {

        Map<String, Integer> availability = inventory.getRoomAvailability();

        System.out.println("Room Search\n");

        if (availability.get("Single") > 0) {
            System.out.println("Single Room:");
            singleRoom.displayRoomDetails();
            System.out.println("Available: " + availability.get("Single") + "\n");
        }

        if (availability.get("Double") > 0) {
            System.out.println("Double Room:");
            doubleRoom.displayRoomDetails();
            System.out.println("Available: " + availability.get("Double") + "\n");
        }

        if (availability.get("Suite") > 0) {
            System.out.println("Suite Room:");
            suiteRoom.displayRoomDetails();
            System.out.println("Available: " + availability.get("Suite"));
        }
    }
}

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue = new LinkedList<>();

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }
}

class RoomAllocationService {

    private Set<String> allocatedRoomIds = new HashSet<>();

    public String allocateRoom(Reservation reservation, RoomInventory inventory) {

        String roomType = reservation.getRoomType();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (!availability.containsKey(roomType) || availability.get(roomType) <= 0) {
            System.out.println("Booking failed for Guest: " + reservation.getGuestName());
            return null;
        }

        String roomId = generateRoomId(roomType);
        allocatedRoomIds.add(roomId);

        inventory.updateAvailability(roomType, availability.get(roomType) - 1);

        System.out.println("Booking confirmed for Guest: "
                + reservation.getGuestName() + ", Room ID: " + roomId);

        return roomId;
    }

    private String generateRoomId(String roomType) {
        int count = 1;
        String roomId;

        do {
            roomId = roomType + "-" + count;
            count++;
        } while (allocatedRoomIds.contains(roomId));

        return roomId;
    }
}

/**
 * UC7: Add-On Service
 */
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() { return serviceName; }
    public double getCost() { return cost; }
}

/**
 * UC7: Add-On Service Manager
 */
class AddOnServiceManager {
    private Map<String, List<AddOnService>> servicesByReservation = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        servicesByReservation
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    public double calculateTotalServiceCost(String reservationId) {
        double total = 0.0;

        List<AddOnService> services = servicesByReservation.get(reservationId);

        if (services != null) {
            for (AddOnService s : services) {
                total += s.getCost();
            }
        }

        return total;
    }
}

public class BookMyStay {

    public static void main(String[] args) {

        // UC1
        System.out.println("Welcome to the Hotel Booking Management System\n");

        // UC4
        SingleRoom single = new SingleRoom();
        DoubleRoom doubleRoom = new DoubleRoom();
        SuiteRoom suite = new SuiteRoom();
        RoomInventory inventory = new RoomInventory();

        new RoomSearchService().searchAvailableRooms(inventory, single, doubleRoom, suite);

        // UC5
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Subha", "Double"));
        queue.addRequest(new Reservation("Vanmathi", "Suite"));

        // UC6
        System.out.println("\nRoom Allocation Processing\n");

        RoomAllocationService allocator = new RoomAllocationService();
        List<String> confirmedReservations = new ArrayList<>();

        while (queue.hasPendingRequests()) {
            Reservation r = queue.getNextRequest();
            String roomId = allocator.allocateRoom(r, inventory);

            if (roomId != null) {
                confirmedReservations.add(roomId);
            }
        }

        // UC7
        System.out.println("\nAdd-On Service Selection\n");

        AddOnServiceManager serviceManager = new AddOnServiceManager();

        if (!confirmedReservations.isEmpty()) {
            String reservationId = confirmedReservations.get(0);

            serviceManager.addService(reservationId, new AddOnService("Breakfast", 500));
            serviceManager.addService(reservationId, new AddOnService("Spa", 1000));

            double total = serviceManager.calculateTotalServiceCost(reservationId);

            System.out.println("Reservation ID: " + reservationId);
            System.out.println("Total Add-On Cost: " + total);
        }
    }
}