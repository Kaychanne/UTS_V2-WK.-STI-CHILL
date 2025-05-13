package com.mycompany.mavenproject3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Mavenproject3 extends JFrame implements Runnable {
    private String text;
    private int x;
    private int width;
    private BannerPanel bannerPanel;
    private JButton addProductButton;

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
        
        addProductButton.addActionListener(e -> {
            new ProductForm().setVisible(true);
        });

        setVisible(true);

        Thread thread = new Thread(this);
        thread.start();
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
        // new Mavenproject3("Menu yang tersedia: Americano | Pandan Latte | Aren Latte | Matcha Frappucino | Jus Apel");
        ArrayList<String> menu = new ArrayList<String>();
        menu.add("Americano");
        menu.add("Pandan Latte");
        menu.add("Aren Latte");
        menu.add("Matcha Frappucino");
        menu.add("Jus Apel");
        System.out.println(menu);

        StringBuilder tampilanMenu = new StringBuilder("Menu yang tersedia: ");
        for(int i=0; i< menu.size(); i++){
            tampilanMenu.append(menu.get(i));
            if(i != menu.size() - 1){
                tampilanMenu.append(" | ");
            }
        }

        new Mavenproject3(tampilanMenu.toString());
    }
}