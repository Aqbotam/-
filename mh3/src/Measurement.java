import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Measurement {
    private LocalDateTime time;
    private int pulse;
    private int systolic;
    private int diastolic;

    public Measurement(int pulse, int systolic, int diastolic) {
        this.time = LocalDateTime.now();
        this.pulse = pulse;
        this.systolic = systolic;
        this.diastolic = diastolic;
    }

    public LocalDateTime getTime() { return time; }
    public int getPulse() { return pulse; }
    public int getSystolic() { return systolic; }
    public int getDiastolic() { return diastolic; }

    public String getTimeString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return time.format(fmt);
    }
}
