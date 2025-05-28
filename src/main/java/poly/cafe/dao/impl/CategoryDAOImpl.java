/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao.impl;

import java.util.List;
import poly.cafe.dao.CategoryDAO;
import poly.cafe.entity.Categories;
import poly.cafe.util.XJdbc;
import poly.cafe.util.XQuery;

public class CategoryDAOImpl implements CategoryDAO {

    private final String createSql = "INSERT INTO Categories(Id, Name) VALUES(?, ?)";
    private final String updateSql = "UPDATE Categories SET Name=? WHERE Id=?";
    private final String deleteByIdSql = "DELETE FROM Categories WHERE Id=?";
    private final String findAllSql = "SELECT * FROM Categories";
    private final String findByIdSql = findAllSql + " WHERE Id=?";

    @Override
    public Categories create(Categories entity) {
        Object[] values = { entity.getId(), entity.getName() };
        XJdbc.executeUpdate(createSql, values);
        return entity;
    }

    @Override
    public void update(Categories entity) {
        Object[] values = { entity.getName(), entity.getId() };
        XJdbc.executeUpdate(updateSql, values);
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(deleteByIdSql, id);
    }

    @Override
    public List<Categories> findAll() {
        return XQuery.getBeanList(Categories.class, findAllSql);
    }

    @Override
    public Categories findById(String id) {
        return XQuery.getSingleBean(Categories.class, findByIdSql, id);
    }
}
