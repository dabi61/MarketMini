package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import model.Salary;

/**
 * Utility class để xuất dữ liệu ra file Excel
 * @author macbook
 */
public class ExcelExporter {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Xuất bảng lương ra file Excel
     * @param table Bảng chứa dữ liệu lương
     * @param parentComponent Component cha để hiển thị dialog
     * @return true nếu thành công, false nếu thất bại
     */
    public static boolean exportSalaryTable(JTable table, java.awt.Component parentComponent) {
        try {
            // Hiển thị dialog chọn file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx)", "xlsx"));
            
            // Đặt tên file mặc định
            String defaultFileName = "Bao_Cao_Luong_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".xlsx";
            fileChooser.setSelectedFile(new File(defaultFileName));
            
            int result = fileChooser.showSaveDialog(parentComponent);
            if (result != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            
            // Tạo workbook và sheet
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Báo cáo lương");
                
                // Tạo style cho header
                CellStyle headerStyle = createHeaderStyle(workbook);
                CellStyle dataStyle = createDataStyle(workbook);
                CellStyle currencyStyle = createCurrencyStyle(workbook);
                CellStyle dateStyle = createDateStyle(workbook);
                
                // Tạo header
                Row headerRow = sheet.createRow(0);
                String[] headers = {
                    "ID", "Tên nhân viên", "Tổng giờ làm", "Lương/giờ", 
                    "Thưởng", "Overtime Pay", "Gross Salary", "Penalty", 
                    "Net Salary", "Ngày thanh toán", "Ngày tạo"
                };
                
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                    sheet.setColumnWidth(i, 15 * 256); // Set column width
                }
                
                // Lấy dữ liệu từ table
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int rowCount = model.getRowCount();
                
                // Thêm dữ liệu
                for (int i = 0; i < rowCount; i++) {
                    Row dataRow = sheet.createRow(i + 1);
                    
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Cell cell = dataRow.createCell(j);
                        Object value = model.getValueAt(i, j);
                        
                        if (value != null) {
                            setCellValue(cell, value, j, dataStyle, currencyStyle, dateStyle);
                        }
                    }
                }
                
                // Tạo summary row
                createSummaryRow(sheet, rowCount + 2, model, workbook);
                
                // Ghi file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                
                JOptionPane.showMessageDialog(parentComponent, 
                    "Xuất Excel thành công!\nFile được lưu tại: " + file.getAbsolutePath(),
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                return true;
                
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentComponent, 
                    "Lỗi khi ghi file: " + e.getMessage(),
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentComponent, 
                "Lỗi khi xuất Excel: " + e.getMessage(),
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Xuất danh sách lương từ List<Salary> ra Excel
     * @param salaries Danh sách lương
     * @param parentComponent Component cha
     * @return true nếu thành công
     */
    public static boolean exportSalaryList(List<Salary> salaries, java.awt.Component parentComponent) {
        try {
            // Hiển thị dialog chọn file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx)", "xlsx"));
            
            String defaultFileName = "Danh_Sach_Luong_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".xlsx";
            fileChooser.setSelectedFile(new File(defaultFileName));
            
            int result = fileChooser.showSaveDialog(parentComponent);
            if (result != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            
            // Tạo workbook và sheet
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Danh sách lương");
                
                // Tạo styles
                CellStyle headerStyle = createHeaderStyle(workbook);
                CellStyle dataStyle = createDataStyle(workbook);
                CellStyle currencyStyle = createCurrencyStyle(workbook);
                CellStyle dateStyle = createDateStyle(workbook);
                
                // Tạo header
                Row headerRow = sheet.createRow(0);
                String[] headers = {
                    "ID", "ID Nhân viên", "Tổng giờ làm", "Lương/giờ", 
                    "Thưởng", "Overtime Rate", "Gross Salary", "Penalty", 
                    "Net Salary", "Ngày thanh toán", "Ngày tạo"
                };
                
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                    sheet.setColumnWidth(i, 15 * 256);
                }
                
                // Thêm dữ liệu
                for (int i = 0; i < salaries.size(); i++) {
                    Salary salary = salaries.get(i);
                    Row dataRow = sheet.createRow(i + 1);
                    
                    // ID
                    createCell(dataRow, 0, salary.getSalary_id(), dataStyle);
                    
                    // Employee ID
                    createCell(dataRow, 1, salary.getEmployee_id(), dataStyle);
                    
                    // Total hours
                    createCell(dataRow, 2, salary.getTotal_hours(), currencyStyle);
                    
                    // Hourly wage
                    createCell(dataRow, 3, salary.getHourly_wage(), currencyStyle);
                    
                    // Bonus
                    createCell(dataRow, 4, salary.getBonus(), currencyStyle);
                    
                    // Overtime rate
                    createCell(dataRow, 5, salary.getOvertime_rate(), currencyStyle);
                    
                    // Gross salary
                    createCell(dataRow, 6, salary.getGross_salary(), currencyStyle);
                    
                    // Penalty deduction
                    createCell(dataRow, 7, salary.getPenalty_deduction(), currencyStyle);
                    
                    // Net salary
                    createCell(dataRow, 8, salary.getNet_salary(), currencyStyle);
                    
                    // Payment date
                    if (salary.getPayment_date() != null) {
                        createCell(dataRow, 9, salary.getPayment_date(), dateStyle);
                    }
                    
                    // Created date
                    if (salary.getCreated_date() != null) {
                        createCell(dataRow, 10, salary.getCreated_date(), dateStyle);
                    }
                }
                
                // Tạo summary
                createSummaryForSalaryList(sheet, salaries.size() + 2, salaries, workbook);
                
                // Ghi file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                
                JOptionPane.showMessageDialog(parentComponent, 
                    "Xuất Excel thành công!\nFile được lưu tại: " + file.getAbsolutePath(),
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                return true;
                
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentComponent, 
                    "Lỗi khi ghi file: " + e.getMessage(),
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentComponent, 
                "Lỗi khi xuất Excel: " + e.getMessage(),
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Xuất Excel theo nhân viên được chọn
     * @param salaries Danh sách lương của nhân viên
     * @param employeeName Tên nhân viên
     * @param parentComponent Component cha
     * @return true nếu thành công
     */
    public static boolean exportSalaryByEmployee(List<Salary> salaries, String employeeName, java.awt.Component parentComponent) {
        try {
            // Hiển thị dialog chọn file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx)", "xlsx"));
            
            // Đặt tên file mặc định với tên nhân viên
            String defaultFileName = "Bao_Cao_Luong_" + employeeName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".xlsx";
            fileChooser.setSelectedFile(new File(defaultFileName));
            
            int result = fileChooser.showSaveDialog(parentComponent);
            if (result != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            
            // Tạo workbook và sheet
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Báo cáo lương - " + employeeName);
                
                // Tạo styles
                CellStyle headerStyle = createHeaderStyle(workbook);
                CellStyle dataStyle = createDataStyle(workbook);
                CellStyle currencyStyle = createCurrencyStyle(workbook);
                CellStyle dateStyle = createDateStyle(workbook);
                CellStyle titleStyle = createTitleStyle(workbook);
                
                // Tạo tiêu đề
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("BÁO CÁO LƯƠNG - " + employeeName.toUpperCase());
                titleCell.setCellStyle(titleStyle);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 10));
                
                // Tạo thông tin nhân viên
                Row infoRow = sheet.createRow(1);
                Cell infoCell = infoRow.createCell(0);
                infoCell.setCellValue("Thời gian xuất báo cáo: " + DATETIME_FORMAT.format(new java.util.Date()));
                infoCell.setCellStyle(dataStyle);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 10));
                
                // Tạo header
                Row headerRow = sheet.createRow(3);
                String[] headers = {
                    "ID", "Tên nhân viên", "Tổng giờ làm", "Lương/giờ", 
                    "Thưởng", "Overtime Pay", "Gross Salary", "Penalty", 
                    "Net Salary", "Ngày thanh toán", "Ngày tạo"
                };
                
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                    sheet.setColumnWidth(i, 15 * 256);
                }
                
                // Thêm dữ liệu
                for (int i = 0; i < salaries.size(); i++) {
                    Salary salary = salaries.get(i);
                    Row dataRow = sheet.createRow(i + 4);
                    
                    // ID
                    createCell(dataRow, 0, salary.getSalary_id(), dataStyle);
                    
                    // Tên nhân viên
                    createCell(dataRow, 1, employeeName, dataStyle);
                    
                    // Total hours
                    createCell(dataRow, 2, salary.getTotal_hours(), currencyStyle);
                    
                    // Hourly wage
                    createCell(dataRow, 3, salary.getHourly_wage(), currencyStyle);
                    
                    // Bonus
                    createCell(dataRow, 4, salary.getBonus(), currencyStyle);
                    
                    // Overtime rate
                    createCell(dataRow, 5, salary.getOvertime_rate(), currencyStyle);
                    
                    // Gross salary
                    createCell(dataRow, 6, salary.getGross_salary(), currencyStyle);
                    
                    // Penalty deduction
                    createCell(dataRow, 7, salary.getPenalty_deduction(), currencyStyle);
                    
                    // Net salary
                    createCell(dataRow, 8, salary.getNet_salary(), currencyStyle);
                    
                    // Payment date
                    if (salary.getPayment_date() != null) {
                        createCell(dataRow, 9, salary.getPayment_date(), dateStyle);
                    }
                    
                    // Created date
                    if (salary.getCreated_date() != null) {
                        createCell(dataRow, 10, salary.getCreated_date(), dateStyle);
                    }
                }
                
                // Tạo summary
                createSummaryForEmployee(sheet, salaries.size() + 4, salaries, workbook, employeeName);
                
                // Ghi file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                
                JOptionPane.showMessageDialog(parentComponent, 
                    "Xuất Excel thành công!\nFile được lưu tại: " + file.getAbsolutePath(),
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                return true;
                
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentComponent, 
                    "Lỗi khi ghi file: " + e.getMessage(),
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentComponent, 
                "Lỗi khi xuất Excel: " + e.getMessage(),
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Tạo style cho header
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    /**
     * Tạo style cho dữ liệu
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    /**
     * Tạo style cho tiền tệ
     */
    private static CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0 \"VNĐ\""));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
    
    /**
     * Tạo style cho ngày tháng
     */
    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("dd/mm/yyyy"));
        return style;
    }
    
