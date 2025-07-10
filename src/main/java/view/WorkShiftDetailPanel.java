package view;

import controller.WorkShiftController;
import model.WorkShift;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Panel chi tiết hiển thị đầy đủ thông tin ca làm việc
 * @author macbook
 */
public class WorkShiftDetailPanel extends JPanel {
    
    private WorkShiftController workShiftController;
    private WorkShift currentShift;
    
    // Modern Color Scheme
    private static final Color PRIMARY_COLOR = new Color(46, 125, 50);       // Green
    private static final Color SECONDARY_COLOR = new Color(76, 175, 80);     // Light Green  
    private static final Color ACCENT_COLOR = new Color(255, 152, 0);        // Orange
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);  // Light Gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(66, 66, 66);
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color WARNING_COLOR = new Color(255, 152, 0);
    private static final Color ERROR_COLOR = new Color(244, 67, 54);
    
    // Components
    private JLabel lblSessionId, lblEmployeeId, lblEmployeeName;
    private JLabel lblLoginTime, lblLogoutTime, lblWorkingHours;
    private JLabel lblDate, lblWorkStatus, lblHourlyWage;
    private JLabel lblEarnings;
    private JButton btnClose, btnDelete;
    private JPanel mainPanel, infoPanel, actionPanel;
    
    public WorkShiftDetailPanel(WorkShift shift) {
        this.currentShift = shift;
        this.workShiftController = new WorkShiftController();
        
        setupLookAndFeel();
        initComponents();
        loadShiftData();
    }
    
    private void setupLookAndFeel() {
        setBackground(BACKGROUND_COLOR);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Info panel
        infoPanel = createInfoPanel();
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        
      
        
        // Action panel
        actionPanel = createActionPanel();
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel title = new JLabel("📋 CHI TIẾT CA LÀM VIỆC", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        
        panel.add(title, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(400, 500));
        
        // Title
        JLabel title = new JLabel("📊 Thông tin chi tiết");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(PRIMARY_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        
        // Session ID
        panel.add(createInfoRow("🆔 ID Ca làm:", "lblSessionId"));
        panel.add(Box.createVerticalStrut(8));
        
        // Employee info
        panel.add(createInfoRow("👤 Nhân viên:", "lblEmployeeName"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createInfoRow("🆔 ID Nhân viên:", "lblEmployeeId"));
        panel.add(Box.createVerticalStrut(8));
        
        // Date
        panel.add(createInfoRow("📅 Ngày làm việc:", "lblDate"));
        panel.add(Box.createVerticalStrut(8));
        
        // Time info
        panel.add(createInfoRow("⏰ Giờ vào:", "lblLoginTime"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createInfoRow("⏰ Giờ ra:", "lblLogoutTime"));
        panel.add(Box.createVerticalStrut(8));
        
        // Working hours
        panel.add(createInfoRow("⏱️ Giờ làm:", "lblWorkingHours"));
        panel.add(Box.createVerticalStrut(8));
        
        // Status
        panel.add(createInfoRow("📊 Trạng thái:", "lblWorkStatus"));
        panel.add(Box.createVerticalStrut(8));
        
        // Hourly wage
        panel.add(createInfoRow("💰 Lương/giờ:", "lblHourlyWage"));
        panel.add(Box.createVerticalStrut(8));
        
        // Earnings
        panel.add(createInfoRow("💵 Thu nhập:", "lblEarnings"));
        
        return panel;
    }
    
    private JPanel createInfoRow(String labelText, String componentName) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(CARD_COLOR);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        label.setPreferredSize(new Dimension(120, 25));
        
        JLabel value = new JLabel();
        value.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        value.setForeground(TEXT_COLOR);
        value.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        
        // Store reference to component
        switch (componentName) {
            case "lblSessionId" -> lblSessionId = value;
            case "lblEmployeeName" -> lblEmployeeName = value;
            case "lblEmployeeId" -> lblEmployeeId = value;
            case "lblDate" -> lblDate = value;
            case "lblLoginTime" -> lblLoginTime = value;
            case "lblLogoutTime" -> lblLogoutTime = value;
            case "lblWorkingHours" -> lblWorkingHours = value;
            case "lblWorkStatus" -> lblWorkStatus = value;
            case "lblHourlyWage" -> lblHourlyWage = value;
            case "lblEarnings" -> lblEarnings = value;
        }
        
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.CENTER);
        
        return row;
    }
    
    
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);
        
        btnDelete = createStyledButton("🗑️ Xóa", ERROR_COLOR);
        btnClose = createStyledButton("❌ Đóng", SECONDARY_COLOR);
        
        btnDelete.addActionListener(e -> deleteShift());
        btnClose.addActionListener(e -> closePanel());
        
        panel.add(btnDelete);
        panel.add(btnClose);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void loadShiftData() {
        if (currentShift == null) {
            showErrorMessage("Không có dữ liệu ca làm việc");
            return;
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        // Basic info
        lblSessionId.setText(String.valueOf(currentShift.getWorkingSessionId()));
        lblEmployeeId.setText(String.valueOf(currentShift.getEmployeeId()));
        lblEmployeeName.setText(currentShift.getEmployeeName());
        
        // Date
        if (currentShift.getDate() != null) {
            lblDate.setText(dateFormat.format(currentShift.getDate()));
        } else {
            lblDate.setText("N/A");
        }
        
        // Login time
        if (currentShift.getLoginTime() != null) {
            lblLoginTime.setText(dateTimeFormat.format(currentShift.getLoginTime()));
        } else {
            lblLoginTime.setText("Chưa vào ca");
        }
        
        // Logout time
        if (currentShift.getLogoutTime() != null) {
            lblLogoutTime.setText(dateTimeFormat.format(currentShift.getLogoutTime()));
        } else {
            lblLogoutTime.setText("Chưa kết thúc");
        }
        
        // Working hours
        if (currentShift.getWorkingHours() != null) {
            BigDecimal hours = currentShift.getWorkingHours();
            lblWorkingHours.setText(hours.setScale(2, BigDecimal.ROUND_HALF_UP) + " giờ");
        } else {
            lblWorkingHours.setText("0.00 giờ");
        }
        
        // Work status
        String status = currentShift.getWorkStatus();
        lblWorkStatus.setText(getStatusDisplayName(status));
        
        // Hourly wage
        if (currentShift.getHourlyWage() != null) {
            BigDecimal wage = currentShift.getHourlyWage();
            lblHourlyWage.setText(wage.setScale(0, BigDecimal.ROUND_HALF_UP) + " VNĐ/giờ");
        } else {
            lblHourlyWage.setText("N/A");
        }
        
        // Earnings
        if (currentShift.getWorkingHours() != null && currentShift.getHourlyWage() != null) {
            BigDecimal earnings = currentShift.getWorkingHours().multiply(currentShift.getHourlyWage());
            lblEarnings.setText(earnings.setScale(0, BigDecimal.ROUND_HALF_UP) + " VNĐ");
        } else {
            lblEarnings.setText("N/A");
        }
    }
    
    private String getStatusDisplayName(String status) {
        return switch (status) {
            case "SCHEDULED" -> "📅 Đã lên lịch";
            case "IN_PROGRESS" -> "🔄 Đang làm việc";
            case "COMPLETED" -> "✅ Hoàn thành";
            case "ABSENT" -> "❌ Vắng mặt";
            case "CANCELLED" -> "🚫 Đã hủy";
            default -> "❓ Không xác định";
        };
    }
    
    private void deleteShift() {
        if (currentShift == null) {
            showErrorMessage("Không có ca làm việc để xóa");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa ca làm việc này?\n\n" +
            "ID: " + currentShift.getWorkingSessionId() + "\n" +
            "Nhân viên: " + currentShift.getEmployeeName() + "\n" +
            "Ngày: " + (currentShift.getDate() != null ? currentShift.getDate() : "N/A"), 
            "❓ Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = workShiftController.deleteShift(currentShift.getWorkingSessionId());
                if (success) {
                    showSuccessMessage("Xóa ca làm việc thành công!");
                    closePanel();
                } else {
                    showErrorMessage("Lỗi khi xóa ca làm việc!");
                }
            } catch (Exception ex) {
                Logger.getLogger(WorkShiftDetailPanel.class.getName()).log(Level.SEVERE, null, ex);
                showErrorMessage("Lỗi: " + ex.getMessage());
            }
        }
    }
    
    private void closePanel() {
        // Find and close the parent window
        Window parent = SwingUtilities.getWindowAncestor(this);
        if (parent != null) {
            parent.dispose();
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "❌ Lỗi", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "✅ Thành công", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Tạo dialog hiển thị chi tiết ca làm việc
     */
    public static void showShiftDetail(WorkShift shift, Component parent) {
        WorkShiftDetailPanel detailPanel = new WorkShiftDetailPanel(shift);
        
        // Tìm parent window
        Window parentWindow = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog;
        
        if (parentWindow instanceof Frame) {
            dialog = new JDialog((Frame) parentWindow, "Chi tiết ca làm việc", true);
        } else if (parentWindow instanceof Dialog) {
            dialog = new JDialog((Dialog) parentWindow, "Chi tiết ca làm việc", true);
        } else {
            // Fallback nếu không tìm thấy parent window phù hợp
            dialog = new JDialog();
            dialog.setTitle("Chi tiết ca làm việc");
            dialog.setModal(true);
        }
        
        dialog.setLayout(new BorderLayout());
        dialog.add(detailPanel, BorderLayout.CENTER);
        
        // Set dialog properties
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        dialog.setVisible(true);
    }
} 