import java.sql.*;
import java.util.*;

public class DBHelper {

    private Connection conn;

    public DBHelper() {
        conn = DBConnection.getConnection(); // uses your DBConnection class
    }

    // Load all stores
    public List<Store> loadAllStores() {
        List<Store> list = new ArrayList<>();
        String sql = "SELECT id, name, category, location_id FROM Stores";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Store(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getInt("location_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Load all locations
    public List<Location> loadAllLocations() {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT id, name, type, floor FROM Locations";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Location(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("floor")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Find locationId by store name
    public Integer findLocationIdByStoreName(String storeName) {
        String sql = "SELECT location_id FROM Stores WHERE LOWER(name)=LOWER(?) LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, storeName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("location_id");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    public List<Path> loadAllPaths() {
    List<Path> paths = new ArrayList<>();
    String sql = "SELECT id, source_id, dest_id, distance, is_blocked FROM Paths";
    try (Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
        while (rs.next()) {
            paths.add(new Path(
                rs.getInt("id"),
                rs.getInt("source_id"),
                rs.getInt("dest_id"),
                rs.getInt("distance"),
                rs.getBoolean("is_blocked")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return paths;
}
public void setPathBlocked(int pathId, boolean blocked) {
    String sql = "UPDATE Paths SET is_blocked = ? WHERE id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setBoolean(1, blocked);
        ps.setInt(2, pathId);
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    // Close DB connection
    public void close() {
        try { if (conn != null && !conn.isClosed()) conn.close(); }
        catch (SQLException e) { e.printStackTrace(); }
    }
}