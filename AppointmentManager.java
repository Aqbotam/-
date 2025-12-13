import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppointmentManager {
    private static final String FILE_PATH = "appointments.txt";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static List<String> getAvailableSlots(String date) {
        List<String> slots = new ArrayList<>();

        for (int hour = 8; hour < 13; hour++) {
            slots.add(String.format("%s %02d:00", date, hour));
            slots.add(String.format("%s %02d:30", date, hour));
        }

        for (int hour = 14; hour < 17; hour++) {
            slots.add(String.format("%s %02d:00", date, hour));
            slots.add(String.format("%s %02d:30", date, hour));
        }

        slots.removeAll(getBookedSlots(date));
        return slots;
    }

    public static List<String> getBookedSlots(String date) {
        List<String> booked = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(date)) {
                    booked.add(line.split(",")[0]);
                }
            }
        } catch (IOException e) {
            // файл жоқ болса, booked бос қалуы қалыпты
        }
        return booked;
    }

    public static boolean bookAppointment(String datetime, String patient) {
        if (getBookedSlots(datetime.substring(0, 10)).contains(datetime)) {
            return false;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(datetime + "," + patient + "\n");
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
