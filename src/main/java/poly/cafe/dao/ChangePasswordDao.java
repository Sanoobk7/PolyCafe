package poly.cafe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import poly.cafe.entity.Users;

public class ChangePasswordDao {
    public Users getUser(String username) {
        String sql = "SELECT * FROM Users WHERE Username = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Users(
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getBoolean("Enabled"),
                    rs.getString("Fullname"),
                    rs.getString("Photo"),
                    rs.getBoolean("Manager")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE Users SET Password = ? WHERE Username = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected); // Debug
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}