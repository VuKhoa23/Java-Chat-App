package com.vukhoa23.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {
    public static String username;
    public static String password;
    public static String port;
    public static Connection getConnection() throws SQLException{
        String url = "jdbc:mysql://localhost:" + port + "/chatapp";

        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new SQLException("Cannot connect to DB");
        }
    }
}
