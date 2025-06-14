package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class FormUser extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField idCustomerField;
    private JTextField orderIdField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField passwordField;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean isUpdateMode = false;
    private int rowBeingEdited = -1;

    private List<User> userList = new ArrayList<>();

    public FormUser() {
        setTitle("Form Customer");
        setSize(750, 450);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        formPanel.add(new JLabel("ID Customer:"));
        idCustomerField = new JTextField();
        idCustomerField.setEditable(false);
        formPanel.add(idCustomerField);

        formPanel.add(new JLabel("Order ID:"));
        orderIdField = new JTextField();
        orderIdField.setEditable(false);
        formPanel.add(orderIdField);

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JTextField();
        formPanel.add(passwordField);

        saveButton = new JButton("Simpan");
        formPanel.add(saveButton);

        cancelButton = new JButton("Batal");
        cancelButton.setVisible(false);
        formPanel.add(cancelButton);

        tableModel = new DefaultTableModel(new String[]{"ID Customer", "Order ID", "Username", "Email", "Password", "Edit", "Delete"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        saveButton.addActionListener(e -> {
            String username  = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi.");
                return;
            }
                        
            if (isUpdateMode) {
                User user = userList.get(rowBeingEdited);
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);

                tableModel.setValueAt(user.getUsername(), rowBeingEdited, 2);
                tableModel.setValueAt(user.getEmail(), rowBeingEdited, 3);
                tableModel.setValueAt(user.getPassword(), rowBeingEdited, 4);

                saveButton.setText("Simpan");
                cancelButton.setVisible(false);
                isUpdateMode = false;
                rowBeingEdited = -1;
            } else {
                int id = userList.size() + 1;
                String idCustomer = "CUST" + String.format("%03d", id);
                String orderId = "ORD" + System.currentTimeMillis();

                User user = new User(idCustomer, orderId, username, email, password);
                userList.add(user);
                tableModel.addRow(new Object[]{idCustomer, orderId, username, email, password, "Edit", "Delete"});
            }

            clearFields();
        });

        cancelButton.addActionListener(e -> {
            clearFields();
            saveButton.setText("Simpan");
            cancelButton.setVisible(false);
            isUpdateMode = false;
            rowBeingEdited = -1;
        });

        TableColumn editColumn = userTable.getColumnModel().getColumn(5);
        editColumn.setCellRenderer(new ButtonRenderer("Edit"));
        editColumn.setCellEditor(new UserButtonEditor(new JCheckBox(), "Edit"));

        TableColumn deleteColumn = userTable.getColumnModel().getColumn(6);
        deleteColumn.setCellRenderer(new ButtonRenderer("Delete"));
        deleteColumn.setCellEditor(new UserButtonEditor(new JCheckBox(), "Delete"));
    }

    private void clearFields() {
        idCustomerField.setText("");
        orderIdField.setText("");
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormUser().setVisible(true));
    }

    // ----- Supporting Classes -----

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String label) {
            setText(label);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class UserButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private int selectedRow;

        public UserButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            this.label = label;
            button = new JButton(label);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (label.equals("Edit")) {
                User user = userList.get(selectedRow);
                idCustomerField.setText(user.getIdCustomer());
                orderIdField.setText(user.getOrderId());
                usernameField.setText(user.getUsername());
                emailField.setText(user.getEmail());
                passwordField.setText(user.getPassword());

                saveButton.setText("Update");
                cancelButton.setVisible(true);
                isUpdateMode = true;
                rowBeingEdited = selectedRow;
            } else if (label.equals("Delete")) {
                int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus user ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    userList.remove(selectedRow);
                    tableModel.removeRow(selectedRow);
                }
            }
            return label;
        }
    }
}
