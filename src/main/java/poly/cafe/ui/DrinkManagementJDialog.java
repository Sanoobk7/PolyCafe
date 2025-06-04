/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui;

import java.util.List;
import java.util.Locale.Category;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.dao.DrinkDAO;
import poly.cafe.dao.impl.CategoryDAOImpl;
import poly.cafe.dao.impl.DrinkDAOImpl;
import poly.cafe.entity.Drinks;
import poly.cafe.entity.Categories;
import poly.cafe.entity.Categories;
import poly.cafe.entity.Drinks;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import poly.cafe.dao.DrinkManagementDao;
import poly.cafe.entity.Categories;
import poly.cafe.entity.Drinks;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.io.File;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import java.awt.Color;

/**
 *
 * @author Home
 */
public class DrinkManagementJDialog extends javax.swing.JDialog{
DefaultTableModel drinkTableModel;
DefaultTableModel categoryTableModel;
DrinkManagementDao dao = new DrinkManagementDao();
private String photoPath = "";
    /**
     * Creates new form BeverageManagementJDialog
     */
    
   
    
    public DrinkManagementJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        initDrinkTable();
        initCategoryTable();
        initComboBox();
        initSlider();
        loadCategoryDataToTable();
        loadDrinkDataToTable();
        styleCategoryTable();
        LamMoi();
    }
    private void filterDrinksByCategory(int row) {
    if (row >= 0 && row < tblDrinks.getRowCount()) {
        String categoryName = (String) tblDrinks.getValueAt(row, 0);
        Categories category = dao.findCategoryByName(categoryName);
        if (category != null) {
            drinkTableModel.setRowCount(0); // Xóa dữ liệu cũ
            List<Drinks> drinks = dao.findByCategoryId(category.getId());
            if (drinks != null) {
                try {
                    for (Drinks drink : drinks) {
                        String status = drink.isAvailable() ? "Sẵn sàng" : "Hết";
                        drinkTableModel.addRow(new Object[]{
                            drink.getId(),
                            drink.getName(),
                            drink.getUnitPrice(),
                            drink.getDiscount(),
                            status,
                            categoryName,
                            false // Checkbox mặc định
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
    private void styleCategoryTable() {
    tblDrinks.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(c.getFont().deriveFont(java.awt.Font.BOLD)); // Đặt chữ in đậm
            if (isSelected) {
                c.setForeground(Color.RED); // Chữ đỏ khi chọn
            } else {
                c.setForeground(Color.BLUE); // Chữ xanh dương khi không chọn
            }
            return c;
        }
    });
}

   private void initDrinkTable() {
    drinkTableModel = new DefaultTableModel(
        new String[]{"Mã đồ uống", "Tên đồ uống", "Đơn giá", "Giảm giá", "Trạng thái", "Loại", "Chọn"}, 0
    ) {
        Class<?>[] types = new Class<?>[]{String.class, String.class, Double.class, Double.class, String.class, String.class, Boolean.class};

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return types[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 6; // Chỉ cột "Chọn" (checkbox) được chỉnh sửa
        }
    };
    jTable2.setModel(drinkTableModel);
}
    private void initCategoryTable() {
        categoryTableModel = new DefaultTableModel(
            new String[]{"Tên loại đồ uống"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDrinks.setModel(categoryTableModel);
    }
    private void initComboBox() {
        List<Categories> categories = dao.findAllCategories();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (Categories category : categories) {
            model.addElement(category.getId() + " - " + category.getName());
        }
        cboLoai.setModel(model);
        if (cboLoai.getItemCount() > 0) {
            cboLoai.setSelectedIndex(0);
        }
    }
    private void initSlider() {
        sldGiamGia.setMinimum(0);
        sldGiamGia.setMaximum(100);
        txtPhanTram.setText("0%");
        sldGiamGia.addChangeListener(evt -> {
            int value = sldGiamGia.getValue();
            txtPhanTram.setText(value + "%");
        });
    }
   private void loadDrinkDataToTable() {
    drinkTableModel.setRowCount(0);
    List<Drinks> list = dao.findAll();
    if (list == null || list.isEmpty()) {
        return;
    }

    try {
        for (Drinks drink : list) {
            String status = drink.isAvailable() ? "Sẵn sàng" : "Hết";
            Categories category = dao.findCategoryById(drink.getCategoryId());
            String categoryName = category != null ? category.getName() : "N/A";
            drinkTableModel.addRow(new Object[]{
                drink.getId(),
                drink.getName(),
                drink.getUnitPrice(),
                drink.getDiscount(),
                status,
                categoryName,
                false // Giá trị mặc định cho checkbox
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
   private void chonTatCa() {
    for (int row = 0; row < jTable2.getRowCount(); row++) {
        jTable2.setValueAt(true, row, 6); // Cột 7 là checkbox
    }
}

private void boChonTatCa() {
    for (int row = 0; row < jTable2.getRowCount(); row++) {
        jTable2.setValueAt(false, row, 6); // Cột 7 là checkbox
    }
}

private void xoaCacDongDaChon() {
    StringBuilder sb = new StringBuilder();
    int count = 0;

    for (int i = 0; i < jTable2.getRowCount(); i++) {
        Boolean checked = (Boolean) jTable2.getValueAt(i, 6); // Cột checkbox
        if (checked != null && checked) {
            String id = jTable2.getValueAt(i, 0).toString(); // Cột Id
            if (dao.deleteById(id)) {
                sb.append("- ").append(id).append("\n");
                count++;
            }
        }
    }

    if (count > 0) {
        JOptionPane.showMessageDialog(this,
            "Đã xóa thành công " + count + " đồ uống:\n" + sb.toString(),
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        loadDrinkDataToTable(); // Cập nhật lại bảng
        
    } else {
        JOptionPane.showMessageDialog(this,
            "Không có dòng nào được chọn để xóa.",
            "Thông báo", JOptionPane.WARNING_MESSAGE);
    }}
    private void loadCategoryDataToTable() {
    categoryTableModel.setRowCount(0);
    List<Categories> list = dao.findAllCategories();
    if (list == null || list.isEmpty()) {
        return;
    }

    try {
        for (Categories category : list) {
            categoryTableModel.addRow(new Object[]{category.getName()});
        }
        if (tblDrinks.getRowCount() > 0) {
            tblDrinks.setRowSelectionInterval(0, 0); // Chọn dòng đầu tiên
            filterDrinksByCategory(0); // Lọc đồ uống
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu loại đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "png", "jpeg", "gif"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            photoPath = selectedFile.getName();
            lblPhotoPath.setText(photoPath);
            ImageIcon imageIcon = new ImageIcon(selectedFile.getAbsolutePath());
            Image image = imageIcon.getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH);
            lblPhotoPath.setIcon(new ImageIcon(image));
        }
    }

    private void Them() {
        String maDoUong = txtMaDoUong.getText().trim();
        String tenDoUong = txtTenDoUong.getText().trim();
        String donGiaStr = txtDonGia.getText().trim();
        String categorySelection = (String) cboLoai.getSelectedItem();
        String categoryId = categorySelection != null ? categorySelection.split(" - ")[0] : null;
        boolean available = rdoSan.isSelected();
        double discount = sldGiamGia.getValue();

        // Kiểm tra các trường
        if (maDoUong.isEmpty() || tenDoUong.isEmpty() || donGiaStr.isEmpty() || categoryId == null || photoPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (Mã, Tên, Đơn giá, Loại, Hình ảnh)!");
            return;
        }

        // Kiểm tra độ dài
        if (maDoUong.length() > 20) {
            JOptionPane.showMessageDialog(this, "Mã đồ uống không được vượt quá 20 ký tự!");
            return;
        }
        if (tenDoUong.length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên đồ uống không được vượt quá 50 ký tự!");
            return;
        }
        if (photoPath.length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên file hình ảnh không được vượt quá 50 ký tự!");
            return;
        }

        // Kiểm tra mã trùng
        if (dao.checkDrinkExists(maDoUong)) {
            JOptionPane.showMessageDialog(this, "Mã đồ uống đã tồn tại!");
            return;
        }

        // Kiểm tra đơn giá
        double unitPrice;
        try {
            unitPrice = Double.parseDouble(donGiaStr);
            if (unitPrice <= 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số hợp lệ!");
            return;
        }

        // Kiểm tra loại
        if (!dao.checkCategoryExists(categoryId)) {
            JOptionPane.showMessageDialog(this, "Loại đồ uống không tồn tại!");
            return;
        }

        // Tạo đối tượng Drinks
        Drinks drink = new Drinks();
        drink.setId(maDoUong);
        drink.setName(tenDoUong);
        drink.setUnitPrice(unitPrice);
        drink.setDiscount(discount);
        drink.setImage(photoPath);
        drink.setAvailable(available);
        drink.setCategoryId(categoryId);

        // Thêm đồ uống
        try {
            if (dao.insert(drink)) {
                JOptionPane.showMessageDialog(this, "Thêm đồ uống thành công!");
                loadDrinkDataToTable();
                
            } else {
                JOptionPane.showMessageDialog(this, "Thêm đồ uống thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void Sua() {
        String maDoUong = txtMaDoUong.getText().trim();
        String tenDoUong = txtTenDoUong.getText().trim();
        String donGiaStr = txtDonGia.getText().trim();
        String categorySelection = (String) cboLoai.getSelectedItem();
        String categoryId = categorySelection != null ? categorySelection.split(" - ")[0] : null;
        boolean available = rdoSan.isSelected();
        double discount = sldGiamGia.getValue();

        // Kiểm tra các trường
        if (maDoUong.isEmpty() || tenDoUong.isEmpty() || donGiaStr.isEmpty() || categoryId == null || photoPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin (Mã, Tên, Đơn giá, Loại, Hình ảnh)!");
            return;
        }

        // Kiểm tra độ dài
        if (maDoUong.length() > 20) {
            JOptionPane.showMessageDialog(this, "Mã đồ uống không được vượt quá 20 ký tự!");
            return;
        }
        if (tenDoUong.length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên đồ uống không được vượt quá 50 ký tự!");
            return;
        }
        if (photoPath.length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên file hình ảnh không được vượt quá 50 ký tự!");
            return;
        }

        // Kiểm tra mã tồn tại
        if (!dao.checkDrinkExists(maDoUong)) {
            JOptionPane.showMessageDialog(this, "Mã đồ uống không tồn tại!");
            return;
        }

        // Kiểm tra đơn giá
        double unitPrice;
        try {
            unitPrice = Double.parseDouble(donGiaStr);
            if (unitPrice <= 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số hợp lệ!");
            return;
        }

        // Kiểm tra loại
        if (!dao.checkCategoryExists(categoryId)) {
            JOptionPane.showMessageDialog(this, "Loại đồ uống không tồn tại!");
            return;
        }

        // Tạo đối tượng Drinks
        Drinks drink = new Drinks();
        drink.setId(maDoUong);
        drink.setName(tenDoUong);
        drink.setUnitPrice(unitPrice);
        drink.setDiscount(discount);
        drink.setImage(photoPath);
        drink.setAvailable(available);
        drink.setCategoryId(categoryId);

        // Cập nhật đồ uống
        try {
            if (dao.update(drink)) {
                JOptionPane.showMessageDialog(this, "Cập nhật đồ uống thành công!");
                loadDrinkDataToTable();
                
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật đồ uống thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void Xoa() {
        String maDoUong = txtMaDoUong.getText().trim();
        if (maDoUong.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã đồ uống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!dao.checkDrinkExists(maDoUong)) {
            JOptionPane.showMessageDialog(this, "Mã đồ uống không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa Đồ uống với mã: " + maDoUong + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (dao.deleteById(maDoUong)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadDrinkDataToTable();
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại! Vui lòng kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void LamMoi() {
        txtMaDoUong.setText("");
        txtTenDoUong.setText("");
        txtDonGia.setText("");
        sldGiamGia.setValue(0);
        txtPhanTram.setText("0%");
        buttonGroup1.clearSelection();
        loadDrinkDataToTable();
        lblPhotoPath.setIcon(new ImageIcon("C:\\Users\\Home\\Documents\\NetBeansProjects\\PolyCafe\\src\\main\\resources\\img\\catloveu.png"));
    }

    private void updateFormFromRow(int row) {
    if (row >= 0 && row < jTable2.getRowCount()) {
        txtMaDoUong.setText((String) jTable2.getValueAt(row, 0));
        txtTenDoUong.setText((String) jTable2.getValueAt(row, 1));
        txtDonGia.setText(String.valueOf(jTable2.getValueAt(row, 2)));
        sldGiamGia.setValue((int) ((Double) jTable2.getValueAt(row, 3)).doubleValue());
        txtPhanTram.setText(sldGiamGia.getValue() + "%");
        String status = (String) jTable2.getValueAt(row, 4);
        rdoSan.setSelected("Sẵn sàng".equals(status));
        rdoHet.setSelected("Hết".equals(status));

        // Lấy CategoryId từ Drinks
        Drinks drink = dao.findById((String) jTable2.getValueAt(row, 0));
        if (drink != null) {
            String categoryId = drink.getCategoryId();
            // Kiểm tra CategoryId tồn tại
            if (dao.checkCategoryExists(categoryId)) {
                // Tìm và đặt item trong cboLoai
                for (int i = 0; i < cboLoai.getItemCount(); i++) {
                    if (cboLoai.getItemAt(i).startsWith(categoryId)) {
                        cboLoai.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Loại đồ uống này đã bị xóa!", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
                if (cboLoai.getItemCount() > 0) {
                    cboLoai.setSelectedIndex(0); // Đặt về item đầu tiên
                }
            }
            // Không load ảnh, chỉ đặt photoPath
            photoPath = drink.getImage() != null ? drink.getImage() : "";
            lblPhotoPath.setText(photoPath);
            lblPhotoPath.setIcon(new ImageIcon("C:\\Users\\Home\\Documents\\NetBeansProjects\\PolyCafe\\src\\main\\resources\\img\\catloveu.png"));
        }
    }
}

    public void firstRow() {
        if (jTable2.getRowCount() > 0) {
            jTable2.setRowSelectionInterval(0, 0);
            jTable2.scrollRectToVisible(jTable2.getCellRect(0, 0, true));
            updateFormFromRow(0);
        }
    }

    public void lastRow() {
        int lastIndex = jTable2.getRowCount() - 1;
        if (lastIndex >= 0) {
            jTable2.setRowSelectionInterval(lastIndex, lastIndex);
            jTable2.scrollRectToVisible(jTable2.getCellRect(lastIndex, 0, true));
            updateFormFromRow(lastIndex);
        }
    }

    public void nextRow() {
        int current = jTable2.getSelectedRow();
        int next = current + 1;
        if (next < jTable2.getRowCount()) {
            jTable2.setRowSelectionInterval(next, next);
            jTable2.scrollRectToVisible(jTable2.getCellRect(next, 0, true));
            updateFormFromRow(next);
        }
    }

    public void backRow() {
        int current = jTable2.getSelectedRow();
        int back = current - 1;
        if (back >= 0) {
            jTable2.setRowSelectionInterval(back, back);
            jTable2.scrollRectToVisible(jTable2.getCellRect(back, 0, true));
            updateFormFromRow(back);
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

        jButton2 = new javax.swing.JButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDrinks = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnChonTatCa = new javax.swing.JButton();
        btnBoChonTatCa = new javax.swing.JButton();
        btnXoaDaChon = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        BtnLamMoi = new javax.swing.JButton();
        BtnSua = new javax.swing.JButton();
        BtnXoa = new javax.swing.JButton();
        BtnThem = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        rdoSan = new javax.swing.JRadioButton();
        rdoHet = new javax.swing.JRadioButton();
        cboLoai = new javax.swing.JComboBox<>();
        txtMaDoUong = new javax.swing.JTextField();
        sldGiamGia = new javax.swing.JSlider();
        txtTenDoUong = new javax.swing.JTextField();
        txtDonGia = new javax.swing.JTextField();
        txtPhanTram = new javax.swing.JLabel();
        lblPhotoPath = new javax.swing.JLabel();

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/delete.png"))); // NOI18N
        jButton2.setText("bỏ chọn tất cả");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý đồ uống");

        tblDrinks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Loại đồ uống"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDrinks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDrinksMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDrinks);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã đồ uống", "Tên đồ uống", "Đơn giá", "Giảm giá", "Trạng thái", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        btnChonTatCa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/accept.png"))); // NOI18N
        btnChonTatCa.setText("Chọn tất cả");
        btnChonTatCa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChonTatCaActionPerformed(evt);
            }
        });

        btnBoChonTatCa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/delete.png"))); // NOI18N
        btnBoChonTatCa.setText("Bỏ chọn tất cả");
        btnBoChonTatCa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoChonTatCaActionPerformed(evt);
            }
        });

        btnXoaDaChon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/trash.png"))); // NOI18N
        btnXoaDaChon.setText("Xóa các mục chọn");
        btnXoaDaChon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaDaChonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(203, 203, 203)
                .addComponent(btnChonTatCa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBoChonTatCa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnXoaDaChon)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChonTatCa)
                    .addComponent(btnBoChonTatCa)
                    .addComponent(btnXoaDaChon))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Danh sách", jPanel1);

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        BtnLamMoi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/refresh.png"))); // NOI18N
        BtnLamMoi.setText("Làm mới");
        BtnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLamMoiActionPerformed(evt);
            }
        });

        BtnSua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/edit.png"))); // NOI18N
        BtnSua.setText("Cập nhập");
        BtnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSuaActionPerformed(evt);
            }
        });

        BtnXoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/trash.png"))); // NOI18N
        BtnXoa.setText("Xóa");
        BtnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnXoaActionPerformed(evt);
            }
        });

        BtnThem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add.png"))); // NOI18N
        BtnThem.setText("Tạo mới");
        BtnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnThemActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Fisrt.png"))); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back.png"))); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/last.png"))); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/next.png"))); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BtnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnSua))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BtnSua)
                            .addComponent(BtnThem))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BtnLamMoi)
                            .addComponent(BtnXoa)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton8)
                            .addComponent(jButton9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton11, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );

        jLabel2.setText("Mã đồ uống");

        jLabel3.setText("Tên đồ uống");

        jLabel4.setText("Đơn giá");

        jLabel5.setText("Giảm giá");

        jLabel6.setText("Loại");

        jLabel7.setText("Trạng thái");

        buttonGroup1.add(rdoSan);
        rdoSan.setText("Sẵn sàng");

        buttonGroup1.add(rdoHet);
        rdoHet.setText("Hết hàng");
        rdoHet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoHetActionPerformed(evt);
            }
        });

        cboLoai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nước trái cây", "Item 2", "Item 3", "Item 4" }));

        sldGiamGia.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
                sldGiamGiaAncestorMoved(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        sldGiamGia.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldGiamGiaStateChanged(evt);
            }
        });

        txtPhanTram.setText("0%");

        lblPhotoPath.setFont(new java.awt.Font("Segoe UI", 0, 1)); // NOI18N
        lblPhotoPath.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/catloveu.png"))); // NOI18N
        lblPhotoPath.setText("jLabel1");
        lblPhotoPath.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblPhotoPath.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPhotoPathMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblPhotoPath, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLoai, 0, 192, Short.MAX_VALUE)
                            .addComponent(txtMaDoUong)
                            .addComponent(txtDonGia))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addComponent(rdoSan, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(rdoHet, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(txtTenDoUong, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)))
                                .addGap(30, 30, 30))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(sldGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtPhanTram, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtMaDoUong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(txtTenDoUong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(sldGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPhanTram)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDonGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(19, 19, 19)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdoSan)
                            .addComponent(rdoHet)
                            .addComponent(cboLoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblPhotoPath, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Biểu mẫu", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChonTatCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChonTatCaActionPerformed
        chonTatCa();
    }//GEN-LAST:event_btnChonTatCaActionPerformed

    private void sldGiamGiaAncestorMoved(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_sldGiamGiaAncestorMoved
       
    }//GEN-LAST:event_sldGiamGiaAncestorMoved

    private void sldGiamGiaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldGiamGiaStateChanged
       
    }//GEN-LAST:event_sldGiamGiaStateChanged

    private void rdoHetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoHetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdoHetActionPerformed

    private void BtnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnThemActionPerformed
