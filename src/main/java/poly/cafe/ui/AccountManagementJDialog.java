/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poly.cafe.ui;
import javax.swing.*;
import java.io.File;
import java.awt.Image;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.AccountManagementDao;
import poly.cafe.entity.Users;

/**
 *
 * @author Admin
 */
public class AccountManagementJDialog extends javax.swing.JDialog {
DefaultTableModel tableModel;
JTable table;
        
    /**
     * Creates new form AccountManagement
     */
    public AccountManagementJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initTable();
        loaddatatotable();
        setupEventListeners();
        setLocationRelativeTo(null);
    }
    //Username,Password,Enabled,Fullname,Photo,Manager
    private void initTable() {
        // Thiết lập bảng jTable1 với các cột và kiểu dữ liệu tương ứng
        // Mỗi cột tương ứng với một kiểu dữ liệu
        // Xác định kiểu dữ liệu của từng cột (để hiển thị checkbox ở cột cuối)
        // Chỉ cho phép chỉnh sửa cột cuối cùng (checkbox "Chọn")
        //mấy cái này đều cần cái Dao.java hỗ trợ để làm cho dễ
    tableModel = new DefaultTableModel(new String[]{"Tên đăng nhập", "Mật khẩu", "Trạng thái", "Họ và tên", "Ảnh", "Vai trò", "Chọn"}, 0) {
        Class<?>[] types = new Class<?>[]{String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class};
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return types[columnIndex];
        }
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 6;
        }
    };
    jTable1.setModel(tableModel);
    // Gán mô hình dữ liệu cho bảng
}
//load thông tin từ cơ sở dữ liệu lên bảng 
   public void loaddatatotable() {
    tableModel.setRowCount(0);
    AccountManagementDao dao = new AccountManagementDao();
    List<Users> list = dao.findAll();
    for (Users nv : list) {
        tableModel.addRow(new Object[]{
            nv.getUsername(),
            nv.getPassword(),
            nv.isEnabled() ? "Hoạt động" : "Vô hiệu",
            nv.getFullname(),
            nv.getPhoto(),
            nv.isManager() ? "Quản lý" : "Nhân viên",
            false
        });
    }
}
   // code này hình là để tự động gắn hàm mình viết vào nút mà không cần gắn thủ công thì phải,
   private void setupEventListeners() {
    lblPhoto.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            chonAnh();
        }
    });
}
    // Biến toàn cục lưu đường dẫn ảnh đã chọn
private String photoPath = "";

private void chonAnh() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "png", "jpeg", "gif"));
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        photoPath = selectedFile.getName(); // Lấy tên file (cmm.png)
        lblPhoto.setText(photoPath); // Hiển thị tên file
        // Hiển thị ảnh
        ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
        Image image = imageIcon.getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH);
        lblPhoto.setIcon(new ImageIcon(image));
    }
}

private void Them() {
    AccountManagementDao dao = new AccountManagementDao();
    String username = txtTenDangNhap.getText().trim();
    String password = txtMatKhau.getText().trim();
    String confirmPassword = txtXacNhanMatKhau.getText().trim();
    String fullname = txtHoVaTen.getText().trim();

    if (username.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
        return;
    }

if (photoPath == null || photoPath.isEmpty()) {
    JOptionPane.showMessageDialog(this, "Vui lòng chọn ảnh đại diện!");
    return;
}
if (photoPath.length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên file hình ảnh không được vượt quá 50 ký tự!");
            return;
        }
   


if (dao.checkUsernameExists(username)) {
    JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!");
    return;
}


    if (!password.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
        return;
    }

    Users user = new Users();
    user.setUsername(username);
    user.setPassword(password);
    user.setEnabled(rdoHoatDong.isSelected());
    user.setFullname(fullname);
    user.setPhoto(photoPath.isEmpty() ? null : photoPath); // Lưu tên file
    user.setManager(rdoQuanLy.isSelected());

    if (dao.insert(user)) {
        JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
        loaddatatotable();
       
    } else {
        JOptionPane.showMessageDialog(this, "Thêm tài khoản thất bại!");
    }
}


    void Xoa() {
        String username = txtTenDangNhap.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa Tài khoản với tên đăng nhập: " + username + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Users u = new Users();
            u.setUsername(username);

            AccountManagementDao deleteAcc = new AccountManagementDao();
            boolean result = deleteAcc.delete(u);

            if (result) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập không tồn tại, xóa thắt bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        loaddatatotable();
    }

    private void Sua() {
    AccountManagementDao dao = new AccountManagementDao();
    String username = txtTenDangNhap.getText().trim();
    String password = txtMatKhau.getText().trim();
    String confirmPassword = txtXacNhanMatKhau.getText().trim();
    String fullname = txtHoVaTen.getText().trim();

    if (username.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
        return;
    }

    if (!rdoQuanLy.isSelected() && (photoPath == null || photoPath.isEmpty())) {
        JOptionPane.showMessageDialog(this, "Nhân viên phải có ảnh đại diện!");
        return;
    }

    if (!password.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
        return;
    }

    Users user = new Users();
    user.setUsername(username);
    user.setPassword(password);
    user.setEnabled(rdoHoatDong.isSelected());
    user.setFullname(fullname);
    user.setPhoto(photoPath.isEmpty() ? null : photoPath);
    user.setManager(rdoQuanLy.isSelected());

    if (dao.update(user)) {
        JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
        loaddatatotable();
    } else {
        JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thất bại!");
    }
}


