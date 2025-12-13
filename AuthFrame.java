import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class AuthFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private static Locale currentLocale = Lang.getCurrentLocale() != null ? Lang.getCurrentLocale() : new Locale("kk");
    private final String[][] roles = new String[][] {
            { safeLang("role_patient_display", "Пациент"), "patient" },
            { safeLang("role_doctor_display",  "Доктор")  , "doctor" }
    };

    // Путь к логотипу (classpath ресурс и fallback на твой локальный файл)
    private static final String LOGO_RESOURCE = "/images/logo.png"; // путь внутри src/main/resources
    private static final String LOGO_FALLBACK_PATH = "C:\\Users\\omara\\IdeaProjects\\mh3\\src\\src\\main\\resources\\images\\logo.png";

    // Цветовая схема (красный акцент и тёплый фон)
    private static final Color ACCENT_RED = new Color(190, 18, 28);
    private static final Color FIELD_BG = new Color(250, 248, 246); // почти белый с тёплым оттенком
    private static final Color OUTER_BG = new Color(246, 243, 241);

    public static class LayoutConfig {
        // размеры окна
        public static int FRAME_W = 500;
        public static int FRAME_H = 800;

        // Заголовок (Label)
        public static int HEADER_X = 28;
        public static int HEADER_Y = 12;
        public static int HEADER_W = 260;
        public static int HEADER_H = 36;

        // Логотип (вверху)
        public static int LOGO_X = 80;
        public static int LOGO_Y = 60;
        public static int LOGO_W = 350;
        public static int LOGO_H = 330;

        // Кнопка языка (в правом верхнем углу)
        public static int LANG_X = 390;
        public static int LANG_Y = 12;
        public static int LANG_W = 80;
        public static int LANG_H = 32;

        // Поля формы: метки и поля ввода
        public static int LABEL_X = 70;
        public static int LABEL_W = 350;
        public static int FIELD_X = 70;
        public static int FIELD_W = 350;
        public static int FIELD_H = 38;

        // Начальная Y для первой группы "метка + поле"
        public static int FORM_START_Y = 410;
        public static int FORM_V_GAP = 64; // расстояние между группами

        // Кнопки внизу
        public static int BTN_REGISTER_X = 60;
        public static int BTN_LOGIN_X = 245;
        public static int BTNS_Y = 700;
        public static int BTNS_W = 180;
        public static int BTNS_H = 44;
    }

    public AuthFrame() {
        // --- настройки окна ---
        setTitle(Lang.get("btn_login"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(LayoutConfig.FRAME_W, LayoutConfig.FRAME_H);
        setResizable(false);
        setLocationRelativeTo(null);

        // Фон окна
        getContentPane().setBackground(OUTER_BG);
        getContentPane().setLayout(null); // абсолютное позиционирование как просили

        // --- Заголовок ---
        JLabel header = new JLabel(Lang.get("btn_login"), SwingConstants.LEFT);
        header.setFont(new Font("Dialog", Font.BOLD, 26));
        header.setForeground(ACCENT_RED);
        header.setBounds(LayoutConfig.HEADER_X, LayoutConfig.HEADER_Y, LayoutConfig.HEADER_W, LayoutConfig.HEADER_H);
        getContentPane().add(header);

        // --- Кнопка переключения языка ---
        JButton langBtn = new RoundedButton("Тіл");
        langBtn.setBounds(LayoutConfig.LANG_X, LayoutConfig.LANG_Y, LayoutConfig.LANG_W, LayoutConfig.LANG_H);
        langBtn.setBackground(Color.WHITE);
        langBtn.setForeground(ACCENT_RED);
        langBtn.setBorder(BorderFactory.createLineBorder(ACCENT_RED, 2));
        langBtn.addActionListener(e -> switchLanguage());
        getContentPane().add(langBtn);

        // --- Логотип (в верхней части) ---
        JLabel logoLabel = new JLabel();
        logoLabel.setOpaque(false);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setVerticalAlignment(SwingConstants.CENTER);
        logoLabel.setBounds(LayoutConfig.LOGO_X, LayoutConfig.LOGO_Y, LayoutConfig.LOGO_W, LayoutConfig.LOGO_H);
        // Попытаться загрузить логотип (classpath -> fallback путь)
        ImageIcon logoIcon = loadLogoScaled(LayoutConfig.LOGO_W, LayoutConfig.LOGO_H);
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
            logoLabel.setText(null);
        } else {
            // если логотип не найден — показывать контурное поле с иконкой монитора
            logoLabel.setBorder(BorderFactory.createLineBorder(ACCENT_RED, 3, true));
            logoLabel.setBackground(FIELD_BG);
            logoLabel.setOpaque(true);
            logoLabel.setText("LOGO");
            logoLabel.setFont(new Font("Dialog", Font.BOLD, 24));
            logoLabel.setForeground(ACCENT_RED);
        }
        getContentPane().add(logoLabel);

        // --- Формы (метки + поля) ---
        Font labelFont = new Font("Dialog", Font.BOLD, 16);
        int y = LayoutConfig.FORM_START_Y;

        // Username
        JLabel usernameLabel = new JLabel(Lang.get("label_username"));
        usernameLabel.setFont(labelFont);
        usernameLabel.setForeground(ACCENT_RED);
        usernameLabel.setBounds(LayoutConfig.LABEL_X, y, LayoutConfig.LABEL_W, 22);
        getContentPane().add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(LayoutConfig.FIELD_X, y + 26, LayoutConfig.FIELD_W, LayoutConfig.FIELD_H);
        styleField(usernameField);
        getContentPane().add(usernameField);
        y += LayoutConfig.FORM_V_GAP;

        // Password
        JLabel passwordLabel = new JLabel(Lang.get("label_password"));
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(ACCENT_RED);
        passwordLabel.setBounds(LayoutConfig.LABEL_X, y, LayoutConfig.LABEL_W, 22);
        getContentPane().add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(LayoutConfig.FIELD_X, y + 26, LayoutConfig.FIELD_W, LayoutConfig.FIELD_H);
        styleField(passwordField);
        getContentPane().add(passwordField);
        y += LayoutConfig.FORM_V_GAP;

        // Role label + combo
        JLabel roleLabel = new JLabel(Lang.get("label_role"));
        roleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        roleLabel.setForeground(ACCENT_RED);
        roleLabel.setBounds(LayoutConfig.LABEL_X, y, LayoutConfig.LABEL_W, 22);
        getContentPane().add(roleLabel);

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (String[] r : roles) model.addElement(r[0]);
        roleBox = new JComboBox<>(model);
        roleBox.setBounds(LayoutConfig.FIELD_X, y + 26, 240, LayoutConfig.FIELD_H);
        roleBox.setBackground(Color.WHITE);
        roleBox.setBorder(BorderFactory.createLineBorder(ACCENT_RED, 2));
        roleBox.setForeground(Color.DARK_GRAY);
        getContentPane().add(roleBox);
        y += LayoutConfig.FORM_V_GAP;

        // --- Кнопки внизу: Регистрация и Вход ---
        JButton registerBtn = new RoundedButton(Lang.get("btn_register"));
        JButton loginBtn = new RoundedButton(Lang.get("btn_login"));

        registerBtn.setBounds(LayoutConfig.BTN_REGISTER_X, LayoutConfig.BTNS_Y, LayoutConfig.BTNS_W, LayoutConfig.BTNS_H);
        loginBtn.setBounds(LayoutConfig.BTN_LOGIN_X, LayoutConfig.BTNS_Y, LayoutConfig.BTNS_W, LayoutConfig.BTNS_H);

        // Стиль кнопок: регистрация — белая с красным контуром, вход — красная заливка
        registerBtn.setBackground(Color.WHITE);
        registerBtn.setForeground(ACCENT_RED);
        registerBtn.setBorder(BorderFactory.createLineBorder(ACCENT_RED, 2));
        registerBtn.setFont(new Font("Dialog", Font.BOLD, 16));

        loginBtn.setBackground(ACCENT_RED);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBorder(BorderFactory.createEmptyBorder());
        loginBtn.setFont(new Font("Dialog", Font.BOLD, 16));

        // Сохраняем существующие действия (логика не изменялась)
        registerBtn.addActionListener(e -> new RegisterFrame(this));
        loginBtn.addActionListener(this::handleLoginBackground);

        getContentPane().add(registerBtn);
        getContentPane().add(loginBtn);

        // Нажатие Enter — выполнить вход
        getRootPane().setDefaultButton(loginBtn);

        // Показать окно
        setVisible(true);
    }

    // helper: безопасный Lang.get для статической инициализации
    private static String safeLang(String key, String fallback) {
        try {
            String s = Lang.get(key);
            if (s == null || s.equals(key)) return fallback;
            return s;
        } catch (Exception ex) {
            return fallback;
        }
    }

    // --- login через SwingWorker (функция сохранена) ---
    private void handleLoginBackground(ActionEvent e) {
        final String username = usernameField.getText().trim();
        final char[] passwordChars = passwordField.getPassword();

        if (username.isEmpty() || passwordChars.length == 0) {
            JOptionPane.showMessageDialog(this, Lang.get("message_fill_fields"));
            Arrays.fill(passwordChars, '\0');
            return;
        }

        // UI feedback
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getRootPane().setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                String pass = new String(passwordChars);
                try {
                    return DataManager.checkLogin(username, pass); // может быть IO
                } finally {
                    Arrays.fill(passwordChars, '\0');
                }
            }

            @Override
            protected void done() {
                try {
                    String role = get(); // роль от DataManager (или null)
                    setCursor(Cursor.getDefaultCursor());
                    getRootPane().setEnabled(true);

                    if (role == null) {
                        JOptionPane.showMessageDialog(AuthFrame.this, Lang.get("message_login_failed"));
                        return;
                    }

                    // Открываем соответствующую панель (не передаём пароль дальше)
                    if ("patient".equals(role)) {
                        new PatientPanel(username);
                    } else {
                        new DoctorDashboard(username);
                    }
                    dispose();
                } catch (Exception ex) {
                    setCursor(Cursor.getDefaultCursor());
                    getRootPane().setEnabled(true);
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(AuthFrame.this, "Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    // переключение языка (функция сохранена)
    private void switchLanguage() {
        if (currentLocale.getLanguage().equals("kk")) {
            currentLocale = new Locale("ru");
        } else {
            currentLocale = new Locale("kk");
        }
        Lang.setLocale(currentLocale);

        // Пересоздаём окно для обновления текстов
        SwingUtilities.invokeLater(() -> {
            dispose();
            new AuthFrame();
        });
    }

    // возвращает текущую локаль (если нужно извне)
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    // --- Вспомогательные методы для стиля ---

    // Оформление полей ввода: фон, рамка, отступы
    private void styleField(JComponent field) {
        field.setBackground(FIELD_BG);
        field.setForeground(Color.DARK_GRAY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, ACCENT_RED),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        field.setFont(new Font("Dialog", Font.PLAIN, 14));
    }

    // Попытка загрузить логотип: сначала из classpath, иначе из fallback пути
    private ImageIcon loadLogoScaled(int w, int h) {
        BufferedImage img = null;
        try {
            // 1) попытка загрузить из ресурсов (если упаковано в jar): /images/logo.png
            try {
                java.net.URL res = getClass().getResource(LOGO_RESOURCE);
                if (res != null) {
                    img = ImageIO.read(res);
                }
            } catch (Exception ignored) {}

            // 2) fallback: локальный путь (например, в среде разработки)
            if (img == null) {
                File f = new File(LOGO_FALLBACK_PATH);
                if (f.exists()) {
                    img = ImageIO.read(f);
                } else {
                    System.out.println("Logo not found at fallback path: " + LOGO_FALLBACK_PATH);
                }
            }

            if (img != null) {
                Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // --- Вспомогательные классы для округлённой рамки и кнопки ---

    /**
     * RoundedBorder — рамка с закруглёнными углами и цветом акцента.
     */
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
            Shape round = new RoundRectangle2D.Float(0, 0, width, height, 20, 20);

            // фон: берем background у кнопки (может быть ACCENT_RED или белый)
            g2.setColor(getBackground());
            g2.fill(round);

            // если у кнопки белый фон — рисуем контур акцентом
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
