/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package poly.cafe.dao;

/**
 *
 * @author PC
 */
import java.util.List;
import poly.cafe.entity.Cards;

public interface CardDAO {
    List<Cards> findAll();
}