private void updateFormFromRow(int row) {
    if (row >= 0 && row < jTable1.getRowCount()) {
        txtTenDangNhap.setText((String) jTable1.getValueAt(row, 0)); // username
        txtMatKhau.setText((String) jTable1.getValueAt(row, 1)); // password
        txtXacNhanMatKhau.setText((String) jTable1.getValueAt(row, 1)); // password
        txtHoVaTen.setText((String) jTable1.getValueAt(row, 3)); // fullname
        photoPath = (String) jTable1.getValueAt(row, 4); // photo
        lblPhoto.setText(photoPath != null ? photoPath : ""); // Hiển thị tên file
        lblPhoto.setIcon(new ImageIcon("C:\\Users\\Home\\Documents\\NetBeansProjects\\PolyCafe\\src\\main\\resources\\img\\NiggaCat.png"));

        String status = (String) jTable1.getValueAt(row, 2); // enabled
        rdoHoatDong.setSelected("Hoạt động".equals(status));
        rdoTamDung.setSelected("Vô hiệu".equals(status));

        String role = (String) jTable1.getValueAt(row, 5); // manager
        rdoQuanLy.setSelected("Quản lý".equals(role));
        rdoNhanVien.setSelected("Nhân viên".equals(role));
    }
}
public void firstRow() {
    if (jTable1.getRowCount() > 0) {
        jTable1.setRowSelectionInterval(0, 0);
        jTable1.scrollRectToVisible(jTable1.getCellRect(0, 0, true));
        updateFormFromRow(0);
    }
}

public void lastRow() {
    int lastIndex = jTable1.getRowCount() - 1;
    if (lastIndex >= 0) {
        jTable1.setRowSelectionInterval(lastIndex, lastIndex);
        jTable1.scrollRectToVisible(jTable1.getCellRect(lastIndex, 0, true));
        updateFormFromRow(lastIndex);
    }
}

public void nextRow() {
    int current = jTable1.getSelectedRow();
    int next = current + 1;
    if (next < jTable1.getRowCount()) {
        jTable1.setRowSelectionInterval(next, next);
        jTable1.scrollRectToVisible(jTable1.getCellRect(next, 0, true));
        updateFormFromRow(next);
    }
}

public void backRow() {
    int current = jTable1.getSelectedRow();
    int back = current - 1;
    if (back >= 0) {
        jTable1.setRowSelectionInterval(back, back);
        jTable1.scrollRectToVisible(jTable1.getCellRect(back, 0, true));
        updateFormFromRow(back);
    }
}
public void BoChonTatCa(){
    for (int row = 0; row < jTable1.getRowCount(); row++) {
        jTable1.setValueAt(false, row, 6);
}
}
public void ChonTatCa(){   
    for (int row = 0; row < jTable1.getRowCount(); row++) {
        jTable1.setValueAt(true, row, 6); // Cột 7 là checkbox
    } 
    }
