package database;

import model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public int createPayment(Payment payment) {
        String sql = "INSERT INTO payments (appointment_id, patient_id, amount_paid, payment_method, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getAppointmentId());
            stmt.setInt(2, payment.getPatientId());
            stmt.setDouble(3, payment.getAmountPaid());
            stmt.setString(4, payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "CASH");
            stmt.setString(5, payment.getStatus() != null ? payment.getStatus() : "COMPLETED");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in PaymentDAO.createPayment: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT pay.*, p.name AS patient_name " +
                     "FROM payments pay " +
                     "JOIN patients p ON pay.patient_id = p.id " +
                     "ORDER BY pay.payment_date DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in PaymentDAO.getAllPayments: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Payment> getPaymentsByPatientId(int patientId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT pay.*, p.name AS patient_name " +
                     "FROM payments pay " +
                     "JOIN patients p ON pay.patient_id = p.id " +
                     "WHERE pay.patient_id = ? " +
                     "ORDER BY pay.payment_date DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in PaymentDAO.getPaymentsByPatientId: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0.0) AS total_revenue FROM payments WHERE UPPER(status) IN ('COMPLETED', 'PAID')";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0.0;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
        } catch (SQLException e) {
            System.err.println("Error in PaymentDAO.getTotalRevenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment pay = new Payment();
        pay.setId(rs.getInt("id"));
        pay.setAppointmentId(rs.getInt("appointment_id"));
        pay.setPatientId(rs.getInt("patient_id"));
        pay.setAmountPaid(rs.getDouble("amount_paid"));
        pay.setPaymentDate(rs.getTimestamp("payment_date"));
        pay.setPaymentMethod(rs.getString("payment_method"));
        pay.setStatus(rs.getString("status"));
        
        // Joined field
        pay.setPatientName(rs.getString("patient_name"));
        return pay;
    }
}
