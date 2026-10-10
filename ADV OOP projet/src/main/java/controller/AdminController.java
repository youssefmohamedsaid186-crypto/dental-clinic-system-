package controller;

import database.DoctorDAO;
import database.ReceptionistDAO;
import database.UserDAO;
import model.Doctor;
import model.Receptionist;
import model.User;

import java.util.List;

public class AdminController {

    private final UserDAO userDAO;
    private final DoctorDAO doctorDAO;
    private final ReceptionistDAO receptionistDAO;

    public AdminController() {
        this.userDAO = new UserDAO();
        this.doctorDAO = new DoctorDAO();
        this.receptionistDAO = new ReceptionistDAO();
    }

    public boolean registerDoctor(String username, String password, String name, String specialization, String phone, String email) {
        if (isInvalid(username) || isInvalid(password) || isInvalid(name) || isInvalid(specialization) || isInvalid(phone) || isInvalid(email)) {
            return false;
        }

        // 1. Create User account with role DOCTOR
        User user = new User(username.trim(), password.trim(), "DOCTOR");
        int userId = userDAO.createUser(user);
        if (userId <= 0) {
            return false;
        }

        // 2. Create Doctor profile linked to user
        Doctor doctor = new Doctor(userId, name.trim(), specialization.trim(), phone.trim(), email.trim());
        int doctorId = doctorDAO.createDoctor(doctor);

        return doctorId > 0;
    }

    public boolean registerReceptionist(String username, String password, String name, String phone, String email) {
        if (isInvalid(username) || isInvalid(password) || isInvalid(name) || isInvalid(phone) || isInvalid(email)) {
            return false;
        }

        // 1. Create User account with role RECEPTIONIST
        User user = new User(username.trim(), password.trim(), "RECEPTIONIST");
        int userId = userDAO.createUser(user);
        if (userId <= 0) {
            return false;
        }

        // 2. Create Receptionist profile linked to user
        Receptionist receptionist = new Receptionist(userId, name.trim(), phone.trim(), email.trim());
        int receptionistId = receptionistDAO.createReceptionist(receptionist);

        return receptionistId > 0;
    }

    public List<Doctor> getAllDoctors() {
        return doctorDAO.getAllDoctors();
    }

    public List<Receptionist> getAllReceptionists() {
        return receptionistDAO.getAllReceptionists();
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    private boolean isInvalid(String value) {
        return value == null || value.trim().isEmpty();
    }
}
