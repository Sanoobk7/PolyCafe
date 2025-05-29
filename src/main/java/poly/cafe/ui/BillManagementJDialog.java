/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui;
import poly.cafe.entity.Bills;
import poly.cafe.dao.BillDao;
import poly.cafe.dao.CrudDAO;
import poly.cafe.util.XJdbc;
import poly.cafe.ui.manager.CategoryController;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.BillsManagerDao;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.dao.impl.CategoryDAOImpl;
import poly.cafe.entity.Categories;
import poly.cafe.util.XDialog;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JComboBox;
import poly.cafe.dao.AccountManagementDao;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
/**
 *
 * @author Home
 */
public class BillManagementJDialog extends javax.swing.JDialog {
DefaultTableModel tableModel;
String[] thoiGianOptions = {"Hôm nay", "Tuần này", "Tháng này", "Năm nay", "Toàn thời gian"};
BillsManagerDao dao = new BillsManagerDao();
//private javax.swing.JComboBox<String> cboThoiGian;
//JTable table;
    /**
     * Creates new form ReceiptManagementJDialog
     */
    public BillManagementJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        initTable1();
        initComboBox();
        loaddatatotable();
    }

private void initComboBox() {
    cboThoiGian.setModel(new DefaultComboBoxModel<>(thoiGianOptions));
    cboThoiGian.addActionListener(e -> loaddatatotable());
    if (cboThoiGian.getItemCount() > 0) {
        cboThoiGian.setSelectedItem("Toàn thời gian"); // Đặt "Toàn thời gian" làm mặc định
        loaddatatotable();
    }
}


     private void initTable1() {
        // Thiết lập bảng jTable1 với các cột và kiểu dữ liệu tương ứng
        // Mỗi cột tương ứng với một kiểu dữ liệu
        // Xác định kiểu dữ liệu của từng cột (để hiển thị checkbox ở cột cuối)
        // Chỉ cho phép chỉnh sửa cột cuối cùng (checkbox "Chọn")
        //mấy cái này đều cần cái Dao.java hỗ trợ để làm cho dễ
    tableModel = new DefaultTableModel(new String[]{"Mã phiếu", "thẻ số", "thời điểm tạo", "thời điểm thanh toán", "Trạng thái", "Người tạo", "Chọn"}, 0) {
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
    jTable.setModel(tableModel);
    // Gán mô hình dữ liệu cho bảng
}
      // Cập nhật mảng thoiGianOptions để thêm "Toàn thời gian"


// Cập nhật phương thức loaddatatotable để xử lý "Toàn thời gian"
public void loaddatatotable() {
    tableModel.setRowCount(0); // Xóa dữ liệu cũ
    String selectedOption = (String) cboThoiGian.getSelectedItem();
    if (selectedOption == null) {
        return;
    }

    List<Bills> list;
    if (selectedOption.equals("Toàn thời gian")) {
        list = dao.findAll(); // Gọi findAll cho Toàn thời gian
    } else {
        list = dao.findByTimeFrame(selectedOption); // Gọi findByTimeFrame cho các mục khác
    }

    if (list == null || list.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Không có dữ liệu cho " + selectedOption.toLowerCase() + "!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    try {
        for (Bills bill : list) {
            String status;
            switch (bill.getStatus()) {
                case 0:
                    status = "Đang bảo trì";
                    break;
                case 1:
                    status = "Hoàn thành";
                    break;
                case 2:
                    status = "Đã hủy";
                    break;
                default:
                    status = "Không xác định";
            }
            tableModel.addRow(new Object[]{
                bill.getId(),
                bill.getCardId(),
                bill.getCheckin(),
                bill.getCheckout(),
                status,
                bill.getUsername(),
                false
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
      private void Them() {
    BillsManagerDao dao = new BillsManagerDao();
    AccountManagementDao userDao = new AccountManagementDao();
    String username = txtNguoiTao.getText().trim();
    String cardIdStr = txtTheSo.getText().trim();
    String checkinStr = txtThoiDiemTao.getText().trim();
    String checkoutStr = txtThoiDiemThanhToan.getText().trim();

    // Validate required fields
    if (username.isEmpty() || cardIdStr.isEmpty() || checkinStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin (Người tạo, Thẻ số, Thời điểm tạo)!");
        return;
    }

    // Validate Username exists in Users table
    if (!userDao.checkUsernameExists(username)) {
        JOptionPane.showMessageDialog(this, "Người tạo (Username) không tồn tại trong cơ sở dữ liệu!");
        return;
    }

    // Validate CardId (1-30 and exists in Cards table)
    int cardId;
    try {
        cardId = Integer.parseInt(cardIdStr);
        if (!dao.checkCardIdExists(cardId)) {
            JOptionPane.showMessageDialog(this, "Thẻ số không tồn tại trong cơ sở dữ liệu!");
            return;
        }
        
        if (cardId < 1 || cardId > 30) {
            JOptionPane.showMessageDialog(this, "Thẻ số phải nằm trong khoảng từ 1 đến 30!");
            return;
        }
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Thẻ số phải là số nguyên hợp lệ!");
        return;
    }

    // Parse Checkin date
    Date checkin;
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        checkin = sdf.parse(checkinStr);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Thời điểm tạo phải có định dạng dd/MM/yyyy HH:mm!");
        return;
    }

    // Parse Checkout date (nullable)
    Date checkout = null;
    if (!checkoutStr.isEmpty()) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            checkout = sdf.parse(checkoutStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Thời điểm thanh toán phải có định dạng dd/MM/yyyy HH:mm hoặc để trống!");
            return;
        }
    }

    // Determine Status
    int status;
    if (rdoBaoTri.isSelected()) {
        status = 0; // Đang bảo trì
    } else if (rdoHoanThanh.isSelected()) {
        status = 1; // Hoàn thành
    } else if (rdoDaHuy.isSelected()) {
        status = 2; // Đã hủy
    } else {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái (Đang bảo trì, Hoàn thành, hoặc Đã hủy)!");
        return;
    }

    // Create Bills object
    Bills bill = new Bills();
    bill.setUsername(username);
    bill.setCardId(cardId);
    bill.setCheckin(checkin);
    bill.setCheckout(checkout);
    bill.setStatus(status);

    // Insert bill
    try {
        if (dao.insert(bill)) {
            JOptionPane.showMessageDialog(this, "Thêm phiếu thành công!");
            loaddatatotable();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm phiếu thất bại!");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi thêm hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

private void Sua() {
    BillsManagerDao dao = new BillsManagerDao();
    AccountManagementDao userDao = new AccountManagementDao();
    String maPhieuStr = txtMaPhieu.getText().trim();
    String username = txtNguoiTao.getText().trim();
    String cardIdStr = txtTheSo.getText().trim();
    String checkinStr = txtThoiDiemTao.getText().trim();
    String checkoutStr = txtThoiDiemThanhToan.getText().trim();

    // Validate required fields
    if (maPhieuStr.isEmpty() || username.isEmpty() || cardIdStr.isEmpty() || checkinStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin (Mã phiếu, Người tạo, Thẻ số, Thời điểm tạo)!");
        return;
    }

    // Parse MaPhieu
    long maPhieu;
    try {
        maPhieu = Long.parseLong(maPhieuStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Mã phiếu phải là số hợp lệ!");
        return;
    }

    // Validate BillId exists
    if (!dao.checkBillExists(maPhieu)) {
        JOptionPane.showMessageDialog(this, "Mã phiếu không tồn tại trong cơ sở dữ liệu!");
        return;
    }

    // Validate Username exists
    if (!userDao.checkUsernameExists(username)) {
        JOptionPane.showMessageDialog(this, "Người tạo (Username) không tồn tại trong cơ sở dữ liệu!");
        return;
    }

    // Validate CardId (1-30 and exists in Cards table)
    int cardId;
    try {
        cardId = Integer.parseInt(cardIdStr);
        if (cardId < 1 || cardId > 30) {
            JOptionPane.showMessageDialog(this, "Thẻ số phải nằm trong khoảng từ 1 đến 30!");
            return;
        }
        if (!dao.checkCardIdExists(cardId)) {
            JOptionPane.showMessageDialog(this, "Thẻ số không tồn tại trong cơ sở dữ liệu!");
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Thẻ số phải là số nguyên hợp lệ!");
        return;
    }

    // Parse Checkin date
    Date checkin;
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        checkin = sdf.parse(checkinStr);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Thời điểm tạo phải có định dạng dd/MM/yyyy HH:mm!");
        return;
    }

    // Parse Checkout date (nullable)
    Date checkout = null;
    if (!checkoutStr.isEmpty()) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            checkout = sdf.parse(checkoutStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Thời điểm thanh toán phải có định dạng dd/MM/yyyy HH:mm hoặc để trống!");
            return;
        }
    }

    // Determine Status
    int status;
    if (rdoBaoTri.isSelected()) {
        status = 0; // Đang bảo trì
    } else if (rdoHoanThanh.isSelected()) {
        status = 1; // Hoàn thành
    } else if (rdoDaHuy.isSelected()) {
        status = 2; // Đã hủy
    } else {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái (Đang bảo trì, Hoàn thành, hoặc Đã hủy)!");
        return;
    }

    // Create Bills object
    Bills bill = new Bills();
    bill.setId(maPhieu);
    bill.setUsername(username);
    bill.setCardId(cardId);
    bill.setCheckin(checkin);
    bill.setCheckout(checkout);
    bill.setStatus(status);

    // Update bill
    try {
        if (dao.update(bill)) {
            JOptionPane.showMessageDialog(this, "Cập nhật phiếu thành công!");
            loaddatatotable();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật phiếu thất bại!");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

private void Xoa() {
    String maPhieuStr = txtMaPhieu.getText().trim();
    if (maPhieuStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã phiếu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    long maPhieu;
    try {
        maPhieu = Long.parseLong(maPhieuStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Mã phiếu phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    BillsManagerDao dao = new BillsManagerDao();
    // Kiểm tra xem BillId có tồn tại không
    if (!dao.checkBillExists(maPhieu)) {
        JOptionPane.showMessageDialog(this, "Mã phiếu không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa Phiếu với mã: " + maPhieu + "?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            if (dao.deleteById(maPhieu)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loaddatatotable();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại! Vui lòng kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
     private void updateFormFromRow(int row) {
        if (row >= 0 && row < jTable.getRowCount()) {
            txtMaPhieu.setText(String.valueOf(jTable.getValueAt(row, 0))); // Id
            txtNguoiTao.setText((String) jTable.getValueAt(row, 1)); // Username
            txtTheSo.setText(String.valueOf(jTable.getValueAt(row, 2))); // CardId

            // Format Checkin and Checkout
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Object checkin = jTable.getValueAt(row, 3); // Checkin
            txtThoiDiemTao.setText(checkin != null ? sdf.format((Date) checkin) : "");
            Object checkout = jTable.getValueAt(row, 4); // Checkout
            txtThoiDiemThanhToan.setText(checkout != null && !"N/A".equals(checkout) ? sdf.format((Date) checkout) : "");

            // Set Status radio buttons
            String status = (String) jTable.getValueAt(row, 5); // Status
            rdoBaoTri.setSelected("Đang bảo trì".equals(status));
            rdoHoanThanh.setSelected("Hoàn thành".equals(status));
            rdoDaHuy.setSelected("Đã hủy".equals(status));
        }
    }

    public void firstRow() {
        if (jTable.getRowCount() > 0) {
            jTable.setRowSelectionInterval(0, 0);
            jTable.scrollRectToVisible(jTable.getCellRect(0, 0, true));
            updateFormFromRow(0);
        }
    }

    public void lastRow() {
        int lastIndex = jTable.getRowCount() - 1;
        if (lastIndex >= 0) {
            jTable.setRowSelectionInterval(lastIndex, lastIndex);
            jTable.scrollRectToVisible(jTable.getCellRect(lastIndex, 0, true));
            updateFormFromRow(lastIndex);
        }
    }

    public void nextRow() {
        int current = jTable.getSelectedRow();
        int next = current + 1;
        if (next < jTable.getRowCount()) {
            jTable.setRowSelectionInterval(next, next);
            jTable.scrollRectToVisible(jTable.getCellRect(next, 0, true));
            updateFormFromRow(next);
        }
    }

    public void backRow() {
        int current = jTable.getSelectedRow();
        int back = current - 1;
        if (back >= 0) {
            jTable.setRowSelectionInterval(back, back);
            jTable.scrollRectToVisible(jTable.getCellRect(back, 0, true));
            updateFormFromRow(back);
        }
    }

    public void BoChonTatCa() {
        for (int row = 0; row < jTable.getRowCount(); row++) {
            jTable.setValueAt(false, row, 6); // Checkbox column
        }
    }

    public void ChonTatCa() {
        for (int row = 0; row < jTable.getRowCount(); row++) {
            jTable.setValueAt(true, row, 6); // Checkbox column
        }
    }

    public void xoaCacDongDaChon() {
        BillsManagerDao dao = new BillsManagerDao();
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (int i = 0; i < jTable.getRowCount(); i++) {
            Boolean checked = (Boolean) jTable.getValueAt(i, 6); // Checkbox column
            if (checked != null && checked) {
                long id = Long.parseLong(jTable.getValueAt(i, 0).toString()); // Id column
                if (dao.deleteById(id)) {
                    sb.append("- ID: ").append(id).append("\n");
                    count++;
                }
            }
        }

        if (count > 0) {
            JOptionPane.showMessageDialog(this,
                "Đã xóa thành công " + count + " phiếu:\n" + sb.toString(),
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loaddatatotable(); // Refresh table
        } else {
            JOptionPane.showMessageDialog(this,
                "Không có dòng nào được chọn để xóa.",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        BtnChonTatCa = new javax.swing.JButton();
        BtnBoChonTatCa = new javax.swing.JButton();
        BtnXoaTatCaDaChon = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtTuNgay = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDenNgay = new javax.swing.JTextField();
        BtnLoc = new javax.swing.JButton();
        cboThoiGian = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        BtnThem = new javax.swing.JButton();
        BtnCapNhap = new javax.swing.JButton();
        BtnXoa = new javax.swing.JButton();
        BtnLamMoi = new javax.swing.JButton();
        BtnBack = new javax.swing.JButton();
        BtnFirst = new javax.swing.JButton();
        BtnNext = new javax.swing.JButton();
        BtnLast = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtMaPhieu = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTheSo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtThoiDiemTao = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtThoiDiemThanhToan = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtNguoiTao = new javax.swing.JTextField();
        rdoBaoTri = new javax.swing.JRadioButton();
        rdoHoanThanh = new javax.swing.JRadioButton();
        rdoDaHuy = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý phiếu");

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã phiếu", "Thẻ số", "Thời điểm tạo", "Thời điểm thanh toán", "Trạng thái", "Người tạo", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable);

        BtnChonTatCa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/accept.png"))); // NOI18N
        BtnChonTatCa.setText("Chọn tất cả");
        BtnChonTatCa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnChonTatCaActionPerformed(evt);
            }
        });

        BtnBoChonTatCa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/delete.png"))); // NOI18N
        BtnBoChonTatCa.setText("bỏ chọn tất cả");
        BtnBoChonTatCa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBoChonTatCaActionPerformed(evt);
            }
        });

        BtnXoaTatCaDaChon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/trash.png"))); // NOI18N
        BtnXoaTatCaDaChon.setText("xóa các mục chọn");
        BtnXoaTatCaDaChon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnXoaTatCaDaChonActionPerformed(evt);
            }
        });

        jLabel1.setText("Từ ngày: ");

        jLabel2.setText("Đến ngày:");

        BtnLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Search.png"))); // NOI18N
        BtnLoc.setText("Lọc");
        BtnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLocActionPerformed(evt);
            }
        });

        cboThoiGian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hôm nay", "Tuần này", "Tháng này", "Năm nay" }));
        cboThoiGian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboThoiGianActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(219, Short.MAX_VALUE)
                .addComponent(BtnChonTatCa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnBoChonTatCa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnXoaTatCaDaChon)
                .addGap(12, 12, 12))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(123, 123, 123)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTuNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDenNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnLoc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(9, 9, 9)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(txtTuNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtDenNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(BtnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cboThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnBoChonTatCa)
                    .addComponent(BtnChonTatCa)
                    .addComponent(BtnXoaTatCaDaChon))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Danh sách", jPanel1);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        BtnThem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        BtnThem.setText("Tạo mới");
        BtnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnThemActionPerformed(evt);
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

        BtnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back.png"))); // NOI18N
        BtnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBackActionPerformed(evt);
            }
        });

        BtnFirst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Fisrt.png"))); // NOI18N
        BtnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnFirstActionPerformed(evt);
            }
        });

        BtnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/next.png"))); // NOI18N
        BtnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnNextActionPerformed(evt);
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
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(BtnThem, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnCapNhap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(146, 146, 146))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(BtnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(BtnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(BtnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BtnThem)
                        .addComponent(BtnCapNhap))
                    .addComponent(BtnBack)
                    .addComponent(BtnNext))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BtnXoa)
                        .addComponent(BtnLamMoi))
                    .addComponent(BtnFirst)
                    .addComponent(BtnLast))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("Mã phiếu");

        txtMaPhieu.setEditable(false);

        jLabel4.setText("Thẻ số");

        jLabel5.setText("Thời điểm tạo");

        jLabel6.setText("Thời điểm thanh toán");

        jLabel7.setText("trạng thái");

        jLabel8.setText("Người tạo");

        buttonGroup1.add(rdoBaoTri);
        rdoBaoTri.setText("đang bảo trì");

        buttonGroup1.add(rdoHoanThanh);
        rdoHoanThanh.setText("hoàn thành");

        buttonGroup1.add(rdoDaHuy);
        rdoDaHuy.setText("đã hủy");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Đồ uống", "Đơn giá", "Giảm giá", "Số lượng", "Thành tiền"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtThoiDiemTao, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtMaPhieu, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(rdoBaoTri, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoHoanThanh, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rdoDaHuy, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTheSo)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtThoiDiemThanhToan)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNguoiTao, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE))
                .addGap(55, 55, 55))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaPhieu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTheSo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtThoiDiemTao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtThoiDiemThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNguoiTao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdoBaoTri)
                    .addComponent(rdoHoanThanh)
                    .addComponent(rdoDaHuy))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Biểu mẫu", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnChonTatCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnChonTatCaActionPerformed
        ChonTatCa();
    }//GEN-LAST:event_BtnChonTatCaActionPerformed

    private void BtnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnThemActionPerformed
        Them();
    }//GEN-LAST:event_BtnThemActionPerformed

    private void BtnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnXoaActionPerformed
        Xoa();
    }//GEN-LAST:event_BtnXoaActionPerformed

    private void BtnCapNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCapNhapActionPerformed
        Sua();
    }//GEN-LAST:event_BtnCapNhapActionPerformed

    private void BtnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLamMoiActionPerformed
        buttonGroup1.clearSelection();
        buttonGroup2.clearSelection();
        txtMaPhieu.setText(null);
        txtNguoiTao.setText(null);
        txtTheSo.setText(null);
        txtThoiDiemTao.setText(null);
        txtThoiDiemThanhToan.setText(null);
    }//GEN-LAST:event_BtnLamMoiActionPerformed

    private void BtnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBackActionPerformed
       backRow();
    }//GEN-LAST:event_BtnBackActionPerformed

    private void BtnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnNextActionPerformed
        nextRow();
    }//GEN-LAST:event_BtnNextActionPerformed

    private void BtnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnFirstActionPerformed
        firstRow();
    }//GEN-LAST:event_BtnFirstActionPerformed

    private void BtnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLastActionPerformed
        lastRow();
    }//GEN-LAST:event_BtnLastActionPerformed

    private void BtnBoChonTatCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBoChonTatCaActionPerformed
        BoChonTatCa();
    }//GEN-LAST:event_BtnBoChonTatCaActionPerformed

    private void BtnXoaTatCaDaChonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnXoaTatCaDaChonActionPerformed
       xoaCacDongDaChon();
    }//GEN-LAST:event_BtnXoaTatCaDaChonActionPerformed

    private void cboThoiGianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboThoiGianActionPerformed
String[] thoiGianOptions = {"Năm nay", "Tháng này", "Tuần này", "Hôm nay"};
String selected = cboThoiGian.getSelectedItem() != null ? cboThoiGian.getSelectedItem().toString() : "";
BillsManagerDao dao = new BillsManagerDao();
List<Bills> list = new ArrayList<>();

try {
    switch (selected) {
        case "Hôm nay":
            list = dao.findToday();
            break;
        case "Tuần này":
            list = dao.findThisWeek();
            break;
        case "Tháng này":
            list = dao.findThisMonth();
            break;
        case "Năm nay":
            list = dao.findThisYear();
            break;
        default:
            list = dao.findAll();
            break;
    }
    tableModel.setRowCount(0);
    loaddatatotable();
} catch (Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "Error loading bills: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_cboThoiGianActionPerformed

    private void BtnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLocActionPerformed
        String fromDateStr = txtTuNgay.getText().trim();
    String toDateStr = txtDenNgay.getText().trim();

    if (fromDateStr.isEmpty() || toDateStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập cả Từ ngày và Đến ngày!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date fromDate, toDate;
    try {
        fromDate = sdf.parse(fromDateStr);
        toDate = sdf.parse(toDateStr);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Ngày phải có định dạng dd/MM/yyyy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (toDate.before(fromDate)) {
        JOptionPane.showMessageDialog(this, "Đến ngày phải sau Từ ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    List<Bills> list = dao.findByDateRange(fromDate, toDate);
    tableModel.setRowCount(0); // Xóa dữ liệu cũ
    if (list == null || list.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Không có dữ liệu cho khoảng thời gian từ " + fromDateStr + " đến " + toDateStr + "!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    try {
        for (Bills bill : list) {
            String status;
            switch (bill.getStatus()) {
                case 0:
                    status = "Đang bảo trì";
                    break;
                case 1:
                    status = "Hoàn thành";
                    break;
                case 2:
                    status = "Đã hủy";
                    break;
                default:
                    status = "Không xác định";
            }
            tableModel.addRow(new Object[]{
                bill.getId(),
                bill.getCardId(),
                bill.getCheckin(),
                bill.getCheckout(),
                status,
                bill.getUsername(),
                false
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_BtnLocActionPerformed

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
            java.util.logging.Logger.getLogger(BillManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BillManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BillManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BillManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BillManagementJDialog dialog = new BillManagementJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton BtnFirst;
    private javax.swing.JButton BtnLamMoi;
    private javax.swing.JButton BtnLast;
    private javax.swing.JButton BtnLoc;
    private javax.swing.JButton BtnNext;
    private javax.swing.JButton BtnThem;
    private javax.swing.JButton BtnXoa;
    private javax.swing.JButton BtnXoaTatCaDaChon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<String> cboThoiGian;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JTable jTable2;
    private javax.swing.JRadioButton rdoBaoTri;
    private javax.swing.JRadioButton rdoDaHuy;
    private javax.swing.JRadioButton rdoHoanThanh;
    private javax.swing.JTextField txtDenNgay;
    private javax.swing.JTextField txtMaPhieu;
    private javax.swing.JTextField txtNguoiTao;
    private javax.swing.JTextField txtTheSo;
    private javax.swing.JTextField txtThoiDiemTao;
    private javax.swing.JTextField txtThoiDiemThanhToan;
    private javax.swing.JTextField txtTuNgay;
    // End of variables declaration//GEN-END:variables
}
