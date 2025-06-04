/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.dao.DrinkDAO;
import poly.cafe.dao.impl.CategoryDAOImpl;
import poly.cafe.dao.impl.DrinkDAOImpl;
import poly.cafe.entity.Categories;
import poly.cafe.entity.Drinks;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import poly.cafe.dao.DrinkManagementDao;
import poly.cafe.dao.BillDetailsDao;
import poly.cafe.entity.BillDetails;
import poly.cafe.util.XDialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import poly.cafe.dao.BillsManagerDao;

/**
 *
 * @author Home
 */
public class DrinkJDialog extends javax.swing.JDialog {
    private DefaultTableModel categoryTableModel;
    private DefaultTableModel drinkTableModel;
    private DrinkManagementDao dao = new DrinkManagementDao();
    private BillJDialog billDialog; // Reference to BillJDialog
    private long billId; // Bill ID for associating BillDetails

    /**
     * Creates new form DrinkJDialog
     */
    public DrinkJDialog(java.awt.Frame parent, boolean modal, BillJDialog billDialog, long billId) {
        super(parent, modal);
        this.billDialog = billDialog;
        this.billId = billId;
        System.out.println("DrinkJDialog initialized with billId: " + this.billId); // Log để debug
        initComponents();
        setLocationRelativeTo(null);
        initDrinkTable();
        initCategoryTable();
        styleCategoryTable();
        loadDrinkDataToTable();
        loadCategoryDataToTable();
        initTableSelectListener();
        System.out.println("DrinkJDialog after initComponents, billId: " + this.billId); // Log để kiểm tra sau initComponents
    }

    private void initDrinkTable() {
        drinkTableModel = new DefaultTableModel(
            new String[]{"Mã đồ uống", "Tên đồ uống", "Đơn giá", "Giảm giá"}, 0
        ) {
            Class<?>[] types = new Class<?>[]{String.class, String.class, Double.class, Double.class};

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        TableSelect.setModel(drinkTableModel);
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

    private void styleCategoryTable() {
        tblDrinks.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(c.getFont().deriveFont(java.awt.Font.BOLD));
                if (isSelected) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLUE);
                }
                return c;
            }
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
                if (drink.isAvailable()) { // Only add available drinks
                    drinkTableModel.addRow(new Object[]{
                        drink.getId(),
                        drink.getName(),
                        drink.getUnitPrice(),
                        drink.getDiscount()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

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
                tblDrinks.setRowSelectionInterval(0, 0);
                filterDrinksByCategory(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu loại đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterDrinksByCategory(int row) {
        if (row >= 0 && row < tblDrinks.getRowCount()) {
            String categoryName = (String) tblDrinks.getValueAt(row, 0);
            Categories category = dao.findCategoryByName(categoryName);
            if (category != null) {
                drinkTableModel.setRowCount(0); // Clear old data
                List<Drinks> drinks = dao.findByCategoryId(category.getId());
                if (drinks != null) {
                    try {
                        for (Drinks drink : drinks) {
                            if (drink.isAvailable()) { // Only add available drinks
                                String status = drink.isAvailable() ? "Sẵn sàng" : "Hết";
                                drinkTableModel.addRow(new Object[]{
                                    drink.getId(),
                                    drink.getName(),
                                    drink.getUnitPrice(),
                                    drink.getDiscount(),
                                    status,
                                    categoryName,
                                    false // Checkbox default
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void initTableSelectListener() {
        TableSelect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = TableSelect.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    TableSelect.setRowSelectionInterval(row, row);
                    addDrinkToBill(row);
                }
            }
        });
    }

    private void addDrinkToBill(int row) {
        try {
            String drinkId = (String) TableSelect.getValueAt(row, 0);
            String drinkName = (String) TableSelect.getValueAt(row, 1);
            double unitPrice = (Double) TableSelect.getValueAt(row, 2);
            double discount = (Double) TableSelect.getValueAt(row, 3);

            String quantityStr = XDialog.prompt("Nhập số lượng cho " + drinkName + ":", "Nhập số lượng");
            if (quantityStr == null || quantityStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Số lượng không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

            // Log billId để debug
            System.out.println("addDrinkToBill using billId: " + this.billId);

            // Validate billId với checkBillExists2
            BillsManagerDao billsDao = new BillsManagerDao();
            if (!billsDao.checkBillExists2(this.billId)) {
                JOptionPane.showMessageDialog(this, "Hóa đơn không tồn tại! billId: " + this.billId, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BillDetails billDetail = new BillDetails();
            billDetail.setBillId(this.billId);
            billDetail.setDrinkId(drinkId);
            billDetail.setUnitPrice(unitPrice);
            billDetail.setDiscount(discount);
            billDetail.setQuantity(quantity);

            BillDetailsDao billDetailsDao = new BillDetailsDao();
            if (billDetailsDao.insert(billDetail)) {
                billDialog.loadBillDetails(this.billId);
                setVisible(false);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm đồ uống vào hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm đồ uống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        TableSelect = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDrinks = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Chọn đồ uống");

        TableSelect.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Mã", "Tên đồ uống", "Đơn giá", "Giảm giá"
            }
        ));
        jScrollPane1.setViewportView(TableSelect);

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
        ));
        tblDrinks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDrinksMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblDrinks);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblDrinksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDrinksMouseClicked
        int row = tblDrinks.rowAtPoint(evt.getPoint());
    if (row >= 0) {
        tblDrinks.setRowSelectionInterval(row, row);
        filterDrinksByCategory(row);
    }
    }//GEN-LAST:event_tblDrinksMouseClicked

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
            java.util.logging.Logger.getLogger(DrinkJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DrinkJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DrinkJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DrinkJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BillJDialog billDialog = new BillJDialog(new javax.swing.JFrame(), true);
                DrinkJDialog dialog = new DrinkJDialog(new javax.swing.JFrame(), true, billDialog, 10016L);
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
    private javax.swing.JTable TableSelect;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblDrinks;
    // End of variables declaration//GEN-END:variables
}

