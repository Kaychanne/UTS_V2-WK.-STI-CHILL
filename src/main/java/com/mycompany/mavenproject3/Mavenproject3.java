package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mycompany.mavenproject3.customer.CustomerForm;
import com.mycompany.mavenproject3.product.Product;
import com.mycompany.mavenproject3.product.ProductForm;
import com.mycompany.mavenproject3.product.ProductService;
import com.mycompany.mavenproject3.transaction.TransactionHistory;

public class Mavenproject3 extends JFrame implements Runnable {
    private String text;
    private int x;
    private int width;

    private final BannerPanel bannerPanel;
    private final JButton addProductButton;
    private final JButton processProductButton;
    private final JButton addcustomerbutton;
    private final JButton historyButton;

    public Mavenproject3(String text) {
        this.text = text;
        setTitle("WK. STI Chill");
        setSize(600, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel teks berjalan
        bannerPanel = new BannerPanel();
        add(bannerPanel, BorderLayout.CENTER);

        // Tombol "Kelola Produk"
        JPanel bottomPanel = new JPanel();
        addProductButton = new JButton("Kelola Produk");
        bottomPanel.add(addProductButton);
        add(bottomPanel, BorderLayout.SOUTH);

        processProductButton = new JButton("Form Penjualan");
        bottomPanel.add(processProductButton);
        add(bottomPanel, BorderLayout.SOUTH);


        addcustomerbutton = new JButton("Kelola Pelanggan");
        bottomPanel.add(addcustomerbutton);

        historyButton = new JButton("Riwayat Penjualan");
        bottomPanel.add(historyButton);
        add(bottomPanel, BorderLayout.SOUTH);
        addProductButton.addActionListener(e -> {
            new ProductForm().setVisible(true);
        });
        processProductButton.addActionListener(e -> {
            new SalesForm().setVisible(true);
        });
        addcustomerbutton.addActionListener(e -> {
            new CustomerForm().setVisible(true);
        });
        historyButton.addActionListener(e -> {
            new TransactionHistory().setVisible(true);
        });

        var listener = ProductService.addDataChangeListener(e -> {
            this.text = buildBannerText();
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                ProductService.removeDataChangeListener(listener);
            }
        });

        runThread();
    }

    private void runThread() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public static String buildBannerText() {
        List<Product> all = ProductService.getAllProducts();
        if (all.isEmpty()) {
            return "Tidak ada produk.";
        }
        StringBuilder sb = new StringBuilder("Menu yang tersedia: ");
        for (int i = 0; i < all.size(); i++) {
            sb.append(all.get(i).getName());
            if (i < all.size() - 1) {
                sb.append(" | ");
            }
        }
        return sb.toString();
    }
    
    class BannerPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString(text, x, getHeight() / 2);
        }
    }

    @Override
    public void run() {
        width = getWidth();
        while (true) {
            x += 5;
            if (x > width) {
                x = -getFontMetrics(new Font("Arial", Font.BOLD, 18)).stringWidth(text);
            }
            bannerPanel.repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}