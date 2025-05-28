/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

import java.util.List;
import poly.cafe.entity.Drinks;

/**
 *
 * @author PC
 */
public interface DrinkDAO extends CrudDAO<Drinks, String> {

    List<Drinks> findByCategoryId(String categoryId);
}
