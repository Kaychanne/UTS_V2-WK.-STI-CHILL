package com.mycompany.mavenproject3.customer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.google.gson.Gson;

public class CustomerForm extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField idCustomerField;
    private JTextField usernameField;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean isUpdateMode = false;
    private int rowBeingEdited = -1;
    private JLabel idCustomerLabel;

    public CustomerForm() {
        setTitle("Form Customer");
        setSize(750, 450);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        idCustomerLabel = new JLabel("Kode Pelanggan:");
        formPanel.add(idCustomerLabel);

        idCustomerField = new JTextField();
        idCustomerField.setEditable(false);
        formPanel.add(idCustomerField);

        formPanel.add(new JLabel("Nama:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        saveButton = new JButton("Tambah");
        formPanel.add(saveButton);

        cancelButton = new JButton("Batal");
        cancelButton.setVisible(false);
        formPanel.add(cancelButton);

        // Sembunyikan ID Customer saat bukan mode edit
        idCustomerLabel.setVisible(false);
        idCustomerField.setVisible(false);

        tableModel = new DefaultTableModel(new String[] { "Kode Pelanggan", "Nama", "Update", "Delete" }, 0);
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
                Customer customer = CustomerService.getCustomerByIndex(rowBeingEdited);
                customer.setName(username);
                CustomerService.updateCustomer(customer);

                tableModel.setValueAt(customer.getName(), rowBeingEdited, 1);

                saveButton.setText("Tambah");
                cancelButton.setVisible(false);
                isUpdateMode = false;
                rowBeingEdited = -1;
            } else {
                int nextId = CustomerService.getNextId();
                String idCustomer = String.format("C%03d", nextId);

                Customer customer = new Customer(nextId, idCustomer, username);
                CustomerService.addCustomer(customer);
            }

             try {
                String APIUrl = "http://localhost:4567/api/customer";
                URL url;
                HttpURLConnection conn;

                Gson gson = new Gson();
                Customer payload;

                if (isUpdateMode) {
                    int id = CustomerService.getCustomerByIndex(rowBeingEdited).getId();
                    String code = CustomerService.getCustomerByIndex(rowBeingEdited).getCode();
                    payload = new Customer(0, code, username);

                    url = new URL(APIUrl + "/" + id);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                } else {
                    int nextId = CustomerService.getCurrentId();
                    String idCustomer = String.format("C%03d", nextId);
                    payload = new Customer(0, idCustomer, username);

                    url = new URL(APIUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                }

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String json = gson.toJson(payload);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes());
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200 || responseCode == 201) {
                    System.out.println("Customer berhasil disimpan ke API.");
                } else {
                    System.out.println("Gagal menyimpan ke API. Code: " + responseCode);
                }
            } catch (Exception ex) {
                System.out.println("Error API:\n" + ex.getMessage());
            }

            clearFields();
        });

        // Batal tombol
        cancelButton.addActionListener(e -> {
            clearFields();
            saveButton.setText("Tambah");
            cancelButton.setVisible(false);
            isUpdateMode = false;
            rowBeingEdited = -1;
        });

        // Kolom "Update" dan "Delete"
        TableColumn editColumn = userTable.getColumnModel().getColumn(2);
        editColumn.setCellRenderer(new ButtonRenderer("Update"));
        editColumn.setCellEditor(new UserButtonEditor(new JCheckBox(), "Update"));

        TableColumn deleteColumn = userTable.getColumnModel().getColumn(3);
        deleteColumn.setCellRenderer(new ButtonRenderer("Delete"));
        deleteColumn.setCellEditor(new UserButtonEditor(new JCheckBox(), "Delete"));

        loadCustomersData();
        var listener = CustomerService.addDataChangeListener(e -> loadCustomersData());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                CustomerService.removeDataChangeListener(listener);
            }
        });
    }

    private void loadCustomersData() {
        try {
            URL url = new URL("http://localhost:4567/api/customer");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = in.lines().collect(Collectors.joining());
            in.close();

            tableModel.setRowCount(0);
            var customers = CustomerService.getAllCustomers();
            for (Customer c : customers) {
                tableModel.addRow(new Object[] { c.getCode(), c.getName(), "Update", "Delete" });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data\n" + e.getMessage());
        }   
    }

    private void clearFields() {
        idCustomerField.setText("");
        usernameField.setText("");

        idCustomerLabel.setVisible(false);
        idCustomerField.setVisible(false);
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
        private final JButton button;
        private final String label;
        private int selectedRow;

        public UserButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            this.label = label;
            button = new JButton(label);
            button.addActionListener(e -> {
                fireEditingStopped();

                if (label.equals("Update")) {
                    Customer customer = CustomerService.getCustomerByIndex(selectedRow);
                    idCustomerField.setText(customer.getCode());
                    usernameField.setText(customer.getName());

                    idCustomerLabel.setVisible(true);
                    idCustomerField.setVisible(true);

                    saveButton.setText("Simpan");
                    cancelButton.setVisible(true);
                    isUpdateMode = true;
                    rowBeingEdited = selectedRow;
                    try {
                        String APIUrl = "http://localhost:4567/api/customer/" + customer.getId();
                        URL url = new URL(APIUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("PUT");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);

                        Gson gson = new Gson();
                        String json = gson.toJson(customer);

                        try (OutputStream os = conn.getOutputStream()) {
                            os.write(json.getBytes());
                            os.flush();
                        }

                        int responseCode = conn.getResponseCode();
                        if (responseCode == 200) {
                            System.out.println("Customer berhasil diupdate lewat API.");
                        } else {
                            System.out.println("Gagal update customer lewat API. Code: " + responseCode);
                        }
                    } catch (Exception ex) {
                        System.out.println("Error API Update:\n" + ex.getMessage());
                    }
                } else if (label.equals("Delete")) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus user ini?", "Konfirmasi",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        Customer customer = CustomerService.getCustomerByIndex(selectedRow);
                        CustomerService.deleteCustomerByIndex(selectedRow);
                        tableModel.removeRow(selectedRow);

                        try {
                            String APIUrl = "http://localhost:4567/api/customer/" + customer.getId();
                            URL url = new URL(APIUrl);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("DELETE");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setDoOutput(true);

                            int responseCode = conn.getResponseCode();
                            if (responseCode == 200 || responseCode == 204) {
                                System.out.println("Customer berhasil dihapus.");
                            } else {
                                System.out.println("Gagal menghapus customer. Code: " + responseCode);
                            }
                        } catch (Exception ex) {
                            System.out.println("Error API Delete:\n" + ex.getMessage());
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }
    }

}
