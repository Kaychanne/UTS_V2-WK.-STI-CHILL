package com.mycompany.mavenproject3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginForm() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        statusLabel = new JLabel(" ", SwingConstants.CENTER);

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);
        add(statusLabel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/login_db", 
                "postgres", 
                "12345"  
            );

            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login berhasil!");
                dispose(); 

               
                if (ProductService.getAllProducts().isEmpty()) {
                    ProductService.addProduct(new Product(ProductService.getNextId(), "P001", "Americano", "Coffee", 18000, 10));
                    ProductService.addProduct(new Product(ProductService.getNextId(), "P002", "Pandan Latte", "Coffee", 15000, 8));
                }

                
                new Mavenproject3(Mavenproject3.buildBannerText()).setVisible(true);
            } else {
                statusLabel.setText("Login gagal. Coba lagi.");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Kesalahan koneksi.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
