import java.util.*;
import java.io.*;

/* =========================
   INVENTORY (REUSED)
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
   UC12: FILE PERSISTENCE SERVICE
   ========================= */
class FilePersistenceService {

    // SAVE inventory to file
    public void saveInventory(RoomInventory inventory, String filePath) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (Map.Entry<String, Integer> entry :
                    inventory.getRoomAvailability().entrySet()) {

                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }

            System.out.println("Inventory saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving inventory.");
        }
    }

    // LOAD inventory from file
    public void loadInventory(RoomInventory inventory, String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("No valid inventory data found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("=");

                if (parts.length == 2) {
                    String roomType = parts[0];
                    int count = Integer.parseInt(parts[1]);

                    inventory.updateAvailability(roomType, count);
                }
            }

            System.out.println("Inventory restored successfully.");

        } catch (Exception e) {
            System.out.println("Error loading inventory. Starting with default values.");
        }
    }
}

/* =========================
   MAIN CLASS (UC12)
   ========================= */
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        System.out.println("System Recovery\n");

        RoomInventory inventory = new RoomInventory();
        FilePersistenceService persistenceService = new FilePersistenceService();

        String filePath = "inventory.txt";

        // LOAD previous state
        persistenceService.loadInventory(inventory, filePath);

        // DISPLAY current inventory
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry :
                inventory.getRoomAvailability().entrySet()) {

            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // SAVE state before exit
        persistenceService.saveInventory(inventory, filePath);
    }
}