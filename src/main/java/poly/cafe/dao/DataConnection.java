/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package poly.cafe.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author famut
 */
public class DataConnection {
    public static Connection open() throws ClassNotFoundException, SQLException{
        Connection con=null;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String username = "sa";
        String password = "123";
        String url = "jdbc:sqlserver://localhost:1433;databaseName=PolyCafe";
        con = DriverManager.getConnection(url, username, password);
        return con;
    }
}
