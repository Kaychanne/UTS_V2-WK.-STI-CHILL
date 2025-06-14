package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;

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
        categoryField = new JComboBox<>(new String[] { "Coffee", "Dairy", "Juice", "Soda", "Tea" });
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Harga Jual:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("Stok Tersedia:"));
        stockField = new JTextField();
        formPanel.add(stockField);

        saveButton = new JButton("Simpan");
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

        loadProductData();

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

            if (isUpdateMode) {
                Product product = ProductService.getProductByIndex(rowBeingEdited);
                product.setCode(code);
                product.setName(name);
                product.setCategory(category);
                product.setPrice(price);
                product.setStock(stock);
                ProductService.updateProduct(product);

                tableModel.setValueAt(code, rowBeingEdited, 0);
                tableModel.setValueAt(name, rowBeingEdited, 1);
                tableModel.setValueAt(category, rowBeingEdited, 2);
                tableModel.setValueAt(price, rowBeingEdited, 3);
                tableModel.setValueAt(stock, rowBeingEdited, 4);

                isUpdateMode = false;
                rowBeingEdited = -1;
                saveButton.setText("Tambah");
                cancelButton.setVisible(false);
            } else {
                Product product = new Product(0, code, name, category, price, stock);
                ProductService.addProduct(product);
                loadProductData();
            }

            codeField.setText("");
            nameField.setText("");
            categoryField.setSelectedIndex(0);
            priceField.setText("");
            stockField.setText("");
        });

        cancelButton.addActionListener(e -> {
            codeField.setText("");
            nameField.setText("");
            priceField.setText("");
            stockField.setText("");
            categoryField.setSelectedIndex(0);
            saveButton.setText("Simpan");
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
        tableModel.setRowCount(0);
        List<Product> products = ProductService.getAllProducts();
        for (Product p : products) {
            tableModel.addRow(new Object[] {
                    p.getCode(), p.getName(), p.getCategory(), p.getPrice(), p.getStock(), "Update", "Delete"
            });
        }
    }

    static class ButtonRenderer extends JButton implements TableCellRenderer {
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
                        ProductService.deleteProductByIndex(selectedRow);
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