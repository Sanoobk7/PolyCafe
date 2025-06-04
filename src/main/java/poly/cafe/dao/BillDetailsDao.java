package poly.cafe.dao;

import poly.cafe.entity.BillDetails;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.entity.Users;
import poly.cafe.dao.DataConnection;

public class BillDetailsDao {
//    private long id;
//    private long billId;
//    private String drinkId;
//    private double unitPrice;
//    private double discount;
//    private int quantity;
    public boolean insert(BillDetails billDetail) {
        String sql = "INSERT INTO BillDetails (BillId, DrinkId, UnitPrice, Discount, Quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setLong(1, billDetail.getBillId());
            pre.setString(2, billDetail.getDrinkId());
            pre.setDouble(3, billDetail.getUnitPrice());
            pre.setDouble(4, billDetail.getDiscount());
            pre.setInt(5, billDetail.getQuantity()); // Changed to setInt to match schema

            return pre.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<BillDetails> findAll() {
        String sql = "SELECT * FROM BillDetails";
        List<BillDetails> list = new ArrayList<>();
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql);
             ResultSet rs = pre.executeQuery()) {
            while (rs.next()) {
                BillDetails bd = new BillDetails();
                bd.setId(rs.getLong("Id"));
                bd.setBillId(rs.getLong("BillId"));
                bd.setDrinkId(rs.getString("DrinkId"));
                bd.setUnitPrice(rs.getDouble("UnitPrice"));
                bd.setDiscount(rs.getDouble("Discount"));
                bd.setQuantity(rs.getInt("Quantity"));
                list.add(bd);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }
    }

    public List<BillDetails> findByBillId(long billId) {
        String sql = "SELECT * FROM BillDetails WHERE BillId = ?";
        List<BillDetails> list = new ArrayList<>();
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setLong(1, billId);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                BillDetails detail = new BillDetails();
                detail.setId(rs.getLong("Id"));
                detail.setBillId(rs.getLong("BillId"));
                detail.setDrinkId(rs.getString("DrinkId"));
                detail.setUnitPrice(rs.getDouble("UnitPrice"));
                detail.setDiscount(rs.getDouble("Discount"));
                detail.setQuantity(rs.getInt("Quantity"));
                list.add(detail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean update(BillDetails billDetail) {
        String sql = "UPDATE BillDetails SET BillId = ?, DrinkId = ?, UnitPrice = ?, Discount = ?, Quantity = ? WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setLong(1, billDetail.getBillId());
            pre.setString(2, billDetail.getDrinkId());
            pre.setDouble(3, billDetail.getUnitPrice());
            pre.setDouble(4, billDetail.getDiscount());
            pre.setInt(5, billDetail.getQuantity());
            pre.setLong(6, billDetail.getId());
            return pre.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public BillDetails findById(long id) {
        String sql = "SELECT * FROM BillDetails WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setLong(1, id);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                BillDetails detail = new BillDetails();
                detail.setId(rs.getLong("Id"));
                detail.setBillId(rs.getLong("BillId"));
                detail.setDrinkId(rs.getString("DrinkId"));
                detail.setUnitPrice(rs.getDouble("UnitPrice"));
                detail.setDiscount(rs.getDouble("Discount"));
                detail.setQuantity(rs.getInt("Quantity"));
                return detail;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean deleteById(long id) {
    String sql = "DELETE FROM BillDetails WHERE Id = ?";
    try (Connection con = DataConnection.open();
         PreparedStatement pre = con.prepareStatement(sql)) {
        pre.setLong(1, id);
        return pre.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
      public List<Object[]> FindByBillDetail(long billId) {
        String sql = "SELECT bd.Id, bd.BillId, bd.DrinkId, bd.UnitPrice, bd.Discount, bd.Quantity, d.Name AS DrinkName " +
                     "FROM BillDetails bd " +
                     "JOIN Drinks d ON bd.DrinkId = d.Id " +
                     "WHERE bd.BillId = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setLong(1, billId);
            ResultSet rs = pre.executeQuery();
            List<Object[]> list = new ArrayList<>();
            while (rs.next()) {
                BillDetails bd = new BillDetails();
                bd.setId(rs.getLong("Id"));
                bd.setBillId(rs.getLong("BillId"));
                bd.setDrinkId(rs.getString("DrinkId"));
                bd.setUnitPrice(rs.getDouble("UnitPrice"));
                bd.setDiscount(rs.getDouble("Discount"));
                bd.setQuantity(rs.getInt("Quantity"));
                String drinkName = rs.getString("DrinkName");
                // Tính thành tiền
                double totalPrice = bd.getUnitPrice() * (1 - bd.getDiscount() / 100) * bd.getQuantity();
                // Thêm vào danh sách với các cột cần thiết
                list.add(new Object[]{
                    drinkName,
                    bd.getUnitPrice(),
                    bd.getDiscount(),
                    bd.getQuantity(),
                    totalPrice
                });
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}