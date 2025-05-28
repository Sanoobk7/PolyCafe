/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.ui.manager;
import poly.cafe.entity.Bills;
import poly.cafe.entity.BillDetails;
/**
 *
 * @author Home
 */
public interface BillController extends CrudController<Bills>{
    void fillBillDetails(); // tải và hiển thị chi tiết phiếu
    void selectTimeRange(); // xử lý chọn khoảng thời gian trong cboTimeRanges
}
