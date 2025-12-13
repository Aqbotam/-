public class Patient extends User {

    public Patient(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "patient";
    }

    @Override
    public void displayDashboard() {
        new PatientPanel(username);
    }
}
