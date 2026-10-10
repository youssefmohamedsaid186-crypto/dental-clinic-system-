package database;

import model.Receptionist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReceptionistDAO {

    public int createReceptionist(Receptionist receptionist) {
        String sql = "INSERT INTO receptionists (user_id, name, phone, email) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, receptionist.getUserId());
            stmt.setString(2, receptionist.getName());
            stmt.setString(3, receptionist.getPhone());
            stmt.setString(4, receptionist.getEmail());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ReceptionistDAO.createReceptionist: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public List<Receptionist> getAllReceptionists() {
        List<Receptionist> list = new ArrayList<>();
        String sql = "SELECT * FROM receptionists ORDER BY id ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Receptionist r = mapResultSetToReceptionist(rs);
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error in ReceptionistDAO.getAllReceptionists: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public Receptionist getReceptionistById(int id) {
        String sql = "SELECT * FROM receptionists WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReceptionist(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ReceptionistDAO.getReceptionistById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Receptionist getReceptionistByUserId(int userId) {
        String sql = "SELECT * FROM receptionists WHERE user_id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReceptionist(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ReceptionistDAO.getReceptionistByUserId: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Receptionist mapResultSetToReceptionist(ResultSet rs) throws SQLException {
        Receptionist r = new Receptionist();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setName(rs.getString("name"));
        r.setPhone(rs.getString("phone"));
        r.setEmail(rs.getString("email"));
        return r;
    }
}
