import java.awt.Color;
// тек қажет класстар
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;


public class GraphPanel extends JPanel {
    private List<Measurement> measurements;

    public GraphPanel(List<Measurement> measurements) {
        this.measurements = measurements;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (measurements == null || measurements.size() < 2) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int margin = 30;
        // рамка графика
        g2.setColor(Color.GRAY);
        g2.drawRect(margin, margin, width - 2 * margin, height - 2 * margin);

        // максимум мәнді табу
        int max = 0;
        for (Measurement m : measurements) {
            max = Math.max(max, m.getPulse());
            max = Math.max(max, m.getSystolic());
            max = Math.max(max, m.getDiastolic());
        }
        // өлшеу коэффициенті
        double xScale = (double)(width - 2 * margin) / (measurements.size() - 1);
        double yScale = (double)(height - 2 * margin) / max;

        // сызықтарды сызу (сериялар: пульс, систолалық, диастолалық)
        for (int i = 0; i < measurements.size() - 1; i++) {
            int x1 = margin + (int)(i * xScale);
            int x2 = margin + (int)((i + 1) * xScale);

            // пульс (көк сызық)
            g2.setColor(Color.BLUE);
            int y1 = height - margin - (int)(measurements.get(i).getPulse() * yScale);
            int y2 = height - margin - (int)(measurements.get(i + 1).getPulse() * yScale);
            g2.drawLine(x1, y1, x2, y2);

            // систолалық (қызыл сызық)
            g2.setColor(Color.RED);
            y1 = height - margin - (int)(measurements.get(i).getSystolic() * yScale);
            y2 = height - margin - (int)(measurements.get(i + 1).getSystolic() * yScale);
            g2.drawLine(x1, y1, x2, y2);

            // диастолалық (жасыл сызық)
            g2.setColor(Color.GREEN.darker());
            y1 = height - margin - (int)(measurements.get(i).getDiastolic() * yScale);
            y2 = height - margin - (int)(measurements.get(i + 1).getDiastolic() * yScale);
            g2.drawLine(x1, y1, x2, y2);
        }
    }
}
