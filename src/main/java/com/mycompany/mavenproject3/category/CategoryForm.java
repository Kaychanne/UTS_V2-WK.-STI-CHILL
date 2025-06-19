package com.mycompany.mavenproject3.category;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class CategoryForm extends JFrame {
    private final JTable drinkTable;
    private DefaultTableModel tableModel;
    private JTextField categoryField;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean isUpdateMode = false;
    private int rowBeingEdited = -1;

    public CategoryForm() {
        setTitle("WK. Cuan | Data Kategori");
        setSize(600, 450);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.add(new JLabel("Nama Kategori"));
        categoryField = new JTextField(10);
        formPanel.add(categoryField);

        saveButton = new JButton("Simpan");
        formPanel.add(saveButton);

        cancelButton = new JButton("Batal");
        cancelButton.setVisible(false);
        formPanel.add(cancelButton);

        tableModel = new DefaultTableModel(new String[] { "ID", "Nama Kategori", "Update", "Delete" }, 0);
        drinkTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(drinkTable);
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadCategoriesData();

        saveButton.addActionListener(e -> {
            String categoryName = categoryField.getText().trim();

            if (categoryName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Field Nama Kategori harus diisi!");
            }

            if (isUpdateMode) {
                Category category = CategoryService.getCategoryByIndex(rowBeingEdited);
                category.setName(categoryName);
                CategoryService.updateCategory(category);

                tableModel.setValueAt(categoryName, rowBeingEdited, 1);

                isUpdateMode = false;
                rowBeingEdited = -1;
                saveButton.setText("Tambah");
                cancelButton.setVisible(false);
            } else {
                Category category = new Category(CategoryService.getNextId(), categoryName);
                CategoryService.addCategory(category);
                tableModel.addRow(new Object[] { category.getId(), category.getName(), "Update", "Delete" });
            }

            categoryField.setText("");
        });

        cancelButton.addActionListener(e -> {
            categoryField.setText("");
            saveButton.setText("Simpan");
            cancelButton.setVisible(false);
            isUpdateMode = false;
            rowBeingEdited = -1;
        });

        TableColumn updateColumn = drinkTable.getColumnModel().getColumn(2);
        updateColumn.setCellRenderer(new ButtonRenderer("Update"));
        updateColumn.setCellEditor(new ButtonEditor(
                new JCheckBox(), "Update"));

        TableColumn deleteColumn = drinkTable.getColumnModel().getColumn(3);
        deleteColumn.setCellRenderer(new ButtonRenderer("Delete"));
        deleteColumn.setCellEditor(new ButtonEditor(
                new JCheckBox(), "Delete"));
    }

    private void loadCategoriesData() {
        tableModel.setRowCount(0);
        var categories = CategoryService.getAllCategories();
        for (Category c : categories) {
            tableModel.addRow(new Object[] {
                    c.getId(), c.getName(), "Update", "Delete"
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
        private final JButton button;
        private final String label;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            this.label = label;

            button = new JButton(label);
            button.addActionListener(e -> {
                fireEditingStopped();

                if (label.equals("Update")) {
                    Category category = CategoryService.getCategoryByIndex(selectedRow);
                    categoryField.setText(category.getName());

                    isUpdateMode = true;
                    rowBeingEdited = selectedRow;
                    saveButton.setText("Simpan");
                    cancelButton.setVisible(true);
                } else if (label.equals("Delete")) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus kategori ini?",
                            "Konfirmasi", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        CategoryService.deleteCategoryByIndex(selectedRow);
                        tableModel.removeRow(selectedRow);
                    }
                }
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
