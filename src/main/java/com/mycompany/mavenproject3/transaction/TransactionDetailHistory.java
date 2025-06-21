package com.mycompany.mavenproject3.transaction;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.mycompany.mavenproject3.MoneyFormat;
import com.mycompany.mavenproject3.Product;
import com.mycompany.mavenproject3.ProductService;

public class TransactionDetailHistory extends JFrame {
    private final DefaultTableModel tableModel;
    private final TransactionDetailService detailService;

    public TransactionDetailHistory(int selectedRow) {
        setTitle("Detail Transaksi");
        setSize(800, 600);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(
                new String[] { "ID", "Nama Barang", "Qty", "Total" }, 0);
        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        detailService = TransactionService.getTransactionByIndex(selectedRow).getDetailService();
        var listener = detailService.addDataChangeListener(e -> loadDetailTransaction());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                detailService.removeDataChangeListener(listener);
            }
        });

        loadDetailTransaction();
    }

    private void loadDetailTransaction() {
        tableModel.setRowCount(0);

        List<TransactionDetail> transactions = detailService.getAllTransactionDetails();
        for (TransactionDetail t : transactions) {
            Product p = ProductService.getProductById(t.getProductId());

            tableModel.addRow(new Object[] { t.getId(), p.getName(), t.getQty(), MoneyFormat.IDR(t.getTotal()) });
        }

    }
}
