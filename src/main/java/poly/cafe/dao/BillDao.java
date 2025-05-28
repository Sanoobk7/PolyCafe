/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

import java.util.Date;
import java.util.List;
import poly.cafe.entity.Bills;

public interface BillDao extends CrudDAO<Bills, Long> {
    List<Bills> findByTimeRange(Date begin, Date end); // Bills với 's'
}

