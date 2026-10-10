package database;

import model.Resource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    public int addResource(Resource resource) {
        String sql = "INSERT INTO resources (tool_name, total_qty, available_qty, status) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return -1;

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, resource.getToolName());
            stmt.setInt(2, resource.getTotalQty());
            stmt.setInt(3, resource.getAvailableQty());
            stmt.setString(4, Resource.calculateStatus(resource.getAvailableQty(), resource.getTotalQty()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ResourceDAO.addResource: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public List<Resource> getAllResources() {
        List<Resource> list = new ArrayList<>();
        String sql = "SELECT * FROM resources ORDER BY tool_name ASC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToResource(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error in ResourceDAO.getAllResources: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public Resource getResourceById(int id) {
        String sql = "SELECT * FROM resources WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToResource(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in ResourceDAO.getResourceById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateResource(Resource resource) {
        String sql = "UPDATE resources SET tool_name = ?, total_qty = ?, available_qty = ?, status = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resource.getToolName());
            stmt.setInt(2, resource.getTotalQty());
            stmt.setInt(3, resource.getAvailableQty());
            stmt.setString(4, Resource.calculateStatus(resource.getAvailableQty(), resource.getTotalQty()));
            stmt.setInt(5, resource.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in ResourceDAO.updateResource: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateQuantity(int id, int availableQty, int totalQty) {
        String sql = "UPDATE resources SET available_qty = ?, total_qty = ?, status = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, availableQty);
            stmt.setInt(2, totalQty);
            stmt.setString(3, Resource.calculateStatus(availableQty, totalQty));
            stmt.setInt(4, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error in ResourceDAO.updateQuantity: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Resource mapResultSetToResource(ResultSet rs) throws SQLException {
        Resource r = new Resource();
        r.setId(rs.getInt("id"));
        r.setToolName(rs.getString("tool_name"));
        r.setTotalQty(rs.getInt("total_qty"));
        r.setAvailableQty(rs.getInt("available_qty"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}
