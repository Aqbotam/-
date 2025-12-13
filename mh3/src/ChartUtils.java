import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChartUtils {

    public static JPanel createPulseChart(String username) {
        TimeSeries series = new TimeSeries("Pulse");

        try (BufferedReader reader = new BufferedReader(new FileReader("data_" + username + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(parts[0]);
                    int pulse = Integer.parseInt(parts[1]);
                    series.addOrUpdate(new Second(date), pulse);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Пульс vs Уақыт", "Уақыт", "Пульс", dataset,
                false, true, false
        );
        return new ChartPanel(chart);
    }
}
