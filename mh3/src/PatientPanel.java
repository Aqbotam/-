import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.awt.event.ActionListener;


public class PatientPanel extends JFrame {
    private final String username;
    private final ResourceBundle bundle;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    // Data fields
    private JTextField pulseField;
    private JTextField sysField;
    private JTextField diaField;
    private JTextArea dataOutputArea;

    // Comments
    private JTextArea commentsArea;
    private JTextField addCommentField;

    // Appointments
    private JTextArea appointmentsArea;
    private JTextField newAppointmentField;

    // slot format shared between read/write
    private static final DateTimeFormatter SLOT_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public PatientPanel(String username) {
        this.username = username;

        Locale currentLocale;
        try {
            currentLocale = AuthFrame.getCurrentLocale();
        } catch (Throwable t) {
            currentLocale = Locale.getDefault();
        }
        ResourceBundle tmp;
        try {
            tmp = ResourceBundle.getBundle("lang", currentLocale);
        } catch (Throwable t) {
            tmp = null;
        }
        this.bundle = tmp;

        setTitle((bundle != null && bundle.containsKey("title_patient_panel") ? bundle.getString("title_patient_panel") + " - " + username : "Patient Panel - " + username));
        setSize(500, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(248, 244, 242));
        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(main);

        cards.setOpaque(false);
        main.add(cards, BorderLayout.CENTER);

        cards.add(createCommentsPanel(), "COMMENTS");
        cards.add(createDataPanel(), "DATA");
        cards.add(createAppointmentsPanel(), "APPOINTMENTS");
        cards.add(createProfileViewPanel(), "PROFILE_VIEW");

        main.add(createBottomNav(), BorderLayout.SOUTH);
        cardLayout.show(cards, "DATA");

        setVisible(true);
    }

    // COMMENTS
    private JPanel createCommentsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JLabel title = new JLabel(bundleHasKey("label_doctor_comments","Комментарии врача"));
        title.setFont(title.getFont().deriveFont(Font.BOLD,18f));
        title.setForeground(new Color(200,20,20));
        p.add(title, BorderLayout.NORTH);

        commentsArea = new JTextArea();
        commentsArea.setEditable(false);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        commentsArea.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        p.add(new JScrollPane(commentsArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(6,6));
        bottom.setOpaque(false);
        addCommentField = new JTextField();
        addCommentField.setBorder(new RoundedBorder(new Color(200,20,20)));
        bottom.add(addCommentField, BorderLayout.CENTER);
        JButton addBtn = new JButton("Добавить (для доктора)");
        addBtn.addActionListener(e -> appendComment());
        bottom.add(addBtn, BorderLayout.EAST);

        p.add(bottom, BorderLayout.SOUTH);
        loadComments();
        return p;
    }

    // DATA
    private JPanel createDataPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        JLabel title = new JLabel("Измерения и график");
        title.setFont(title.getFont().deriveFont(Font.BOLD,18f));
        title.setForeground(new Color(200,20,20));
        p.add(title, BorderLayout.NORTH);

        JPanel input = new JPanel(new GridLayout(4,2,8,8));
        input.setOpaque(false);
        input.setBorder(BorderFactory.createEmptyBorder(8,0,8,0));
        input.add(new JLabel(bundleHasKey("label_pulse","Пульс:")));
        pulseField = new JTextField(); pulseField.setBorder(new RoundedBorder(new Color(200,20,20))); input.add(pulseField);
        input.add(new JLabel(bundleHasKey("label_systolic","Систолическое:")));
        sysField = new JTextField(); sysField.setBorder(new RoundedBorder(new Color(200,20,20))); input.add(sysField);
        input.add(new JLabel(bundleHasKey("label_diastolic","Диастолическое:")));
        diaField = new JTextField(); diaField.setBorder(new RoundedBorder(new Color(200,20,20))); input.add(diaField);

        JButton addBtn = new JButton(bundleHasKey("btn_add","Сохранить"));
        addBtn.addActionListener(this::saveData);
        input.add(addBtn);
        JButton chartBtn = new JButton(bundleHasKey("btn_show_chart","Показать график"));
        chartBtn.addActionListener(e -> showChartWindow());
        input.add(chartBtn);

