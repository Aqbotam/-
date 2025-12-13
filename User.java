import java.util.List;
import java.util.ArrayList;

public abstract class User {
    protected String username;
    protected String password;
    protected List<Measurement> measurements;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.measurements = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public List<Measurement> getMeasurements() { return measurements; }

    public void addMeasurement(Measurement m) {
        measurements.add(m);
    }

    public abstract String getRole();
    public abstract void displayDashboard();
}
