package view;

import dao.ThongKeDAO;
import model.Session;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Form thống kê cá nhân cho staff
 */
public class PersonalStatsForm extends JFrame {
    
    private int employeeId;
    private ThongKeDAO thongKeDAO;
    
    public PersonalStatsForm(int employeeId) {
        this.employeeId = employeeId;
        
        try {
            this.thongKeDAO = new ThongKeDAO();
            initComponents();
            loadData();
        } catch (SQLException ex) {
            Logger.getLogger(PersonalStatsForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(PersonalStatsForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo form: " + ex.getMessage());
        }
    }
    
    private void initComponents() {
        setTitle("👤 Thống Kê Cá Nhân - " + Session.getInstance().getFullName());
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void loadData() {
        try {
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // Tab 1: Lịch sử ca làm
            JPanel shiftHistoryPanel = createShiftHistoryPanel();
            tabbedPane.addTab("⏰ Lịch Sử Ca Làm", shiftHistoryPanel);
            
            setLayout(new BorderLayout());
            add(tabbedPane, BorderLayout.CENTER);
            
            // Close button
            JPanel buttonPanel = new JPanel();
            JButton closeButton = new JButton("Đóng");
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(closeButton);
            add(buttonPanel, BorderLayout.SOUTH);
            
        } catch (SQLException ex) {
            Logger.getLogger(PersonalStatsForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    private JLabel createInfoLabel(String label, String value) {
        JLabel infoLabel = new JLabel("<html><b>" + label + "</b><br/>" + value + "</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return infoLabel;
    }
    
    private JPanel createShiftHistoryPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("⏰ LỊCH SỬ CA LÀM VIỆC", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        String[] columns = {
            "Ngày", "Ca Làm", "Giờ Vào", "Giờ Ra", 
            "Trạng Thái", "Ghi Chú"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        List<Map<String, Object>> recentShifts = thongKeDAO.getRecentShifts(employeeId);
        for (Map<String, Object> shift : recentShifts) {
            // Kiểm tra null cho shift_type
            Object shiftTypeObj = shift.get("shift_type");
            String shiftType = (shiftTypeObj != null) ? shiftTypeObj.toString() : "UNKNOWN";
            String displayShiftType = "";
            switch (shiftType) {
                case "SANG": displayShiftType = "Ca Sáng"; break;
                case "CHIEU": displayShiftType = "Ca Chiều"; break;
                case "TOI": displayShiftType = "Ca Tối"; break;
                case "FULL": displayShiftType = "Ca Nguyên"; break;
                default: displayShiftType = shiftType;
            }
            
            // Kiểm tra null cho status
            Object statusObj = shift.get("status");
            String status = (statusObj != null) ? statusObj.toString() : "UNKNOWN";
            String displayStatus = "";
            switch (status) {
                case "COMPLETED": displayStatus = "Hoàn thành"; break;
                case "IN_PROGRESS": displayStatus = "Đang làm"; break;
                case "SCHEDULED": displayStatus = "Đã lên lịch"; break;
                case "ABSENT": displayStatus = "Vắng mặt"; break;
                default: displayStatus = status;
            }
            
            model.addRow(new Object[]{
                shift.get("shift_date"),
                displayShiftType,
                shift.get("start_time"),
                shift.get("end_time") != null ? shift.get("end_time") : "Chưa kết thúc",
                displayStatus,
                shift.getOrDefault("notes", "")
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        
        // Color rows based on status
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) table.getValueAt(row, 4); // Status column
                if (!isSelected) {
                    switch (status) {
                        case "Vắng mặt":
                            setBackground(new java.awt.Color(255, 235, 238));
                            break;
                        case "Đang làm":
                            setBackground(new java.awt.Color(232, 245, 233));
                            break;
                        case "Hoàn thành":
                            setBackground(new java.awt.Color(237, 247, 237));
                            break;
                        default:
                            setBackground(java.awt.Color.WHITE);
                    }
                }
                return this;
            }
        });
        
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