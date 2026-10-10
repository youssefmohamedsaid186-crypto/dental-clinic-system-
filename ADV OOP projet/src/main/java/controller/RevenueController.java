package controller;

import database.PaymentDAO;
import model.Payment;

import java.util.List;

public class RevenueController {

    private final PaymentDAO paymentDAO;

    public RevenueController() {
        this.paymentDAO = new PaymentDAO();
    }

    public double getTotalRevenue() {
        return paymentDAO.getTotalRevenue();
    }

    public List<Payment> getPaymentHistory() {
        return paymentDAO.getAllPayments();
    }

    public List<Payment> getPaymentsByPatient(int patientId) {
        if (patientId <= 0) {
            return List.of();
        }
        return paymentDAO.getPaymentsByPatientId(patientId);
    }
}
