import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Locale;

public class MonitorFrame extends JFrame {
    private User user;
    private DefaultTableModel tableModel;
    private JTable table;
    private GraphPanel graphPanel;
    private JTextField tfPulse, tfSystolic, tfDiastolic;
    private JLabel lblPulse, lblSys, lblDia;
    private JButton btnAdd;
    private JMenuItem miKaz, miRus;

    public MonitorFrame(User user) {
        this.user = user;
        setTitle("Zhúrek Monitoringı - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // Til tańdau menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuLang = new JMenu(Lang.get("menu_language"));
        miKaz = new JMenuItem(Lang.get("lang_kazakh"));
        miRus = new JMenuItem(Lang.get("lang_russian"));
        miKaz.addActionListener(e -> switchLanguage("kk"));
        miRus.addActionListener(e -> switchLanguage("ru"));
        menuLang.add(miKaz);
        menuLang.add(miRus);
        menuBar.add(menuLang);
        setJMenuBar(menuBar);

        // Jógargy panel (qoldanushy ólshem engizedi)
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(Color.WHITE);
        lblPulse = new JLabel(Lang.get("label_pulse"));
        tfPulse = new JTextField(5);
        lblSys = new JLabel(Lang.get("label_systolic"));
        tfSystolic = new JTextField(5);
        lblDia = new JLabel(Lang.get("label_diastolic"));
        tfDiastolic = new JTextField(5);
        btnAdd = new JButton(Lang.get("btn_add"));

        inputPanel.add(lblPulse); inputPanel.add(tfPulse);
        inputPanel.add(lblSys);   inputPanel.add(tfSystolic);
        inputPanel.add(lblDia);   inputPanel.add(tfDiastolic);
        inputPanel.add(btnAdd);

        // Keste
        String[] columns = {
                Lang.get("col_time"),
                Lang.get("col_pulse"),
                Lang.get("col_systolic"),
                Lang.get("col_diastolic")
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new ThresholdCellRenderer());
        JScrollPane scrollPane = new JScrollPane(table);

        // Grafik panel
        graphPanel = new GraphPanel(user.getMeasurements());
        graphPanel.setPreferredSize(new Dimension(800, 500));

        // Ornalastyru
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(graphPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addMeasurement());

        setVisible(true);
    }

    private void switchLanguage(String lang) {
        Lang.setLocale(new Locale(lang));
        SwingUtilities.invokeLater(() -> {
            dispose();
            new MonitorFrame(user); // Til ózgergenderi ushın bastan qosady
        });
    }

    private void addMeasurement() {
        try {
            int pulse = Integer.parseInt(tfPulse.getText().trim());
            int sys = Integer.parseInt(tfSystolic.getText().trim());
            int dia = Integer.parseInt(tfDiastolic.getText().trim());

            if (pulse <= 0 || sys <= 0 || dia <= 0 || pulse > 300 || sys > 300 || dia > 300) {
                JOptionPane.showMessageDialog(this, Lang.get("message_fill_fields"));
                return;
            }

            Measurement m = new Measurement(pulse, sys, dia);
            user.addMeasurement(m);
            tableModel.addRow(new Object[]{m.getTimeString(), pulse, sys, dia});
            graphPanel.repaint();
            tfPulse.setText("");
            tfSystolic.setText("");
            tfDiastolic.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, Lang.get("message_fill_fields"));
        }
    }
}
