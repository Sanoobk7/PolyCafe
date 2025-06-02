/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao;

import poly.cafe.entity.Users;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.DataConnection;


/**
 *
 * @author Home
 */
public class AccountManagementDao {
    //Users
    //Username,Password,Enabled,Fullname,Photo,Manager
    public boolean insert(Users nv) {
        String sql = "insert into Users(Username,Password,Enabled,Fullname,Photo,Manager) values(?,?,?,?,?,?) ";
        try {
            Connection con = DataConnection.open();
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setString(1, nv.getUsername());
            pre.setString(2, nv.getPassword());
            pre.setBoolean(3, nv.isEnabled());
            pre.setString(4, nv.getFullname());
            pre.setString(5, nv.getPhoto());
            pre.setBoolean(6, nv.isManager());

            return pre.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Users nv) {
        //Users
        //Username,Password,Enabled,Fullname,Photo,Manager
        String sql = "UPDATE Users set Password = ?, Enabled = ?, Fullname = ?, Photo = ?, Manager = ? WHERE Username = ?";

        try {
            Connection con = DataConnection.open();
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setString(1, nv.getPassword());
            pre.setBoolean(2, nv.isEnabled());
            pre.setString(3, nv.getFullname());
            pre.setString(4, nv.getPhoto());
            pre.setBoolean(5, nv.isManager());
            pre.setString(6, nv.getUsername());
            return pre.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
//Users
//Username,Password,Enabled,Fullname,Photo,Manager
    public boolean delete(Users nv) {
        String sql = "DELETE FROM Users WHERE Username = ?";
        try (Connection con = DataConnection.open(); PreparedStatement preStm = con.prepareStatement(sql)) {

            preStm.setString(1, nv.getUsername());

            int row = preStm.executeUpdate();
            System.out.println("Xóa thành công: " + row + " dòng.");

            return row > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;

    }
//username,password,enabled,fullname,photo,manager
    public List<Users> findAll() {
        String sql = "SELECT * FROM Users";
        try {
            Connection con = DataConnection.open();
            PreparedStatement pre = con.prepareStatement(sql);
            List<Users> List = new ArrayList<>();
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                Users nv = new Users();
                nv.setUsername(rs.getString("username"));
                nv.setPassword(rs.getString("password"));
                nv.setEnabled(rs.getBoolean("enabled"));
                nv.setFullname(rs.getString("fullname"));
                nv.setPhoto(rs.getString("photo"));
                nv.setManager(rs.getBoolean("manager"));
                List.add(nv);
            }
            return List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
   public boolean checkUsernameExists(String username) {
    String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
    try (
        Connection con = DataConnection.open();
        PreparedStatement ps = con.prepareStatement(sql);
    ) {
        ps.setString(1, username);
        System.out.println("Đang kiểm tra username: " + username); // DEBUG

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
   public Users findByID(String username) {
    String sql = "SELECT * FROM Users WHERE Username = ?";
    try (
        Connection con = DataConnection.open();
        PreparedStatement ps = con.prepareStatement(sql);
    ) {
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Users user = new Users();
            user.setUsername(rs.getString("Username"));
            user.setPassword(rs.getString("Password"));
            user.setFullname(rs.getString("Fullname"));
            user.setPhoto(rs.getString("Photo"));
            user.setEnabled(rs.getBoolean("Enabled"));
            user.setManager(rs.getBoolean("Manager"));
            return user;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

   public boolean deleteByUsername(String username) {
    String sql = "DELETE FROM Users WHERE Username = ?";
    try (Connection con = DataConnection.open(); 
         PreparedStatement preStm = con.prepareStatement(sql)) {
        preStm.setString(1, username);
        int row = preStm.executeUpdate();
        return row > 0;
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    return false;
}




//    public NhanVien findByID(String MaNV) {
//        String sql = "select * from NhanVien where MaNV = ?";
//        try {
//            Connection con = DataConnection.open();
//            PreparedStatement preStm = con.prepareStatement(sql);
//            preStm.setString(1, MaNV);
//            ResultSet rs = preStm.executeQuery();
//            if (rs.next()) {
//                NhanVien nv = new NhanVien();
//                nv.setMaNV(rs.getString("MaNV"));
//                nv.setTenNV(rs.getString("TenNV"));
//                nv.setEmail(rs.getString("Email"));
//                nv.setSDT(rs.getString("SDT"));
//                nv.setGioiTinh(rs.getString("GioiTinh"));
//                nv.setDiaChi(rs.getString("DiaChi"));
//                nv.setNgaySinh(rs.getDate("NgaySinh"));
//                return nv;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }

//    public boolean checkMaNV(String maNV) {
//        String sql = "SELECT COUNT(*) FROM NhanVien WHERE MaNV = ?";
//        try (Connection conn = DataConnection.open(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, maNV);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                return rs.getInt(1) > 0; // Nếu COUNT(*) > 0 tức là đã có mã NV này
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//    
//    public List<NhanVien> findByMa (String ma){
//        String sql="Select * from NhanVien where MaNV like ?";
//        try {
//            Connection conn = DataConnection.open();
//            PreparedStatement p = conn.prepareStatement(sql);
//            p.setString(1, ma);
//            List<NhanVien> list = new ArrayList<>();
//            ResultSet rs = p.executeQuery();
//            while(rs.next()){
//                NhanVien nv = new NhanVien();
//                nv.setMaNV(rs.getString("MaNV"));
//                nv.setTenNV(rs.getString("TenNV"));
//                nv.setEmail(rs.getString("Email"));
//                nv.setSDT(rs.getString("SDT"));
//                nv.setGioiTinh(rs.getString("GioiTinh"));
//                nv.setDiaChi(rs.getString("DiaChi"));
//                nv.setNgaySinh(rs.getDate("NgaySinh"));
//                
//                list.add(nv);
//            }
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    
//    public List<NhanVien> findByTen (String ten){
//        String sql="Select * from NhanVien where TenNV like ?";
//        try {
//            Connection conn = DataConnection.open();
//            PreparedStatement p = conn.prepareStatement(sql);
//            p.setString(1, ten);
//            List<NhanVien> list = new ArrayList<>();
//            ResultSet rs = p.executeQuery();
//            while(rs.next()){
//                NhanVien nv = new NhanVien();
//                nv.setMaNV(rs.getString("MaNV"));
//                nv.setTenNV(rs.getString("TenNV"));
//                nv.setEmail(rs.getString("Email"));
//                nv.setSDT(rs.getString("SDT"));
//                nv.setGioiTinh(rs.getString("GioiTinh"));
//                nv.setDiaChi(rs.getString("DiaChi"));
//                nv.setNgaySinh(rs.getDate("NgaySinh"));
//                
//                list.add(nv);
//            }
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
