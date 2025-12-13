import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Lang {
    private static Locale currentLocale = new Locale("kk");
    private static ResourceBundle bundle = ResourceBundle.getBundle("lang", currentLocale);

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // Кілт табылмаса — өзі кілтті қайтару, оңай қателерді көруге ыңғайлы
            return key;
        }
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("lang", currentLocale);
    }

    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}
