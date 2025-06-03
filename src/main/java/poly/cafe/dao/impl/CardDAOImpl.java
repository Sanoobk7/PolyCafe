/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

/**
 *
 * @author PC
 */

import java.util.List;
import poly.cafe.dao.CardDAO;
import poly.cafe.entity.Cards;
import poly.cafe.util.XQuery;

public class CardDAOImpl implements CardDAO {
    
    

    @Override
    public List<Cards> findAll() {
        String sql = "SELECT * FROM Cards";
        return XQuery.getBeanList(Cards.class, sql);
    }
}