Them();
    }//GEN-LAST:event_BtnThemActionPerformed

    private void BtnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSuaActionPerformed
 Sua();
    }//GEN-LAST:event_BtnSuaActionPerformed

    private void BtnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnXoaActionPerformed
 Xoa();
    }//GEN-LAST:event_BtnXoaActionPerformed

    private void BtnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLamMoiActionPerformed
LamMoi();
    }//GEN-LAST:event_BtnLamMoiActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
backRow();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
nextRow();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
firstRow();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
lastRow();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void btnBoChonTatCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBoChonTatCaActionPerformed
        boChonTatCa();
    }//GEN-LAST:event_btnBoChonTatCaActionPerformed

    private void btnXoaDaChonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaDaChonActionPerformed
        xoaCacDongDaChon();
    }//GEN-LAST:event_btnXoaDaChonActionPerformed

    private void tblDrinksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDrinksMouseClicked
int row = tblDrinks.rowAtPoint(evt.getPoint());
    if (row >= 0) {
        tblDrinks.setRowSelectionInterval(row, row);
        filterDrinksByCategory(row);
    }
    }//GEN-LAST:event_tblDrinksMouseClicked

    private void lblPhotoPathMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPhotoPathMouseClicked
     chonAnh();
    }//GEN-LAST:event_lblPhotoPathMouseClicked

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
            java.util.logging.Logger.getLogger(DrinkManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DrinkManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DrinkManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DrinkManagementJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DrinkManagementJDialog dialog = new DrinkManagementJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton BtnLamMoi;
    private javax.swing.JButton BtnSua;
    private javax.swing.JButton BtnThem;
    private javax.swing.JButton BtnXoa;
    private javax.swing.JButton btnBoChonTatCa;
    private javax.swing.JButton btnChonTatCa;
    private javax.swing.JButton btnXoaDaChon;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboLoai;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel lblPhotoPath;
    private javax.swing.JRadioButton rdoHet;
    private javax.swing.JRadioButton rdoSan;
    private javax.swing.JSlider sldGiamGia;
    private javax.swing.JTable tblDrinks;
    private javax.swing.JTextField txtDonGia;
    private javax.swing.JTextField txtMaDoUong;
    private javax.swing.JLabel txtPhanTram;
    private javax.swing.JTextField txtTenDoUong;
    // End of variables declaration//GEN-END:variables
CategoryDAO categoryDao = new CategoryDAOImpl();
    List<Category> categories = List.of();
    DrinkDAO drinkDao = new DrinkDAOImpl();
    List<Drinks> drinks = List.of();

    public void open() {
        this.setLocationRelativeTo(null);
//        this.fillCategories();
//        this.fillDrinks();
    }

//    @Override
//    public void fillCategories() {
//        categories = categoryDao.findAll();
//        DefaultTableModel model = (DefaultTableModel) tblCategories.getModel();
//        model.setRowCount(0);
//        categories.forEach(d -> model.addRow(new Object[]{d.getName()}));
//        tblCategories.setRowSelectionInterval(0, 0);
//    }

//    @Override
//    public void fillDrinks() {
//        Category category = categories.get(tblCategories.getSelectedRow());
//        drinks = drinkDao.findByCategoryId(category.getId());
//        DefaultTableModel model = (DefaultTableModel) tblDrinks.getModel();
//        model.setRowCount(0);
//        drinks.forEach(d -> {
//            Object[] row = {
//                d.getId(),
//                d.getName(),
//                String.format("$%.1f", d.getUnitPrice()),
//                String.format("%.0f%%", d.getDiscount() * 100)
//            };
//            model.addRow(row);
//        });
//    }

//    @Override
//    public void addDrinkToBill() {
//        String quantity = XDialog.prompt("Số lượng?");
//        if (quantity != null && quantity.length() > 0) {
//            Drink drink = drinks.get(tblDrinks.getSelectedRow());
//            BillDetail detail = new BillDetail();
//            detail.setBillId(bill.getId());
//            detail.setDiscount(drink.getDiscount());
//            detail.setDrinkId(drink.getId());
//            detail.setQuantity(Integer.parseInt(quantity));
//            detail.setUnitPrice(drink.getUnitPrice());
//            new BillDetailDAOImpl().create(detail);
//        }
//    }

}
