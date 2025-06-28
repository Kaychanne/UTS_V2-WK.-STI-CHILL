package com.mycompany.mavenproject3.category;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;

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

import com.google.gson.reflect.TypeToken;
import com.mycompany.mavenproject3.ServerQuery;

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
                return;
            }
            try{
                if (isUpdateMode) {
                    Category category = CategoryService.getCategoryByIndex(rowBeingEdited);
                    category.setName(categoryName);
                    // CategoryService.updateCategory(category);

                    // tableModel.setValueAt(categoryName, rowBeingEdited, 1);

                    ServerQuery.update("categories", category, category.getId());
                } else {
                    ServerQuery.add("categories", new Category(CategoryService.getNextId(), categoryName));
                    // Category category = new Category(CategoryService.getNextId(), categoryName);
                    // CategoryService.addCategory(category);
                    // tableModel.addRow(new Object[] { category.getId(), category.getName(), "Update", "Delete" });
                }
            } catch (Exception ex) {
                System.out.println("Error:\n" + ex.getMessage());
            }

            if (isUpdateMode) {
                isUpdateMode = false;
                rowBeingEdited = -1;
                cancelButton.setVisible(false);
            }

                categoryField.setText("");
                loadCategoriesData();
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
        try{
            tableModel.setRowCount(0);
            List<Category> categories = ServerQuery.get("categories", new TypeToken<List<Category>>() {}.getType());
            for (Category c : categories) {
                tableModel.addRow(new Object[] {
                        c.getId(), c.getName(), "Update", "Delete"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load data dari API\n" + e.getMessage());
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
                        Category category = CategoryService.getCategoryByIndex(selectedRow);

                        try {
                            ServerQuery.delete("categories", category.getId());
                        } catch (Exception ex) {
                            System.out.println("Error:/n" + ex.getMessage());
                        }

                        loadCategoriesData();
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
