package com.mycompany.mavenproject3.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    private static String _username = "admin";

    public static String getUsername() {
        return _username;
    }

    @SuppressWarnings({"CallToPrintStackTrace", "ConvertToTryWithResources"})
    public static int login(String username, String password) {
        _username = username;

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/login_db",
                    "postgres",
                    "12345");

            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            boolean isValid = rs.next();

            rs.close();
            stmt.close();
            conn.close();

            return isValid ? 1 : 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }
}
