package com.mycompany.mavenproject3;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SalesForm extends JFrame {
    private final Map<Integer, Integer> quantityMap = new HashMap<>();
    private final JLabel totalQuantityLabel;
    private final JLabel totalPriceLabel;

    private int totalQuantity = 0;
    private String selectedCategory = null;
    private final JPanel productsPanel;

    private long salesId = 0;

    public SalesForm() {
        setTitle("Form Penjualan");
        setSize(600, 450);
        setLocationRelativeTo(null);

        totalQuantityLabel = new JLabel("0 barang");
        totalPriceLabel = new JLabel("Rp0,00");
        productsPanel = new JPanel(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Kode Penjualan:"), gbc);

        JTextField salesCodeField = new JTextField(String.format("SF%03d", ++salesId));
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(salesCodeField, gbc);

        JLabel dateLabel = new JLabel(formatter.format(LocalDateTime.now()));
        gbc.gridx = 3;
        gbc.weightx = 0;
        formPanel.add(dateLabel, gbc);

        Thread thread = new Thread(() -> {
            while (true) {
                dateLabel.setText(formatter.format(LocalDateTime.now()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Pelanggan:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(new JComboBox<>(), gbc);

        JComboBox<String> categoryField = new JComboBox<>(
                new String[] { "Semua", "Coffee", "Dairy", "Juice", "Soda", "Tea" });
        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Kategori:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0;
        formPanel.add(categoryField, gbc);

        categoryField.addActionListener(e -> {
            var category = categoryField.getSelectedItem().toString();

            if (category.equals("Semua"))
                selectedCategory = null;
            else
                selectedCategory = category;

            loadProductsPanel();
        });

        loadProductsPanel();

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        formPanel.add(new JScrollPane(productsPanel), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        formPanel.add(new JLabel("Total:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        formPanel.add(totalQuantityLabel, gbc);
        gbc.gridy = 5;
        formPanel.add(totalPriceLabel, gbc);

        JButton processButton = new JButton("Proses");

        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
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

            salesCodeField.setText(String.format("SF%03d", ++salesId));
        });

        add(formPanel);

        ProductService.addDataChangeListener(e -> loadProductsPanel());
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

    private void loadProductsPanel() {
        productsPanel.removeAll();

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.weightx = 1;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        var pCount = ProductService.getAllProducts().size();
        gbc2.gridy = -1;
        int count = 0;
        for (int idx = 0; idx < pCount; idx++) {
            var elem = (Product) ProductService.getProductByIndex(idx);
            if (selectedCategory != null && !elem.getCategory().equals(selectedCategory))
                continue;

            gbc2.gridy = count++;
            if (idx != 0)
                gbc2.insets = new java.awt.Insets(10, 0, 0, 0);
            productsPanel.add(new ProductItemPanel(elem), gbc2);
        }
        gbc2.gridy = count;
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

        private boolean isUpdating = false;

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
            quantityField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent de) {
                    if (!isUpdating)
                        event(de);
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    if (!isUpdating)
                        event(de);
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                }

                private void event(DocumentEvent de) {
                    SwingUtilities.invokeLater(() -> {
                        isUpdating = true;
                        try {
                            String qtyStr = quantityField.getText().trim();
                            if (qtyStr.isEmpty())
                                quantity = 0;
                            else {
                                quantity = Integer.parseInt(qtyStr);

                                if (quantity < 0)
                                    quantity = 0;
                                else if (quantity > product.getStock())
                                    quantity = product.getStock();
                            }

                            updateQuantity(0);
                        } finally {
                            isUpdating = false;
                        }
                    });
                }
            });

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
            String qtyStr = String.valueOf(quantity);
            if (!quantityField.getText().equals(qtyStr))
                quantityField.setText(qtyStr);

            decreaseButton.setEnabled(quantity > 0);
            increaseButton.setEnabled(quantity < product.getStock());

            quantityMap.put(product.getId(), quantity);
            calculateTotal();
        }
    }
}
