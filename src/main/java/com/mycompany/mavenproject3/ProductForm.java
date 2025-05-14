/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject3;

/**
 *
 * @author ASUS
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
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
import javax.swing.SwingUtilities;
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
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "P001", "Americano", "Coffee", 18000, 10));
        products.add(new Product(2, "P002", "Pandan Latte", "Coffee", 15000, 8));
        
        setTitle("WK. Cuan | Stok Barang");
        setSize(600, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel form pemesanan
        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Kode Barang"));
        codeField = new JTextField();
        formPanel.add(codeField);
        
        formPanel.add(new JLabel("Nama Barang:"));
        nameField = new JTextField();
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Kategori:"));
        categoryField = new JComboBox<>(new String[]{"Coffee", "Dairy", "Juice", "Soda", "Tea"});
        formPanel.add(categoryField);
        
        formPanel.add(new JLabel("Harga Jual:"));
        priceField = new JTextField();
        formPanel.add(priceField);
        
        formPanel.add(new JLabel("Stok Tersedia:"));
        stockField = new JTextField();
        formPanel.add(stockField);
        
        saveButton = new JButton("Simpan");
        formPanel.add(saveButton);
        
        tableModel = new DefaultTableModel(new String[]{"Kode", "Nama", "Kategori", "Harga Jual", "Stok", "Update", "Delete"}, 0);

        drinkTable = new JTable(tableModel);
        loadProductData(products);
        cancelButton = new JButton("Batal");
        cancelButton.setVisible(false);

        JScrollPane scrollPane = new JScrollPane(drinkTable);
        formPanel.setLayout(new GridLayout(7, 2, 5, 5));
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(cancelButton, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            String code = codeField.getText();
            String name = nameField.getText();
            String category = categoryField.getSelectedItem().toString();
            try {
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());

                if(isUpdateMode){
                    Product product = products.get(rowBeingEdited);
                    product.setCode(code);
                    product.setName(name);
                    product.setCategory(category);
                    product.setPrice(price);
                    product.setStock(stock);

                    tableModel.setValueAt(code, rowBeingEdited, 0);
                    tableModel.setValueAt(name, rowBeingEdited, 1);
                    tableModel.setValueAt(category, rowBeingEdited, 2);
                    tableModel.setValueAt(price, rowBeingEdited, 3);
                    tableModel.setValueAt(stock, rowBeingEdited, 4);

                    isUpdateMode = false;
                    rowBeingEdited = -1;
                    saveButton.setText("Tambah");
                    cancelButton.setVisible(false);
                }else{
                    Product product = new Product(0, code, name, category, price, stock);
                    products.add(product);
                    tableModel.addRow(new Object[]{code, name, category, price, stock, "Update", "Delete"});
                }

                codeField.setText("");
                nameField.setText("");
                priceField.setText("");
                stockField.setText("");
                categoryField.setSelectedIndex(0);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Masukkan harga yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            // Reset form ketika tombol batal ditekan
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

        drinkTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && drinkTable.getSelectedRow() != -1) {
                rowBeingEdited = drinkTable.getSelectedRow();
                Product product = products.get(rowBeingEdited);
                codeField.setText(product.getCode());
                nameField.setText(product.getName());
                categoryField.setSelectedItem(product.getCategory());
                priceField.setText(String.valueOf(product.getPrice()));
                stockField.setText(String.valueOf(product.getStock()));

                isUpdateMode = true;
                saveButton.setText("Simpan");
                cancelButton.setVisible(true);
            }
        });


        TableColumn updateColumn = drinkTable.getColumnModel().getColumn(5);
        updateColumn.setCellRenderer(new ButtonRenderer("Update"));
        updateColumn.setCellEditor(new ButtonEditor(
            new JCheckBox(), drinkTable, "Update",
            codeField, nameField, categoryField, priceField, stockField,
            saveButton, cancelButton, products
        ));

        TableColumn deleteColumn = drinkTable.getColumnModel().getColumn(6);
        deleteColumn.setCellRenderer(new ButtonRenderer("Delete"));
        deleteColumn.setCellEditor(new ButtonEditor(
            new JCheckBox(), drinkTable, "Delete",
            codeField, nameField, categoryField, priceField, stockField,
            saveButton, cancelButton, products
        ));

    }
    

    private void loadProductData(List<Product> productList) {
        for (Product product : productList) {
            tableModel.addRow(new Object[]{
                product.getCode(), product.getName(), product.getCategory(), product.getPrice(), product.getStock(),
                "Update", "Delete"  // ← isi dummy
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
        private JButton button;
        private String label;
        private int selectedRow;
        private JTable table;
        private List<Product> products;
        private JTextField codeField;
        private JTextField nameField;
        private JComboBox<String> categoryField;
        private JTextField priceField;
        private JTextField stockField;
        private JButton saveButton;
        private JButton cancelButton;
        public ButtonEditor(JCheckBox checkBox, JTable table, String label, 
        JTextField codeField, JTextField nameField, JComboBox<String> categoryField,
        JTextField priceField, JTextField stockField,
        JButton saveButton, JButton cancelButton,
        List<Product> products){
            super(checkBox);
            this.label = label;
            this.table = table;
            this.codeField = codeField;
            this.nameField = nameField;
            this.categoryField = categoryField;
            this.priceField = priceField;
            this.stockField = stockField;
            this.saveButton = saveButton;
            this.cancelButton = cancelButton;
            this.products = products;
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
            if (label.equals("Update")) {
                Product product = products.get(selectedRow);
                codeField.setText(product.getCode());  // ← ini yang dulu hilang!
                nameField.setText(product.getName());
                categoryField.setSelectedItem(product.getCategory());
                priceField.setText(String.valueOf(product.getPrice()));
                stockField.setText(String.valueOf(product.getStock()));
                isUpdateMode = true;
                rowBeingEdited = selectedRow;
                saveButton.setText("Simpan"); // ← bukan "Update"
                cancelButton.setVisible(true);            
            } else if (label.equals("Delete")) {
                int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus produk ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    products.remove(selectedRow);
                    tableModel.removeRow(selectedRow);
                }
            }
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }
    }
 public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ProductForm().setVisible(true);
        });
    }
    
}