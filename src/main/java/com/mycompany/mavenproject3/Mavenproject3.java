package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Mavenproject3 extends JFrame implements Runnable {
    private String text;
    private int x;
    private int width;
    private BannerPanel bannerPanel;
    private JButton addProductButton;
    private JButton processProductButton;
    private Timer timer;

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

        addProductButton.addActionListener(e -> {
            new ProductForm().setVisible(true);
        });
        processProductButton.addActionListener(e -> {
            new PenjualanForm().setVisible(true);
        });
     
       timer = new Timer(1000, new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            Mavenproject3.this.text = buildBannerText(); 
            bannerPanel.repaint();
        }
    });
    timer.start();

        Thread thread = new Thread(this);
        thread.start();
    }

    
    private static String buildBannerText() {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            new Mavenproject3(buildBannerText()).setVisible(true);
        });
    }
}