public void xoaCacDongDaChon() {
    AccountManagementDao dao = new AccountManagementDao();
    StringBuilder sb = new StringBuilder();
    int count = 0;

    for (int i = 0; i < jTable1.getRowCount(); i++) {
        Boolean checked = (Boolean) jTable1.getValueAt(i, 6); // Cột checkbox
        if (checked != null && checked) {
            String username = jTable1.getValueAt(i, 0).toString(); // Cột Username
            if (dao.deleteByUsername(username)) {
                sb.append("- ").append(username).append("\n");
                count++;
            }
        }
    }

    if (count > 0) {
        JOptionPane.showMessageDialog(this,
            "Đã xóa thành công " + count + " tài khoản:\n" + sb.toString(),
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        loaddatatotable(); // Cập nhật lại bảng sau khi xóa
    } else {
        JOptionPane.showMessageDialog(this,
            "Không có dòng nào được chọn để xóa.",
            "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
}
//gay rizz




// Gán sự kiện cho các nút (giả sử bạn có các nút btnFirst, btnLast, btnNext, btnBack)







    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        AccTable = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        BtnChonTatCa = new javax.swing.JButton();
        BtnBoChonTatCa = new javax.swing.JButton();
        XoaCacMucDuocChon = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblPhoto = new javax.swing.JLabel();
        txtTenDangNhap = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtHoVaTen = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtMatKhau = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtXacNhanMatKhau = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        rdoQuanLy = new javax.swing.JRadioButton();
        rdoNhanVien = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        rdoHoatDong = new javax.swing.JRadioButton();
        rdoTamDung = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        BtnThem = new javax.swing.JButton();
        BtncapNhap = new javax.swing.JButton();
        BtnXoa = new javax.swing.JButton();
        BtnLamMoi = new javax.swing.JButton();
        BtnBack = new javax.swing.JButton();
        BtnNext = new javax.swing.JButton();
        BtnFirst = new javax.swing.JButton();
        BtnLast = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý tài khoản");

        AccTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AccTableMouseClicked(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Tên đăng nhập", "Mật khẩu", "Họ và tên", "Hình ảnh", "Vai trò", "Trạng thái", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        AccTable.setViewportView(jTable1);

        BtnChonTatCa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/accept.png"))); // NOI18N
        BtnChonTatCa.setText("Chọn tất cả");
        BtnChonTatCa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnChonTatCaActionPerformed(evt);
            }
        });

        BtnBoChonTatCa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/delete.png"))); // NOI18N
        BtnBoChonTatCa.setText("Bỏ chọn tất cả");
        BtnBoChonTatCa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBoChonTatCaActionPerformed(evt);
            }
        });

        XoaCacMucDuocChon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/delete.png"))); // NOI18N
        XoaCacMucDuocChon.setText("Xóa các mục chọn");
        XoaCacMucDuocChon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XoaCacMucDuocChonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(AccTable, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(BtnChonTatCa)
                        .addGap(40, 40, 40)
                        .addComponent(BtnBoChonTatCa)
                        .addGap(30, 30, 30)
                        .addComponent(XoaCacMucDuocChon)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(AccTable, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnChonTatCa)
                    .addComponent(BtnBoChonTatCa)
                    .addComponent(XoaCacMucDuocChon))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("DANH SÁCH", jPanel2);

        lblPhoto.setFont(new java.awt.Font("Segoe UI", 0, 1)); // NOI18N
        lblPhoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/NiggaCat.png"))); // NOI18N
        lblPhoto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 51)));
        lblPhoto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblPhoto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPhotoMouseClicked(evt);
            }
        });

        jLabel2.setText("Tên đăng nhập ");

        jLabel3.setText("Họ và tên");

        jLabel4.setText("Mật khẩu");

        jLabel5.setText("Xác nhận mật khẩu");

        jLabel6.setText("Vai trò");

        buttonGroup1.add(rdoQuanLy);
        rdoQuanLy.setText("Quản lý");

        buttonGroup1.add(rdoNhanVien);
        rdoNhanVien.setText("Nhân viên");

        jLabel7.setText("Trạng thái");

        buttonGroup2.add(rdoHoatDong);
        rdoHoatDong.setText("Hoạt động");

        buttonGroup2.add(rdoTamDung);
        rdoTamDung.setText("Tạm dừng");

        BtnThem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        BtnThem.setText("Tạo mới");
        BtnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnThemActionPerformed(evt);
            }
        });

        BtncapNhap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/edit.png"))); // NOI18N
        BtncapNhap.setText("Cập nhật");
        BtncapNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtncapNhapActionPerformed(evt);
            }
        });

        BtnXoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/trash.png"))); // NOI18N
        BtnXoa.setText("Xóa");
        BtnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnXoaActionPerformed(evt);
            }
        });

        BtnLamMoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/refresh.png"))); // NOI18N
        BtnLamMoi.setText("Làm mới");
        BtnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLamMoiActionPerformed(evt);
            }
        });

        BtnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back.png"))); // NOI18N
        BtnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBackActionPerformed(evt);
            }
        });

        BtnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/next.png"))); // NOI18N
        BtnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnNextActionPerformed(evt);
            }
        });

        BtnFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Fisrt.png"))); // NOI18N
        BtnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnFirstActionPerformed(evt);
            }
        });

        BtnLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/last.png"))); // NOI18N
        BtnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLastActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblPhoto)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(rdoQuanLy)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdoNhanVien))
                    .addComponent(txtMatKhau, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(txtTenDangNhap))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(txtXacNhanMatKhau, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(txtHoVaTen))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(rdoHoatDong)
                                .addGap(18, 18, 18)
                                .addComponent(rdoTamDung)))
                        .addContainerGap(55, Short.MAX_VALUE))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(BtnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(BtnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BtncapNhap))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jSeparator1)
                                .addGap(98, 98, 98)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(BtnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BtnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(BtnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BtnLast, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(37, 37, 37))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTenDangNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHoVaTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtXacNhanMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(lblPhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoQuanLy)
                    .addComponent(rdoNhanVien)
                    .addComponent(rdoHoatDong)
                    .addComponent(rdoTamDung))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BtnBack, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(BtnNext, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BtnLast)
                            .addComponent(BtnFirst)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(BtncapNhap)
                                    .addComponent(BtnThem)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(BtnXoa)
                                    .addComponent(BtnLamMoi))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(151, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("BIỂU MẪU", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBackActionPerformed
       backRow();
    }//GEN-LAST:event_BtnBackActionPerformed

    private void lblPhotoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPhotoMouseClicked
// chonAnh();
    }//GEN-LAST:event_lblPhotoMouseClicked

    private void BtnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLamMoiActionPerformed
