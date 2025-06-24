package view;

import dao.ThongKeDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Form chi tiết tồn kho
 */
public class InventoryDetailForm extends JFrame {
    
    private int userRole;
    private ThongKeDAO thongKeDAO;
    
    public InventoryDetailForm(int userRole) {
        this.userRole = userRole;
        
        try {
            this.thongKeDAO = new ThongKeDAO();
            initComponents();
            loadData();
        } catch (SQLException ex) {
            Logger.getLogger(InventoryDetailForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + ex.getMessage());
        }
    }
    
    private void initComponents() {
        setTitle("📦 Chi Tiết Tồn Kho");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Title
        JLabel title = new JLabel("📦 CHI TIẾT TỒN KHO & INVENTORY", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);
        
        // Close button
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        
        JButton refreshButton = new JButton("🔄 Làm Mới");
        refreshButton.addActionListener(e -> loadData());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadData() {
        try {
            // Inventory table
            String[] columns = {
                "Mã SP", "Tên Sản Phẩm", "Danh Mục", "Tồn Kho", 
                "Giá Bán", "Giá Trị Tồn", "Trạng Thái", "Đơn Vị"
            };
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            
            List<Map<String, Object>> inventory = thongKeDAO.getInventoryStatus();
            for (Map<String, Object> item : inventory) {
                model.addRow(new Object[]{
                    item.getOrDefault("product_id", ""),
                    item.getOrDefault("product_name", ""),
                    item.getOrDefault("category_name", ""),
                    item.getOrDefault("stock_quantity", 0),
                    String.format("%,d VNĐ", ((Number) item.getOrDefault("price", 0)).intValue()),
                    String.format("%,d VNĐ", ((Number) item.getOrDefault("inventory_value", 0L)).longValue()),
                    item.getOrDefault("stock_status", "Không xác định"),
                    "Cái" // Default unit
                });
            }
            
            JTable table = new JTable(model);
            table.setRowHeight(25);
            table.setFont(new Font("Arial", Font.PLAIN, 12));
            
            // Color rows based on stock status
            table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(JTable table, Object value, 
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    try {
                        String status = (String) table.getValueAt(row, 6); // Status column
                        if (!isSelected && status != null) {
                            switch (status) {
                                case "Hết hàng":
                                    setBackground(new Color(255, 235, 238));
                                    setForeground(Color.RED);
                                    break;
                                case "Sắp hết":
                                    setBackground(new Color(255, 243, 224));
                                    setForeground(new Color(255, 152, 0));
                                    break;
                                case "Ít":
                                    setBackground(new Color(255, 248, 225));
                                    setForeground(new Color(255, 193, 7));
                                    break;
                                default:
                                    setBackground(Color.WHITE);
                                    setForeground(Color.BLACK);
                            }
                        } else if (!isSelected) {
                            setBackground(Color.WHITE);
                            setForeground(Color.BLACK);
                        }
                    } catch (Exception e) {
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                    }
                    return this;
                }
            });
            
            // Add summary panel
            JPanel summaryPanel = createSummaryPanel(inventory);
            
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(summaryPanel, BorderLayout.NORTH);
            contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
            
            // Remove previous content and add new
            getContentPane().removeAll();
            add(createTitlePanel(), BorderLayout.NORTH);
            add(contentPanel, BorderLayout.CENTER);
            add(createButtonPanel(), BorderLayout.SOUTH);
            
            revalidate();
            repaint();
            
        } catch (SQLException ex) {
            Logger.getLogger(InventoryDetailForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        JLabel title = new JLabel("📦 CHI TIẾT TỒN KHO & INVENTORY", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        panel.add(title);
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        
        JButton refreshButton = new JButton("🔄 Làm Mới");
        refreshButton.addActionListener(e -> loadData());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        return buttonPanel;
    }
    
    private JPanel createSummaryPanel(List<Map<String, Object>> inventory) {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.X_AXIS));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("📊 Tổng Quan Tồn Kho"));
        
        // Calculate summary stats
        int totalProducts = inventory.size();
        int outOfStock = 0;
        int lowStock = 0;
        long totalValue = 0;
        
        for (Map<String, Object> item : inventory) {
            String status = (String) item.getOrDefault("stock_status", "");
            if ("Hết hàng".equals(status)) outOfStock++;
            else if ("Sắp hết".equals(status) || "Ít".equals(status)) lowStock++;
            
            totalValue += ((Number) item.getOrDefault("inventory_value", 0L)).longValue();
        }
        
        // Create summary labels
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        
        statsPanel.add(new JLabel("📦 Tổng số sản phẩm: " + totalProducts));
        statsPanel.add(new JLabel("❌ Hết hàng: " + outOfStock));
        statsPanel.add(new JLabel("⚠️ Sắp hết/Ít: " + lowStock));
        statsPanel.add(new JLabel("💰 Tổng giá trị tồn: " + String.format("%,d VNĐ", totalValue)));
        
        summaryPanel.add(statsPanel);
        
        return summaryPanel;
    }
    
    @Override
    public void dispose() {
        if (thongKeDAO != null) {
            thongKeDAO.closeConnection();
        }
        super.dispose();
    }
} 