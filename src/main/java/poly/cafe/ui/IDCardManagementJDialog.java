/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import poly.cafe.entity.Cards;
import poly.cafe.dao.IDCardManagementDao;
import java.util.List;
import javax.swing.JOptionPane;
import poly.cafe.dao.AccountManagementDao;
import poly.cafe.entity.Users;
/**
 *
 * @author Home
 */
public class IDCardManagementJDialog extends javax.swing.JDialog {
DefaultTableModel tableModel;
JTable table;
    /**
     * Creates new form IDCardManagementJDialog
     */
    public IDCardManagementJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        initTable();
        loaddatatotable();
    }
    private void initTable() {
    tableModel = new DefaultTableModel(new String[]{"Mã thẻ","Trạng thái","Chọn"}, 0) {
        Class<?>[] types = new Class<?>[]{String.class, String.class , Boolean.class};
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return types[columnIndex];
        }
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 2;
        }
    };
    jTable1.setModel(tableModel);
}
    public void loaddatatotable() {
    tableModel.setRowCount(0);
    IDCardManagementDao dao = new IDCardManagementDao();
    List<Cards> list = dao.findAll();
    for (Cards c : list) {

String statusText;
if (c.getStatus() == 1) {
    statusText = "Đang hoạt động";
} else if (c.getStatus() == 2) {
    statusText = "Lỗi";
} else if (c.getStatus() == 3) {
    statusText = "Thất bại";
} else {
    statusText = "Không xác định";
}
 

        tableModel.addRow(new Object[]{
            c.getId(),
            statusText,
            false
        });
    }
}
  private void Them() {
    IDCardManagementDao dao = new IDCardManagementDao();

    String idStr = txtMaThe.getText().trim(); // Mã thẻ từ ô nhập txtMaThe
    if (idStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập mã thẻ!");
        return;
    }

    int id;
    try {
        id = Integer.parseInt(idStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Mã thẻ phải là số!");
        return;
    }

    int status = 0;
    if (rdoHoatDong.isSelected()) {
        status = 1;
    } else if (rdoLoi.isSelected()) {
        status = 2;
    } else if (rdoThatBai.isSelected()) {
        status = 3;
    } else {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái!");
        return;
    }

    if (dao.checkUsernameExists(idStr)) {
        JOptionPane.showMessageDialog(this, "Mã thẻ đã tồn tại!");
        return;
    }

    Cards card = new Cards();
    card.setId(id);
    card.setStatus(status);

    if (dao.insert(card)) {
        JOptionPane.showMessageDialog(this, "Thêm thẻ thành công!");
        loaddatatotable(); // Load lại bảng dữ liệu nếu có
    } else {
        JOptionPane.showMessageDialog(this, "Thêm thẻ thất bại!");
    }
}



private void Xoa() {
    String idText = txtMaThe.getText().trim();
    if (idText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã thẻ để xóa!");
        return;
    }

    try {
        int id = Integer.parseInt(idText);

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa thẻ có ID: " + id + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        IDCardManagementDao dao = new IDCardManagementDao();
        Cards c = new Cards();
        c.setId(id);

        if (dao.delete(c)) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            loaddatatotable();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa thất bại! Kiểm tra lại Mã thẻ.");
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Mã thẻ phải là số hợp lệ!");
    }
}


private void Sua() {
    String idText = txtMaThe.getText().trim();
    if (idText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã thẻ để sửa!");
        return;
    }

    try {
        int id = Integer.parseInt(idText);
        int status;

        if (rdoHoatDong.isSelected()) {
            status = 1;
        } else if (rdoLoi.isSelected()) {
            status = 0;
        } else if (rdoThatBai.isSelected()) {
            status = 2;
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái!");
            return;
        }

        Cards c = new Cards();
        c.setId(id);
        c.setStatus(status);

        IDCardManagementDao dao = new IDCardManagementDao();
        if (dao.update(c)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loaddatatotable();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại! Kiểm tra lại Mã thẻ.");
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Mã thẻ phải là số hợp lệ!");
    }
}
private void updateFormFromRow(int row) {
    if (row >= 0 && row < jTable1.getRowCount()) {
        // Lấy ID (cột 0)
        txtMaThe.setText(jTable1.getValueAt(row, 0).toString());

        // Lấy trạng thái (cột 1)
        String status = (String) jTable1.getValueAt(row, 1);
        rdoHoatDong.setSelected("Đang hoạt động".equals(status));
        rdoLoi.setSelected("Lỗi".equals(status));
        rdoThatBai.setSelected("Thất bại".equals(status));
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
public void BoChonTatCa() {
    for (int row = 0; row < jTable1.getRowCount(); row++) {
        jTable1.setValueAt(false, row, 2); // cột 2 checkbox
    }
}

public void ChonTatCa() {
    for (int row = 0; row < jTable1.getRowCount(); row++) {
        jTable1.setValueAt(true, row, 2);
    }
}
public void xoaCacDongDaChon() {
    IDCardManagementDao dao = new IDCardManagementDao();
    StringBuilder sb = new StringBuilder();
    int count = 0;

    for (int i = 0; i < jTable1.getRowCount(); i++) {
        Boolean checked = (Boolean) jTable1.getValueAt(i, 2); // checkbox cột 2
        if (checked != null && checked) {
            String id = jTable1.getValueAt(i, 0).toString();
            if (dao.deleteById(id)) {  // deleteById nhận String hoặc int tùy bạn implement
                sb.append("- ID: ").append(id).append("\n");
                count++;
            }
        }
    }

    if (count > 0) {
        JOptionPane.showMessageDialog(this,
            "Đã xóa thành công " + count + " thẻ:\n" + sb.toString(),
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        loaddatatotable(); // load lại bảng
    } else {
        JOptionPane.showMessageDialog(this,
            "Không có dòng nào được chọn để xóa.",
            "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
}


    private void LamMoi(){
    txtMaThe.setText(null);
    buttonGroup1.clearSelection();
    loaddatatotable();
       }

    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        BtnChonTatCa = new javax.swing.JButton();
        BtnBoChonTatCa = new javax.swing.JButton();
        BtnXoaTatCaCacMuc = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtMaThe = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        rdoHoatDong = new javax.swing.JRadioButton();
        rdoLoi = new javax.swing.JRadioButton();
        rdoThatBai = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        BtnTaoMoi = new javax.swing.JButton();
        BtnCapNhap = new javax.swing.JButton();
        BtnXoa = new javax.swing.JButton();
        BtnLamMoi = new javax.swing.JButton();
        BtnNext = new javax.swing.JButton();
        BtnBack = new javax.swing.JButton();
        BtnFIrst = new javax.swing.JButton();
        BtnLast = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý thẻ định danh");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mã thẻ", "Trạng thái", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(2).setResizable(false);
        }

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

        BtnXoaTatCaCacMuc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/trash.png"))); // NOI18N
        BtnXoaTatCaCacMuc.setText("xóa các mục chọn");
        BtnXoaTatCaCacMuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnXoaTatCaCacMucActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnChonTatCa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnBoChonTatCa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnXoaTatCaCacMuc)
                .addGap(13, 13, 13))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnChonTatCa)
                    .addComponent(BtnBoChonTatCa)
                    .addComponent(BtnXoaTatCaCacMuc))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DANH SÁCH", jPanel1);

        jLabel1.setText("Mã thẻ");

        jLabel2.setText("Trạng thái");

        buttonGroup1.add(rdoHoatDong);
        rdoHoatDong.setText("Đang hoạt động");

        buttonGroup1.add(rdoLoi);
        rdoLoi.setText("Lỗi");

        buttonGroup1.add(rdoThatBai);
        rdoThatBai.setText("Thất bại");

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        BtnTaoMoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        BtnTaoMoi.setText("Tạo mới");
        BtnTaoMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTaoMoiActionPerformed(evt);
            }
        });

        BtnCapNhap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/edit.png"))); // NOI18N
        BtnCapNhap.setText("Cập nhập");
        BtnCapNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCapNhapActionPerformed(evt);
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

        BtnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/next.png"))); // NOI18N
        BtnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnNextActionPerformed(evt);
            }
        });

        BtnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back.png"))); // NOI18N
        BtnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBackActionPerformed(evt);
            }
        });

        BtnFIrst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Fisrt.png"))); // NOI18N
        BtnFIrst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnFIrstActionPerformed(evt);
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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BtnTaoMoi, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                    .addComponent(BtnXoa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BtnCapNhap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnLamMoi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnBack, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnFIrst, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnLast, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BtnCapNhap)
                        .addComponent(BtnTaoMoi))
                    .addComponent(BtnNext)
                    .addComponent(BtnBack))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BtnXoa)
                        .addComponent(BtnLamMoi))
                    .addComponent(BtnFIrst)
                    .addComponent(BtnLast))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaThe, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(rdoHoatDong, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoLoi, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoThatBai, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaThe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoHoatDong)
                    .addComponent(rdoLoi)
                    .addComponent(rdoThatBai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 226, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("BIỂU MẪU", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLamMoiActionPerformed
        LamMoi();
    }//GEN-LAST:event_BtnLamMoiActionPerformed
        // TDO add your handling cOode here:

    private void BtnFIrstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnFIrstActionPerformed
        firstRow();
    }//GEN-LAST:event_BtnFIrstActionPerformed

    private void BtnBoChonTatCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBoChonTatCaActionPerformed
     BoChonTatCa();
    }//GEN-LAST:event_BtnBoChonTatCaActionPerformed

    private void BtnCapNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCapNhapActionPerformed
        Sua();
    }//GEN-LAST:event_BtnCapNhapActionPerformed

    private void BtnTaoMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTaoMoiActionPerformed
        Them();
    }//GEN-LAST:event_BtnTaoMoiActionPerformed

    private void BtnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnXoaActionPerformed
        Xoa();
    }//GEN-LAST:event_BtnXoaActionPerformed

    private void BtnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBackActionPerformed
        backRow();
    }//GEN-LAST:event_BtnBackActionPerformed

    private void BtnChonTatCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnChonTatCaActionPerformed
        ChonTatCa();
    }//GEN-LAST:event_BtnChonTatCaActionPerformed

    private void BtnXoaTatCaCacMucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnXoaTatCaCacMucActionPerformed
        xoaCacDongDaChon();
    }//GEN-LAST:event_BtnXoaTatCaCacMucActionPerformed

    private void BtnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnNextActionPerformed
        nextRow();
    }//GEN-LAST:event_BtnNextActionPerformed

    private void BtnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLastActionPerformed
        lastRow();
    }//GEN-LAST:event_BtnLastActionPerformed

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
            java.util.logging.Logger.getLogger(IDCardManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IDCardManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IDCardManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IDCardManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                IDCardManagementJDialog dialog = new IDCardManagementJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton BtnBack;
    private javax.swing.JButton BtnBoChonTatCa;
    private javax.swing.JButton BtnCapNhap;
    private javax.swing.JButton BtnChonTatCa;
    private javax.swing.JButton BtnFIrst;
    private javax.swing.JButton BtnLamMoi;
    private javax.swing.JButton BtnLast;
    private javax.swing.JButton BtnNext;
    private javax.swing.JButton BtnTaoMoi;
    private javax.swing.JButton BtnXoa;
    private javax.swing.JButton BtnXoaTatCaCacMuc;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JRadioButton rdoHoatDong;
    private javax.swing.JRadioButton rdoLoi;
    private javax.swing.JRadioButton rdoThatBai;
    private javax.swing.JTextField txtMaThe;
    // End of variables declaration//GEN-END:variables
}
