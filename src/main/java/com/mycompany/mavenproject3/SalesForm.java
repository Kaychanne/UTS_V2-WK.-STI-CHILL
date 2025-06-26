package com.mycompany.mavenproject3;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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

import com.google.gson.reflect.TypeToken;
import com.mycompany.mavenproject3.auth.AuthService;
import com.mycompany.mavenproject3.category.Category;
import com.mycompany.mavenproject3.category.CategoryService;
import com.mycompany.mavenproject3.customer.Customer;
import com.mycompany.mavenproject3.customer.CustomerService;
import com.mycompany.mavenproject3.product.Product;
import com.mycompany.mavenproject3.product.ProductService;
import com.mycompany.mavenproject3.transaction.Transaction;
import com.mycompany.mavenproject3.transaction.TransactionDetail;
import com.mycompany.mavenproject3.transaction.TransactionDetailService;
import com.mycompany.mavenproject3.transaction.TransactionService;

public class SalesForm extends JFrame {
    private final Map<Integer, Integer> quantityMap = new HashMap<>();
    private final JLabel totalQuantityLabel;
    private final JLabel totalPriceLabel;
    private final JTextField salesCodeField;
    private final JComboBox<String> customerField;
    private final JComboBox<String> categoryField;

    private double totalPrice = 0;
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

        salesCodeField = new JTextField(String.format("SF%03d", ++salesId));
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

        customerField = new JComboBox<>();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(customerField, gbc);

        categoryField = new JComboBox<>();
        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Kategori:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0;
        formPanel.add(categoryField, gbc);

        categoryField.addActionListener(e -> {
            var category = categoryField.getSelectedItem().toString();

            if (category.equals("All"))
                selectedCategory = null;
            else
                selectedCategory = category;

            loadProductsPanel();
        });

        loadCustomersData();
        loadCategoriesData();
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

        processButton.addActionListener(e -> processSales());

        add(formPanel);

        var customerListener = CustomerService.addDataChangeListener(e -> loadCustomersData());
        var categoryListener = CategoryService.addDataChangeListener(e -> loadCategoriesData());
        var listener = ProductService.addDataChangeListener(e -> loadProductsPanel());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                CustomerService.removeDataChangeListener(customerListener);
                CategoryService.removeDataChangeListener(categoryListener);
                ProductService.removeDataChangeListener(listener);
            }
        });
    }

    private void loadCustomersData() {
        customerField.removeAllItems();
        for (Customer c : CustomerService.getAllCustomers()) {
            customerField.addItem(c.getName());
        }
    }

    private void loadCategoriesData() {
        categoryField.removeAllItems();
        categoryField.addItem("All");
        for (Category c : CategoryService.getAllCategories()) {
            categoryField.addItem(c.getName());
        }
    }

    private void calculateTotal() {
        totalPrice = 0;
        totalQuantity = 0;

        for (var entry : quantityMap.entrySet()) {
            var product = ProductService.getProductById(entry.getKey());
            if (product == null) {
                quantityMap.remove(entry.getKey());
                continue;
            }

            var qty = entry.getValue();

            totalQuantity += qty;
            totalPrice += product.getPrice() * qty;
        }

        totalQuantityLabel.setText(totalQuantity + " barang");
        totalPriceLabel.setText(MoneyFormat.IDR(totalPrice));
    }

    private void loadProductsPanel() {
        productsPanel.removeAll();

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.weightx = 1;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridy = -1;

        int count = 0;
        try {
            List<Product> products = ServerQuery.get("products", new TypeToken<List<Product>>() {
            }.getType());

            for (int idx = 0; idx < products.size(); idx++) {
                var elem = products.get(idx);

                if (selectedCategory != null && !elem.getCategory().equals(selectedCategory))
                    continue;

                gbc2.gridy = count++;
                if (idx != 0)
                    gbc2.insets = new java.awt.Insets(10, 0, 0, 0);
                productsPanel.add(new ProductItemPanel(elem), gbc2);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data dari API\n" + e.getMessage());
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

    private void processSales() {
        if (totalQuantity <= 0) {
            JOptionPane.showMessageDialog(this, "Jual minimal 1 barang");
            return;
        }

        TransactionDetailService detailService = new TransactionDetailService();

        var tTotalPrice = totalPrice;
        for (var entry : quantityMap.entrySet()) {
            var qty = entry.getValue();
            if (qty <= 0)
                continue;

            var productId = entry.getKey();
            var product = ProductService.getProductById(productId);

            var productTotalPrice = product.getPrice() * qty;
            detailService.addTransactionDetail(
                    new TransactionDetail(detailService.getNextId(), productId, qty, productTotalPrice));

            quantityMap.put(productId, 0);

            product.setStock(product.getStock() - qty);
            ProductService.updateProduct(product);
        }
        totalQuantity = 0;

        var transaction = new Transaction(TransactionService.getNextId(), salesCodeField.getText(),
                AuthService.getUsername(), customerField.getSelectedItem().toString(), LocalDateTime.now(), tTotalPrice,
                detailService);

        try {
            ServerQuery.add("transaction", transaction);
        } catch (Exception ex) {
            System.out.println("Error:\n" + ex.getMessage());
        }

        JOptionPane.showMessageDialog(this, "Penjualan berhasil!");

        salesCodeField.setText(String.format("SF%03d", ++salesId));
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
                        event();
                }

                @Override
                public void removeUpdate(DocumentEvent de) {
                    if (!isUpdating)
                        event();
                }

                @Override
                public void changedUpdate(DocumentEvent de) {
                }

                private void event() {
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
