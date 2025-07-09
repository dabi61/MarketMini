/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ExpenseDAO;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import model.Expense;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 *
 * @author THIS PC
 */
public class ExpenseController {
    private ExpenseDAO dao;

    public ExpenseController() {
        try {
            dao = new ExpenseDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối CSDL: " + e.getMessage());
        }
    }

    public DefaultTableModel getExpenseTableModel() {
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    model.setColumnIdentifiers(new String[] {
        "Mã chi phí", "Tháng/Năm", "Chi phí điện", "Chi phí thuê", "Chi phí nước", "Chi phí sửa chữa"
    });

    try {
        List<Object[]> list = dao.getAllExpenses();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy"); // ✅ định dạng Tháng/Năm

        for (Object[] row : list) {
            Object[] formattedRow = new Object[row.length];
            formattedRow[0] = row[0]; // Mã chi phí

            // ✅ Format lại cột Tháng/Năm
            if (row[1] instanceof Date) {
                formattedRow[1] = sdf.format((Date) row[1]);
            } else {
                formattedRow[1] = row[1]; // fallback nếu không phải Date
            }

            for (int i = 2; i < row.length; i++) {
                formattedRow[i] = row[i];
            }

            model.addRow(formattedRow);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu chi phí!");
    }

    return model;
}


    
    public boolean saveExpense(
    boolean isEditing,
    int editingExpenseId,
    Date monthYear,
    int electricity,
    int rent,
    int water,
    int repair
) throws SQLException {
    Expense expense = new Expense();
    expense.setMonthYear(monthYear);
    expense.setElectricityCost(electricity);
    expense.setRentCost(rent);
    expense.setWaterCost(water);
    expense.setRepairCost(repair);

    ExpenseDAO dao = new ExpenseDAO();

    // 🔒 Kiểm tra trùng tháng-năm nếu đang thêm mới
    if (!isEditing && dao.isMonthYearExists(monthYear)) {
        JOptionPane.showMessageDialog(null,
            "Chi phí của tháng này đã tồn tại. Vui lòng chọn tháng khác!",
            "Trùng tháng",
            JOptionPane.WARNING_MESSAGE);
        return false;
    }

    if (isEditing) {
        expense.setExpenseId(editingExpenseId);
        return dao.update(expense);
    } else {
        return dao.insert(expense);
    }
}

    
    public boolean deleteExpense(int expenseId) {
        return dao.delete(expenseId);
    }

    public DefaultTableModel searchExpenses(int month, int year) {
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    model.setColumnIdentifiers(new String[] {
        "Mã chi phí", "Tháng/Năm", "Chi phí điện", "Chi phí mặt bằng", "Chi phí nước", "Chi phí sửa chữa"
    });

    try {
        List<Object[]> list = dao.searchByMonthAndYear(month + 1, year); // +1 vì tháng trong DB là 1-12
        for (Object[] row : list) {
            model.addRow(row);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Lỗi tìm kiếm: " + e.getMessage());
    }

    return model;
}


    
    public void exportToExcel(TableModel model, String filePath) {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Chi phí hàng tháng");

        // Style tiêu đề
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Header
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < model.getColumnCount(); col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(model.getColumnName(col));
            cell.setCellStyle(headerStyle);
        }

        // Dữ liệu
        for (int row = 0; row < model.getRowCount(); row++) {
            Row excelRow = sheet.createRow(row + 1);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = excelRow.createCell(col);
                Object value = model.getValueAt(row, col);
                cell.setCellValue(value != null ? value.toString() : "");
            }
        }

        for (int i = 0; i < model.getColumnCount(); i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(filePath));
        }

        JOptionPane.showMessageDialog(null, "Xuất Excel thành công:\n" + filePath);

    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Lỗi khi xuất Excel: " + e.getMessage());
    }
}
    
}
