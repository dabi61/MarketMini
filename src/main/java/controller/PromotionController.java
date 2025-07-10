/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ProductDAO;
import dao.PromotionDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import model.Promotion;
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
public class PromotionController {
    private PromotionDAO dao;
    private final PromotionDAO promotionDAO = new PromotionDAO();
    private final ProductDAO productDAO = new ProductDAO();
    public PromotionController() throws SQLException {
        dao = new PromotionDAO();
    }

    public DefaultTableModel getPromotionTableModel() throws SQLException {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.setColumnIdentifiers(new String[] {
            "Promotion ID", "Tên khuyến mãi", "Ngày tạo mã", "Ngày hết hạn",
            "Tên sản phẩm", "Giảm (%)", "Giá gốc", "Giá sau giảm"
        });

        List<Object[]> list = dao.getAllPromotionsWithProductNames();
        for (Object[] row : list) {
            model.addRow(row);
        }

        return model;
    }
    public boolean isPromotionNameDuplicate(String promotionName) throws SQLException {
    PromotionDAO dao = new PromotionDAO();
    return dao.isPromotionNameExists(promotionName);
}

    public boolean savePromotion(
        boolean isEditing,
        int editingPromotionId,
        String promotionName,
        Date startDate,
        Date endDate,
        String productName,
        int discount
    ) throws Exception {
        if (promotionName.trim().isEmpty() || productName.trim().isEmpty()) {
            throw new Exception("Vui lòng điền đầy đủ thông tin!");
        }

        if (discount < 0 || discount > 100) {
            throw new Exception("Phần trăm giảm giá không hợp lệ!");
        }

        int productId = productDAO.getProductIdByName(productName);
        int originalPrice = productDAO.getPriceByProductId(productId);
        int discountedPrice = originalPrice * (100 - discount) / 100;

        Promotion promotion = new Promotion();
        promotion.setPromotionName(promotionName);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setProductId(productId);
        promotion.setDiscount(discount);
        promotion.setOriginalPrice(originalPrice);
        promotion.setDiscountedPrice(discountedPrice);

        if (isEditing) {
            promotion.setPromotionId(editingPromotionId);
            return promotionDAO.update(promotion);
        } else {
            return promotionDAO.insert(promotion);
        }
    }
    
    public boolean deletePromotion(int promotionId) {
    return promotionDAO.delete(promotionId);
}

    public DefaultTableModel searchPromotions(String keyword) throws SQLException {
    List<Object[]> results;

    if (keyword == null || keyword.trim().isEmpty()) {
        results = promotionDAO.getAllPromotionsWithProductNames();
    } else {
        results = promotionDAO.searchByPromotionName(keyword);
    }

    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    model.setColumnIdentifiers(new String[] {
        "ID", "Tên khuyến mãi", "Ngày bắt đầu", "Ngày kết thúc",
        "Tên sản phẩm", "Giảm giá (%)", "Giá gốc", "Giá sau giảm"
    });

    for (Object[] row : results) {
        model.addRow(row);
    }

    return model;
}

    public void exportToExcel(TableModel model, File file) throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Danh sách khuyến mãi");

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        // Ghi header
        Row headerRow = sheet.createRow(0);
        for (int col = 0; col < model.getColumnCount(); col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(model.getColumnName(col));
            cell.setCellStyle(headerStyle);
        }

        // Ghi dữ liệu
        for (int row = 0; row < model.getRowCount(); row++) {
            Row excelRow = sheet.createRow(row + 1);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = excelRow.createCell(col);
                Object value = model.getValueAt(row, col);
                cell.setCellValue(value != null ? value.toString() : "");
            }
        }

        // Auto-size
        for (int col = 0; col < model.getColumnCount(); col++) {
            sheet.autoSizeColumn(col);
        }

        // Ghi file
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
    }
}

}