txtHoVaTen.setText(null);
txtMatKhau.setText(null);
txtTenDangNhap.setText(null);
txtXacNhanMatKhau.setText(null);
buttonGroup1.clearSelection();
buttonGroup2.clearSelection();
lblPhoto.setIcon(new ImageIcon("C:\\Users\\Home\\Documents\\NetBeansProjects\\PolyCafe\\src\\main\\resources\\img\\NiggaCat.png"));
    }//GEN-LAST:event_BtnLamMoiActionPerformed

    private void BtnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnThemActionPerformed
Them();
    }//GEN-LAST:event_BtnThemActionPerformed

    private void BtncapNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtncapNhapActionPerformed
Sua();
    }//GEN-LAST:event_BtncapNhapActionPerformed

    private void BtnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnXoaActionPerformed
Xoa();
    }//GEN-LAST:event_BtnXoaActionPerformed

    private void AccTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AccTableMouseClicked
//FindByClick();
    }//GEN-LAST:event_AccTableMouseClicked

    private void BtnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnFirstActionPerformed
        firstRow();
    }//GEN-LAST:event_BtnFirstActionPerformed

    private void BtnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLastActionPerformed
        lastRow();
    }//GEN-LAST:event_BtnLastActionPerformed

    private void BtnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnNextActionPerformed
        nextRow();
    }//GEN-LAST:event_BtnNextActionPerformed

    private void XoaCacMucDuocChonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XoaCacMucDuocChonActionPerformed
        xoaCacDongDaChon();
    }//GEN-LAST:event_XoaCacMucDuocChonActionPerformed

    private void BtnChonTatCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnChonTatCaActionPerformed
       ChonTatCa();
    }//GEN-LAST:event_BtnChonTatCaActionPerformed

    private void BtnBoChonTatCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBoChonTatCaActionPerformed
        BoChonTatCa();
    }//GEN-LAST:event_BtnBoChonTatCaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AccountManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AccountManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AccountManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AccountManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AccountManagementJDialog dialog = new AccountManagementJDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane AccTable;
    private javax.swing.JButton BtnBack;
    private javax.swing.JButton BtnBoChonTatCa;
    private javax.swing.JButton BtnChonTatCa;
    private javax.swing.JButton BtnFirst;
    private javax.swing.JButton BtnLamMoi;
    private javax.swing.JButton BtnLast;
    private javax.swing.JButton BtnNext;
    private javax.swing.JButton BtnThem;
    private javax.swing.JButton BtnXoa;
    private javax.swing.JButton BtncapNhap;
    private javax.swing.JButton XoaCacMucDuocChon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblPhoto;
    private javax.swing.JRadioButton rdoHoatDong;
    private javax.swing.JRadioButton rdoNhanVien;
    private javax.swing.JRadioButton rdoQuanLy;
    private javax.swing.JRadioButton rdoTamDung;
    private javax.swing.JTextField txtHoVaTen;
    private javax.swing.JTextField txtMatKhau;
    private javax.swing.JTextField txtTenDangNhap;
    private javax.swing.JTextField txtXacNhanMatKhau;
    // End of variables declaration//GEN-END:variables
}
