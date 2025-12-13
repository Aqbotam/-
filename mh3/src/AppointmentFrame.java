import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AppointmentFrame extends JFrame {
    private JComboBox<String> dateBox;
    private JComboBox<String> slotBox;
    private JButton bookButton;
    private final String username;

    public AppointmentFrame(String username) {
        this.username = username;

        setTitle("Кездесуге жазылу");
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Күнді таңдаңыз:"));
        dateBox = new JComboBox<>(new String[]{"2025-12-06", "2025-12-07", "2025-12-08"});
        dateBox.addActionListener(e -> updateSlots());
        add(dateBox);

        add(new JLabel("Уақытты таңдаңыз:"));
        slotBox = new JComboBox<>();
        add(slotBox);

        bookButton = new JButton("Жазылу");
        bookButton.addActionListener(e -> handleBooking());
        add(bookButton);

        updateSlots(); // Бастапқы таңдау

        setVisible(true);
    }

    private void updateSlots() {
        String selectedDate = (String) dateBox.getSelectedItem();
        List<String> slots = AppointmentManager.getAvailableSlots(selectedDate);
        slotBox.removeAllItems();
        for (String s : slots) {
            slotBox.addItem(s);
        }
    }

    private void handleBooking() {
        String selectedSlot = (String) slotBox.getSelectedItem();
        if (selectedSlot == null) {
            JOptionPane.showMessageDialog(this, "Бос уақыт жоқ.");
            return;
        }

        boolean success = AppointmentManager.bookAppointment(selectedSlot, username);
        if (success) {
            JOptionPane.showMessageDialog(this, "Жазылу сәтті!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Бұл уақыт бос емес.");
        }
    }
}
