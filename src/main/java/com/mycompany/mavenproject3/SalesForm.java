package com.mycompany.mavenproject3;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class SalesForm extends JFrame {
    private final Map<Integer, Integer> quantityMap = new HashMap<>();
    private final JLabel totalQuantityLabel;
    private final JLabel totalPriceLabel;

    private int totalQuantity = 0;

    public SalesForm() {
        setTitle("Form Penjualan");
        setSize(600, 450);
        setLocationRelativeTo(null);

        totalQuantityLabel = new JLabel("0 barang");
        totalPriceLabel = new JLabel("Rp0,00");

        JPanel formPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        formPanel.add(new JLabel("Pelanggan:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JComboBox<>(), gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Filter:"), gbc);
        gbc.gridx = 3;
        formPanel.add(new JComboBox<>(), gbc);

        JPanel productsPanel = new JPanel(new GridBagLayout());
        loadProductsPanel(productsPanel);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        formPanel.add(new JScrollPane(productsPanel), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        formPanel.add(new JLabel("Total:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        formPanel.add(totalQuantityLabel, gbc);
        gbc.gridy = 4;
        formPanel.add(totalPriceLabel, gbc);

        JButton processButton = new JButton("Proses");

        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        formPanel.add(processButton, gbc);

        processButton.addActionListener(e -> {
            if (totalQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Jual minimal 1 barang");
                return;
            }

            for (var entry : quantityMap.entrySet()) {
                var qty = entry.getValue();
                quantityMap.put(entry.getKey(), 0);

                var product = ProductService.getProductById(entry.getKey());
                product.setStock(product.getStock() - qty);
                ProductService.updateProduct(product);
            }
            totalQuantity = 0;

            JOptionPane.showMessageDialog(this, "Penjualan berhasil!");
        });

        add(formPanel);

        ProductService.addDataChangeListener(e -> loadProductsPanel(productsPanel));
    }

    private void calculateTotal() {
        int totalPrice = 0;
        totalQuantity = 0;

        for (var entry : quantityMap.entrySet()) {
            var qty = entry.getValue();

            totalQuantity += qty;
            totalPrice += ProductService.getProductById(entry.getKey()).getPrice() * qty;
        }

        totalQuantityLabel.setText(totalQuantity + " barang");
        totalPriceLabel.setText(MoneyFormat.IDR(totalPrice));
    }

    private void loadProductsPanel(JPanel productsPanel) {
        productsPanel.removeAll();

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.weightx = 1;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        var pCount = ProductService.getAllProducts().size();
        for (int idx = 0; idx < pCount; idx++) {
            gbc2.gridy = idx;
            if (idx != 0)
                gbc2.insets = new java.awt.Insets(10, 0, 0, 0);
            Object elem = ProductService.getProductByIndex(idx);
            productsPanel.add(new ProductItemPanel((Product) elem), gbc2);
        }
        gbc2.gridy = pCount;
        gbc2.weighty = 1;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.anchor = GridBagConstraints.NORTH;
        gbc2.insets = new java.awt.Insets(0, 0, 0, 0);
        productsPanel.add(Box.createVerticalGlue(), gbc2);

        productsPanel.revalidate();
        productsPanel.repaint();
    }

    class ProductItemPanel extends JPanel {
        private final JTextField quantityField;
        private final JButton decreaseButton;
        private final JButton increaseButton;
        private int quantity = 0;
        private final Product product;

        public ProductItemPanel(Product product) {
            this.product = product;
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new java.awt.Insets(0, 5, 0, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weighty = 0;
            add(new JLabel(product.getName()), gbc);

            gbc.gridy = 1;
            add(new JLabel("Stok tersedia: " + product.getStock()), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridheight = 2;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 0.5;
            add(new JLabel(MoneyFormat.IDR(product.getPrice())), gbc);

            quantityField = new JTextField("");
            decreaseButton = new JButton("-");
            increaseButton = new JButton("+");
            updateQuantity(quantityMap.getOrDefault(product.getId(), 0));

            decreaseButton.addActionListener(e -> updateQuantity(-1));
            increaseButton.addActionListener(e -> updateQuantity(1));

            gbc.gridx = 2;
            gbc.weightx = 0;
            add(decreaseButton, gbc);

            gbc.gridx = 3;
            gbc.ipadx = 20;
            add(quantityField, gbc);

            gbc.gridx = 4;
            gbc.ipadx = 0;
            add(increaseButton, gbc);

            quantityMap.put(product.getId(), quantity);
        }

        private void updateQuantity(int increment) {
            quantity += increment;
            quantityField.setText(String.valueOf(quantity));

            decreaseButton.setEnabled(quantity > 0);
            increaseButton.setEnabled(quantity < product.getStock());

            quantityMap.put(product.getId(), quantity);
            calculateTotal();
        }
    }
}

