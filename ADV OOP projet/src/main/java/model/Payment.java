package model;

import java.sql.Timestamp;

public class Payment {
    private int id;
    private int appointmentId;
    private int patientId;
    private double amountPaid;
    private Timestamp paymentDate;
    private String paymentMethod;
    private String status;

    // Display field for UI table
    private String patientName;

    public Payment() {
    }

    public Payment(int id, int appointmentId, int patientId, double amountPaid, Timestamp paymentDate, String paymentMethod, String status) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public Payment(int appointmentId, int patientId, double amountPaid, String paymentMethod) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.status = "COMPLETED";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String toString() {
        return "Payment #" + id + " - $" + amountPaid + " (" + paymentMethod + ")";
    }
}
