package com.vukhoa23.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {
    public static Connection getConnection(){
        String url = "jdbc:mysql://localhost:3306/chatapp";
        String username = "root";
        String password = "anhkhoa0123";
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to DB");
        }
    }
}
