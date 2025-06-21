/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.awt.Component;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author THIS PC
 */
public class ButtonEditor extends DefaultCellEditor {
   private JButton button;
    private String label;
    private boolean clicked;
    private JTable table;

    public ButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        this.table = table;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        label = (value == null) ? "Chi tiết" : value.toString();
        button.setText(label);
        clicked = true;
        return button;
    }

    @Override
public Object getCellEditorValue() {
    if (clicked) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) { // Kiểm tra dòng được chọn
            int orderId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString()); // Lấy order_id
            System.out.println("Order ID được chọn: " + orderId); // Kiểm tra dữ liệu
            try {
                new view.OrderDetail(orderId).setVisible(true); // Mở form với order_id
            } catch (SQLException ex) {
                Logger.getLogger(ButtonEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    clicked = false;
    return label;
}

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    } 
}
