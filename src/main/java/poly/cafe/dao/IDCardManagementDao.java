/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.entity.Cards;

/**
 *
 * @author Home
 */
public class IDCardManagementDao {
          // Id,Status
    public boolean insert(Cards c) {
        String sql = "insert into Cards(Id,Status) values(?,?) ";
        try {
            Connection con = DataConnection.open();
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setInt(1, c.getId());
            pre.setInt(2, c.getStatus());


            return pre.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean update(Cards c) {
        // Id,Status
        String sql = "UPDATE Cards set Status = ? WHERE Id = ?";

        try {
            Connection con = DataConnection.open();
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setInt(1, c.getStatus());
            pre.setInt(2, c.getId());
            return pre.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
// Id,Status
    public boolean delete(Cards c) {
        String sql = "DELETE FROM Cards WHERE Id = ?";
        try (Connection con = DataConnection.open(); PreparedStatement preStm = con.prepareStatement(sql)) {

            preStm.setInt(1, c.getId());

            int row = preStm.executeUpdate();
            System.out.println("Xóa thành công: " + row + " dòng.");

            return row > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;

    }
//username,password,enabled,fullname,photo,manager
    public List<Cards> findAll() {
        String sql = "SELECT * FROM Cards";
        try {
            Connection con = DataConnection.open();
            PreparedStatement pre = con.prepareStatement(sql);
            List<Cards> List = new ArrayList<>();
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                Cards c = new Cards();
                c.setId(rs.getInt("id"));
                c.setStatus(rs.getInt("status"));
                
                List.add(c);
            }
            return List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // Id,Status
    //id,status
    // Cards
   public boolean checkUsernameExists(String id) {
    String sql = "SELECT COUNT(*) FROM Cards WHERE Id = ?";
    try (
        Connection con = DataConnection.open();
        PreparedStatement ps = con.prepareStatement(sql);
    ) {
        ps.setString(1, id);
        System.out.println("Đang kiểm tra Mã định danh: " + id); // DEBUG

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int count = rs.getInt(1);
            System.out.println("Số lượng tài khoản trùng: " + count); // DEBUG
            return count > 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
   // Id,Status
    //id,status
    // Cards
   public Cards findByID(String id) {
    String sql = "SELECT * FROM Cards WHERE Id = ?";
    try (
        Connection con = DataConnection.open();
        PreparedStatement ps = con.prepareStatement(sql);
    ) {
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Cards card = new Cards();
            card.setId(rs.getInt("id"));
            card.setId(rs.getInt("status"));
            return card;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
    // Id,Status
    //id,status
    // Cards
   public boolean deleteById(String id) {
    String sql = "DELETE FROM Cards WHERE Id = ?";
    try (Connection con = DataConnection.open(); 
         PreparedStatement preStm = con.prepareStatement(sql)) {
        preStm.setString(1, id);
        int row = preStm.executeUpdate();
        return row > 0;
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return false;
}
}
