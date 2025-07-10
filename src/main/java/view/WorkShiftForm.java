package view;

import dao.WorkShiftDAO;
import dao.EmployeeDAO;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import model.WorkShift;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

/**
 * Form quản lý ca làm việc cho Admin - Enhanced UI
 * @author macbook
 */
public class WorkShiftForm extends JPanel {
    
    private WorkShiftDAO workShiftDAO;
    private EmployeeDAO employeeDAO;
    private Map<String, Integer> employeeMap;
    private DefaultTableModel tableModel;
    
    // Modern Color Scheme
    private static final Color PRIMARY_COLOR = new Color(46, 125, 50);       // Green
    private static final Color SECONDARY_COLOR = new Color(76, 175, 80);     // Light Green  
    private static final Color ACCENT_COLOR = new Color(255, 152, 0);        // Orange
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);  // Light Gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(66, 66, 66);
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    
    // Components
    private JComboBox<String> cboEmployee;
    private JComboBox<String> cboShiftType;
    private JDateChooser dateChooser;
    private JSpinner timeSpinner;
    private JTextArea txtNotes;
    private JTable tblShifts;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private JLabel lblStatus;
    
    public WorkShiftForm() {
        try {
            this.workShiftDAO = new WorkShiftDAO();
            this.employeeDAO = new EmployeeDAO();
            this.employeeMap = new HashMap<>();
            
            setupLookAndFeel();
            initComponents();
            loadEmployees();
            loadShifts();
        } catch (SQLException ex) {
            Logger.getLogger(WorkShiftForm.class.getName()).log(Level.SEVERE, null, ex);
            showErrorMessage("Lỗi khởi tạo: " + ex.getMessage());
        }
    }
    
    private void setupLookAndFeel() {
        setBackground(BACKGROUND_COLOR);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header với gradient background
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Input panel với modern design
        JPanel inputPanel = createModernInputPanel();
        mainPanel.add(inputPanel, BorderLayout.WEST);
        
        // Table panel với styling
        JPanel tablePanel = createModernTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel title = new JLabel(" QUẢN LÝ CA LÀM VIỆC", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.add(title, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createModernInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        panel.setPreferredSize(new Dimension(350, 600));
        
        // Title
        JLabel formTitle = new JLabel(" Thông tin ca làm việc");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(PRIMARY_COLOR);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(formTitle);
        panel.add(Box.createVerticalStrut(15));
        
        // Employee selection
        panel.add(createFieldGroup(" Nhân viên:", createEmployeeCombo()));
        panel.add(Box.createVerticalStrut(10));
        
        // Date selection với DatePicker
        panel.add(createFieldGroup(" Ngày làm việc:", createDateChooser()));
        panel.add(Box.createVerticalStrut(10));
        
        // Shift type
        panel.add(createFieldGroup(" Loại ca:", createShiftTypeCombo()));
        panel.add(Box.createVerticalStrut(10));
        
        // Time selection với Spinner
        panel.add(createFieldGroup(" Giờ bắt đầu:", createTimeSpinner()));
        panel.add(Box.createVerticalStrut(10));
        
        // Notes
        panel.add(createFieldGroup(" Ghi chú:", createNotesArea()));
        panel.add(Box.createVerticalStrut(15));
        
        // Buttons
        panel.add(createButtonPanel());
        
        return panel;
    }
    
    private JPanel createFieldGroup(String labelText, JComponent component) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(CARD_COLOR);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setMaximumSize(new Dimension(500, component.getPreferredSize().height));
        
        group.add(label);
        group.add(Box.createVerticalStrut(5));
        group.add(component);
        
        return group;
    }
    
    private JComboBox<String> createEmployeeCombo() {
        cboEmployee = new JComboBox<>(new String[]{"-- Chọn nhân viên --"});
        styleComboBox(cboEmployee);
        return cboEmployee;
    }
    
    private JDateChooser createDateChooser() {
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new Date()); // Set ngày hiện tại
        
        // Style DateChooser
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        dateChooser.setBackground(Color.WHITE);
        
        return dateChooser;
    }
    
    private JComboBox<String> createShiftTypeCombo() {
        String[] shiftTypes = {"-- Chọn ca làm việc --", " Ca Sáng", "️ Ca Chiều", " Ca Tối", " Ca Nguyên"};
        cboShiftType = new JComboBox<>(shiftTypes);
        styleComboBox(cboShiftType);
        return cboShiftType;
    }
    
    private JSpinner createTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        timeSpinner = new JSpinner(model);
        
        JSpinner.DateEditor editor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(editor);
        
        // Set default time to 8:00 AM
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        timeSpinner.setValue(cal.getTime());
        
        styleSpinner(timeSpinner);
        return timeSpinner;
    }
    
    private JScrollPane createNotesArea() {
        txtNotes = new JTextArea(3, 20);
        txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNotes.setBorder(new EmptyBorder(8, 12, 8, 12));
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        
        // Placeholder text effect
        txtNotes.setForeground(Color.GRAY);
        txtNotes.setText("Nhập ghi chú cho ca làm việc...");
        
        txtNotes.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtNotes.getText().equals("Nhập ghi chú cho ca làm việc...")) {
                    txtNotes.setText("");
                    txtNotes.setForeground(TEXT_COLOR);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (txtNotes.getText().isEmpty()) {
                    txtNotes.setForeground(Color.GRAY);
                    txtNotes.setText("Nhập ghi chú cho ca làm việc...");
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(txtNotes);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        return scrollPane;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(CARD_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(300, 80));
        
        btnAdd = createStyledButton(" Thêm", PRIMARY_COLOR);
        btnUpdate = createStyledButton("️ Sửa", ACCENT_COLOR);
        btnDelete = createStyledButton("️ Xóa", new Color(244, 67, 54));
        btnRefresh = createStyledButton(" Làm mới", SECONDARY_COLOR);
        
        btnAdd.addActionListener(e -> addShift());
        btnUpdate.addActionListener(e -> updateShift());
        btnDelete.addActionListener(e -> deleteShift());
        btnRefresh.addActionListener(e -> {
            clearForm();
            loadShifts();
            showStatusMessage("Đã làm mới dữ liệu", false);
        });
        
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
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
    
    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_COLOR);
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        // Style spinner editor
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setBorder(null);
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
    }
    
    private JPanel createModernTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Table title
        JLabel tableTitle = new JLabel(" Danh sách ca làm việc");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(PRIMARY_COLOR);
        panel.add(tableTitle, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", " Nhân viên", " Ngày", " Ca làm", " Giờ vào", " Giờ ra", " Dự kiến (h)", " Thực tế (h)", " Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblShifts = new JTable(tableModel);
        setupModernTable();
        
        JScrollPane scrollPane = new JScrollPane(tblShifts);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupModernTable() {
        tblShifts.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblShifts.setRowHeight(35);
        tblShifts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblShifts.setSelectionBackground(new Color(232, 245, 233));
        tblShifts.setSelectionForeground(TEXT_COLOR);
        tblShifts.setGridColor(BORDER_COLOR);
        tblShifts.setShowVerticalLines(true);
        tblShifts.setShowHorizontalLines(true);
        
        // Setup table sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tblShifts.setRowSorter(sorter);
        
        // Enable sorting only for Date (column 2) and Status (column 8)
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            if (i != 2 && i != 8) { // Only allow sorting on Date and Status columns
                sorter.setSortable(i, false);
            }
        }
        
        // Set default sort by Date (column 2) - newest first
        java.util.List<RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        
        // Custom comparator for date column to handle java.sql.Date properly
        sorter.setComparator(2, new java.util.Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof java.sql.Date && o2 instanceof java.sql.Date) {
                    return ((java.sql.Date) o2).compareTo((java.sql.Date) o1); // Descending order
                }
                return o1.toString().compareTo(o2.toString());
            }
        });
        
        // Header styling
        JTableHeader header = tblShifts.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        // Add cursor hint for sortable columns
        header.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int column = header.columnAtPoint(e.getPoint());
                if (column == 2 || column == 8) {
                    header.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    header.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
        
        // Custom cell renderer for status column
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 8) { // Status column
                    String status = value.toString();
                    setHorizontalAlignment(JLabel.CENTER);
                    
                    if (!isSelected) {
                        switch (status) {
                            case "Hoàn thành" -> setBackground(new Color(200, 230, 201));
                            case "Đang làm" -> setBackground(new Color(255, 243, 224));
                            case "Đã lên lịch" -> setBackground(new Color(227, 242, 253));
                            case "Vắng mặt" -> setBackground(new Color(255, 205, 210));
                            default -> setBackground(Color.WHITE);
                        }
                    }
                }
                return this;
            }
        };
        
        tblShifts.getColumnModel().getColumn(8).setCellRenderer(statusRenderer);
        
        // Center align date column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblShifts.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        // Column widths
        tblShifts.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblShifts.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblShifts.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblShifts.getColumnModel().getColumn(8).setPreferredWidth(120);
        
        // Double click to edit
        tblShifts.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    fillFormFromTable();
                }
            }
        });
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        lblStatus = new JLabel("📌 Sẵn sàng");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(TEXT_COLOR);
        
        panel.add(lblStatus, BorderLayout.WEST);
        
        return panel;
    }
    
    private void showStatusMessage(String message, boolean isError) {
        if (isError) {
            lblStatus.setText("❌ " + message);
            lblStatus.setForeground(new Color(244, 67, 54));
        } else {
            lblStatus.setText("✅ " + message);
            lblStatus.setForeground(new Color(76, 175, 80));
        }
        
        // Auto clear after 3 seconds
        Timer timer = new Timer(3000, e -> {
            lblStatus.setText("📌 Sẵn sàng");
            lblStatus.setForeground(TEXT_COLOR);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "❌ Lỗi", 
            JOptionPane.ERROR_MESSAGE);
        showStatusMessage(message, true);
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "✅ Thành công", 
            JOptionPane.INFORMATION_MESSAGE);
        showStatusMessage(message, false);
    }
    
    private void loadEmployees() {
        try {
            // Sử dụng load_execel với empty employee để lấy tất cả
            model.Employees emptyEmployee = new model.Employees();
            emptyEmployee.setEmployee_name(""); // Empty name để lấy tất cả
            
            ResultSet rs = employeeDAO.load_execel(emptyEmployee);
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            model.addElement("-- Chọn nhân viên --");
            
            while (rs.next()) {
                int id = rs.getInt("employee_id");
                String fullName = rs.getString("full_name");
                String displayName = fullName + " (ID: " + id + ")";
                model.addElement(displayName);
                employeeMap.put(displayName, id);
            }
            
            cboEmployee.setModel(model);
            rs.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(WorkShiftForm.class.getName()).log(Level.SEVERE, null, ex);
            showErrorMessage("Lỗi tải danh sách nhân viên: " + ex.getMessage());
        }
    }
    
    private void loadShifts() {
        try {
            List<WorkShift> shifts = workShiftDAO.getAllShifts();
            updateTable(shifts);
            showStatusMessage("Đã tải " + shifts.size() + " ca làm việc", false);
        } catch (SQLException ex) {
            Logger.getLogger(WorkShiftForm.class.getName()).log(Level.SEVERE, null, ex);
            showErrorMessage("Lỗi tải danh sách ca làm: " + ex.getMessage());
        }
    }
    
    private void updateTable(List<WorkShift> shifts) {
        tableModel.setRowCount(0);
        
        for (WorkShift shift : shifts) {
            Object[] row = {
                shift.getShiftId(),
                shift.getEmployeeName(),
                shift.getShiftDate(),
                shift.getShiftType().getDisplayName(),
                shift.getStartTime(),
                shift.getEndTime() != null ? shift.getEndTime() : "Chưa kết thúc",
                shift.getPlannedHours(),
                shift.getActualHours() != null ? shift.getActualHours() : "N/A",
                shift.getStatus().getDisplayName()
            };
            tableModel.addRow(row);
        }
    }
    
    private void addShift() {
        try {
            WorkShift shift = getFormData();
            if (workShiftDAO.createShift(shift)) {
                showSuccessMessage("Thêm ca làm thành công!");
                clearForm();
                loadShifts();
            } else {
                showErrorMessage("Lỗi khi thêm ca làm!");
            }
        } catch (Exception ex) {
            showErrorMessage("Lỗi: " + ex.getMessage());
        }
    }
    
    private void updateShift() {
        try {
            int selectedRow = tblShifts.getSelectedRow();
            if (selectedRow < 0) {
                showErrorMessage("Vui lòng chọn ca làm cần sửa!");
                return;
            }
            
            // Convert view row to model row for sorting support
            int modelRow = tblShifts.convertRowIndexToModel(selectedRow);
            
            WorkShift shift = getFormData();
            shift.setShiftId((Integer) tableModel.getValueAt(modelRow, 0));
            
            if (workShiftDAO.updateShift(shift)) {
                showSuccessMessage("Cập nhật ca làm thành công!");
                clearForm();
                loadShifts();
            } else {
                showErrorMessage("Lỗi khi cập nhật ca làm!");
            }
        } catch (Exception ex) {
            showErrorMessage("Lỗi: " + ex.getMessage());
        }
    }
    
    private void deleteShift() {
        try {
            int selectedRow = tblShifts.getSelectedRow();
            if (selectedRow < 0) {
                showErrorMessage("Vui lòng chọn ca làm cần xóa!");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa ca làm này?", 
                "❓ Xác nhận", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Convert view row to model row for sorting support
                int modelRow = tblShifts.convertRowIndexToModel(selectedRow);
                int shiftId = (Integer) tableModel.getValueAt(modelRow, 0);
                
                if (workShiftDAO.deleteShift(shiftId)) {
                    showSuccessMessage("Xóa ca làm thành công!");
                    clearForm();
                    loadShifts();
                } else {
                    showErrorMessage("Lỗi khi xóa ca làm!");
                }
            }
        } catch (Exception ex) {
            showErrorMessage("Lỗi: " + ex.getMessage());
        }
    }
    
    private WorkShift getFormData() throws Exception {
        // Validate
        if (cboEmployee.getSelectedIndex() == 0) {
            throw new Exception("Vui lòng chọn nhân viên!");
        }
        
        if (dateChooser.getDate() == null) {
            throw new Exception("Vui lòng nhập ngày!");
        }
        
        if (cboShiftType.getSelectedIndex() == 0) {
            throw new Exception("Vui lòng chọn ca làm!");
        }
        
        if (timeSpinner.getValue() == null) {
            throw new Exception("Vui lòng nhập giờ bắt đầu!");
        }
        
        // Get data
        String selectedEmp = (String) cboEmployee.getSelectedItem();
        Integer employeeId = employeeMap.get(selectedEmp);
        
        WorkShift shift = new WorkShift();
        shift.setEmployeeId(employeeId);
        
        // Parse date
        java.sql.Date date = java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate()));
        shift.setShiftDate(date);
        
        // Parse shift type
        String shiftTypeStr = (String) cboShiftType.getSelectedItem();
        WorkShift.ShiftType shiftType = switch (shiftTypeStr) {
            case "🌅 Ca Sáng" -> WorkShift.ShiftType.SANG;
            case "☀️ Ca Chiều" -> WorkShift.ShiftType.CHIEU;
            case "🌙 Ca Tối" -> WorkShift.ShiftType.TOI;
            case "⭐ Ca Nguyên" -> WorkShift.ShiftType.FULL;
            default -> WorkShift.ShiftType.FULL;
        };
        shift.setShiftType(shiftType);
        
        // Parse start time
        String timeStr = new SimpleDateFormat("HH:mm").format(timeSpinner.getValue()) + ":00";
        java.sql.Time startTime = java.sql.Time.valueOf(timeStr);
        shift.setStartTime(startTime);
        
        // Get notes, ignore placeholder text
        String notesText = txtNotes.getText().trim();
        if (notesText.equals("Nhập ghi chú cho ca làm việc...")) {
            notesText = "";
        }
        shift.setNotes(notesText);
        
        return shift;
    }
    
    private void fillFormFromTable() {
        int selectedRow = tblShifts.getSelectedRow();
        if (selectedRow >= 0) {
            // Convert view row index to model row index for sorting support
            int modelRow = tblShifts.convertRowIndexToModel(selectedRow);
            
            // Find employee in combo
            String empName = (String) tableModel.getValueAt(modelRow, 1);
            for (int i = 0; i < cboEmployee.getItemCount(); i++) {
                String item = cboEmployee.getItemAt(i);
                if (item.contains(empName)) {
                    cboEmployee.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set date
            Object dateValue = tableModel.getValueAt(modelRow, 2);
            if (dateValue instanceof java.sql.Date) {
                dateChooser.setDate(new Date(((java.sql.Date) dateValue).getTime()));
            }
            
            // Set shift type
            String shiftType = (String) tableModel.getValueAt(modelRow, 3);
            for (int i = 0; i < cboShiftType.getItemCount(); i++) {
                String item = cboShiftType.getItemAt(i);
                if (item.contains("Ca Sáng") && shiftType.equals("Ca Sáng") ||
                    item.contains("Ca Chiều") && shiftType.equals("Ca Chiều") ||
                    item.contains("Ca Tối") && shiftType.equals("Ca Tối") ||
                    item.contains("Ca Nguyên") && shiftType.equals("Ca Nguyên")) {
                    cboShiftType.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set start time
            Object timeValue = tableModel.getValueAt(modelRow, 4);
            if (timeValue instanceof java.sql.Time) {
                Calendar cal = Calendar.getInstance();
                cal.setTime((java.sql.Time) timeValue);
                timeSpinner.setValue(cal.getTime());
            }
        }
    }
    
    private void clearForm() {
        cboEmployee.setSelectedIndex(0);
        dateChooser.setDate(new Date());
        cboShiftType.setSelectedIndex(0);
        
        // Reset time to 8:00 AM
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        timeSpinner.setValue(cal.getTime());
        
        txtNotes.setForeground(Color.GRAY);
        txtNotes.setText("Nhập ghi chú cho ca làm việc...");
        tblShifts.clearSelection();
    }
    
    public void cleanup() {
        if (workShiftDAO != null) {
            workShiftDAO.closeConnection();
        }
        // EmployeeDAO doesn't have a close method
    }
} 