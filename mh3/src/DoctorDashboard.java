import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class DoctorDashboard extends JFrame {
    private final ResourceBundle bundle;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // Appointments card
    private JTextArea appointmentsArea;
    private JTextField newAppointmentField;

    // Patients/Data card
    private JComboBox<String> patientList;
    private JTextArea patientDataArea;
    private JTextArea commentWriteArea; // комментарий жазу жері (2 кнопка)

    // Profile card
    private JTextField doctorNameField;
    private JTextField doctorPhoneField;

    // bottom nav
    private RoundedIconButton navBtn1, navBtn2, navBtn3;

    public DoctorDashboard(String doctorName) {
        // load resource bundle if available
        ResourceBundle tmp;
        try {
            tmp = ResourceBundle.getBundle("lang", Locale.getDefault());
        } catch (Throwable t) {
            tmp = null;
        }
        this.bundle = tmp;

        setTitle(getTextKey("doctor_panel", "Doctor Panel") + (doctorName != null && !doctorName.isEmpty() ? " - " + doctorName : ""));
        setSize(520, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(248, 244, 242)); // cream
        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(main);

        // HEADER
        JLabel title = new JLabel(getTextKey("doctor_panel", "Doctor Dashboard") + (doctorName != null && !doctorName.isEmpty() ? " — " + doctorName : ""));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(new Color(200, 20, 20));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(title, BorderLayout.WEST);

        JButton logout = new JButton(getTextKey("btn_logout", "Logout"));
        logout.addActionListener(e -> {
            dispose();
            // Если хотите открывать AuthFrame при логауте — раскомментируйте следующую строку:
            // new AuthFrame();
        });
        JPanel hdrActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        hdrActions.setOpaque(false);
        hdrActions.add(logout);
        header.add(hdrActions, BorderLayout.EAST);

        main.add(header, BorderLayout.NORTH);

        // CENTER: cards area
        cards.setOpaque(false);
        cards.add(createAppointmentsCard(), "APPOINTMENTS");
        cards.add(createPatientsCard(), "PATIENTS");
        cards.add(createProfileCard(doctorName), "PROFILE");
        main.add(cards, BorderLayout.CENTER);

        // BOTTOM nav (3 round buttons) with labels under icons
        JPanel bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.setOpaque(false);
        bottomWrap.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));
        JPanel nav = new JPanel(new GridLayout(1, 3, 12, 12));
        nav.setOpaque(false);

        // Use exact image paths you provided (falls back to text)
        navBtn1 = createNavButtonIcon("src/src/main/resources/images/img_3.png", "Записи");
        navBtn2 = createNavButtonIcon("src/src/main/resources/images/img_2.png", "Пациенты");
        navBtn3 = createNavButtonIcon("src/src/main/resources/images/img.png", "Профиль");

        navBtn1.addActionListener(e -> cardLayout.show(cards, "APPOINTMENTS"));
        navBtn2.addActionListener(e -> cardLayout.show(cards, "PATIENTS"));
        navBtn3.addActionListener(e -> cardLayout.show(cards, "PROFILE"));

        // wrap with labels
        nav.add(wrapNavButton(navBtn1, ""));
        nav.add(wrapNavButton(navBtn2, ""));
        nav.add(wrapNavButton(navBtn3, ""));

        JPanel framed = new JPanel(new BorderLayout());
        framed.setOpaque(false);
        framed.setBorder(new LineBorder(new Color(200, 20, 20), 2, false));
        framed.add(nav, BorderLayout.CENTER);

        bottomWrap.add(framed, BorderLayout.CENTER);
        main.add(bottomWrap, BorderLayout.SOUTH);

        // default view: PATIENTS
        cardLayout.show(cards, "PATIENTS");

        setVisible(true);
    }

    // ---------- Cards ----------
    private JPanel createAppointmentsCard() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel(getTextKey("label_appointments", "Записи пациентов"));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(new Color(200, 20, 20));
        p.add(title, BorderLayout.NORTH);

        appointmentsArea = new JTextArea();
        appointmentsArea.setEditable(false);
        p.add(new JScrollPane(appointmentsArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        newAppointmentField = new JTextField();
        newAppointmentField.setBorder(new RoundedBorder(new Color(200, 20, 20)));
        bottom.add(newAppointmentField, BorderLayout.CENTER);

        JButton addBtn = new JButton(getTextKey("btn_add", "Добавить запись"));
        addBtn.addActionListener(this::addAppointment);
        bottom.add(addBtn, BorderLayout.EAST);

        p.add(bottom, BorderLayout.SOUTH);

        loadAppointments();

        return p;
    }

    private JPanel createPatientsCard() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel(getTextKey("label_patients", "Пациенты и данные"));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(new Color(200, 20, 20));
        p.add(title, BorderLayout.NORTH);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(220, 0));

        List<String> patients = DataManager.getAllPatients();
        patientList = new JComboBox<>(patients.toArray(new String[0]));
        patientList.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        patientList.setBorder(new RoundedBorder(new Color(200, 20, 20)));
        left.add(patientList);
        left.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton viewBtn = new JButton(getTextKey("btn_view", "Показать данные"));
        viewBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        viewBtn.addActionListener(this::loadPatientData);
        left.add(viewBtn);
        left.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton chartBtn = new JButton(getTextKey("btn_show_chart", "Показать график"));
        chartBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        chartBtn.addActionListener(this::showChart);
        left.add(chartBtn);

        p.add(left, BorderLayout.WEST);

        // Center area shows data and comments list
        JPanel center = new JPanel(new BorderLayout(6,6));
        center.setOpaque(false);
        patientDataArea = new JTextArea();
        patientDataArea.setEditable(false);
        center.add(new JScrollPane(patientDataArea), BorderLayout.CENTER);

        // Comment input area (now ON the 2nd button page)
        JPanel commentPanel = new JPanel(new BorderLayout(4,3));
        commentPanel.setOpaque(false);
        commentPanel.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));
        JLabel cLabel = new JLabel("Комментарий врача:");
        cLabel.setForeground(new Color(200,20,20));
        cLabel.setFont(cLabel.getFont().deriveFont(Font.BOLD, 14f));
        commentPanel.add(cLabel, BorderLayout.NORTH);

        commentWriteArea = new JTextArea(3, 20);
        commentWriteArea.setBorder(new RoundedBorder(new Color(200,20,20)));
        commentPanel.add(new JScrollPane(commentWriteArea), BorderLayout.CENTER);

        JButton sendCommentBtn = new JButton("Отправить");
        sendCommentBtn.addActionListener(ev -> saveCommentForPatient());
        commentPanel.add(sendCommentBtn, BorderLayout.EAST);

        center.add(commentPanel, BorderLayout.SOUTH);

        p.add(center, BorderLayout.CENTER);

        return p;
    }

    private JPanel createProfileCard(String doctorName) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel(getTextKey("label_profile", "Профиль"));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(new Color(200, 20, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(title);

        p.add(Box.createRigidArea(new Dimension(0, 12)));

        p.add(createLabeledFieldForProfile("Имя:", doctorName));
        p.add(Box.createRigidArea(new Dimension(0, 10)));
        doctorPhoneField = new JTextField("");
        p.add(createLabeledFieldComponent("Телефон:", doctorPhoneField));

        return p;
    }

    private JPanel createLabeledFieldForProfile(String labelText, String initial) {
        JTextField field = new JTextField(initial);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setBorder(new RoundedBorder(new Color(200, 20, 20)));
        return createLabeledFieldComponent(labelText, field);
    }

    private JPanel createLabeledFieldComponent(String labelText, JComponent comp) {
        JPanel holder = new JPanel();
        holder.setOpaque(false);
        holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        label.setForeground(new Color(200, 20, 20));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        holder.add(label);
        holder.add(Box.createRigidArea(new Dimension(0, 6)));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        holder.add(comp);
        return holder;
    }

    // ---------- Appointments ----------
    private void loadAppointments() {
        appointmentsArea.setText("");
        File f = new File("appointments.txt");
        if (!f.exists()) {
            appointmentsArea.append("Нет записей.\n");
            return;
        }
        try (BufferedReader in = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = in.readLine()) != null) {
                appointmentsArea.append(line + "\n");
            }
        } catch (IOException e) {
            appointmentsArea.append("Ошибка чтения записей\n");
        }
    }

    private void addAppointment(ActionEvent e) {
        String text = newAppointmentField.getText().trim();
        if (text.isEmpty()) return;
        File f = new File("appointments.txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(f, true))) {
            out.println(text);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        newAppointmentField.setText("");
        loadAppointments();
    }

    // ---------- Patients / Data ----------
    private void loadPatientData(ActionEvent e) {
        String selected = (String) patientList.getSelectedItem();
        if (selected == null) return;
        patientDataArea.setText(getTextKey("label_measurements", "Measurements") + ":\n");
        File f = new File("data_" + selected + ".txt");
        if (!f.exists()) {
            patientDataArea.append(getTextKey("message_no_data", "No data") + "\n");
            return;
        }
        try (BufferedReader in = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = in.readLine()) != null) {
                patientDataArea.append(line + "\n");
            }
        } catch (IOException ex) {
            patientDataArea.append("Ошибка чтения данных\n");
        }

        // doctor comments (list)
        patientDataArea.append("\n" + getTextKey("label_doctor_comments", "Doctor comments") + ":\n");
        File cf = new File("comments_" + selected + ".txt");
        boolean has = false;
        if (cf.exists()) {
            try (BufferedReader in = new BufferedReader(new FileReader(cf))) {
                String line;
                while ((line = in.readLine()) != null) {
                    patientDataArea.append("- " + line + "\n");
                    has = true;
                }
            } catch (IOException ex) {
                patientDataArea.append(getTextKey("message_no_comments", "No comments") + "\n");
            }
        }
        if (!has) patientDataArea.append(getTextKey("message_no_comments", "No comments") + "\n");
    }

    private void saveCommentForPatient() {
        String selected = (String) patientList.getSelectedItem();
        if (selected == null) return;
        String text = commentWriteArea.getText().trim();
        if (text.isEmpty()) return;
        File f = new File("comments_" + selected + ".txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(f, true))) {
            out.println(text);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении комментария: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        commentWriteArea.setText("");
        loadPatientData(null); // refresh display
        JOptionPane.showMessageDialog(this, "Комментарий сохранён");
    }

    private void showChart(ActionEvent e) {
        String selected = (String) patientList.getSelectedItem();
        if (selected == null) return;

        TimeSeries series = new TimeSeries("Pulse");
        File f = new File("data_" + selected + ".txt");
        if (!f.exists()) {
            JOptionPane.showMessageDialog(this, getTextKey("message_no_data", "No data"), "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try (BufferedReader in = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String timestamp = parts[0].trim();
                    int pulse = Integer.parseInt(parts[1].trim());
                    String[] dt = timestamp.split(" ");
                    String[] dmy = dt[0].split("-");
                    String[] hms = dt[1].split(":");
                    int year = Integer.parseInt(dmy[0]);
                    int month = Integer.parseInt(dmy[1]);
                    int day = Integer.parseInt(dmy[2]);
                    int hour = Integer.parseInt(hms[0]);
                    int minute = Integer.parseInt(hms[1]);
                    series.addOrUpdate(new Minute(minute, hour, day, month, year), pulse);
                }
            }
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            dataset.addSeries(series);
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    getTextKey("chart_title_pulse_time", "Pulse over Time"),
                    "Time", "Pulse", dataset
            );
            ChartPanel chartPanel = new ChartPanel(chart);
            JDialog dialog = new JDialog(this, getTextKey("chart_title_pulse_time", "Pulse over Time"), true);
            dialog.setContentPane(chartPanel);
            dialog.setSize(700, 450);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при создании графика:\n" + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Utilities ----------
    private String getTextKey(String key, String fallback) {
        if (bundle != null && bundle.containsKey(key)) return bundle.getString(key);
        return fallback;
    }

    // Navigation button creation using images (tries classpath / exact path / src/main/resources)
    private RoundedIconButton createNavButtonIcon(String path, String fallbackText) {
        ImageIcon icon = loadIconFromPath(path, 40, 40);
        RoundedIconButton btn;
        if (icon != null) btn = new RoundedIconButton(icon);
        else btn = new RoundedIconButton(fallbackText.substring(0, Math.min(2, fallbackText.length())));

        btn.setBackground(new Color(255, 250, 248));
        btn.setBorderColor(new Color(200, 20, 20));
        btn.setPreferredSize(new Dimension(72, 72));
        return btn;
    }

    private ImageIcon loadIconFromPath(String path, int w, int h) {
        try {
            String name = new File(path).getName();
            java.net.URL url = getClass().getResource("/images/" + name);
            if (url != null) {
                ImageIcon ic = new ImageIcon(url);
                Image scaled = ic.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Throwable ignored) {}
        try {
            File f = new File(path);
            if (f.exists()) {
                ImageIcon ic = new ImageIcon(f.getAbsolutePath());
                Image scaled = ic.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Throwable ignored) {}
        try {
            String name = new File(path).getName();
            File f2 = new File("src/main/resources/images/" + name);
            if (f2.exists()) {
                ImageIcon ic = new ImageIcon(f2.getAbsolutePath());
                Image scaled = ic.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Throwable ignored) {}
        return null;
    }

    // Wrap nav button with a label under it
    private JPanel wrapNavButton(RoundedIconButton btn, String label) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btn);

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 13f));
        lbl.setForeground(new Color(200, 20, 20));
        panel.add(Box.createRigidArea(new Dimension(0,6)));
        panel.add(lbl);

        return panel;
    }

    // Rounded icon button (same concept as PatientPanel)
    static class RoundedIconButton extends JComponent {
        private final ImageIcon icon;
        private final String textFallback;
        private Color borderColor = new Color(200, 20, 20);
        private Color background = new Color(255, 250, 248);
        private ActionListener action;

        public RoundedIconButton(ImageIcon icon) {
            this.icon = icon;
            this.textFallback = null;
            init();
        }

        public RoundedIconButton(String textFallback) {
            this.icon = null;
            this.textFallback = textFallback;
            init();
        }

        private void init() {
            setPreferredSize(new Dimension(72, 72));
            setOpaque(false);
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mousePressed(java.awt.event.MouseEvent e) { repaint(); }
                @Override public void mouseReleased(java.awt.event.MouseEvent e) {
                    repaint();
                    if (action != null && contains(e.getPoint())) {
                        action.actionPerformed(new ActionEvent(RoundedIconButton.this, ActionEvent.ACTION_PERFORMED, "click"));
                    }
                }
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { setCursor(Cursor.getDefaultCursor()); }
            });
        }

        public void setBorderColor(Color c) { this.borderColor = c; repaint(); }
        @Override public void setBackground(Color bg) { this.background = bg; repaint(); }
        public void addActionListener(ActionListener l) { this.action = l; }

        @Override protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            int d = Math.min(w, h) - 6;
            int x = (w - d) / 2, y = (h - d) / 2;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background);
            g2.fillOval(x, y, d, d);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x, y, d, d);

            if (icon != null) {
                int maxIcon = (int) (d * 0.56);
                Image img = icon.getImage().getScaledInstance(maxIcon, maxIcon, Image.SCALE_SMOOTH);
                int ix = x + (d - maxIcon) / 2;
                int iy = y + (d - maxIcon) / 2;
                g2.drawImage(img, ix, iy, this);
            } else if (textFallback != null) {
                Font f = getFont().deriveFont(Font.PLAIN, d * 0.38f);
                g2.setFont(f);
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(textFallback);
                int th = fm.getAscent();
                int tx = x + (d - tw) / 2;
                int ty = y + (d + th) / 2 - 4;
                g2.drawString(textFallback, tx, ty);
            }
            g2.dispose();
        }
    }

    // RoundedBorder for text fields
    static class RoundedBorder implements Border {
        private final Color color;
        private final int radius;
        public RoundedBorder(Color color) { this.color = color; this.radius = 12; }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
        @Override public boolean isBorderOpaque() { return false; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x+1, y+1, width-3, height-3, radius, radius);
            g2.dispose();
        }
    }

    // quick main to test UI — entry point for launching
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorDashboard(""));
    }
}
