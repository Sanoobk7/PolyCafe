/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui;

import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.BillDetailsDao;
import poly.cafe.dao.BillsManagerDao;
import poly.cafe.entity.BillDetails;
import poly.cafe.entity.Bills;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.util.Date;
import javax.swing.JTable;
import poly.cafe.dao.DrinkDAO;
import poly.cafe.dao.impl.DrinkDAOImpl;
import poly.cafe.entity.Drinks;
import poly.cafe.util.XDialog;

/**
 *
 * @author Home
 */
public class BillJDialog extends javax.swing.JDialog {
    private DefaultTableModel tableModel;
    private final Frame parentFrame;
    
     private Bills bill;

public void setBill(Bills bill) {
    this.bill = bill;
}

    /**
     * Creates new form BillJDialog
     */
    public BillJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        this.parentFrame = parent;
        initTable();
        loaddatatotable();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
            handleClose();
        }
    });
    }
    private void handleClose() {
    try {
        String maPhieuStr = txtMaPhieu.getText().trim();
        if (!maPhieuStr.isEmpty()) {
            long billId = Long.parseLong(maPhieuStr);
            BillsManagerDao billsDao = new BillsManagerDao();
            BillDetailsDao detailsDao = new BillDetailsDao();
            List<BillDetails> details = detailsDao.findByBillId(billId);
            
            // Nếu không có chi tiết hóa đơn, xóa phiếu
            if (details.isEmpty()) {
                if (billsDao.deleteById(billId)) {
                    JOptionPane.showMessageDialog(this, 
                        "Hóa đơn không có chi tiết, đã xóa phiếu thành công!", 
                        "Thông báo", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        // Đóng dialog
        dispose();
    } catch (Exception ex) {
        ex.printStackTrace();
        // Không hiển thị thông báo lỗi, chỉ đóng dialog
        dispose();
    }
}

    private void initTable() {
        tableModel = new DefaultTableModel(
            new String[]{"Mã chi tiết", "Mã hóa đơn", "Mã đồ uống", "Đơn giá", "Giảm giá", "Số lượng", "Thành tiền", "Chọn"},
            0
        ) {
            Class<?>[] types = new Class<?>[]{
                Long.class, Long.class, String.class, Double.class, Double.class, Integer.class, Double.class, Boolean.class
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Chỉ cho phép chỉnh sửa cột checkbox
            }
        };
        jTable1.setModel(tableModel);

        // Thêm renderer để hiển thị % cho cột Giảm giá (cột 4)
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    double discount = (Double) value;
                    setText(String.format("%.1f%%", discount));
                } else {
                    setText("");
                }
                return c;
            }
        });
    }

    public void loaddatatotable() {
        tableModel.setRowCount(0);
        BillDetailsDao dao = new BillDetailsDao();
        List<BillDetails> list = dao.findAll();
        for (BillDetails bd : list) {
            double total = bd.getUnitPrice() * (1 - bd.getDiscount() / 100) * bd.getQuantity();
            tableModel.addRow(new Object[]{
                bd.getId(),
                bd.getBillId(),
                bd.getDrinkId(),
                bd.getUnitPrice(),
                bd.getDiscount(),
                bd.getQuantity(),
                total,
                false
            });
        }
    }

    public void setBillDetails(Bills bill) {
        txtMaPhieu.setText(String.valueOf(bill.getId()));
        txtTheSo.setText(String.valueOf(bill.getCardId()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        txtThoiDiemDatHang.setText(sdf.format(bill.getCheckin()));
        txtThoiDiemThanhToan.setText(bill.getCheckout() != null ? sdf.format(bill.getCheckout()) : "");
        txtTrangThai.setText(getStatusText(bill.getStatus()));
        txtNhanVien.setText(bill.getUsername());
        loadBillDetails(bill.getId());
    }

    private String getStatusText(int status) {
        switch (status) {
            case 0: return "Đang phục vụ";
            case 1: return "Hoàn thành";
            case 2: return "Đã hủy";
            default: return "";
        }
    }

    public void loadBillDetails(long billId) {
        BillDetailsDao detailsDao = new BillDetailsDao();
        List<BillDetails> details = detailsDao.findByBillId(billId);
        tableModel.setRowCount(0);
        for (BillDetails detail : details) {
            //double total = 
            double total = detail.getUnitPrice() * (1 - detail.getDiscount() / 100) * detail.getQuantity();
            tableModel.addRow(new Object[]{
                detail.getId(),
                detail.getBillId(),
                detail.getDrinkId(),
                detail.getUnitPrice(),
                detail.getDiscount(),
                detail.getQuantity(),
                total,
                false
            });
        }
    }

    void ThemDoUong() {
        try {
            String maPhieu = txtMaPhieu.getText().trim();
            if (maPhieu.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hoặc tạo một hóa đơn trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            long billId;
            try {
                billId = Long.parseLong(maPhieu);
                if (billId <= 0) {
                    JOptionPane.showMessageDialog(this, "Mã hóa đơn phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra xem billId có tồn tại trong bảng Bills không
            BillsManagerDao billsDao = new BillsManagerDao();
            if (!billsDao.checkBillExists2(billId)) {
                JOptionPane.showMessageDialog(this, "Hóa đơn không tồn tại trong cơ sở dữ liệu! billId: " + billId, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Opening DrinkJDialog with billId: " + billId); // Log để debug
            DrinkJDialog drinkDialog = new DrinkJDialog(parentFrame, true, this, billId);
            drinkDialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi mở giao diện chọn đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    void CapNhapSoLuong() {
    int selectedRow = jTable1.getSelectedRow(); // Row is guaranteed to be selected due to mouse click listener

    // Lấy thông tin từ dòng được chọn
    long billDetailId = (Long) jTable1.getValueAt(selectedRow, 0); // Mã chi tiết hóa đơn
    long billId = (Long) jTable1.getValueAt(selectedRow, 1); // Mã hóa đơn
    String drinkId = (String) jTable1.getValueAt(selectedRow, 2); // Mã đồ uống
    double unitPrice = (Double) jTable1.getValueAt(selectedRow, 3); // Đơn giá
    double discount = (Double) jTable1.getValueAt(selectedRow, 4); // Giảm giá
    int currentQuantity = (Integer) jTable1.getValueAt(selectedRow, 5); // Số lượng hiện tại

    // Lấy tên đồ uống để hiển thị trong prompt
    DrinkDAO drinkDao = new DrinkDAOImpl();
    Drinks drink = drinkDao.findById(drinkId);
    String drinkName = drink != null ? drink.getName() : drinkId;

    // Hiển thị hộp thoại để nhập số lượng mới
    String quantityStr = XDialog.prompt("Nhập số lượng mới cho " + drinkName + " (hiện tại: " + currentQuantity + "):", "Cập nhật số lượng");
    if (quantityStr == null || quantityStr.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Bạn đã không thay đổi số lượng", "??", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int quantity;
    try {
        quantity = Integer.parseInt(quantityStr);
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Cập nhật số lượng trong cơ sở dữ liệu
        BillDetailsDao billDetailsDao = new BillDetailsDao();
        BillDetails billDetail = billDetailsDao.findById(billDetailId);
        if (billDetail == null) {
            JOptionPane.showMessageDialog(this, "Chi tiết hóa đơn không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        billDetail.setQuantity(quantity);
        if (billDetailsDao.update(billDetail)) {
            // Cập nhật lại bảng hiển thị
            double total = unitPrice * (1 - discount / 100) * quantity;
            jTable1.setValueAt(quantity, selectedRow, 5); // Cập nhật cột số lượng
            jTable1.setValueAt(total, selectedRow, 6); // Cập nhật cột thành tiền
            JOptionPane.showMessageDialog(this, "Cập nhật số lượng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật số lượng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật số lượng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

public void xoaCacDongDaChon() {
    BillDetailsDao dao = new BillDetailsDao();
    StringBuilder sb = new StringBuilder();
    int count = 0;

    for (int i = 0; i < jTable1.getRowCount(); i++) {
        Boolean checked = (Boolean) jTable1.getValueAt(i, 7); // Cột checkbox
        if (checked != null && checked) {
            long billDetailId = (Long) jTable1.getValueAt(i, 0); // Cột Id (mã chi tiết hóa đơn)
            String drinkId = (String) jTable1.getValueAt(i, 2); // Cột DrinkId (để hiển thị thông báo)
            if (dao.deleteById(billDetailId)) {
                sb.append("- Đồ uống: ").append(drinkId).append("\n");
                count++;
            }
        }
    }

    if (count > 0) {
        JOptionPane.showMessageDialog(this,
            "Đã xóa thành công " + count + " chi tiết hóa đơn:\n" + sb.toString(),
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        long billId = Long.parseLong(txtMaPhieu.getText().trim()); // Lấy billId từ txtMaPhieu
        loadBillDetails(billId); // Cập nhật lại bảng sau khi xóa
    } else {
        JOptionPane.showMessageDialog(this,
            "Không có dòng nào được chọn để xóa.",
            "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
}
void ThanhToan() {
    // Hiển thị hộp thoại xác nhận
    int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn thanh toán không?",
            "Xác nhận thanh toán",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            // Lấy mã hóa đơn từ txtMaPhieu
            String maPhieuStr = txtMaPhieu.getText().trim();
            if (maPhieuStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã hóa đơn không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long billId;
            try {
                billId = Long.parseLong(maPhieuStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy hóa đơn từ cơ sở dữ liệu
            BillsManagerDao billsDao = new BillsManagerDao();
            Bills bill = billsDao.findById(billId);
            if (bill == null) {
                JOptionPane.showMessageDialog(this, "Hóa đơn không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra trạng thái hóa đơn
            if (bill.getStatus() != 0) {
                JOptionPane.showMessageDialog(this, "Hóa đơn đã được thanh toán hoặc đã hủy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật thời gian thanh toán
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date currentTime = new Date();
            bill.setCheckout(currentTime);
            txtThoiDiemThanhToan.setText(sdf.format(currentTime));

            // Cập nhật trạng thái hóa đơn thành "Hoàn thành" (status = 1)
            bill.setStatus(1);
            txtTrangThai.setText(getStatusText(1)); // Cập nhật hiển thị trạng thái

            // Cập nhật hóa đơn trong cơ sở dữ liệu
            if (billsDao.update(bill)) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
void Cancel() {
    // Hiển thị hộp thoại xác nhận
    int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn muốn hủy phiếu bán hàng?",
            "Xác nhận hủy phiếu",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            // Lấy mã hóa đơn từ txtMaPhieu
            String maPhieuStr = txtMaPhieu.getText().trim();
            if (maPhieuStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã hóa đơn không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long billId;
            try {
                billId = Long.parseLong(maPhieuStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã hóa đơn không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Lấy hóa đơn từ cơ sở dữ liệu
            BillsManagerDao billsDao = new BillsManagerDao();
            Bills bill = billsDao.findById(billId);
            if (bill == null) {
                JOptionPane.showMessageDialog(this, "Hóa đơn không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra trạng thái hóa đơn
            if (bill.getStatus() == 2) {
                JOptionPane.showMessageDialog(this, "Hóa đơn đã ở trạng thái hủy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (bill.getStatus() == 1) {
                JOptionPane.showMessageDialog(this, "Hóa đơn đã hoàn thành, không thể hủy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra xem hóa đơn có chi tiết nào không
            BillDetailsDao detailsDao = new BillDetailsDao();
            List<BillDetails> details = detailsDao.findByBillId(billId);
            boolean hasDetails = !details.isEmpty();

            if (hasDetails) {
                // Nếu có chi tiết, chỉ cập nhật trạng thái thành "Đã hủy"
                bill.setStatus(2);
                if (billsDao.update(bill)) {
                    txtTrangThai.setText(getStatusText(2)); // Cập nhật hiển thị trạng thái
                    JOptionPane.showMessageDialog(this, "Hủy phiếu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Nếu không có chi tiết, xóa hóa đơn
                if (billsDao.deleteById(billId)) {
                    JOptionPane.showMessageDialog(this, "Hủy và xóa phiếu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    // Xóa dữ liệu trên giao diện
                    txtMaPhieu.setText("");
                    txtTheSo.setText("");
                    txtThoiDiemDatHang.setText("");
                    txtThoiDiemThanhToan.setText("");
                    txtTrangThai.setText("");
                    txtNhanVien.setText("");
                    tableModel.setRowCount(0); // Xóa bảng chi tiết
                       setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi hủy phiếu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtMaPhieu = new javax.swing.JTextField();
        txtNhanVien = new javax.swing.JTextField();
        txtTheSo = new javax.swing.JTextField();
        txtTrangThai = new javax.swing.JTextField();
        txtThoiDiemDatHang = new javax.swing.JTextField();
        txtThoiDiemThanhToan = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        BtnXoaDoUong = new javax.swing.JButton();
        BtnThemDoUong = new javax.swing.JButton();
        BtnHuyPhieu = new javax.swing.JButton();
        BtnThanhToan = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Phiếu bán hàng");

        jLabel1.setText("Mã phiếu");

        jLabel2.setText("Thẻ số");

        jLabel3.setText("Thời điểm đặt hàng");

        jLabel4.setText("Nhân viên ");

        jLabel5.setText("Trạng thái");

        jLabel6.setText("Thời điểm thanh toán");

        txtMaPhieu.setEditable(false);

        txtNhanVien.setEditable(false);

        txtTheSo.setEditable(false);

        txtTrangThai.setEditable(false);

        txtThoiDiemDatHang.setEditable(false);

        txtThoiDiemThanhToan.setEditable(false);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "", "Mã phiếu", "Đồ uống", "Đơn giá", "Giảm giá", "Số lượng", "Thành tiền"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        BtnXoaDoUong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/trash.png"))); // NOI18N
        BtnXoaDoUong.setText("Xóa đồ uống");
        BtnXoaDoUong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnXoaDoUongActionPerformed(evt);
            }
        });

        BtnThemDoUong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        BtnThemDoUong.setText("Thêm đồ uống");
        BtnThemDoUong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnThemDoUongActionPerformed(evt);
            }
        });

        BtnHuyPhieu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/delete.png"))); // NOI18N
        BtnHuyPhieu.setText("Hủy phiếu");
        BtnHuyPhieu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHuyPhieuActionPerformed(evt);
            }
        });

        BtnThanhToan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/accept.png"))); // NOI18N
        BtnThanhToan.setText("Thanh toán");
        BtnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnThanhToanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BtnThemDoUong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnXoaDoUong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnHuyPhieu, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnThanhToan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnXoaDoUong, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnThemDoUong, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnHuyPhieu, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtNhanVien, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                                .addComponent(txtMaPhieu, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addGap(61, 61, 61)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTheSo, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(txtThoiDiemDatHang, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtThoiDiemThanhToan))))
                .addGap(29, 29, 29))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaPhieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTheSo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtThoiDiemDatHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtThoiDiemThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnThemDoUongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnThemDoUongActionPerformed
        ThemDoUong();
    }//GEN-LAST:event_BtnThemDoUongActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
  
    if (evt.getClickCount() == 2) { // Kiểm tra nhấp chuột đôi
        int row = jTable1.rowAtPoint(evt.getPoint());
        if (row >= 0) {
            jTable1.setRowSelectionInterval(row, row);
            CapNhapSoLuong();
        }
    }
    }//GEN-LAST:event_jTable1MouseClicked

    private void BtnXoaDoUongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnXoaDoUongActionPerformed
        xoaCacDongDaChon();
    }//GEN-LAST:event_BtnXoaDoUongActionPerformed

    private void BtnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnThanhToanActionPerformed
      ThanhToan();
    }//GEN-LAST:event_BtnThanhToanActionPerformed

    private void BtnHuyPhieuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHuyPhieuActionPerformed
        Cancel();
    }//GEN-LAST:event_BtnHuyPhieuActionPerformed

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
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BillJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BillJDialog dialog = new BillJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton BtnHuyPhieu;
    private javax.swing.JButton BtnThanhToan;
    private javax.swing.JButton BtnThemDoUong;
    private javax.swing.JButton BtnXoaDoUong;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtMaPhieu;
    private javax.swing.JTextField txtNhanVien;
    private javax.swing.JTextField txtTheSo;
    private javax.swing.JTextField txtThoiDiemDatHang;
    private javax.swing.JTextField txtThoiDiemThanhToan;
    private javax.swing.JTextField txtTrangThai;
    // End of variables declaration//GEN-END:variables
}
