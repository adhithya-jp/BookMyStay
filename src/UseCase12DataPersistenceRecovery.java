import java.util.*;
import java.io.*;

/* =========================
   UC12: FILE PERSISTENCE SERVICE
   ========================= */
class FilePersistenceService {

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
   MAIN CLASS
   ========================= */
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        System.out.println("System Recovery\n");

        // ✅ REUSE existing class from BookMyStay
        RoomInventory inventory = new RoomInventory();

        FilePersistenceService persistenceService = new FilePersistenceService();
        String filePath = "inventory.txt";

        persistenceService.loadInventory(inventory, filePath);

        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry :
                inventory.getRoomAvailability().entrySet()) {

            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        persistenceService.saveInventory(inventory, filePath);
    }
}