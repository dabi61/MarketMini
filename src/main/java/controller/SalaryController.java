/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.SalaryDAO;
import dao.EmployeeDAO;
import utils.ExcelExporter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.DBConnection;
import model.Salary;
import model.Employees;
import view.Salary.SalaryForm;

/**
 *
 * @author Admin
 */
public class SalaryController implements ActionListener {

    private SalaryDAO salaryDAO;
    private EmployeeDAO employeeDAO;
    private SalaryForm salaryForm;
    private Connection connection;
    
    public SalaryController() {
        try {
            this.connection = DBConnection.getConnection();
            this.salaryDAO = new SalaryDAO();
            this.employeeDAO = new EmployeeDAO();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối database: " + e.getMessage());
        }
    }

    public SalaryController(SalaryDAO salaryDAO, SalaryForm salaryView) {
        this.salaryDAO = salaryDAO;
        this.salaryForm = salaryView;
        try {
            this.connection = DBConnection.getConnection();
            this.employeeDAO = new EmployeeDAO();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi kết nối database: " + e.getMessage());
        }
    }
    
    /**
     * Thêm lương mới vào database
     * @param salary đối tượng Salary cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addSalary(Salary salary) {
        return salaryDAO.addSalary(salary);
    }
    
    /**
     * Cập nhật lương
     * @param salary đối tượng Salary cần cập nhật
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateSalary(Salary salary) {
        return salaryDAO.updateSalary(salary);
    }
    
    /**
     * Xóa lương theo ID
     * @param salaryId ID lương cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteSalary(int salaryId) {
        return salaryDAO.deleteSalary(salaryId);
    }
    
    /**
     * Lấy lương theo ID
     * @param salaryId ID lương cần lấy
     * @return đối tượng Salary hoặc null
     */
    public Salary getSalaryById(int salaryId) {
        return salaryDAO.getSalaryById(salaryId);
    }
    
    /**
     * Lấy tất cả lương
     * @return danh sách tất cả lương
     */
    public List<Salary> getAllSalaries() {
        return salaryDAO.getAllSalaries();
    }
    
    /**
     * Tạo đối tượng Salary từ dữ liệu đầu vào
     * @param employee_id ID nhân viên
     * @param total_hours Tổng giờ làm việc
     * @param hourly_wage Lương theo giờ
     * @param bonus Thưởng
     * @param payment_date Ngày thanh toán
     * @return đối tượng Salary
     */
    public Salary createSalary(int employee_id, java.math.BigDecimal total_hours, 
                              int hourly_wage, int bonus, Date payment_date) {
        Salary salary = new Salary();
        salary.setEmployee_id(employee_id);
        salary.setTotal_hours(total_hours);
        salary.setHourly_wage(hourly_wage);
        salary.setBonus(bonus);
        salary.setPayment_date(payment_date);
        salary.setCreated_date(new Date()); // Ngày tạo là ngày hiện tại
        
        return salary;
    }
    
    /**
     * Lấy danh sách nhân viên để hiển thị trong ComboBox
     * @param cb_name ComboBox để hiển thị danh sách nhân viên
     */
    public void loadEmployeesToComboBox(JComboBox<String> cb_name) {
        try {
            Vector<String> employeeList = salaryDAO.getEmployeeList();
            cb_name.setModel(new DefaultComboBoxModel<>(employeeList));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải danh sách nhân viên: " + e.getMessage());
        }
    }
    
