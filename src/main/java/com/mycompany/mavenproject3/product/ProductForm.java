package com.mycompany.mavenproject3.product;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import com.google.gson.reflect.TypeToken;
import com.mycompany.mavenproject3.MoneyFormat;
import com.mycompany.mavenproject3.category.Category;
import com.mycompany.mavenproject3.category.CategoryForm;
import com.mycompany.mavenproject3.category.CategoryService;

public class ProductForm extends JFrame {
    private JTable drinkTable;
    private DefaultTableModel tableModel;
    private JTextField codeField;
    private JTextField nameField;
    private JComboBox<String> categoryField;
    private JTextField priceField;
    private JTextField stockField;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton categoryButton;
    private boolean isUpdateMode = false;
    private int rowBeingEdited = -1;

    public ProductForm() {
        setTitle("WK. Cuan | Stok Barang");
        setSize(600, 450);
        setLocationRelativeTo(null);

        // Panel form pemesanan
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.add(new JLabel("Kode Barang"));
        codeField = new JTextField();
        formPanel.add(codeField);

        formPanel.add(new JLabel("Nama Barang:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Kategori:"));
        categoryField = new JComboBox<>();
        formPanel.add(categoryField);

        categoryButton = new JButton("...");
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.X_AXIS));
        categoryPanel.add(categoryField);
        categoryPanel.add(Box.createHorizontalStrut(5));
        categoryPanel.add(categoryButton);
        formPanel.add(categoryPanel);

        formPanel.add(new JLabel("Harga Jual:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Stok Tersedia:"));
        stockField = new JTextField();
        formPanel.add(stockField);

        saveButton = new JButton("Tambah");
        formPanel.add(saveButton);

        cancelButton = new JButton("Batal");
        cancelButton.setVisible(false);
        formPanel.add(cancelButton);

        tableModel = new DefaultTableModel(
                new String[] { "Kode", "Nama", "Kategori", "Harga Jual", "Stok", "Update", "Delete" }, 0);
        drinkTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(drinkTable);
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        categoryButton.addActionListener(e -> {
            new CategoryForm().setVisible(true);
        });

        loadCategoriesData();
        var categoryListener = CategoryService.addDataChangeListener(e -> loadCategoriesData());

        loadProductData();
        var listener = ProductService.addDataChangeListener(e -> loadProductData());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                CategoryService.removeDataChangeListener(categoryListener);
                ProductService.removeDataChangeListener(listener);
            }
        });

        saveButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            String category = (String) categoryField.getSelectedItem();

            if (code.isEmpty() || name.isEmpty() || priceField.getText().isEmpty() || stockField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi.");
                return;
            }

            double price;
            int stock;
            try {
                price = Double.parseDouble(priceField.getText());
                stock = Integer.parseInt(stockField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka.");
                return;
            }

            try {
                String APIUrl = "http://localhost:4567/api/products";
                URL url;
                HttpURLConnection conn;

                Gson gson = new Gson();
                String json;

                if (isUpdateMode) {
                    Product product = ProductService.getProductByIndex(rowBeingEdited);
                    product.setCode(code);
                    product.setName(name);
                    product.setCategory(category);
                    product.setPrice(price);
                    product.setStock(stock);

                    json = gson.toJson(product);
                    url = new URL(APIUrl + "/" + product.getId());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                } else {
                    json = gson.toJson(new Product(ProductService.getNextId(), code, name, category, price, stock));
                    url = new URL(APIUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                }

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes());
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200 || responseCode == 201) {
                    System.out.println("Produk berhasil ditambahkan.");
                } else {
                    System.out.println("Gagal menambahkan produk. Kode: " + responseCode);
                }
            } catch (Exception ex) {
                System.out.println("Error:\n" + ex.getMessage());
            }

            if (isUpdateMode) {
                isUpdateMode = false;
                rowBeingEdited = -1;
                cancelButton.setVisible(false);
            }

            codeField.setText("");
            nameField.setText("");
            categoryField.setSelectedIndex(0);
            priceField.setText("");
            stockField.setText("");

            loadProductData();
        });

        cancelButton.addActionListener(e -> {
            codeField.setText("");
            nameField.setText("");
            priceField.setText("");
            stockField.setText("");
            categoryField.setSelectedIndex(0);
            saveButton.setText("Tambah");
            cancelButton.setVisible(false);
            isUpdateMode = false;
            rowBeingEdited = -1;
        });

        TableColumn updateColumn = drinkTable.getColumnModel().getColumn(5);
        updateColumn.setCellRenderer(new ButtonRenderer("Update"));
        updateColumn.setCellEditor(new ButtonEditor(
                new JCheckBox(), "Update"));

        TableColumn deleteColumn = drinkTable.getColumnModel().getColumn(6);
        deleteColumn.setCellRenderer(new ButtonRenderer("Delete"));
        deleteColumn.setCellEditor(new ButtonEditor(
                new JCheckBox(), "Delete"));
    }

    private void loadProductData() {
        try {
            URL url = new URL("http://localhost:4567/api/products");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = in.lines().collect(Collectors.joining());
            in.close();

            tableModel.setRowCount(0);
            List<Product> products = new Gson().fromJson(json, new TypeToken<List<Product>>() {
            }.getType());
            for (Product p : products) {
                tableModel.addRow(new Object[] {
                        p.getCode(), p.getName(), p.getCategory(), MoneyFormat.IDR(p.getPrice()), p.getStock(),
                        "Update",
                        "Delete"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data dari API\n" + e.getMessage());
        }
    }

    private void loadCategoriesData() {
        categoryField.removeAllItems();
        for (Category c : CategoryService.getAllCategories()) {
            categoryField.addItem(c.getName());
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String action) {
            setText(action);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private final String label;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            this.label = label;

            button = new JButton(label);
            button.addActionListener(e -> {
                fireEditingStopped();

                if (label.equals("Update")) {
                    Product product = ProductService.getProductByIndex(selectedRow);
                    codeField.setText(product.getCode());
                    nameField.setText(product.getName());
                    categoryField.setSelectedItem(product.getCategory());
                    priceField.setText(String.valueOf(product.getPrice()));
                    stockField.setText(String.valueOf(product.getStock()));

                    isUpdateMode = true;
                    rowBeingEdited = selectedRow;
                    saveButton.setText("Simpan");
                    cancelButton.setVisible(true);
                } else if (label.equals("Delete")) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus produk ini?", "Konfirmasi",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        Product product = ProductService.getProductByIndex(selectedRow);

                        try {
                            String APIUrl = "http://localhost:4567/api/products/" + product.getId();
                            URL url = new URL(APIUrl);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("DELETE");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setDoOutput(true);

                            int responseCode = conn.getResponseCode();
                            if (responseCode == 200 || responseCode == 204) {
                                System.out.println("Produk berhasil dihapus.");
                            } else {
                                System.out.println("Gagal menghapus produk. Kode: " + responseCode);
                            }
                        } catch (Exception ex) {
                            System.out.println("Error:/n" + ex.getMessage());
                        }

                        loadProductData();
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

        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }
    }
}