        p.add(input, BorderLayout.NORTH);

        dataOutputArea = new JTextArea();
        dataOutputArea.setEditable(false);
        dataOutputArea.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        p.add(new JScrollPane(dataOutputArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        JButton viewBtn = new JButton(bundleHasKey("btn_view","Показать данные"));
        viewBtn.addActionListener(e -> loadDataToOutput());
        bottom.add(viewBtn);
        p.add(bottom, BorderLayout.SOUTH);

        loadDataToOutput();
        return p;
    }

    // APPOINTMENTS
    private JPanel createAppointmentsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        JLabel title = new JLabel("Записи к врачу");
        title.setFont(title.getFont().deriveFont(Font.BOLD,18f));
        title.setForeground(new Color(200,20,20));
        p.add(title, BorderLayout.NORTH);

        appointmentsArea = new JTextArea();
        appointmentsArea.setEditable(false);
        appointmentsArea.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        p.add(new JScrollPane(appointmentsArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8,8));
        bottom.setOpaque(false);
        newAppointmentField = new JTextField();
        newAppointmentField.setBorder(new RoundedBorder(new Color(200,20,20)));
        bottom.add(newAppointmentField, BorderLayout.CENTER);

        JButton addAppBtn = new JButton("Записаться");
        // теперь кнопка открывает выбор свободного слота
        addAppBtn.addActionListener(e -> showBookingDialog());
        bottom.add(addAppBtn, BorderLayout.EAST);

        p.add(bottom, BorderLayout.SOUTH);

        loadAppointments();
        return p;
    }

    // PROFILE
    private JPanel createProfileViewPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        JLabel title = new JLabel("Профиль");
        title.setFont(title.getFont().deriveFont(Font.BOLD,18f));
        title.setForeground(new Color(200,20,20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(title);
        p.add(Box.createRigidArea(new Dimension(0,12)));

        JLabel photo = new JLabel("", SwingConstants.CENTER);
        ImageIcon profileIcon = loadIconPreferResource("img.png", 140,110);
        if (profileIcon != null) photo.setIcon(profileIcon); else { photo.setText("фото"); photo.setOpaque(true); photo.setBackground(Color.WHITE); photo.setFont(photo.getFont().deriveFont(Font.BOLD,16f)); }
        photo.setPreferredSize(new Dimension(140,110));
        photo.setMaximumSize(new Dimension(140,110));
        photo.setBorder(new LineBorder(new Color(220,20,20),2,true));
        photo.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(photo);

        p.add(Box.createRigidArea(new Dimension(0,18)));
        p.add(createLabeledFieldForProfile("ТАӘ:", "Омар"));
        p.add(Box.createRigidArea(new Dimension(0,10)));
        p.add(createLabeledFieldForProfile("Туған күні:", "Ақбота"));
        p.add(Box.createRigidArea(new Dimension(0,10)));
        p.add(createLabeledFieldForProfile("Жынысы:", "қыз"));
        p.add(Box.createRigidArea(new Dimension(0,10)));
        p.add(createLabeledFieldForProfile("Телефон номері:", "87056596793"));

        return p;
    }

    private JPanel createLabeledFieldForProfile(String labelText, String initial) {
        JPanel holder = new JPanel();
        holder.setOpaque(false);
        holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD,16f));
        label.setForeground(new Color(200,20,20));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        holder.add(label);

