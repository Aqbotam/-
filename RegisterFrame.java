import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class RegisterFrame extends JFrame {
    // Credentials
    private final JTextField tfUsername = new JTextField();
    private final JPasswordField pfPassword = new JPasswordField();
    private final JPasswordField pfConfirm = new JPasswordField();

    // Role selection
    private final JRadioButton rbPatient = new JRadioButton();
    private final JRadioButton rbDoctor = new JRadioButton();

    // Doctor fields
    private final JTextField tfDoctorFullname = new JTextField();
    private final JComboBox<String> cbSpecialty = new JComboBox<>(
            new String[]{
                    "Кардиолог",
                    "Кардиохирург",
                    "Эндокринолог",
                    "Реабилитолог",
                    "Интервенционный кардиолог"}
    );
    private final JTextField tfDoctorPhone = new JTextField();

    // Patient fields
    private final JTextField tfPatientFullname = new JTextField();
    private final JSpinner spinnerDob;
    private final JComboBox<String> cbGender;
    private final JTextField tfPatientPhone = new JTextField();
    private final JTextField tfAddress = new JTextField();

    private final JPanel cardPanel; // card layout panel

    // Стильовые константы
    private static final Color ACCENT_RED = new Color(190, 18, 28);
    private static final Color FIELD_BG = new Color(250, 248, 246);
    private static final Color OUTER_BG = new Color(246, 243, 241);
    private static final Font LABEL_FONT = new Font("Dialog", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("Dialog", Font.PLAIN, 14);

    public RegisterFrame(JFrame parent) {
        setTitle("Регистрация");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 800);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(OUTER_BG);

        // Date spinner initialization (default = today)
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        SpinnerDateModel dateModel = new SpinnerDateModel(today, null, null, Calendar.DAY_OF_MONTH);
        spinnerDob = new JSpinner(dateModel);
        spinnerDob.setEditor(new JSpinner.DateEditor(spinnerDob, "dd-MM-yyyy"));

        // Пол: Мужской / Женский
        cbGender = new JComboBox<>(new String[]{"Мужской", "Женский"});

        // --- Верх: выбор роли ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        top.setBackground(OUTER_BG);
        top.setBorder(BorderFactory.createTitledBorder("Роль"));
        ButtonGroup bg = new ButtonGroup();
        rbPatient.setText("Пациент");
        rbDoctor.setText("Врач");
        rbPatient.setSelected(true);
        styleRadio(rbPatient);
        styleRadio(rbDoctor);
        bg.add(rbPatient);
        bg.add(rbDoctor);

        top.add(new JLabel("Роль:"));
        top.add(rbPatient);
        top.add(rbDoctor);

        // --- Поля учётных данных ---
        JPanel creds = new JPanel();
        creds.setLayout(new BoxLayout(creds, BoxLayout.Y_AXIS));
        creds.setBackground(OUTER_BG);
        creds.setBorder(BorderFactory.createTitledBorder("Данные"));

        JLabel lUser = new JLabel("Имя пользователя");
        lUser.setFont(LABEL_FONT);
        lUser.setForeground(ACCENT_RED);
        creds.add(lUser);
        styleField(tfUsername);
        tfUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        creds.add(tfUsername);
        creds.add(Box.createRigidArea(new Dimension(0,8)));

        JLabel lPass = new JLabel("Пароль");
        lPass.setFont(LABEL_FONT);
        lPass.setForeground(ACCENT_RED);
        creds.add(lPass);
        styleField(pfPassword);
        pfPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        creds.add(pfPassword);
        creds.add(Box.createRigidArea(new Dimension(0,8)));

        JLabel lConfirm = new JLabel("Подтвердите пароль");
        lConfirm.setFont(LABEL_FONT);
        lConfirm.setForeground(ACCENT_RED);
        creds.add(lConfirm);
        styleField(pfConfirm);
        pfConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        creds.add(pfConfirm);

        // --- Карточки: Врач / Пациент ---
        cardPanel = new JPanel(new CardLayout());
        cardPanel.setBackground(OUTER_BG);
        cardPanel.setBorder(BorderFactory.createTitledBorder("Данные профиля"));

        // Форма врача
        JPanel pDoctor = new JPanel();
        pDoctor.setLayout(new BoxLayout(pDoctor, BoxLayout.Y_AXIS));
        pDoctor.setBackground(OUTER_BG);
        addLabeledField(pDoctor, "ФИО (врач)", tfDoctorFullname);

        // Специальность — editable JComboBox: пользователь может выбрать или вписать
        cbSpecialty.setEditable(true);
        styleField(cbSpecialty);
        addLabeledField(pDoctor, "Специальность", cbSpecialty);

        addLabeledField(pDoctor, "Телефон", tfDoctorPhone);
        pDoctor.add(Box.createVerticalGlue());

        // Форма пациента
        JPanel pPatient = new JPanel();
        pPatient.setLayout(new BoxLayout(pPatient, BoxLayout.Y_AXIS));
        pPatient.setBackground(OUTER_BG);
        addLabeledField(pPatient, "ФИО (пациент)", tfPatientFullname);

        JLabel lDob = new JLabel("Дата рождения");
        lDob.setFont(LABEL_FONT);
        lDob.setForeground(ACCENT_RED);
        pPatient.add(lDob);
        styleField(spinnerDob);
        spinnerDob.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        pPatient.add(spinnerDob);
        pPatient.add(Box.createRigidArea(new Dimension(0,8)));

        JLabel lGender = new JLabel("Пол");
        lGender.setFont(LABEL_FONT);
        lGender.setForeground(ACCENT_RED);
        pPatient.add(lGender);
        styleField(cbGender);
        cbGender.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        pPatient.add(cbGender);
        pPatient.add(Box.createRigidArea(new Dimension(0,8)));

        addLabeledField(pPatient, "Телефон", tfPatientPhone);
        addLabeledField(pPatient, "Адрес (необязательно)", tfAddress);
        pPatient.add(Box.createVerticalGlue());

        cardPanel.add(pPatient, "PATIENT");
        cardPanel.add(pDoctor, "DOCTOR");

        // --- Кнопки ---
        JButton btnCancel = new RoundedButton("Отмена");
        JButton btnSubmit = new RoundedButton("Зарегистрироваться");

        stylePrimary(btnSubmit);
        styleSecondary(btnCancel);
        btnSubmit.setPreferredSize(new Dimension(160, 44));
        btnCancel.setPreferredSize(new Dimension(100, 44));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        btnRow.setBackground(OUTER_BG);
        btnRow.add(btnCancel);
        btnRow.add(btnSubmit);

        // --- Сборка лейаута ---
        setLayout(new BorderLayout());
        JPanel northWrap = new JPanel();
        northWrap.setLayout(new BoxLayout(northWrap, BoxLayout.Y_AXIS));
        northWrap.setBackground(OUTER_BG);
        northWrap.add(top);
        northWrap.add(Box.createRigidArea(new Dimension(0,6)));
        northWrap.add(creds);

        add(northWrap, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        add(btnRow, BorderLayout.SOUTH);

        // --- Слушатели ---
        rbPatient.addActionListener(e -> showCard("PATIENT"));
        rbDoctor.addActionListener(e -> showCard("DOCTOR"));

        // При нажатии Отмена — закрыть RegisterFrame и показать AuthFrame
        btnCancel.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(AuthFrame::new);
        });

        btnSubmit.addActionListener(this::onSubmit);

        // Показать начальную карточку
        showCard("PATIENT");
        setVisible(true);
    }

    // Показ карточки
    private void showCard(String name) {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, name);
    }

    // Добавляет метку + поле в панель и применяет стиль
    private void addLabeledField(JPanel parent, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(ACCENT_RED);
        parent.add(lbl);
        styleField(field);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        parent.add(field);
        parent.add(Box.createRigidArea(new Dimension(0,8)));
    }

    // Стилизация полей
    private void styleField(JComponent field) {
        field.setFont(FIELD_FONT);
        if (field instanceof JTextComponent) {
            field.setBackground(FIELD_BG);
            field.setForeground(Color.DARK_GRAY);
        } else {
            field.setBackground(Color.WHITE);
            field.setForeground(Color.DARK_GRAY);
        }
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, ACCENT_RED),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    // Стилизация радиокнопок
    private void styleRadio(JRadioButton rb) {
        rb.setBackground(OUTER_BG);
        rb.setForeground(Color.DARK_GRAY);
        rb.setFont(FIELD_FONT);
    }

    // Главная (красная) кнопка
    private void stylePrimary(JButton btn) {
        btn.setBackground(ACCENT_RED);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Dialog", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setOpaque(false);
    }

    // Вторичная (белая) кнопка
    private void styleSecondary(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(ACCENT_RED);
        btn.setFont(new Font("Dialog", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createLineBorder(ACCENT_RED, 2));
        btn.setOpaque(false);
    }

    // --- Логика регистрации (с final локальными копиями для SwingWorker) ---
    private void onSubmit(ActionEvent e) {
        final String username = tfUsername.getText().trim();
        final char[] pass = pfPassword.getPassword();
        final char[] confirm = pfConfirm.getPassword();

        final boolean isDoctor = rbDoctor.isSelected();

        // Базовая валидация
        if (username.isEmpty() || pass.length == 0 || confirm.length == 0) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, заполните все поля");
            erase(pass); erase(confirm);
            return;
        }
        if (!Arrays.equals(pass, confirm)) {
            JOptionPane.showMessageDialog(this, "Пароли не совпадают");
            erase(pass); erase(confirm);
            return;
        }
        if (username.contains(",") || username.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Имя пользователя не должно содержать пробелы или запятые");
            erase(pass); erase(confirm);
            return;
        }
        if (pass.length < 6) {
            JOptionPane.showMessageDialog(this, "Пароль должен содержать не менее 6 символов");
            erase(pass); erase(confirm);
            return;
        }

        // Сбор данных профиля
        final String role = isDoctor ? "doctor" : "patient";

        final String docFull = tfDoctorFullname.getText().trim();
        final String specialty = (cbSpecialty.getSelectedItem() != null) ? cbSpecialty.getSelectedItem().toString().trim() : "";
        final String docPhone = tfDoctorPhone.getText().trim();

        final String patFull = tfPatientFullname.getText().trim();
        final Date dob = (Date) spinnerDob.getValue();
        final String gender = (String) cbGender.getSelectedItem();
        final String patPhone = tfPatientPhone.getText().trim();
        final String address = tfAddress.getText().trim();

        if (isDoctor) {
            if (docFull.isEmpty() || specialty.isEmpty() || docPhone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, заполните обязательные поля врача");
                erase(pass); erase(confirm);
                return;
            }
            if (!isPhoneValid(docPhone)) {
                JOptionPane.showMessageDialog(this, "Неверный формат телефона");
                erase(pass); erase(confirm);
                return;
            }
        } else {
            if (patFull.isEmpty() || patPhone.isEmpty() || dob == null) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, заполните обязательные поля пациента");
                erase(pass); erase(confirm);
                return;
            }
            if (!isPhoneValid(patPhone)) {
                JOptionPane.showMessageDialog(this, "Неверный формат телефона");
                erase(pass); erase(confirm);
                return;
            }
        }

        // background registration — используем final-переменные внутри SwingWorker
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    if (DataManager.userExists(username)) {
                        return false;
                    }
                    String passwordStr = new String(pass);
                    DataManager.registerUser(username, passwordStr, role);

                    String extra = isDoctor ? specialty : new SimpleDateFormat("dd-MM-yyyy").format(dob);
                    saveProfile(username, role, isDoctor ? docFull : patFull,
                            extra, isDoctor ? docPhone : patPhone, address, gender);
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                } finally {
                    // затираем массивы паролей для безопасности
                    erase(pass);
                    erase(confirm);
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    Boolean res = get();
                    if (res == null) {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Ошибка регистрации");
                    } else if (!res) {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Пользователь уже существует");
                    } else {
                        JOptionPane.showMessageDialog(RegisterFrame.this, "Регистрация прошла успешно");
                        dispose();
                        // После успешной регистрации показать окно входа
                        SwingUtilities.invokeLater(AuthFrame::new);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private static void erase(char[] arr) {
        if (arr != null) Arrays.fill(arr, '\0');
    }

    private static boolean isPhoneValid(String phone) {
        if (phone == null) return false;
        String digits = phone.replaceAll("[^0-9]", "");
        return digits.length() >= 6;
    }

    private static void saveProfile(String username, String role, String fullName,
                                    String extra, String phone, String address, String gender) {
        String filename = "profile_" + username + ".txt";
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
            out.write("username=" + username + "\n");
            out.write("role=" + role + "\n");
            out.write("fullname=" + (fullName == null ? "" : fullName) + "\n");
            if ("doctor".equals(role)) {
                out.write("specialty=" + (extra == null ? "" : extra) + "\n");
            } else {
                out.write("dob=" + (extra == null ? "" : extra) + "\n");
                out.write("gender=" + (gender == null ? "" : gender) + "\n");
            }
            out.write("phone=" + (phone == null ? "" : phone) + "\n");
            out.write("address=" + (address == null ? "" : address) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String lang(String key, String def) {
        try {
            String val = Lang.get(key);
            if (val == null || val.isEmpty()) return def;
            return val;
        } catch (Throwable t) {
            return def;
        }
    }

    // --- Вспомогательные классы для закруглённой рамки и кнопки ---

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 8, 6, 8);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = 8;
            insets.top = insets.bottom = 6;
            return insets;
        }
    }

    private static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setBorderPainted(false);
            setMargin(new Insets(6, 12, 6, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            Shape round = new RoundRectangle2D.Float(0, 0, width, height, 18, 18);

            // фон
            g2.setColor(getBackground());
            g2.fill(round);

            // контур для белых кнопок
            if (getBackground().equals(Color.WHITE)) {
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(ACCENT_RED);
                g2.draw(round);
            }

            // текст
            FontMetrics fm = g2.getFontMetrics();
            Rectangle r = new Rectangle(0, 0, width, height);
            String text = getText();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int tx = (r.width - textWidth) / 2;
            int ty = (r.height + textHeight) / 2 - 3;

            g2.setColor(getForeground());
            g2.setFont(getFont());
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }
}
