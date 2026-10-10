package controller;

import database.AppointmentDAO;
import database.ResourceDAO;
import model.Appointment;
import model.Resource;

import java.util.List;

public class DoctorController {

    private final AppointmentDAO appointmentDAO;
    private final ResourceDAO resourceDAO;

    public DoctorController() {
        this.appointmentDAO = new AppointmentDAO();
        this.resourceDAO = new ResourceDAO();
    }

    public List<Appointment> getAppointmentsForDoctor(int doctorId) {
        return appointmentDAO.getAppointmentsByDoctorId(doctorId);
    }

    public boolean acceptAppointment(int appointmentId) {
        return appointmentDAO.updateAppointmentStatus(appointmentId, "ACCEPTED");
    }

    public boolean rejectAppointment(int appointmentId) {
        return appointmentDAO.updateAppointmentStatus(appointmentId, "REJECTED");
    }

    public List<Resource> getClinicResources() {
        return resourceDAO.getAllResources();
    }
}
