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

public class FormCustomer extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField idCustomerField;
    private JTextField usernameField;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean isUpdateMode = false;
    private int rowBeingEdited = -1;
    private JLabel idCustomerLabel;

    private List<User> userList = new ArrayList<>();

    public FormCustomer() {
        setTitle("Form Customer");
        setSize(750, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        idCustomerLabel = new JLabel("ID Customer:");
        formPanel.add(idCustomerLabel);

        idCustomerField = new JTextField();
        idCustomerField.setEditable(false);
        formPanel.add(idCustomerField);

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        saveButton = new JButton("Simpan");
        formPanel.add(saveButton);

        cancelButton = new JButton("Batal");
        cancelButton.setVisible(false);
        formPanel.add(cancelButton);

        // Sembunyikan ID Customer saat bukan mode edit
        idCustomerLabel.setVisible(false);
        idCustomerField.setVisible(false);

        tableModel = new DefaultTableModel(new String[]{"ID Customer", "Username", "Edit", "Delete"}, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Simpan tombol
        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi.");
                return;
            }

            if (isUpdateMode) {
                User user = userList.get(rowBeingEdited);
                user.setUsername(username);
                tableModel.setValueAt(user.getUsername(), rowBeingEdited, 1);

                saveButton.setText("Simpan");
                cancelButton.setVisible(false);
                isUpdateMode = false;
                rowBeingEdited = -1;
            } else {
                int id = userList.size() + 1;
                String idCustomer = "CUST" + String.format("%03d", id);

                User user = new User(idCustomer, username);
                userList.add(user);
                tableModel.addRow(new Object[]{idCustomer, username, "Edit", "Delete"});
            }

            clearFields();
        });

        // Batal tombol
        cancelButton.addActionListener(e -> {
            clearFields();
            saveButton.setText("Simpan");
            cancelButton.setVisible(false);
            isUpdateMode = false;
            rowBeingEdited = -1;
        });

        // Kolom "Edit" dan "Delete"
        TableColumn editColumn = userTable.getColumnModel().getColumn(2);
        editColumn.setCellRenderer(new ButtonRenderer("Edit"));
        editColumn.setCellEditor(new UserButtonEditor(new JCheckBox(), "Edit"));

        TableColumn deleteColumn = userTable.getColumnModel().getColumn(3);
        deleteColumn.setCellRenderer(new ButtonRenderer("Delete"));
        deleteColumn.setCellEditor(new UserButtonEditor(new JCheckBox(), "Delete"));
    }

    private void clearFields() {
        idCustomerField.setText("");
        usernameField.setText("");

        idCustomerLabel.setVisible(false);
        idCustomerField.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormCustomer().setVisible(true));
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
                usernameField.setText(user.getUsername());

                idCustomerLabel.setVisible(true);
                idCustomerField.setVisible(true);

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
