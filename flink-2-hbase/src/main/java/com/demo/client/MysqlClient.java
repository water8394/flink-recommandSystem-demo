package com.demo.client;

import java.sql.*;

public class MysqlClient {

    private static String URL = "jdbc:mysql://localhost/con?serverTimezone=GMT%2B8";
    private static String NAME = "root";
    private static String PASS = "root";
    private static Statement stmt;
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, NAME, PASS);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据Id筛选产品
     * @param id
     * @return
     * @throws SQLException
     */
    public static ResultSet selectById(int id) throws SQLException {
        String sql = String.format("select  * from product where product_id = %s",id);
        return stmt.executeQuery(sql);
    }


    public static ResultSet selectUserById(int id) throws SQLException{
        String sql = String.format("select  * from user where user_id = %s",id);
        return stmt.executeQuery(sql);
    }

}
