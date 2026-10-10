package database;

import model.Patient;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public int createPatient(Patient patient) {
        String sql = "INSERT INTO patients (user_id, name, phone, email, dob, gender, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (patient.getUserId() != null && patient.getUserId() > 0) {
                stmt.setInt(1, patient.getUserId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, patient.getName());
            stmt.setString(3, patient.getPhone());
            stmt.setString(4, patient.getEmail());
            stmt.setDate(5, patient.getDob() != null ? Date.valueOf(patient.getDob()) : null);
            stmt.setString(6, patient.getGender());
            stmt.setString(7, patient.getAddress());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in PatientDAO.createPatient: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY name ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in PatientDAO.getAllPatients: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public Patient getPatientById(int id) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in PatientDAO.getPatientById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Patient getPatientByUserId(int userId) {
        String sql = "SELECT * FROM patients WHERE user_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in PatientDAO.getPatientByUserId: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Patient searchPatientByPhone(String phone) {
        String sql = "SELECT * FROM patients WHERE phone = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in PatientDAO.searchPatientByPhone: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Patient> searchPatientByNameOrPhone(String query) {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE name LIKE ? OR phone LIKE ? ORDER BY name ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + query.trim() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPatient(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in PatientDAO.searchPatientByNameOrPhone: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET name = ?, phone = ?, email = ?, dob = ?, gender = ?, address = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getPhone());
            stmt.setString(3, patient.getEmail());
            stmt.setDate(4, patient.getDob() != null ? Date.valueOf(patient.getDob()) : null);
            stmt.setString(5, patient.getGender());
            stmt.setString(6, patient.getAddress());
            stmt.setInt(7, patient.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in PatientDAO.updatePatient: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setId(rs.getInt("id"));
        
        int uId = rs.getInt("user_id");
        if (rs.wasNull()) {
            p.setUserId(null);
        } else {
            p.setUserId(uId);
        }

        p.setName(rs.getString("name"));
        p.setPhone(rs.getString("phone"));
        p.setEmail(rs.getString("email"));
        
        Date dobDate = rs.getDate("dob");
        if (dobDate != null) {
            p.setDob(dobDate.toLocalDate());
        }
        
        p.setGender(rs.getString("gender"));
        p.setAddress(rs.getString("address"));
        return p;
    }
}
