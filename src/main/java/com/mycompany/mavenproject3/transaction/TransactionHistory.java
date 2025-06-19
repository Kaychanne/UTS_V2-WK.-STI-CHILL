package com.mycompany.mavenproject3.transaction;

import java.awt.Component;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.mycompany.mavenproject3.MoneyFormat;

public class TransactionHistory extends JFrame {
    private final DefaultTableModel tableModel;

    public TransactionHistory() {
        setTitle("Riwayat Transaksi");
        setSize(800, 600);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(
                new String[] { "Kode", "Kasir", "Pelanggan", "Waktu Transaksi", "Total Harga", "Detail" }, 0);
        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        TableColumn detailColumn = table.getColumnModel().getColumn(5);
        detailColumn.setCellRenderer(new ButtonRenderer("Detail"));
        detailColumn.setCellEditor(new ButtonEditor(
                new JCheckBox(), "Detail"));

        var listener = TransactionService.addDataChangeListener(e -> loadTransaction());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                TransactionService.removeDataChangeListener(listener);
            }
        });

        loadTransaction();
    }

    private void loadTransaction() {
        tableModel.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");

        List<Transaction> transactions = TransactionService.getAllTransactions();
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[] {
                    t.getCode(), t.getCashier(), t.getCustomer(), formatter.format(t.getDateTime()),
                    MoneyFormat.IDR(t.getTotal()),
                    "Detail",
            });
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
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

                if (label.equals("Detail")) {
                    new TransactionDetailHistory(selectedRow).setVisible(true);
                } /*
                   * else if (label.equals("Delete")) {
                   * int confirm = JOptionPane.showConfirmDialog(null,
                   * "Yakin ingin menghapus produk ini?", "Konfirmasi",
                   * JOptionPane.YES_NO_OPTION);
                   * if (confirm == JOptionPane.YES_OPTION) {
                   * ProductService.deleteProductByIndex(selectedRow);
                   * loadProductData();
                   * }
                   * }
                   */
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
