package socket;

public class NotificationMessage {
    private int doctorId;
    private String patientName;
    private String date;
    private String time;

    public NotificationMessage() {
    }

    public NotificationMessage(int doctorId, String patientName, String date, String time) {
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.date = date;
        this.time = time;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Serialize message for socket transmission
    public String toSocketPayload() {
        return "NOTIFICATION|" + doctorId + "|" + patientName + "|" + date + "|" + time;
    }

    // Parse payload from socket message
    public static NotificationMessage parsePayload(String payload) {
        if (payload == null || !payload.startsWith("NOTIFICATION|")) {
            return null;
        }
        String[] parts = payload.split("\\|");
        if (parts.length >= 5) {
            try {
                int docId = Integer.parseInt(parts[1]);
                return new NotificationMessage(docId, parts[2], parts[3], parts[4]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
