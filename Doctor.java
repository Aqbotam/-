public class Doctor extends User {
    public Doctor(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "doctor";
    }

    @Override
    public void displayDashboard() {
        new DoctorDashboard(username);  // Мұнда нақты GUI терезе ашылады
    }
}
