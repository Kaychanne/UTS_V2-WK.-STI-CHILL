package com.mycompany.mavenproject3.auth;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.mycompany.mavenproject3.Mavenproject3;
import com.mycompany.mavenproject3.category.CategoryService;
import com.mycompany.mavenproject3.customer.CustomerService;
import com.mycompany.mavenproject3.product.ProductService;

public class LoginForm extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JLabel statusLabel;

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

        loginButton.addActionListener(e -> login());
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // 0 = failed, 1 = success, -1 = error
        var status = AuthService.login(username, password);

        switch (status) {
            case 1 -> {
                JOptionPane.showMessageDialog(this, "Login berhasil!");
                dispose();
                CategoryService.init();
                CustomerService.init();
                ProductService.init();
                new Mavenproject3(Mavenproject3.buildBannerText()).setVisible(true);
            }
            case 0 -> statusLabel.setText("Login gagal. Coba lagi.");
            default -> statusLabel.setText("Kesalahan koneksi.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
