import javax.swing.*;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Locale defaultLocale = new Locale("kk");
        Lang.setLocale(defaultLocale);

        SwingUtilities.invokeLater(() -> {
            new AuthFrame();
        });
    }
}
