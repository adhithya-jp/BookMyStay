import java.util.*;

/* =========================
   ROOM CLASSES (UC2)
   ========================= */
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

/* =========================
   INVENTORY (UC3)
   ========================= */
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

/* =========================
   SEARCH (UC4)
   ========================= */
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

/* =========================
   RESERVATION (UC5)
   ========================= */
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

/* =========================
   QUEUE (UC5)
   ========================= */
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

/* =========================
   ROOM ALLOCATION (UC6)
   ========================= */
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

/* =========================
   ADD-ON SERVICES (UC7)
   ========================= */
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

/* =========================
   BOOKING HISTORY (UC8)
   ========================= */
class BookingHistory {
    private List<Reservation> confirmedReservations = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    public List<Reservation> getConfirmedReservations() {
        return confirmedReservations;
    }
}

/* =========================
   REPORT SERVICE (UC8)
   ========================= */
class BookingReportService {
    public void generateReport(BookingHistory history) {

        System.out.println("\nBooking History Report\n");

        List<Reservation> list = history.getConfirmedReservations();

        if (list.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : list) {
            System.out.println("Guest: " + r.getGuestName()
                    + ", Room Type: " + r.getRoomType());
        }
    }
}

/* =========================
   UC9: EXCEPTION
   ========================= */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/* =========================
   UC9: VALIDATOR
   ========================= */
class ReservationValidator {

    public void validate(String guestName, String roomType, RoomInventory inventory)
            throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        Map<String, Integer> availability = inventory.getRoomAvailability();

        if (!availability.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type selected.");
        }

        if (availability.get(roomType) <= 0) {
            throw new InvalidBookingException("Selected room not available.");
        }
    }
}

/* =========================
   UC10: CANCELLATION SERVICE
   ========================= */
class CancellationService {

    private Stack<String> releasedRoomIds = new Stack<>();
    private Map<String, String> reservationRoomTypeMap = new HashMap<>();

    public void registerBooking(String reservationId, String roomType) {
        reservationRoomTypeMap.put(reservationId, roomType);
    }

    public void cancelBooking(String reservationId, RoomInventory inventory) {

        if (!reservationRoomTypeMap.containsKey(reservationId)) {
            System.out.println("Invalid cancellation request.");
            return;
        }

        String roomType = reservationRoomTypeMap.get(reservationId);

        Map<String, Integer> availability = inventory.getRoomAvailability();
        inventory.updateAvailability(roomType, availability.get(roomType) + 1);

        releasedRoomIds.push(reservationId);
        reservationRoomTypeMap.remove(reservationId);

        System.out.println("Booking cancelled successfully. Inventory restored for room type: " + roomType);
    }

    public void showRollbackHistory() {
        System.out.println("\nRollback History (Most Recent First):");
        for (String id : releasedRoomIds) {
            System.out.println("Released Reservation ID: " + id);
        }
    }
}

/* =========================
   MAIN CLASS
   ========================= */
public class BookMyStay {

    public static void main(String[] args) {

        System.out.println("Welcome to the Hotel Booking Management System\n");

        SingleRoom single = new SingleRoom();
        DoubleRoom doubleRoom = new DoubleRoom();
        SuiteRoom suite = new SuiteRoom();
        RoomInventory inventory = new RoomInventory();

        new RoomSearchService().searchAvailableRooms(inventory, single, doubleRoom, suite);

        BookingRequestQueue queue = new BookingRequestQueue();
        ReservationValidator validator = new ReservationValidator();
        CancellationService cancellationService = new CancellationService();

        // UC9 Validation (per request)
        try {
            validator.validate("Abhi", "Single", inventory);
            queue.addRequest(new Reservation("Abhi", "Single"));
        } catch (Exception e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        try {
            validator.validate("Subha", "Double", inventory);
            queue.addRequest(new Reservation("Subha", "Double"));
        } catch (Exception e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        try {
            validator.validate("Vanmathi", "Suite", inventory);
            queue.addRequest(new Reservation("Vanmathi", "Suite"));
        } catch (Exception e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        // Invalid case
        try {
            validator.validate("Test", "single", inventory);
            queue.addRequest(new Reservation("Test", "single"));
        } catch (Exception e) {
            System.out.println("Booking failed: " + e.getMessage());
        }

        System.out.println("\nRoom Allocation Processing\n");

        RoomAllocationService allocator = new RoomAllocationService();
        List<String> confirmedIds = new ArrayList<>();
        BookingHistory history = new BookingHistory();

        while (queue.hasPendingRequests()) {
            Reservation r = queue.getNextRequest();
            String roomId = allocator.allocateRoom(r, inventory);

            if (roomId != null) {
                confirmedIds.add(roomId);
                history.addReservation(r);
                cancellationService.registerBooking(roomId, r.getRoomType());
            }
        }

        // UC10: Cancel first booking
        if (!confirmedIds.isEmpty()) {
            cancellationService.cancelBooking(confirmedIds.get(0), inventory);
        }

        cancellationService.showRollbackHistory();

        System.out.println("\nAdd-On Service Selection\n");

        AddOnServiceManager serviceManager = new AddOnServiceManager();

        if (!confirmedIds.isEmpty()) {
            String reservationId = confirmedIds.get(0);

            serviceManager.addService(reservationId, new AddOnService("Breakfast", 500));
            serviceManager.addService(reservationId, new AddOnService("Spa", 1000));

            double total = serviceManager.calculateTotalServiceCost(reservationId);

            System.out.println("Reservation ID: " + reservationId);
            System.out.println("Total Add-On Cost: " + total);
        }

        new BookingReportService().generateReport(history);
    }
}