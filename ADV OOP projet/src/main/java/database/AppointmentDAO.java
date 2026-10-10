package database;

import model.Appointment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public int createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, amount_due, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            stmt.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            stmt.setDouble(5, appointment.getAmountDue());
            stmt.setString(6, appointment.getStatus() != null ? appointment.getStatus() : "PENDING");
            stmt.setString(7, appointment.getNotes());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in AppointmentDAO.createAppointment: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS patient_name, d.name AS doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.id " +
                     "JOIN doctors d ON a.doctor_id = d.id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in AppointmentDAO.getAllAppointments: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public Appointment getAppointmentById(int id) {
        String sql = "SELECT a.*, p.name AS patient_name, d.name AS doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.id " +
                     "JOIN doctors d ON a.doctor_id = d.id " +
                     "WHERE a.id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in AppointmentDAO.getAppointmentById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Appointment> getAppointmentsByDoctorId(int doctorId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS patient_name, d.name AS doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.id " +
                     "JOIN doctors d ON a.doctor_id = d.id " +
                     "WHERE a.doctor_id = ? " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in AppointmentDAO.getAppointmentsByDoctorId: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, p.name AS patient_name, d.name AS doctor_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.id " +
                     "JOIN doctors d ON a.doctor_id = d.id " +
                     "WHERE a.patient_id = ? " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in AppointmentDAO.getAppointmentsByPatientId: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in AppointmentDAO.updateAppointmentStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAmountDue(int appointmentId, double amountDue) {
        String sql = "UPDATE appointments SET amount_due = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amountDue);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in AppointmentDAO.updateAmountDue: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment app = new Appointment();
        app.setId(rs.getInt("id"));
        app.setPatientId(rs.getInt("patient_id"));
        app.setDoctorId(rs.getInt("doctor_id"));
        
        Date date = rs.getDate("appointment_date");
        if (date != null) {
            app.setAppointmentDate(date.toLocalDate());
        }
        
        Time time = rs.getTime("appointment_time");
        if (time != null) {
            app.setAppointmentTime(time.toLocalTime());
        }
        
        app.setAmountDue(rs.getDouble("amount_due"));
        app.setStatus(rs.getString("status"));
        app.setNotes(rs.getString("notes"));
        app.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Joined columns
        app.setPatientName(rs.getString("patient_name"));
        app.setDoctorName(rs.getString("doctor_name"));
        return app;
    }
}