    /**
     * Lấy ID nhân viên từ ComboBox
     * @param cb_name ComboBox chứa thông tin nhân viên
     * @return ID nhân viên hoặc -1 nếu không chọn
     */
    public int getEmployeeIdFromComboBox(JComboBox<String> cb_name) {
        String selected = (String) cb_name.getSelectedItem();
        if (selected != null && !selected.equals("-- Chọn nhân viên --")) {
            String[] parts = selected.split(" - ");
            if (parts.length > 0) {
                try {
                    return Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }
    
    /**
     * Xử lý thêm lương từ form
     * @param cb_name ComboBox chọn nhân viên
     * @param et_hourly_wage TextField lương cơ bản
     * @param dt_payment DateChooser ngày thanh toán
     */
    public void handleAddSalary(JComboBox<String> cb_name, JTextField et_hourly_wage, 
                               com.toedter.calendar.JDateChooser dt_payment) {
        try {
            // Lấy dữ liệu từ form
            int employeeId = getEmployeeIdFromComboBox(cb_name);
            if (employeeId == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên!");
                return;
            }
            
            String hourlyWageText = et_hourly_wage.getText().trim();
            if (hourlyWageText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập lương cơ bản!");
                return;
            }
            
            int hourlyWage;
            try {
                hourlyWage = Integer.parseInt(hourlyWageText);
                if (hourlyWage <= 0) {
                    JOptionPane.showMessageDialog(null, "Lương cơ bản phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Lương cơ bản phải là số!");
                return;
            }
            
            if (dt_payment.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn ngày thanh toán!");
                return;
            }
            
            // Tạo đối tượng Salary
            Salary salary = new Salary();
            salary.setEmployee_id(employeeId);
            salary.setTotal_hours(new java.math.BigDecimal("0")); // Sẽ được tính tự động bởi trigger
            salary.setHourly_wage(hourlyWage);
            salary.setBonus(0); // Mặc định 0 thưởng
            salary.setPayment_date(dt_payment.getDate());
            salary.setCreated_date(new Date());
            salary.setPenalty_deduction(0); // Mặc định 0 penalty
            
            // Thêm vào database
            boolean success = addSalary(salary);
            if (success) {
                JOptionPane.showMessageDialog(null, "Thêm lương thành công!");
                clearForm(cb_name, et_hourly_wage, dt_payment);
                // Có thể thêm reload table ở đây
            } else {
                JOptionPane.showMessageDialog(null, "Thêm lương thất bại!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý cập nhật lương
     * @param salaryId ID lương cần cập nhật
     * @param cb_name ComboBox chọn nhân viên
     * @param et_hourly_wage TextField lương
     * @param dt_payment DateChooser ngày thanh toán
     */
    public void handleUpdateSalary(int salaryId, JComboBox<String> cb_name, 
                                  JTextField et_hourly_wage, 
                                  com.toedter.calendar.JDateChooser dt_payment) {
        try {
            // Lấy dữ liệu từ form
            int employeeId = getEmployeeIdFromComboBox(cb_name);
            if (employeeId == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên!");
                return;
            }
            
            String hourlyWageText = et_hourly_wage.getText().trim();
            if (hourlyWageText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập lương cơ bản!");
                return;
            }
            
            int hourlyWage;
            try {
                hourlyWage = Integer.parseInt(hourlyWageText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Lương cơ bản phải là số!");
                return;
            }
            
            if (dt_payment.getDate() == null) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn ngày thanh toán!");
                return;
            }
            
            // Lấy lương hiện tại và cập nhật
            Salary salary = getSalaryById(salaryId);
            if (salary != null) {
                salary.setEmployee_id(employeeId);
                salary.setHourly_wage(hourlyWage);
                salary.setPayment_date(dt_payment.getDate());
                // Giữ nguyên penalty_deduction từ database
                
                boolean success = updateSalary(salary);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Cập nhật lương thành công!");
                    clearForm(cb_name, et_hourly_wage, dt_payment);
                } else {
                    JOptionPane.showMessageDialog(null, "Cập nhật lương thất bại!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy lương cần cập nhật!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Xóa lương
     * @param table Table chứa danh sách lương
     */
    public void handleDeleteSalary(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn lương cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Bạn có chắc chắn muốn xóa lương này?", "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int salaryId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
                boolean success = deleteSalary(salaryId);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Xóa lương thành công!");
                    // Có thể thêm reload table ở đây
                } else {
                    JOptionPane.showMessageDialog(null, "Xóa lương thất bại!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
            }
        }
    }
    
    /**
     * Làm sạch form
     * @param cb_name ComboBox nhân viên
     * @param et_hourly_wage TextField lương
     * @param dt_payment DateChooser ngày thanh toán
     */
    public void clearForm(JComboBox<String> cb_name, JTextField et_hourly_wage, 
                         com.toedter.calendar.JDateChooser dt_payment) {
        cb_name.setSelectedIndex(0);
        et_hourly_wage.setText("");
        dt_payment.setDate(null);
    }
    
    /**
     * Load danh sách lương vào table
     * @param table Table để hiển thị
     */
    public void loadSalaryTable(JTable table) {
        try {
            System.out.println("Đang load salary table...");
            salaryDAO.loadSalaryTable(table);
            System.out.println("Load salary table thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải danh sách lương: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Lỗi khi tải danh sách lương: " + e.getMessage());
        }
    }
    
    /**
     * Load dữ liệu lương vào form để chỉnh sửa
     * @param salaryId ID lương cần chỉnh sửa
     * @param cb_name ComboBox nhân viên
     * @param et_hourly_wage TextField lương
     * @param dt_payment DateChooser ngày thanh toán
     */
    public void loadSalaryToForm(int salaryId, JComboBox<String> cb_name, 
                                 JTextField et_hourly_wage, 
                                 com.toedter.calendar.JDateChooser dt_payment) {
        try {
            Salary salary = getSalaryById(salaryId);
            if (salary != null) {
                // Load danh sách nhân viên trước
                loadEmployeesToComboBox(cb_name);
                
                // Tìm và chọn nhân viên trong ComboBox
                for (int i = 0; i < cb_name.getItemCount(); i++) {
                    String item = cb_name.getItemAt(i);
                    if (item.startsWith(salary.getEmployee_id() + " - ")) {
                        cb_name.setSelectedIndex(i);
                        break;
                    }
                }
                
                et_hourly_wage.setText(String.valueOf(salary.getHourly_wage()));
                dt_payment.setDate(salary.getPayment_date());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu lương: " + e.getMessage());
        }
    }

    /**
     * Mở form chỉnh sửa lương
     * @param salaryId ID lương cần chỉnh sửa
     * @param parentFrame Frame cha
     */
    public boolean openEditSalaryForm(int salaryId, java.awt.Frame parentFrame) {
        try {
            Salary salary = getSalaryById(salaryId);
            if (salary != null) {
                // Lấy tên nhân viên
                String employeeName = getEmployeeNameById(salary.getEmployee_id());
                
                // Mở form chỉnh sửa
                view.Salary.SuaLuongForm editForm = new view.Salary.SuaLuongForm(parentFrame, true);
                editForm.setValue(salary, employeeName);
                editForm.setLocationRelativeTo(parentFrame);
                editForm.setVisible(true);
                
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy thông tin lương!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy tên nhân viên theo ID
     * @param employeeId ID nhân viên
     * @return tên nhân viên
     */
    public String getEmployeeNameById(int employeeId) {
        try {
            String sql = "SELECT full_name FROM employees WHERE employee_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Không xác định";
    }

    /**
     * Xuất Excel từ bảng hiện tại
     * @param table Bảng chứa dữ liệu
     * @param parentComponent Component cha
     */
    public void exportToExcel(JTable table, java.awt.Component parentComponent) {
        try {
            boolean success = ExcelExporter.exportSalaryTable(table, parentComponent);
            if (success) {
                System.out.println("Xuất Excel thành công!");
            } else {
                System.out.println("Xuất Excel thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất Excel: " + e.getMessage());
        }
    }
    
    /**
     * Xuất Excel từ danh sách lương
     * @param parentComponent Component cha
     */
    public void exportSalaryListToExcel(java.awt.Component parentComponent) {
        try {
            List<Salary> salaries = getAllSalaries();
            if (salaries.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Không có dữ liệu để xuất!");
                return;
            }
            
            boolean success = ExcelExporter.exportSalaryList(salaries, parentComponent);
            if (success) {
                System.out.println("Xuất Excel thành công!");
            } else {
                System.out.println("Xuất Excel thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất Excel: " + e.getMessage());
        }
    }

    /**
     * Xuất Excel theo nhân viên được chọn từ bảng
     * @param table Bảng chứa dữ liệu
     * @param parentComponent Component cha
     */
    public void exportSelectedEmployeeToExcel(JTable table, java.awt.Component parentComponent) {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng trong bảng!");
                return;
            }
            
            // Lấy thông tin nhân viên từ dòng được chọn
            Object employeeNameObj = table.getValueAt(selectedRow, 1); // Cột tên nhân viên
            if (employeeNameObj == null) {
                JOptionPane.showMessageDialog(null, "Dữ liệu không hợp lệ!");
                return;
            }
            
            String employeeName = employeeNameObj.toString();
            
            // Lấy ID nhân viên từ tên (format: "ID - Tên")
            int employeeId = -1;
            try {
                String[] parts = employeeName.split(" - ");
                if (parts.length > 0) {
                    employeeId = Integer.parseInt(parts[0]);
                }
            } catch (NumberFormatException e) {
                // Nếu không parse được ID, tìm theo tên
                employeeId = getEmployeeIdByName(employeeName);
            }
            
            if (employeeId == -1) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy thông tin nhân viên!");
                return;
            }
            
            // Lấy dữ liệu lương của nhân viên
            List<Salary> salaries = salaryDAO.getSalaryDataByEmployee(employeeId);
            if (salaries.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Không có dữ liệu lương cho nhân viên này!");
                return;
            }
            
            // Xuất Excel
            boolean success = ExcelExporter.exportSalaryByEmployee(salaries, employeeName, parentComponent);
            if (success) {
                System.out.println("Xuất Excel theo nhân viên thành công!");
            } else {
                System.out.println("Xuất Excel theo nhân viên thất bại!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất Excel: " + e.getMessage());
        }
    }
    
    /**
     * Lấy ID nhân viên theo tên
     * @param employeeName Tên nhân viên
     * @return ID nhân viên hoặc -1 nếu không tìm thấy
     */
    private int getEmployeeIdByName(String employeeName) {
        try {
            String sql = "SELECT employee_id FROM employees WHERE full_name = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, employeeName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("employee_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Xử lý các sự kiện từ form
        if (salaryForm != null) {
            if (e.getSource() == salaryForm.getBt_add()) {
                if ("UPDATE".equals(salaryForm.getBt_add().getActionCommand())) {
                    // Xử lý cập nhật
                    int selectedRow = salaryForm.getTb_list_salary().getSelectedRow();
                    if (selectedRow != -1) {
                        int salaryId = Integer.parseInt(salaryForm.getTb_list_salary().getValueAt(selectedRow, 0).toString());
                        handleUpdateSalary(salaryId, salaryForm.getCb_name(), 
                                        salaryForm.getEt_hourly_wage(), salaryForm.getDt_payment());
                        // Reset button
                        salaryForm.getBt_add().setText("Thêm");
                        salaryForm.getBt_add().setActionCommand("");
                        // Reload table
                        loadSalaryTable(salaryForm.getTb_list_salary());
                    }
                } else {
                    // Xử lý thêm mới
                    handleAddSalary(salaryForm.getCb_name(), salaryForm.getEt_hourly_wage(), 
                                  salaryForm.getDt_payment());
                    // Reload table
                    loadSalaryTable(salaryForm.getTb_list_salary());
                }
            } else if (e.getSource() == salaryForm.getBt_delete()) {
                handleDeleteSalary(salaryForm.getTb_list_salary());
                // Reload table
                loadSalaryTable(salaryForm.getTb_list_salary());
            } else if (e.getSource() == salaryForm.getBt_export_excel()) {
                // Xử lý xuất Excel
                exportToExcel(salaryForm.getTb_list_salary(), salaryForm);
            } else if (e.getSource() == salaryForm.getBt_export_selected()) {
                // Xử lý xuất Excel theo nhân viên được chọn
                exportSelectedEmployeeToExcel(salaryForm.getTb_list_salary(), salaryForm);
            }
        }
    }
}
