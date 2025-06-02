/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package poly.cafe.ui;

import java.awt.Color;
import java.awt.Frame;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import poly.cafe.ui.BillJDialog;
import poly.cafe.dao.BillsManagerDao;
import poly.cafe.dao.AccountManagementDao;
import poly.cafe.entity.Bills;
import poly.cafe.util.XAuth;
import poly.cafe.dao.DataConnection;
import poly.cafe.dao.IDCardManagementDao;
import poly.cafe.ui.IDCardManagementJDialog;
/**
 *
 * @author Home
 */
public class SaleJDialog extends javax.swing.JDialog {
private final Frame parentFrame; // Store the parent Frame
    /**
     * Creates new form SaleJDialog
     */
    public SaleJDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.parentFrame = parent; // Save the parent Frame
        setLocationRelativeTo(null);
        updateCardStatus(); // Cập nhật trạng thái nút khi khởi tạo
    }
  
 void openBillDialog(int cardId) {
        BillsManagerDao billsDao = new BillsManagerDao();
        IDCardManagementDao cardDao = new IDCardManagementDao();

        // Kiểm tra trạng thái thẻ
        int cardStatus = cardDao.getCardStatus(cardId);
        if (cardStatus == 2) {
            JOptionPane.showMessageDialog(this, "Thẻ số " + cardId + " đang ở trạng thái Lỗi, không thể mở hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (cardStatus == 3) {
            JOptionPane.showMessageDialog(this, "Thẻ số " + cardId + " đang ở trạng thái Thất bại, không thể mở hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        } else if (cardStatus != 1) {
            JOptionPane.showMessageDialog(this, "Thẻ số " + cardId + " không ở trạng thái Đang hoạt động!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate CardId
        if (cardId < 1 || cardId > 30) {
            JOptionPane.showMessageDialog(this, "Thẻ số phải nằm trong khoảng từ 1 đến 30!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!billsDao.checkCardIdExists(cardId)) {
            JOptionPane.showMessageDialog(this, "Thẻ số không tồn tại trong cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for existing bill with Status = 0 for the given CardId
        String sql = "SELECT * FROM Bills WHERE CardId = ? AND Status = 0";
        Bills bill = null;
        try (Connection con = DataConnection.open();
             PreparedStatement pre = con.prepareStatement(sql)) {
            pre.setInt(1, cardId);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                bill = new Bills();
                bill.setId(rs.getLong("Id"));
                bill.setUsername(rs.getString("Username"));
                bill.setCardId(rs.getInt("CardId"));
                bill.setCheckin(rs.getTimestamp("Checkin"));
                bill.setCheckout(rs.getTimestamp("Checkout"));
                bill.setStatus(rs.getInt("Status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If no bill is found, create a new bill
        if (bill == null) {
            bill = new Bills();
            bill.setUsername(XAuth.user != null ? XAuth.user.getUsername() : "");
            bill.setCardId(cardId);
            bill.setCheckin(new Date()); // Current date and time
            bill.setCheckout(null); // No checkout time yet
            bill.setStatus(0); // Đang phục vụ

            // Validate username
            if (bill.getUsername().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng đang đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert new bill
            try {
                if (!billsDao.insert(bill)) {
                    JOptionPane.showMessageDialog(this, "Tạo hóa đơn mới thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Retrieve the newly created bill to get its ID
                sql = "SELECT TOP 1 * FROM Bills WHERE CardId = ? AND Status = 0 ORDER BY Checkin DESC";
                try (Connection con = DataConnection.open();
                     PreparedStatement pre = con.prepareStatement(sql)) {
                    pre.setInt(1, cardId);
                    ResultSet rs = pre.executeQuery();
                    if (rs.next()) {
                        bill.setId(rs.getLong("Id"));
                        bill.setUsername(rs.getString("Username"));
                        bill.setCardId(rs.getInt("CardId"));
                        bill.setCheckin(rs.getTimestamp("Checkin"));
                        bill.setCheckout(rs.getTimestamp("Checkout"));
                        bill.setStatus(rs.getInt("Status"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn mới: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Open BillJDialog with the bill details
        try {
            BillJDialog billDialog = new BillJDialog(parentFrame, true);
            billDialog.setBillDetails(bill); // Assuming a method to pass bill data
            billDialog.setVisible(true);
            updateCardStatus(); // Cập nhật trạng thái nút sau khi đóng BillJDialog
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi mở giao diện chi tiết hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCardStatus() {
    BillsManagerDao billsDao = new BillsManagerDao();
    IDCardManagementDao cardDao = new IDCardManagementDao();
    for (int i = 1; i <= 30; i++) {
        JButton button = getButton(i); // Dòng này gây lỗi nếu getButton không tồn tại
        int cardStatus = cardDao.getCardStatus(i);
        boolean hasActiveBill = billsDao.isCardOccupied(i); // Kiểm tra thẻ có bill với Status = 0

        if (cardStatus == 2 || cardStatus == 3) {
            button.setBackground(Color.DARK_GRAY);
            button.setForeground(Color.WHITE);
            button.setEnabled(false); // Không thể tương tác
        } else if (cardStatus == 1) {
            if (hasActiveBill) {
                button.setBackground(Color.GREEN); // Thẻ đang hoạt động và có hóa đơn
                button.setForeground(Color.BLACK);
                button.setEnabled(true);
            } else {
                button.setBackground(null); // Thẻ đang hoạt động nhưng không có hóa đơn
                button.setForeground(Color.BLACK);
                button.setEnabled(true);
            }
        } else {
            button.setBackground(Color.DARK_GRAY); // Trạng thái không xác định
            button.setForeground(Color.WHITE);
            button.setEnabled(false);
        }
    }
}

private JButton getButton(int cardId) {
    switch (cardId) {
        case 1: return BtnCard1;
        case 2: return BtnCard2;
        case 3: return BtnCard3;
        case 4: return BtnCard4;
        case 5: return BtnCard5;
        case 6: return BtnCard6;
        case 7: return BtnCard7;
        case 8: return BtnCard8;
        case 9: return BtnCard9;
        case 10: return BtnCard10;
        case 11: return BtnCard11;
        case 12: return BtnCard12;
        case 13: return BtnCard13;
        case 14: return BtnCard14;
        case 15: return BtnCard15;
        case 16: return BtnCard16;
        case 17: return BtnCard17;
        case 18: return BtnCard18;
        case 19: return BtnCard19;
        case 20: return BtnCard20;
        case 21: return BtnCard21;
        case 22: return BtnCard22;
        case 23: return BtnCard23;
        case 24: return BtnCard24;
        case 25: return BtnCard25;
        case 26: return BtnCard26;
        case 27: return BtnCard27;
        case 28: return BtnCard28;
        case 29: return BtnCard29;
        case 30: return BtnCard30;
        default: return null;
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

        jPanel1 = new javax.swing.JPanel();
        BtnCard1 = new javax.swing.JButton();
        BtnCard2 = new javax.swing.JButton();
        BtnCard3 = new javax.swing.JButton();
        BtnCard4 = new javax.swing.JButton();
        BtnCard5 = new javax.swing.JButton();
        BtnCard6 = new javax.swing.JButton();
        BtnCard7 = new javax.swing.JButton();
        BtnCard8 = new javax.swing.JButton();
        BtnCard9 = new javax.swing.JButton();
        BtnCard10 = new javax.swing.JButton();
        BtnCard11 = new javax.swing.JButton();
        BtnCard12 = new javax.swing.JButton();
        BtnCard13 = new javax.swing.JButton();
        BtnCard14 = new javax.swing.JButton();
        BtnCard15 = new javax.swing.JButton();
        BtnCard16 = new javax.swing.JButton();
        BtnCard17 = new javax.swing.JButton();
        BtnCard18 = new javax.swing.JButton();
        BtnCard19 = new javax.swing.JButton();
        BtnCard20 = new javax.swing.JButton();
        BtnCard21 = new javax.swing.JButton();
        BtnCard22 = new javax.swing.JButton();
        BtnCard23 = new javax.swing.JButton();
        BtnCard24 = new javax.swing.JButton();
        BtnCard25 = new javax.swing.JButton();
        BtnCard26 = new javax.swing.JButton();
        BtnCard27 = new javax.swing.JButton();
        BtnCard28 = new javax.swing.JButton();
        BtnCard29 = new javax.swing.JButton();
        BtnCard30 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Thẻ định danh");

        jPanel1.setLayout(new java.awt.GridLayout(0, 6, 5, 5));

        BtnCard1.setText("Card #1");
        BtnCard1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard1ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard1);

        BtnCard2.setText("Card #2");
        BtnCard2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard2ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard2);

        BtnCard3.setText("Card #3");
        BtnCard3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard3ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard3);

        BtnCard4.setText("Card #4");
        BtnCard4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard4ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard4);

        BtnCard5.setText("Card #5");
        BtnCard5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard5ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard5);

        BtnCard6.setText("Card #6");
        BtnCard6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard6ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard6);

        BtnCard7.setText("Card #7");
        BtnCard7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard7ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard7);

        BtnCard8.setText("Card #8");
        BtnCard8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard8ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard8);

        BtnCard9.setText("Card #9");
        BtnCard9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard9ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard9);

        BtnCard10.setText("Card #10");
        BtnCard10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard10ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard10);

        BtnCard11.setText("Card #11");
        BtnCard11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard11ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard11);

        BtnCard12.setText("Card #12");
        BtnCard12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard12ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard12);

        BtnCard13.setText("Card #13");
        BtnCard13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard13ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard13);

        BtnCard14.setText("Card #14");
        BtnCard14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard14ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard14);

        BtnCard15.setText("Card #15");
        BtnCard15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard15ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard15);

        BtnCard16.setText("Card #16");
        BtnCard16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard16ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard16);

        BtnCard17.setText("Card #17");
        BtnCard17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard17ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard17);

        BtnCard18.setText("Card #18");
        BtnCard18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard18ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard18);

        BtnCard19.setText("Card #19");
        BtnCard19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard19ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard19);

        BtnCard20.setText("Card #20");
        BtnCard20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard20ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard20);

        BtnCard21.setText("Card #21");
        BtnCard21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard21ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard21);

        BtnCard22.setText("Card #22");
        BtnCard22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard22ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard22);

        BtnCard23.setText("Card #23");
        BtnCard23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard23ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard23);

        BtnCard24.setText("Card #24");
        BtnCard24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard24ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard24);

        BtnCard25.setText("Card #25");
        BtnCard25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard25ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard25);

        BtnCard26.setText("Card #26");
        BtnCard26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard26ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard26);

        BtnCard27.setText("Card #27");
        BtnCard27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard27ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard27);

        BtnCard28.setText("Card #28");
        BtnCard28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard28ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard28);

        BtnCard29.setText("Card #29");
        BtnCard29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard29ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard29);

        BtnCard30.setText("Card #30");
        BtnCard30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCard30ActionPerformed(evt);
            }
        });
        jPanel1.add(BtnCard30);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnCard10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard10ActionPerformed
       openBillDialog(10);
    }//GEN-LAST:event_BtnCard10ActionPerformed

    private void BtnCard15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard15ActionPerformed
       openBillDialog(15);
    }//GEN-LAST:event_BtnCard15ActionPerformed

    private void BtnCard16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard16ActionPerformed
       openBillDialog(16);
    }//GEN-LAST:event_BtnCard16ActionPerformed

    private void BtnCard1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard1ActionPerformed
       openBillDialog(1);
    }//GEN-LAST:event_BtnCard1ActionPerformed

    private void BtnCard2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard2ActionPerformed
       openBillDialog(2);
    }//GEN-LAST:event_BtnCard2ActionPerformed

    private void BtnCard3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard3ActionPerformed
       openBillDialog(3);
    }//GEN-LAST:event_BtnCard3ActionPerformed

    private void BtnCard4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard4ActionPerformed
       openBillDialog(4);
    }//GEN-LAST:event_BtnCard4ActionPerformed

    private void BtnCard5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard5ActionPerformed
       openBillDialog(5);
    }//GEN-LAST:event_BtnCard5ActionPerformed

    private void BtnCard6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard6ActionPerformed
       openBillDialog(6);
    }//GEN-LAST:event_BtnCard6ActionPerformed

    private void BtnCard7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard7ActionPerformed
       openBillDialog(7);
    }//GEN-LAST:event_BtnCard7ActionPerformed

    private void BtnCard8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard8ActionPerformed
      openBillDialog(8);
    }//GEN-LAST:event_BtnCard8ActionPerformed

    private void BtnCard9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard9ActionPerformed
       openBillDialog(9);
    }//GEN-LAST:event_BtnCard9ActionPerformed

    private void BtnCard11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard11ActionPerformed
       openBillDialog(11);
    }//GEN-LAST:event_BtnCard11ActionPerformed

    private void BtnCard12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard12ActionPerformed
      openBillDialog(12);
    }//GEN-LAST:event_BtnCard12ActionPerformed

    private void BtnCard13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard13ActionPerformed
      openBillDialog(13);
    }//GEN-LAST:event_BtnCard13ActionPerformed

    private void BtnCard14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard14ActionPerformed
      openBillDialog(14);
    }//GEN-LAST:event_BtnCard14ActionPerformed

    private void BtnCard17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard17ActionPerformed
      openBillDialog(17);
    }//GEN-LAST:event_BtnCard17ActionPerformed

    private void BtnCard18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard18ActionPerformed
      openBillDialog(18);
    }//GEN-LAST:event_BtnCard18ActionPerformed

    private void BtnCard19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard19ActionPerformed
       openBillDialog(19);
    }//GEN-LAST:event_BtnCard19ActionPerformed

    private void BtnCard20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard20ActionPerformed
       openBillDialog(20);
    }//GEN-LAST:event_BtnCard20ActionPerformed

    private void BtnCard21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard21ActionPerformed
       openBillDialog(21);
    }//GEN-LAST:event_BtnCard21ActionPerformed

    private void BtnCard22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard22ActionPerformed
       openBillDialog(22);
    }//GEN-LAST:event_BtnCard22ActionPerformed

    private void BtnCard23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard23ActionPerformed
       openBillDialog(23);
    }//GEN-LAST:event_BtnCard23ActionPerformed

    private void BtnCard24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard24ActionPerformed
       openBillDialog(24);
    }//GEN-LAST:event_BtnCard24ActionPerformed

    private void BtnCard25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard25ActionPerformed
        openBillDialog(25);
    }//GEN-LAST:event_BtnCard25ActionPerformed

    private void BtnCard26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard26ActionPerformed
      openBillDialog(26);
    }//GEN-LAST:event_BtnCard26ActionPerformed

    private void BtnCard27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard27ActionPerformed
       openBillDialog(27);
    }//GEN-LAST:event_BtnCard27ActionPerformed

    private void BtnCard28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard28ActionPerformed
       openBillDialog(28);
    }//GEN-LAST:event_BtnCard28ActionPerformed

    private void BtnCard29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard29ActionPerformed
       openBillDialog(29);
    }//GEN-LAST:event_BtnCard29ActionPerformed

    private void BtnCard30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCard30ActionPerformed
       openBillDialog(30);
    }//GEN-LAST:event_BtnCard30ActionPerformed

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
            java.util.logging.Logger.getLogger(SaleJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SaleJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SaleJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SaleJDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SaleJDialog dialog = new SaleJDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton BtnCard1;
    private javax.swing.JButton BtnCard10;
    private javax.swing.JButton BtnCard11;
    private javax.swing.JButton BtnCard12;
    private javax.swing.JButton BtnCard13;
    private javax.swing.JButton BtnCard14;
    private javax.swing.JButton BtnCard15;
    private javax.swing.JButton BtnCard16;
    private javax.swing.JButton BtnCard17;
    private javax.swing.JButton BtnCard18;
    private javax.swing.JButton BtnCard19;
    private javax.swing.JButton BtnCard2;
    private javax.swing.JButton BtnCard20;
    private javax.swing.JButton BtnCard21;
    private javax.swing.JButton BtnCard22;
    private javax.swing.JButton BtnCard23;
    private javax.swing.JButton BtnCard24;
    private javax.swing.JButton BtnCard25;
    private javax.swing.JButton BtnCard26;
    private javax.swing.JButton BtnCard27;
    private javax.swing.JButton BtnCard28;
    private javax.swing.JButton BtnCard29;
    private javax.swing.JButton BtnCard3;
    private javax.swing.JButton BtnCard30;
    private javax.swing.JButton BtnCard4;
    private javax.swing.JButton BtnCard5;
    private javax.swing.JButton BtnCard6;
    private javax.swing.JButton BtnCard7;
    private javax.swing.JButton BtnCard8;
    private javax.swing.JButton BtnCard9;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
