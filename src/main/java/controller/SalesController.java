/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.SalesDAO;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Customers;
import model.Orders;
import model.Products;
import model.Promotion;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import view.SalesForm;

public class SalesController {

    private SalesDAO salesDAO;
    private SalesForm salesView;
    private Connection connection;

    private int lastOrderId = -1;

    public int getLastOrderId() {
        return lastOrderId;
    }

    public SalesController() {
    }

    public SalesController(SalesDAO salesDAO, SalesForm salesView) {
        this.salesDAO = salesDAO;
        this.salesView = salesView;
        this.connection = connection;
        salesDAO = new SalesDAO(connection);
    }

    public void addCustomer() {
        String fullName = salesView.getTxtThemTenKhach().getText().trim();
        String phoneNumber = salesView.getTxtThemSdtKhach().getText().trim();

        if (fullName.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(salesView, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (!phoneNumber.matches("\\d+")) {
            JOptionPane.showMessageDialog(salesView, "Số điện thoại chỉ được chứa chữ số.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customers customer = new Customers(phoneNumber, fullName);
        boolean success = salesDAO.addCustomer(customer);

        if (success) {
            JOptionPane.showMessageDialog(salesView, "Thêm khách hàng thành công!");
        } else {
            JOptionPane.showMessageDialog(salesView, "Thêm khách hàng thất bại.");
        }
    }

    public void searchCustomers(String searchText) {
        try {
            DefaultTableModel tblModel = (DefaultTableModel) salesView.getTblSuggestions().getModel();
            tblModel.setRowCount(0); // Xóa dữ liệu cũ

            if (searchText.trim().isEmpty()) {
                salesView.getPopupMenu().setVisible(false);
                return;
            }

            List<Customers> customers = salesDAO.searchCustomers(searchText);
            if (customers.isEmpty()) {
                salesView.getPopupMenu().setVisible(false);
            } else {
                for (Customers customer : customers) {
                    tblModel.addRow(new Object[]{
                        customer.getCustomer_id(),
                        customer.getFull_name(),
                        customer.getPhone_number()
                    });
                }
                salesView.getPopupMenu().show(salesView.getTxtTimKiemKhachHang(), 0, salesView.getTxtTimKiemKhachHang().getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(salesView, "Lỗi tìm kiếm khách hàng: " + e.getMessage());
        }
    }

    public void searchProduct() {
        try {
            // Lấy dữ liệu người dùng chọn trên giao diện
            String productName = salesView.getTxtTimSP().getText().trim();

            // Gọi DAO
            List<Products> results = salesDAO.searchProduct(productName);

            // Hiển thị kết quả tìm kiếm lên bảng
            salesView.loadDuLieuProduct1(results);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(salesView, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    public void insertOrder() {
        try {
            // Lấy dữ liệu từ form
            int employeeId = salesView.getEmployeeId();
            // Lấy ngày hiện tại theo múi giờ Việt Nam
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            LocalDate localDate = LocalDate.now(zoneId);
            Date orderDate = Date.valueOf(localDate); // java.sql.Date
            String totalAmountStr = salesView.getTxtTongTienHang().getText().trim();
            int totalAmount = Integer.parseInt(totalAmountStr);
            String customerName = salesView.getTxtTimKiemKhachHang().getText().trim();
            if (customerName.isEmpty()) {
                JOptionPane.showMessageDialog(salesView, "Vui lòng chọn khách hàng trước khi thanh toán.");
                return;
            }
            int customerId = salesDAO.getCustomerIdByName(customerName);
            String pointStr = salesView.getTxtPoint().getText().trim();
            int point = Integer.parseInt(pointStr);

            if (customerId == -1) {
                JOptionPane.showMessageDialog(salesView, "Không tìm thấy khách hàng: " + customerName);
                return;
            }

            String finalAmountStr = salesView.getTxtKhachCanTra().getText().trim();
            int finalAmount = Integer.parseInt(finalAmountStr);

            // 1. Chèn đơn hàng vào DB
            int orderId = salesDAO.insertOrder(employeeId, orderDate, totalAmount, customerId, finalAmount);
            if (orderId == -1) {
                JOptionPane.showMessageDialog(salesView, "Không thể tạo đơn hàng!");
                return;
            }

            lastOrderId = orderId;

            // 2. Tính điểm sau khi giảm giá(nếu có)
            if (salesView.getChkDungPoint().isSelected()) {
                int usedPoints = point;

                boolean updated = salesDAO.subtractCustomerPoints(customerId, usedPoints);
                if (!updated) {
                    JOptionPane.showMessageDialog(salesView, "Lỗi khi trừ điểm khách hàng.");
                    return;
                }
            }

            // 3. Lấy dữ liệu sản phẩm từ bảng JTable
            DefaultTableModel tableModel = (DefaultTableModel) salesView.getTblDonHangView().getModel();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int productId = Integer.parseInt(tableModel.getValueAt(i, 0).toString()); // Cột product_id
                int quantity = Integer.parseInt(tableModel.getValueAt(i, 4).toString()); // Cột quantity
                int unitPrice = Integer.parseInt(tableModel.getValueAt(i, 3).toString()); // Cột unit price

                boolean success = salesDAO.insertOrderDetail(orderId, productId, quantity, unitPrice);
                if (!success) {
                    JOptionPane.showMessageDialog(salesView, "Lỗi khi lưu chi tiết sản phẩm.");
                    return;
                }
                // Cập nhật tồn kho - trigger đã trừ

//                boolean stockUpdated = salesDAO.updateProductStock(productId, quantity);
//                if (!stockUpdated) {
//                    JOptionPane.showMessageDialog(salesView, "Lỗi cập nhật tồn kho. Sản phẩm có thể không đủ số lượng.");
//                    return;
//                }
            }

            // 4. Tích điểm point cho khách hàng
            int earnedPoints = (int) (totalAmount * 0.02);
            int updatedPoints;
            if (salesView.getChkDungPoint().isSelected()) {
                updatedPoints = earnedPoints;
            } else {
                updatedPoints = point + earnedPoints;
            }
            salesDAO.updateCustomerPoint(customerId, updatedPoints);

            JOptionPane.showMessageDialog(salesView, "Thanh toán thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(salesView, "Lỗi tạo đơn hàng: " + e.getMessage());
        }

    }

    public void themSanPhamVaoDonHang(int selectedRow, int soLuongThem) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(salesView, "Vui lòng chọn sản phẩm để thêm!");
            return;
        }

        if (soLuongThem <= 0) {
            JOptionPane.showMessageDialog(salesView, "Số lượng phải lớn hơn 0!");
            return;
        }

        DefaultTableModel modelSanPham = (DefaultTableModel) salesView.getTblViewProduct().getModel();
        DefaultTableModel modelDonHang = (DefaultTableModel) salesView.getTblDonHangView().getModel();

        int maSanPham = (int) modelSanPham.getValueAt(selectedRow, 0);
        String tenSanPham = (String) modelSanPham.getValueAt(selectedRow, 1);
        int giaNhap = (int) modelSanPham.getValueAt(selectedRow, 3);

        // Giá bán tăng 20%
        int giaBan = (int) (giaNhap * 1.2);

        // Lấy khuyến mãi
        int discount = 0;
        int giaKhuyenMai = giaBan;
        Promotion promo = salesDAO.getActivePromotionByProductId(maSanPham);
        if (promo != null) {
            discount = promo.getDiscount(); // %
            giaKhuyenMai = giaBan * (100 - discount) / 100;
        }

        // Kiểm tra nếu đã có thì cộng số lượng
        boolean daCo = false;
        for (int i = 0; i < modelDonHang.getRowCount(); i++) {
            int idTrongDon = (int) modelDonHang.getValueAt(i, 0);
            if (idTrongDon == maSanPham) {
                int soLuongCu = (int) modelDonHang.getValueAt(i, 4);
                modelDonHang.setValueAt(soLuongCu + soLuongThem, i, 4);
                daCo = true;
                break;
            }
        }

        // Nếu chưa có thì thêm mới
        if (!daCo) {
            modelDonHang.addRow(new Object[]{
                maSanPham,
                tenSanPham,
                giaBan,
                giaKhuyenMai,
                soLuongThem,
                discount
            });
        }

        salesView.getSpnSoLuong().setValue(0);
        salesView.tinhTongTienHang();
        salesView.tinhGiamGia();
        salesView.tinhKhachCanTra();
    }

    public void exportInvoiceToExcel(int orderId) {
        try {
            Orders order = salesDAO.getOrderById(orderId);
            if (order == null) {
                JOptionPane.showMessageDialog(salesView, "Không tìm thấy hóa đơn!");
                return;
            }

            List<Products> productList = salesDAO.getOrderDetailsByOrderId(orderId);
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Hóa đơn bán hàng - GreenBuy");

            int rowIdx = 0;

            // --- STYLE ---
            CellStyle titleStyle = workbook.createCellStyle();
            XSSFFont titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle infoStyle = workbook.createCellStyle();
            infoStyle.setAlignment(HorizontalAlignment.LEFT);

            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setBorderTop(BorderStyle.THIN);
            moneyStyle.setBorderBottom(BorderStyle.THIN);
            moneyStyle.setBorderLeft(BorderStyle.THIN);
            moneyStyle.setBorderRight(BorderStyle.THIN);
            moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
            moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0 \"VNĐ\""));

            // --- Hàng 1: Tiêu đề ---
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("HÓA ĐƠN BÁN HÀNG - GREENBUY");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            // --- Thông tin hóa đơn ---
            rowIdx++;
            Row infoRow1 = sheet.createRow(rowIdx++);
            infoRow1.createCell(0).setCellValue("Mã hóa đơn: " + order.getOrder_id());
            infoRow1.createCell(2).setCellValue("Ngày: " + order.getOrder_date().toString());

            Row infoRow2 = sheet.createRow(rowIdx++);
            infoRow2.createCell(0).setCellValue("Khách hàng: " + order.getCustomerName());
            infoRow2.createCell(2).setCellValue("SĐT: " + order.getPhoneNumber());

            rowIdx++;

            // --- Header bảng sản phẩm ---
            String[] columns = {"Tên sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            long tongTienHang = 0;
            for (Products p : productList) {
                Row row = sheet.createRow(rowIdx++);

                Cell nameCell = row.createCell(0);
                nameCell.setCellValue(p.getProduct_name());
                nameCell.setCellStyle(dataStyle);

                Cell qtyCell = row.createCell(1);
                qtyCell.setCellValue(p.getQuantity());
                qtyCell.setCellStyle(dataStyle);

                Cell priceCell = row.createCell(2);
                priceCell.setCellValue(p.getPrice());
                priceCell.setCellStyle(moneyStyle);

                Cell totalCell = row.createCell(3);
                totalCell.setCellValue(p.getTotalPrice());
                totalCell.setCellStyle(moneyStyle);

                tongTienHang += p.getTotalPrice();
            }

            rowIdx++;

            int discount = (int) (tongTienHang - order.getFinalAmount());
            String[][] summaryRows = {
                {"Tổng tiền hàng:", String.valueOf(tongTienHang)},
                {"Chiết khấu:", String.valueOf(discount)},
                {"Khách cần trả:", String.valueOf(order.getFinalAmount())}
            };

            for (String[] item : summaryRows) {
                Row row = sheet.createRow(rowIdx++);
                Cell labelCell = row.createCell(2);
                labelCell.setCellValue(item[0]);
                Cell valueCell = row.createCell(3);
                valueCell.setCellValue(Long.parseLong(item[1]));
                valueCell.setCellStyle(moneyStyle);
            }

            rowIdx++;
            Row thankRow = sheet.createRow(rowIdx++);
            Cell thankCell = thankRow.createCell(0);
            thankCell.setCellValue("Cảm ơn quý khách, hẹn gặp lại!");
            sheet.addMergedRegion(new CellRangeAddress(thankRow.getRowNum(), thankRow.getRowNum(), 0, 3));

            // --- Auto size ---
            sheet.setColumnWidth(0, 10000);
            for (int i = 1; i < columns.length; i++) {
                if (i != 3) {
                    sheet.autoSizeColumn(i);
                }
            }

            sheet.setColumnWidth(3, 6000);
            
            // --- Ghi file ---
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu hóa đơn");
            fileChooser.setSelectedFile(new File("HoaDon_" + orderId + ".xlsx"));

            int userSelection = fileChooser.showSaveDialog(salesView);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                    workbook.write(out);
                }
                workbook.close();

                Desktop.getDesktop().open(fileToSave);
                JOptionPane.showMessageDialog(salesView, "In hóa đơn thành công!\nĐã lưu tại: " + fileToSave.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(salesView, "Đã hủy in hóa đơn.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(salesView, "Lỗi khi in hóa đơn: " + e.getMessage());
        }
    }

}