        JTextField field = new JTextField(initial);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));
        field.setBorder(new RoundedBorder(new Color(200,20,20)));
        holder.add(field);
        return holder;
    }

    // bottom nav
    private JPanel createBottomNav() {
        JPanel nav = new JPanel(new GridLayout(1,4,12,12));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(10,6,12,6));

        nav.add(createNavButtonIcon("src/main/resources/images/img_3.png","COMMENTS"));
        nav.add(createNavButtonIcon("src/main/resources/images/img_2.png","DATA"));
        nav.add(createNavButtonIcon("src/main/resources/images/img_1.png","APPOINTMENTS"));
        nav.add(createNavButtonIcon("src/main/resources/images/img.png","PROFILE_VIEW"));

        JPanel framed = new JPanel(new BorderLayout());
        framed.setBorder(new LineBorder(new Color(200,20,20),2,false));
        framed.setOpaque(false);
        framed.add(nav, BorderLayout.CENTER);
        return framed;
    }

    private RoundedIconButton createNavButtonIcon(String filename, String cardName) {
        ImageIcon icon = loadIconPreferResource(filename, 40,40);
        RoundedIconButton btn;
        if (icon != null) btn = new RoundedIconButton(icon); else {
            String fallback = cardName.equals("COMMENTS") ? "💬" : cardName.equals("DATA") ? "📈" : cardName.equals("APPOINTMENTS") ? "📅" : "👤";
            btn = new RoundedIconButton(fallback);
        }
        btn.setBackground(new Color(255,250,248));
        btn.setBorderColor(new Color(200,20,20));
        btn.setPreferredSize(new Dimension(68,68));
        btn.addActionListener(e -> cardLayout.show(cards, cardName));
        return btn;
    }

    // loads icon from resources or src/main/resources
    private ImageIcon loadIconPreferResource(String filename, int w, int h) {
        try {
            java.net.URL url = getClass().getResource("/images/" + new File(filename).getName());
            if (url != null) {
                ImageIcon ic = new ImageIcon(url);
                Image scaled = ic.getImage().getScaledInstance(w,h,Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Throwable ignored) {}
        try {
            File f = new File(filename);
            if (f.exists()) {
                ImageIcon ic = new ImageIcon(f.getAbsolutePath());
                Image scaled = ic.getImage().getScaledInstance(w,h,Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Throwable ignored) {}
        try {
            File f2 = new File("src/main/resources/images/" + new File(filename).getName());
            if (f2.exists()) {
                ImageIcon ic = new ImageIcon(f2.getAbsolutePath());
                Image scaled = ic.getImage().getScaledInstance(w,h,Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Throwable ignored) {}
        return null;
    }

    // ---------- Data save/load ----------
    private void saveData(ActionEvent evt) {
        String pulse = pulseField.getText().trim();
        String sys = sysField.getText().trim();
        String dia = diaField.getText().trim();
        if (pulse.isEmpty() || sys.isEmpty() || dia.isEmpty()) {
            JOptionPane.showMessageDialog(this, bundleHasKey("message_fill_fields","Заполните все поля"));
            return;
        }
        try {
            String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
            String line = timestamp + "," + pulse + "," + sys + "," + dia;
            File f = new File("data_" + username + ".txt");
            try (PrintWriter out = new PrintWriter(new FileWriter(f, true))) { out.println(line); }
            JOptionPane.showMessageDialog(this, bundleHasKey("message_saved","Сохранено"));
            pulseField.setText(""); sysField.setText(""); diaField.setText("");
            loadDataToOutput();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDataToOutput() {
        if (dataOutputArea == null) dataOutputArea = new JTextArea();
        dataOutputArea.setText("");
        String header = bundleHasKey("col_time","time") + ", " + bundleHasKey("col_pulse","pulse") + ", " + bundleHasKey("col_systolic","systolic") + ", " + bundleHasKey("col_diastolic","diastolic") + "\n";
        dataOutputArea.append(header);
        File f = new File("data_" + username + ".txt");
        if (!f.exists()) { dataOutputArea.append(bundleHasKey("message_no_data","Нет данных") + "\n"); return; }
        try (BufferedReader in = new BufferedReader(new FileReader(f))) {
            String line; while ((line = in.readLine()) != null) dataOutputArea.append(line + "\n");
        } catch (IOException e) { dataOutputArea.append("Ошибка чтения данных\n"); }
    }

    // ---------- Comments ----------
    private void loadComments() {
        if (commentsArea == null) commentsArea = new JTextArea();
        commentsArea.setText("");
        File f = new File("comments_" + username + ".txt");
        if (!f.exists()) { commentsArea.append("Нет комментариев.\n"); return; }
        try (BufferedReader in = new BufferedReader(new FileReader(f))) {
            String line; while ((line = in.readLine()) != null) commentsArea.append("- " + line + "\n");
        } catch (IOException e) { commentsArea.append("Ошибка чтения комментариев\n"); }
    }

    private void appendComment() {
        String text = addCommentField.getText().trim();
        if (text.isEmpty()) return;
        File f = new File("comments_" + username + ".txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(f, true))) { out.println(text); }
        catch (IOException e) { JOptionPane.showMessageDialog(this, "Ошибка при добавлении комментария: " + e.getMessage()); return; }
        addCommentField.setText(""); loadComments();
    }

    // ---------- Appointments ----------
    private void loadAppointments() {
        if (appointmentsArea == null) appointmentsArea = new JTextArea();
        appointmentsArea.setText("");
        File f = new File("appointments.txt");
        if (!f.exists()) { appointmentsArea.append("Нет записей.\n"); return; }
        try (BufferedReader in = new BufferedReader(new FileReader(f))) {
            String line; boolean found = false;
            while ((line = in.readLine()) != null) {
                // format: username,dd-MM-yyyy HH:mm[,note]
                String[] parts = line.split(",",3);
                if (parts.length >= 2) {
                    String owner = parts[0].trim();
                    String slot = parts[1].trim();
                    String note = parts.length >=3 ? parts[2].trim() : "";
                    // if this panel is for current user -> show only own, but also show doctor's aggregated?
                    if (owner.equals(username)) {
                        appointmentsArea.append("• " + slot + (note.isEmpty() ? "" : " — " + note) + "\n");
                        found = true;
                    }
                }
            }
            if (!found) appointmentsArea.append("У вас пока нет записей.\n");
        } catch (IOException e) { appointmentsArea.append("Ошибка чтения записей.\n"); }
    }


    private void showBookingDialog() {
        List<String> available = computeAvailableSlots(7, LocalTime.of(9,0), LocalTime.of(17,0),30);
        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет доступных слотов для записи.", "Инфо", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<String> slotBox = new JComboBox<>(available.toArray(new String[0]));
        JTextField noteField = new JTextField();

        JPanel panel = new JPanel(new BorderLayout(6,6));
        panel.add(new JLabel("Выберите время:"), BorderLayout.NORTH);
        panel.add(slotBox, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(6,6));
        south.add(new JLabel("Комментарий (необязательно):"), BorderLayout.NORTH);
        south.add(noteField, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);

        int res = JOptionPane.showConfirmDialog(this, panel, "Запись к врачу", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String chosen = (String) slotBox.getSelectedItem();
        if (chosen == null) return;
        String note = noteField.getText().trim();

        // final check & write (synchronized on file to reduce race)
        File f = new File("appointments.txt");
        synchronized (PatientPanel.class) {
            // read current taken
            Set<String> taken = new HashSet<>();
            if (f.exists()) {
                try (BufferedReader in = new BufferedReader(new FileReader(f))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        String[] parts = line.split(",",3);
                        if (parts.length >= 2) taken.add(parts[1].trim());
                    }
                } catch (IOException ignored) {}
            }
            if (taken.contains(chosen)) {
                JOptionPane.showMessageDialog(this, "Извините, выбранный слот уже был занят. Выберите другой.", "Занято", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(f, true))) {
                out.println(username + "," + chosen + (note.isEmpty() ? "" : ("," + note)));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // refresh
        loadAppointments();
        JOptionPane.showMessageDialog(this, "Вы успешно записаны на " + chosen);
    }

    private List<String> computeAvailableSlots(int daysAhead, LocalTime start, LocalTime end, int stepMinutes) {
        Set<String> taken = new HashSet<>();
        File f = new File("appointments.txt");
        if (f.exists()) {
            try (BufferedReader in = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = in.readLine()) != null) {
                    String[] parts = line.split(",",3);
                    if (parts.length >= 2) taken.add(parts[1].trim());
                }
            } catch (IOException ignored) {}
        }

        List<String> available = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int d = 0; d < daysAhead; d++) {
            LocalDate date = today.plusDays(d);
            LocalDateTime t = LocalDateTime.of(date, start);
            LocalDateTime endDt = LocalDateTime.of(date, end);
            while (!t.isAfter(endDt.minusMinutes(0))) {
                String s = t.truncatedTo(ChronoUnit.MINUTES).format(SLOT_FORMAT);
                if (!taken.contains(s)) available.add(s);
                t = t.plusMinutes(stepMinutes);
                if (t.isAfter(date.atTime(23,59))) break; // safety
            }
        }
        return available;
    }

    // ---------- Chart ----------
    private void showChartWindow() {
        try {
            JFrame chartFrame = new JFrame(bundleHasKey("chart_title_pulse_time","Pulse over Time"));
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.setSize(800,500);
            chartFrame.setLocationRelativeTo(this);
            chartFrame.add(ChartUtils.createPulseChart(username));
            chartFrame.setVisible(true);
        } catch (Throwable ex) {
            JOptionPane.showMessageDialog(this, "Не удалось показать график: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Utilities ----------
    private String bundleHasKey(String key, String fallback) {
        if (bundle != null && bundle.containsKey(key)) return bundle.getString(key);
        return fallback;
    }

    // RoundedBorder (kept simple)
    static class RoundedBorder implements javax.swing.border.Border {
        private final Color color;
        private final int radius;
        public RoundedBorder(Color color) { this.color = color; this.radius = 12; }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
        @Override public boolean isBorderOpaque() { return false; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(2)); g2.drawRoundRect(x+1,y+1,width-3,height-3,radius,radius); g2.dispose();
        }
    }

    // RoundedIconButton used for bottom nav (simple)
    static class RoundedIconButton extends JComponent {
        private final ImageIcon icon;
        private final String textFallback;
        private Color borderColor = new Color(200,20,20);
        private Color background = new Color(255,250,248);
        private ActionListener action;
        public RoundedIconButton(ImageIcon icon) { this.icon = icon; this.textFallback = null; init(); }
        public RoundedIconButton(String textFallback) { this.icon = null; this.textFallback = textFallback; init(); }
        private void init() {
            setPreferredSize(new Dimension(68,68)); setOpaque(false);
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mousePressed(java.awt.event.MouseEvent e) { repaint(); }
                @Override public void mouseReleased(java.awt.event.MouseEvent e) { repaint(); if (action != null && contains(e.getPoint())) action.actionPerformed(new ActionEvent(RoundedIconButton.this, ActionEvent.ACTION_PERFORMED,"click")); }
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { setCursor(Cursor.getDefaultCursor()); }
            });
        }
        public void setBorderColor(Color c) { this.borderColor = c; repaint(); }
        @Override public void setBackground(Color bg) { this.background = bg; repaint(); }
        public void addActionListener(ActionListener l) { this.action = l; }
        @Override protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight(); int diameter = Math.min(w,h)-4; int x=(w-diameter)/2, y=(h-diameter)/2;
            Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(background); g2.fillOval(x,y,diameter,diameter);
            g2.setColor(borderColor); g2.setStroke(new BasicStroke(2)); g2.drawOval(x,y,diameter,diameter);
            if (icon != null) { int maxIcon = (int)(diameter*0.56); Image img = icon.getImage().getScaledInstance(maxIcon,maxIcon,Image.SCALE_SMOOTH); int ix=x+(diameter-maxIcon)/2; int iy=y+(diameter-maxIcon)/2; g2.drawImage(img, ix, iy, this); }
            else if (textFallback != null) { Font f = getFont().deriveFont(Font.PLAIN, diameter*0.45f); g2.setFont(f); FontMetrics fm=g2.getFontMetrics(); int tw=fm.stringWidth(textFallback); int th=fm.getAscent(); int tx=x+(diameter-tw)/2; int ty=y+(diameter+th)/2-4; g2.drawString(textFallback,tx,ty); }
            g2.dispose();
        }
        @Override public Dimension getPreferredSize() { return new Dimension(68,68); }
    }

    // main for quick testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientPanel("ivanov"));
    }
}
