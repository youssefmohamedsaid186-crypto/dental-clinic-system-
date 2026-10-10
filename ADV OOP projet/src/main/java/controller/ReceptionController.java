package controller;

import database.AppointmentDAO;
import database.DoctorDAO;
import database.PatientDAO;
import database.PaymentDAO;
import database.ResourceDAO;
import model.Appointment;
import model.Doctor;
import model.Patient;
import model.Payment;
import model.Resource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReceptionController {

    private final PatientDAO patientDAO;
    private final DoctorDAO doctorDAO;
    private final AppointmentDAO appointmentDAO;
    private final PaymentDAO paymentDAO;
    private final ResourceDAO resourceDAO;

    public ReceptionController() {
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.paymentDAO = new PaymentDAO();
        this.resourceDAO = new ResourceDAO();
    }

    public Patient searchPatientByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        return patientDAO.searchPatientByPhone(phone.trim());
    }

    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return patientDAO.getAllPatients();
        }
        return patientDAO.searchPatientByNameOrPhone(query.trim());
    }

    public Patient registerPatient(String name, String phone, String email, LocalDate dob, String gender, String address) {
        if (name == null || name.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            return null;
        }

        // Check if patient already exists by phone
        Patient existing = patientDAO.searchPatientByPhone(phone.trim());
        if (existing != null) {
            return existing;
        }

        Patient newPatient = new Patient(name.trim(), phone.trim(), email != null ? email.trim() : "", dob, gender != null ? gender.trim() : "Other", address != null ? address.trim() : "");
        int patientId = patientDAO.createPatient(newPatient);
        if (patientId > 0) {
            newPatient.setId(patientId);
            return newPatient;
        }
        return null;
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.getAllDoctors();
    }

    public List<Patient> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public boolean createAppointment(int patientId, int doctorId, LocalDate date, LocalTime time, double amountDue, String notes) {
        if (patientId <= 0 || doctorId <= 0 || date == null || time == null || amountDue < 0) {
            return false;
        }

        Appointment appointment = new Appointment(patientId, doctorId, date, time, amountDue, notes != null ? notes.trim() : "");
        appointment.setStatus("PENDING");
        int appointmentId = appointmentDAO.createAppointment(appointment);

        if (appointmentId > 0) {
            // Socket Notification: Trigger after successful DB save
            Patient p = patientDAO.getPatientById(patientId);
            String patientName = (p != null) ? p.getName() : "Patient #" + patientId;
            socket.SocketClient.sendAppointmentNotification(doctorId, patientName, date.toString(), time.toString());
            return true;
        }
        return false;
    }

    public boolean processPayment(Appointment appointment, String paymentMethod) {
        if (appointment == null || appointment.getId() <= 0) {
            return false;
        }

        // 1. Record Payment entry
        String method = (paymentMethod == null || paymentMethod.trim().isEmpty()) ? "CASH" : paymentMethod.trim();
        Payment payment = new Payment(appointment.getId(), appointment.getPatientId(), appointment.getAmountDue(), method);
        int paymentId = paymentDAO.createPayment(payment);

        if (paymentId > 0) {
            // 2. Update Appointment status strictly to "PAID"
            return appointmentDAO.updateAppointmentStatus(appointment.getId(), "PAID");
        }
        return false;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    public List<Resource> getClinicResources() {
        return resourceDAO.getAllResources();
    }
}
