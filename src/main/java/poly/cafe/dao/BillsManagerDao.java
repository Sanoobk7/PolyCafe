/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao;

import poly.cafe.entity.Bills;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import poly.cafe.dao.DataConnection;

/**
 *
 * @author Home
 */
public class BillsManagerDao {

    // Insert a new bill
    public boolean insert(Bills bill) {
        String sql = "INSERT INTO Bills(Username, CardId, Checkin, Checkout, Status) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, bill.getUsername());
            pre.setInt(2, bill.getCardId());
            pre.setTimestamp(3, new java.sql.Timestamp(bill.getCheckin().getTime()));
            if (bill.getCheckout() != null) {
                pre.setTimestamp(4, new java.sql.Timestamp(bill.getCheckout().getTime()));
            } else {
                pre.setNull(4, java.sql.Types.TIMESTAMP);
            }
            pre.setInt(5, bill.getStatus());

            return pre.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing bill
    public boolean update(Bills bill) {
        String sql = "UPDATE Bills SET Username = ?, CardId = ?, Checkin = ?, Checkout = ?, Status = ? WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setString(1, bill.getUsername());
            pre.setInt(2, bill.getCardId());
            pre.setTimestamp(3, new java.sql.Timestamp(bill.getCheckin().getTime()));
            if (bill.getCheckout() != null) {
                pre.setTimestamp(4, new java.sql.Timestamp(bill.getCheckout().getTime()));
            } else {
                pre.setNull(4, java.sql.Types.TIMESTAMP);
            }
            pre.setInt(5, bill.getStatus());
            pre.setLong(6, bill.getId());

            return pre.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a bill by ID
    public boolean delete(Bills bill) {
        String sql = "DELETE FROM Bills WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setLong(1, bill.getId());

            int row = pre.executeUpdate();
            System.out.println("Xóa thành công: " + row + " dòng.");
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a bill by ID (alternative method using ID directly)
    public boolean deleteById(long id) {
        String sql = "DELETE FROM Bills WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setLong(1, id);

            int row = pre.executeUpdate();
            System.out.println("Xóa thành công: " + row + " dòng.");
            return row > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Find all bills
    public List<Bills> findAll() {
        String sql = "SELECT * FROM Bills";
        
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            List<Bills> list = new ArrayList<>();
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                Bills bill = new Bills();
                bill.setId(rs.getLong("Id"));
                bill.setUsername(rs.getString("Username"));
                bill.setCardId(rs.getInt("CardId"));
                bill.setCheckin(rs.getTimestamp("Checkin"));
                bill.setCheckout(rs.getTimestamp("Checkout"));
                bill.setStatus(rs.getInt("Status"));
                list.add(bill);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Find a bill by ID
    public Bills findById(long id) {
        String sql = "SELECT * FROM Bills WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setLong(1, id);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                Bills bill = new Bills();
                bill.setId(rs.getLong("Id"));
                bill.setUsername(rs.getString("Username"));
                bill.setCardId(rs.getInt("CardId"));
                bill.setCheckin(rs.getTimestamp("Checkin"));
                bill.setCheckout(rs.getTimestamp("Checkout"));
                bill.setStatus(rs.getInt("Status"));
                return bill;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Check if a bill exists by ID
    public boolean checkBillExists(long id) {
        String sql = "SELECT COUNT(*) FROM Bills WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setLong(1, id);
            System.out.println("Đang kiểm tra bill ID: " + id); // DEBUG
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Số lượng bill trùng: " + count); // DEBUG
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
     public boolean checkCardIdExists(int cardId) {
        String sql = "SELECT COUNT(*) FROM Cards WHERE Id = ?";
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setInt(1, cardId);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
     public List<Bills> findByTimeFrame(String option) {
    String sql = "SELECT * FROM Bills WHERE ";
    switch (option) {
        case "Hôm nay":
            sql += "CAST(Checkin AS DATE) = CAST(GETDATE() AS DATE)";
            break;
        case "Tuần này":
            sql += "DATEPART(WEEK, Checkin) = DATEPART(WEEK, GETDATE()) AND YEAR(Checkin) = YEAR(GETDATE())";
            break;
        case "Tháng này":
            sql += "MONTH(Checkin) = MONTH(GETDATE()) AND YEAR(Checkin) = YEAR(GETDATE())";
            break;
        case "Năm nay":
            sql += "YEAR(Checkin) = YEAR(GETDATE())";
            break;
        default:
            return new ArrayList<>();
    }

    try (Connection con = DataConnection.open(); PreparedStatement pre = con.prepareStatement(sql)) {
        List<Bills> list = new ArrayList<>();
        ResultSet rs = pre.executeQuery();
        while (rs.next()) {
            Bills bill = new Bills();
            bill.setId(rs.getLong("Id"));
            bill.setUsername(rs.getString("Username"));
            bill.setCardId(rs.getInt("CardId"));
            bill.setCheckin(rs.getTimestamp("Checkin"));
            bill.setCheckout(rs.getTimestamp("Checkout"));
            bill.setStatus(rs.getInt("Status"));
            list.add(bill);
        }
        return list;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
     private List<Bills> getBillsBySql(String sql) {
    List<Bills> list = new ArrayList<>();
    try (Connection con = DataConnection.open(); PreparedStatement pre = con.prepareStatement(sql)) {
        ResultSet rs = pre.executeQuery();
        while (rs.next()) {
            Bills bill = new Bills();
            bill.setId(rs.getLong("Id"));
            bill.setUsername(rs.getString("Username"));
            bill.setCardId(rs.getInt("CardId"));
            bill.setCheckin(rs.getTimestamp("Checkin"));
            bill.setCheckout(rs.getTimestamp("Checkout"));
            bill.setStatus(rs.getInt("Status"));
            list.add(bill);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

public List<Bills> findToday() {
    String sql = "SELECT * FROM Bills WHERE CONVERT(DATE, Checkin) = CONVERT(DATE, GETDATE())";
    return getBillsBySql(sql);
}
public List<Bills> findThisWeek() {
    String sql = "SELECT * FROM Bills WHERE DATEPART(WEEK, Checkin) = DATEPART(WEEK, GETDATE()) AND YEAR(Checkin) = YEAR(GETDATE())";
    return getBillsBySql(sql);
}

public List<Bills> findThisMonth() {
    String sql = "SELECT * FROM Bills WHERE MONTH(Checkin) = MONTH(GETDATE()) AND YEAR(Checkin) = YEAR(GETDATE())";
    return getBillsBySql(sql);
}

public List<Bills> findThisYear() {
    String sql = "SELECT * FROM Bills WHERE YEAR(Checkin) = YEAR(GETDATE())";
    return getBillsBySql(sql);
}

public List<Bills> findByDateRange(Date fromDate, Date toDate) {
    String sql = "SELECT * FROM Bills WHERE Checkin BETWEEN ? AND ?";
    try (Connection con = DataConnection.open(); PreparedStatement pre = con.prepareStatement(sql)) {
        pre.setTimestamp(1, new java.sql.Timestamp(fromDate.getTime()));
        pre.setTimestamp(2, new java.sql.Timestamp(toDate.getTime()));
        List<Bills> list = new ArrayList<>();
        ResultSet rs = pre.executeQuery();
        while (rs.next()) {
            Bills bill = new Bills();
            bill.setId(rs.getLong("Id"));
            bill.setUsername(rs.getString("Username"));
            bill.setCardId(rs.getInt("CardId"));
            bill.setCheckin(rs.getTimestamp("Checkin"));
            bill.setCheckout(rs.getTimestamp("Checkout"));
            bill.setStatus(rs.getInt("Status"));
            list.add(bill);
        }
        return list;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return new ArrayList<>();
}
}