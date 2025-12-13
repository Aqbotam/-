import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DataManager {

    // users.txt ????? ??? ?????, ????????? ????? ????????
    static {
        File f = new File("users.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ????????? ?????/????? ?????? ?????? ?????? ?????
    public static String checkLogin(String username, String password) {
        try (BufferedReader in = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    if (parts[0].trim().equals(username.trim()) && parts[1].trim().equals(password.trim())) {
                        return parts[2].trim(); // ???: patient ?????? doctor
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ???? ??????????? ??????
    public static void registerUser(String username, String password, String role) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter("users.txt", true))) {
            out.write(username.trim() + "," + password.trim() + "," + role.trim() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ????????? ???-????? ???????
    public static boolean userExists(String username) {
        try (BufferedReader in = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].trim().equals(username.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    // DataManager.java қосыңыз
    public static void saveProfile(String username, String profile) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter("profile_" + username + ".txt", false))) {
            out.write(profile == null ? "" : profile);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ?????? patient ????????? ?????????????? ???
    public static List<String> getAllPatients() {
        List<String> list = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[2].trim().equals("patient")) {
                    list.add(parts[0].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ??????? ??????? ?????? ??????
    public static void saveData(String username, String dataLine) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter("data_" + username + ".txt", true))) {
            out.write(dataLine + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ??????? ??????? ????
    public static void saveComment(String patient, String comment) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter("comments_" + patient + ".txt", true))) {
            out.write(comment + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