    /**
     * Tạo style cho tiêu đề
     */
    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
    
    /**
     * Set giá trị cho cell
     */
    private static void setCellValue(Cell cell, Object value, int columnIndex, 
                                   CellStyle dataStyle, CellStyle currencyStyle, CellStyle dateStyle) {
        if (value == null) {
            cell.setCellValue("");
            cell.setCellStyle(dataStyle);
            return;
        }
        
        if (value instanceof Number) {
            if (columnIndex == 3 || columnIndex == 4 || columnIndex == 6 || columnIndex == 7 || columnIndex == 8) {
                // Currency columns
                cell.setCellValue(((Number) value).doubleValue());
                cell.setCellStyle(currencyStyle);
            } else {
                cell.setCellValue(((Number) value).doubleValue());
                cell.setCellStyle(dataStyle);
            }
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            cell.setCellStyle(dateStyle);
        } else if (value instanceof java.util.Date) {
            cell.setCellValue((java.util.Date) value);
            cell.setCellStyle(dateStyle);
        } else {
            cell.setCellValue(value.toString());
            cell.setCellStyle(dataStyle);
        }
    }
    
    /**
     * Tạo cell với giá trị
     */
    private static void createCell(Row row, int columnIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (value != null) {
            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof Date) {
                cell.setCellValue((Date) value);
            } else if (value instanceof java.util.Date) {
                cell.setCellValue((java.util.Date) value);
            } else {
                cell.setCellValue(value.toString());
            }
        }
        cell.setCellStyle(style);
    }
    
    /**
     * Tạo summary row cho table
     */
    private static void createSummaryRow(Sheet sheet, int rowIndex, DefaultTableModel model, Workbook workbook) {
        CellStyle summaryStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        
        Row summaryRow = sheet.createRow(rowIndex);
        
        // Tổng số bản ghi
        Cell cell = summaryRow.createCell(0);
        cell.setCellValue("Tổng cộng:");
        cell.setCellStyle(summaryStyle);
        
        // Tính tổng các cột số
        double totalGross = 0;
        double totalNet = 0;
        double totalBonus = 0;
        double totalPenalty = 0;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            Object grossValue = model.getValueAt(i, 6); // Gross Salary column
            Object netValue = model.getValueAt(i, 8);   // Net Salary column
            Object bonusValue = model.getValueAt(i, 4); // Bonus column
            Object penaltyValue = model.getValueAt(i, 7); // Penalty column
            
            if (grossValue instanceof Number) {
                totalGross += ((Number) grossValue).doubleValue();
            }
            if (netValue instanceof Number) {
                totalNet += ((Number) netValue).doubleValue();
            }
            if (bonusValue instanceof Number) {
                totalBonus += ((Number) bonusValue).doubleValue();
            }
            if (penaltyValue instanceof Number) {
                totalPenalty += ((Number) penaltyValue).doubleValue();
            }
        }
        
        // Tổng Gross Salary
        Cell grossCell = summaryRow.createCell(6);
        grossCell.setCellValue(totalGross);
        grossCell.setCellStyle(currencyStyle);
        
        // Tổng Net Salary
        Cell netCell = summaryRow.createCell(8);
        netCell.setCellValue(totalNet);
        netCell.setCellStyle(currencyStyle);
        
        // Tổng Bonus
        Cell bonusCell = summaryRow.createCell(4);
        bonusCell.setCellValue(totalBonus);
        bonusCell.setCellStyle(currencyStyle);
        
        // Tổng Penalty
        Cell penaltyCell = summaryRow.createCell(7);
        penaltyCell.setCellValue(totalPenalty);
        penaltyCell.setCellStyle(currencyStyle);
    }
    
    /**
     * Tạo summary cho danh sách lương
     */
    private static void createSummaryForSalaryList(Sheet sheet, int rowIndex, List<Salary> salaries, Workbook workbook) {
        CellStyle summaryStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        
        Row summaryRow = sheet.createRow(rowIndex);
        
        // Tổng số bản ghi
        Cell cell = summaryRow.createCell(0);
        cell.setCellValue("Tổng cộng:");
        cell.setCellStyle(summaryStyle);
        
        // Tính tổng
        double totalGross = 0;
        double totalNet = 0;
        double totalBonus = 0;
        double totalPenalty = 0;
        
        for (Salary salary : salaries) {
            totalGross += salary.getGross_salary();
            totalNet += salary.getNet_salary();
            totalBonus += salary.getBonus();
            totalPenalty += salary.getPenalty_deduction();
        }
        
        // Tổng Gross Salary
        Cell grossCell = summaryRow.createCell(6);
        grossCell.setCellValue(totalGross);
        grossCell.setCellStyle(currencyStyle);
        
        // Tổng Net Salary
        Cell netCell = summaryRow.createCell(8);
        netCell.setCellValue(totalNet);
        netCell.setCellStyle(currencyStyle);
        
        // Tổng Bonus
        Cell bonusCell = summaryRow.createCell(4);
        bonusCell.setCellValue(totalBonus);
        bonusCell.setCellStyle(currencyStyle);
        
        // Tổng Penalty
        Cell penaltyCell = summaryRow.createCell(7);
        penaltyCell.setCellValue(totalPenalty);
        penaltyCell.setCellStyle(currencyStyle);
    }

    /**
     * Tạo summary cho báo cáo lương theo nhân viên
     */
    private static void createSummaryForEmployee(Sheet sheet, int rowIndex, List<Salary> salaries, Workbook workbook, String employeeName) {
        CellStyle summaryStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        
        Row summaryRow = sheet.createRow(rowIndex);
        
        // Tổng số bản ghi
        Cell cell = summaryRow.createCell(0);
        cell.setCellValue("Tổng cộng:");
        cell.setCellStyle(summaryStyle);
        
        // Tính tổng
        double totalGross = 0;
        double totalNet = 0;
        double totalBonus = 0;
        double totalPenalty = 0;
        
        for (Salary salary : salaries) {
            totalGross += salary.getGross_salary();
            totalNet += salary.getNet_salary();
            totalBonus += salary.getBonus();
            totalPenalty += salary.getPenalty_deduction();
        }
        
        // Tổng Gross Salary
        Cell grossCell = summaryRow.createCell(6);
        grossCell.setCellValue(totalGross);
        grossCell.setCellStyle(currencyStyle);
        
        // Tổng Net Salary
        Cell netCell = summaryRow.createCell(8);
        netCell.setCellValue(totalNet);
        netCell.setCellStyle(currencyStyle);
        
        // Tổng Bonus
        Cell bonusCell = summaryRow.createCell(4);
        bonusCell.setCellValue(totalBonus);
        bonusCell.setCellStyle(currencyStyle);
        
        // Tổng Penalty
        Cell penaltyCell = summaryRow.createCell(7);
        penaltyCell.setCellValue(totalPenalty);
        penaltyCell.setCellStyle(currencyStyle);
    }
} 