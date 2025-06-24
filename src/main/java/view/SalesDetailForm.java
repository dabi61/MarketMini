package view;

import dao.ThongKeDAO;
import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Form chi tiết bán hàng
 */
public class SalesDetailForm extends JFrame {
    
    private int userRole;
    private int employeeId;
    private ThongKeDAO thongKeDAO;
    
    public SalesDetailForm(int userRole, int employeeId) {
        this.userRole = userRole;
        this.employeeId = employeeId;
        
        try {
            this.thongKeDAO = new ThongKeDAO();
            initComponents();
            loadData();
        } catch (SQLException ex) {
            Logger.getLogger(SalesDetailForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + ex.getMessage());
        }
    }
    
    private void initComponents() {
        setTitle("💰 Chi Tiết Bán Hàng");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void loadData() {
        try {
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // Tab 1: Top sản phẩm bán chạy
            JPanel topProductsPanel = createTopProductsPanel();
            tabbedPane.addTab("🏆 Top Sản Phẩm", topProductsPanel);
            
            // Tab 2: Doanh thu theo ngày
            JPanel dailyRevenuePanel = createDailyRevenuePanel();
            tabbedPane.addTab("📅 Doanh Thu Ngày", dailyRevenuePanel);
            
            // Tab 3: Hiệu suất nhân viên (chỉ admin)
            if (userRole == 1) {
                JPanel employeePanel = createEmployeePerformancePanel();
                tabbedPane.addTab("👥 Hiệu Suất NV", employeePanel);
            }
            
            setLayout(new BorderLayout());
            add(tabbedPane, BorderLayout.CENTER);
            
            // Close button
            JPanel buttonPanel = new JPanel();
            JButton closeButton = new JButton("Đóng");
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(closeButton);
            add(buttonPanel, BorderLayout.SOUTH);
            
        } catch (SQLException ex) {
            Logger.getLogger(SalesDetailForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    private JPanel createTopProductsPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("🏆 TOP SẢN PHẨM BÁN CHẠY", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        String[] columns = {"Mã SP", "Tên Sản Phẩm", "Danh Mục", "Số Lượng Bán", "Doanh Thu", "Số Đơn"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        List<Map<String, Object>> topProducts = thongKeDAO.getTopSellingProducts(20);
        for (Map<String, Object> product : topProducts) {
            model.addRow(new Object[]{
                product.get("product_id"),
                product.get("product_name"),
                product.get("category_name"),
                product.get("total_sold"),
                String.format("%,d VNĐ", (Long) product.getOrDefault("total_revenue", 0L)),
                product.get("order_count")
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDailyRevenuePanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("📅 DOANH THU THEO NGÀY", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        String[] columns = {"Ngày", "Số Đơn", "Doanh Thu", "Giảm Giá", "Thực Thu"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        List<Map<String, Object>> dailyRevenue = thongKeDAO.getRecentDaysRevenue();
        for (Map<String, Object> day : dailyRevenue) {
            model.addRow(new Object[]{
                day.get("date"),
                day.get("order_count"),
                String.format("%,d VNĐ", (Long) day.getOrDefault("daily_revenue", 0L)),
                String.format("%,d VNĐ", (Long) day.getOrDefault("total_discount", 0L)),
                String.format("%,d VNĐ", (Long) day.getOrDefault("actual_revenue", 0L))
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEmployeePerformancePanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("👥 HIỆU SUẤT NHÂN VIÊN", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        String[] columns = {"Mã NV", "Họ Tên", "Role", "Số Đơn", "Doanh Thu", "Đơn TB", "Lần Cuối"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        LocalDate now = LocalDate.now();
        List<Map<String, Object>> empPerformance = thongKeDAO.getEmployeePerformance(now.getMonthValue(), now.getYear());
        
        for (Map<String, Object> emp : empPerformance) {
            // Safe role conversion
            String roleStr = "Staff";
            Object roleObj = emp.get("role");
            try {
                if (roleObj != null) {
                    int roleValue;
                    if (roleObj instanceof Integer) {
                        roleValue = (Integer) roleObj;
                    } else if (roleObj instanceof String) {
                        roleValue = Integer.parseInt((String) roleObj);
                    } else {
                        roleValue = 2; // Default to staff
                    }
                    roleStr = roleValue == 1 ? "Admin" : "Staff";
                }
            } catch (Exception e) {
                roleStr = "Staff"; // Default fallback
            }
            
            model.addRow(new Object[]{
                emp.get("employee_id"),
                emp.get("full_name"),
                roleStr,
                emp.get("total_orders"),
                String.format("%,d VNĐ", (Long) emp.getOrDefault("total_sales", 0L)),
                String.format("%,d VNĐ", (Long) emp.getOrDefault("avg_order_value", 0L)),
                emp.get("last_sale_date")
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    @Override
    public void dispose() {
        if (thongKeDAO != null) {
            thongKeDAO.closeConnection();
        }
        super.dispose();
    }
} 