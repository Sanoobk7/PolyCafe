package poly.cafe.dao;

import poly.cafe.entity.Categories;
import poly.cafe.entity.Drinks;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Home
 */
public class DrinkManagementDao {

    // Thêm đồ uống mới
    public boolean insert(Drinks drink) {
        String sql = "INSERT INTO Drinks(Id, Name, UnitPrice, Discount, Image, Available, CategoryId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, drink.getId());
            pre.setString(2, drink.getName());
            pre.setDouble(3, drink.getUnitPrice());
            pre.setDouble(4, drink.getDiscount());
            pre.setString(5, drink.getImage());
            pre.setBoolean(6, drink.isAvailable());
            pre.setString(7, drink.getCategoryId());

            return pre.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật đồ uống
    public boolean update(Drinks drink) {
        String sql = "UPDATE Drinks SET Name = ?, UnitPrice = ?, Discount = ?, Image = ?, Available = ?, CategoryId = ? WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, drink.getName());
            pre.setDouble(2, drink.getUnitPrice());
            pre.setDouble(3, drink.getDiscount());
            pre.setString(4, drink.getImage());
            pre.setBoolean(5, drink.isAvailable());
            pre.setString(6, drink.getCategoryId());
            pre.setString(7, drink.getId());

            return pre.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa đồ uống theo ID
    public boolean deleteById(String id) {
        String sql = "DELETE FROM Drinks WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, id);

            int row = pre.executeUpdate();
            System.out.println("Xóa thành công: " + row + " dòng.");
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm tất cả đồ uống
    public List<Drinks> findAll() {
        String sql = "SELECT * FROM Drinks";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            List<Drinks> list = new ArrayList<>();
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                Drinks drink = new Drinks();
                drink.setId(rs.getString("Id"));
                drink.setName(rs.getString("Name"));
                drink.setUnitPrice(rs.getDouble("UnitPrice"));
                drink.setDiscount(rs.getDouble("Discount"));
                drink.setImage(rs.getString("Image"));
                drink.setAvailable(rs.getBoolean("Available"));
                drink.setCategoryId(rs.getString("CategoryId"));
                list.add(drink);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm đồ uống theo ID
    public Drinks findById(String id) {
        String sql = "SELECT * FROM Drinks WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, id);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                Drinks drink = new Drinks();
                drink.setId(rs.getString("Id"));
                drink.setName(rs.getString("Name"));
                drink.setUnitPrice(rs.getDouble("UnitPrice"));
                drink.setDiscount(rs.getDouble("Discount"));
                drink.setImage(rs.getString("Image"));
                drink.setAvailable(rs.getBoolean("Available"));
                drink.setCategoryId(rs.getString("CategoryId"));
                return drink;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra đồ uống tồn tại
    public boolean checkDrinkExists(String id) {
        String sql = "SELECT COUNT(*) FROM Drinks WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, id);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tất cả danh mục
    public List<Categories> findAllCategories() {
        String sql = "SELECT * FROM Categories ORDER BY Id";
        List<Categories> list = new ArrayList<>();
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                Categories category = new Categories();
                category.setId(rs.getString("Id"));
                category.setName(rs.getString("Name"));
                list.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tìm danh mục theo ID
    public Categories findCategoryById(String id) {
        String sql = "SELECT * FROM Categories WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, id);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                Categories category = new Categories();
                category.setId(rs.getString("Id"));
                category.setName(rs.getString("Name"));
                return category;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Kiểm tra danh mục tồn tại
    public boolean checkCategoryExists(String id) {
        String sql = "SELECT COUNT(*) FROM Categories WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, id);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}