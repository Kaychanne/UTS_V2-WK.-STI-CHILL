package com.mycompany.mavenproject3;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
public class PenjualanForm extends JFrame {
    private DefaultTableModel tableModel;
    private JComboBox<Product> nameField;
    private JTextField stockField;
    private JTextField priceField;
    private JTextField quantityField;
    private JButton processButton;

    public PenjualanForm() {
        setTitle("Form Penjualan");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));

        // Buat komponen dulu
        formPanel.add(new JLabel("Nama Barang:"));
        nameField = new JComboBox<>();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Harga Jual:"));
        priceField = new JTextField();
        priceField.setEditable(false);
        formPanel.add(priceField);

        formPanel.add(new JLabel("Stok Tersedia:"));
        stockField = new JTextField();
        stockField.setEditable(false);
        formPanel.add(stockField);

        formPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        formPanel.add(quantityField);

        processButton = new JButton("Process");
        formPanel.add(processButton);

        add(formPanel, BorderLayout.NORTH);

        // Setelah komponen dibuat, isi datanya
        refreshProductList(); // ⬅️ Panggil di sini

        // Listener ComboBox
        nameField.addActionListener(e -> {
            Product selected = (Product) nameField.getSelectedItem();
            if (selected != null) {
                priceField.setText(String.valueOf(selected.getPrice()));
                stockField.setText(String.valueOf(selected.getStock()));
            }
        });

        // Listener tombol "Process"
        processButton.addActionListener(e -> {
            Product selected = (Product) nameField.getSelectedItem();
            if (selected == null) return;

            int qty;
            try {
                qty = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Jumlah tidak valid!");
                return;
            }

            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0");
                return;
            }

            if (qty > selected.getStock()) {
                JOptionPane.showMessageDialog(this, "Stok tidak mencukupi");
                return;
            }

            selected.setStock(selected.getStock() - qty);
            ProductService.updateProduct(selected);

            JOptionPane.showMessageDialog(this, "Pembelian berhasil!");
            refreshProductList();
            // Update tampilan
            priceField.setText(String.valueOf(selected.getPrice()));
            stockField.setText(String.valueOf(selected.getStock()));
            quantityField.setText("");
        });
    }

    public void refreshProductList() {
        nameField.removeAllItems();
        List<Product> productList = ProductService.getAllProducts();
        for (Product product : productList) {
            nameField.addItem(product);
        }
    }
}


