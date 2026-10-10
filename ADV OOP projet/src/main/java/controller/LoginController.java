package controller;

import database.DoctorDAO;
import database.PatientDAO;
import database.ReceptionistDAO;
import database.UserDAO;
import model.Doctor;
import model.Patient;
import model.Receptionist;
import model.User;

public class LoginController {

    private final UserDAO userDAO;
    private final DoctorDAO doctorDAO;
    private final ReceptionistDAO receptionistDAO;
    private final PatientDAO patientDAO;

    public LoginController() {
        this.userDAO = new UserDAO();
        this.doctorDAO = new DoctorDAO();
        this.receptionistDAO = new ReceptionistDAO();
        this.patientDAO = new PatientDAO();
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        return userDAO.authenticate(username.trim(), password.trim());
    }

    public Doctor getDoctorProfile(int userId) {
        return doctorDAO.getDoctorByUserId(userId);
    }

    public Receptionist getReceptionistProfile(int userId) {
        return receptionistDAO.getReceptionistByUserId(userId);
    }

    public Patient getPatientProfile(int userId) {
        return patientDAO.getPatientByUserId(userId);
    }
}
