import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
// тек қажет класстар


public class ThresholdCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, col);
        try {
            int val = Integer.parseInt(value.toString());
            boolean out = false;
            if (col == 1) { // пульс
                out = (val < 60 || val > 100);
            } else if (col == 2) { // систолалық
                out = (val < 90 || val > 140);
            } else if (col == 3) { // диастолалық
                out = (val < 60 || val > 90);
            }
            if (out) {
                c.setForeground(Color.RED);
            } else {
                c.setForeground(Color.BLACK);
            }
        } catch (Exception e) {
            // Уақыт бағаны үшін (мәтін) қате санға айналдыру
            c.setForeground(Color.BLACK);
        }
        return c;
    }
}
