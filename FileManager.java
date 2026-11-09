import java.io.*;

/** Handles persistent storage for the dorm records. */
public class FileManager {
    private static final String DATA_FILE = "dorm_records.dat";

    public static DormData load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return new DormData();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object value = in.readObject();
            if (value instanceof DormData) {
                System.out.println("Saved records loaded.");
                return (DormData) value;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load records: " + e.getMessage());
        }
        return new DormData();
    }

    public static void save(DormData data) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(data);
            System.out.println("Records saved to " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }
}
