package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class FormUser extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField idCustomerField;
    private JTextField orderIdField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField passwordField;
    private JButton saveButton;
    private List<User> userList = new ArrayList<>();

    public FormUser() {
        setTitle("Form Customer");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));

       
        formPanel.add(new JLabel("ID Customer"));
        idCustomerField = new JTextField();
        idCustomerField.setEditable(false); 
        formPanel.add(idCustomerField);

        
        formPanel.add(new JLabel("Order ID"));
        orderIdField = new JTextField();
        orderIdField.setEditable(false);
        formPanel.add(orderIdField);

        
        formPanel.add(new JLabel("Username"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

     
        formPanel.add(new JLabel("Email"));
        emailField = new JTextField();
        formPanel.add(emailField);

      
        formPanel.add(new JLabel("Password"));
        passwordField = new JTextField();
        formPanel.add(passwordField);

      
        saveButton = new JButton("Simpan");
        formPanel.add(saveButton);

       
        tableModel = new DefaultTableModel(new String[]{"ID Customer", "Order ID", "Username", "Email", "Password"}, 0);
        userTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(userTable);
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

     
        generateIds();

      
        saveButton.addActionListener(e -> {
            String idCustomer = idCustomerField.getText().trim();
            String orderId = orderIdField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi.");
                return;
            }

            int id = userList.size() + 1;
            User user = new User(id, idCustomer, orderId, username, email, password);
            userList.add(user);

          
            tableModel.addRow(new Object[]{idCustomer, orderId, username, email, password});

            usernameField.setText("");
            emailField.setText("");
            passwordField.setText("");

      
            generateIds();
        });
    }

  
    private void generateIds() {
        int nextId = userList.size() + 1;
        idCustomerField.setText("CUST" + String.format("%03d", nextId));
        orderIdField.setText("ORD" + System.currentTimeMillis());
    }

    public static void main(String[] args) {
        new FormUser().setVisible(true);
    }
}
