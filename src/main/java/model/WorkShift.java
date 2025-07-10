package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Model cho quản lý ca làm việc dựa trên bảng workingsession
 * @author macbook
 */
public class WorkShift {
    
    public enum ShiftType {
        SANG("Ca Sáng"), 
        CHIEU("Ca Chiều"), 
        TOI("Ca Tối"), 
        FULL("Ca Nguyên");
        
        private final String displayName;
        
        ShiftType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum ShiftStatus {
        SCHEDULED("Đã lên lịch"),
        IN_PROGRESS("Đang làm"),
        COMPLETED("Hoàn thành"),
        ABSENT("Vắng mặt");
        
        private final String displayName;
        
        ShiftStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Fields từ bảng workingsession
    private int workingSessionId;
    private int employeeId;
    private String employeeName; // Thêm để hiển thị
    private Timestamp loginTime;
    private Timestamp logoutTime;
    private BigDecimal workingHours;
    private Date date;
    private String workStatus;
    
    // Calculated fields
    private BigDecimal hourlyWage;
    private BigDecimal totalEarnings;
    private BigDecimal overtimeEarnings;
    private BigDecimal finalEarnings;
    
    // Helper fields
    private Time startTime;
    private Time endTime;
    private BigDecimal plannedHours;
    private BigDecimal actualHours;
    private BigDecimal overtimeHours;
    private ShiftType shiftType;
    private ShiftStatus status;
    private String notes;

    // Constructors
    public WorkShift() {
        this.plannedHours = BigDecimal.valueOf(8.0);
        this.overtimeHours = BigDecimal.ZERO;
        this.status = ShiftStatus.SCHEDULED;
        this.shiftType = ShiftType.FULL;
        this.workStatus = "SCHEDULED";
    }

    public WorkShift(int employeeId, Date date, ShiftType shiftType, Time startTime) {
        this();
        this.employeeId = employeeId;
        this.date = date;
        this.shiftType = shiftType;
        this.startTime = startTime;
        setPlannedHoursByShiftType();
    }

    // Getters and Setters cho bảng workingsession
    public int getWorkingSessionId() {
        return workingSessionId;
    }

    public void setWorkingSessionId(int workingSessionId) {
        this.workingSessionId = workingSessionId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public Timestamp getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Timestamp logoutTime) {
        this.logoutTime = logoutTime;
    }

    public BigDecimal getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(BigDecimal workingHours) {
        this.workingHours = workingHours;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    // Getters and Setters cho calculated fields
    public BigDecimal getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(BigDecimal hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public BigDecimal getOvertimeEarnings() {
        return overtimeEarnings;
    }

    public void setOvertimeEarnings(BigDecimal overtimeEarnings) {
        this.overtimeEarnings = overtimeEarnings;
    }

    public BigDecimal getFinalEarnings() {
        return finalEarnings;
    }

    public void setFinalEarnings(BigDecimal finalEarnings) {
        this.finalEarnings = finalEarnings;
    }

    // Helper getters and setters
    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getPlannedHours() {
        return plannedHours;
    }

    public void setPlannedHours(BigDecimal plannedHours) {
        this.plannedHours = plannedHours;
    }

    public BigDecimal getActualHours() {
        return actualHours;
    }

    public void setActualHours(BigDecimal actualHours) {
        this.actualHours = actualHours;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
        setPlannedHoursByShiftType();
    }

    public ShiftStatus getStatus() {
        return status;
    }

    public void setStatus(ShiftStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Helper methods
    private void setPlannedHoursByShiftType() {
        if (shiftType != null) {
            switch (shiftType) {
                case SANG:
                    this.plannedHours = BigDecimal.valueOf(4.0);
                    break;
                case CHIEU:
                    this.plannedHours = BigDecimal.valueOf(5.0);
                    break;
                case TOI:
                    this.plannedHours = BigDecimal.valueOf(4.0);
                    break;
                case FULL:
                default:
                    this.plannedHours = BigDecimal.valueOf(8.0);
                    break;
            }
        }
    }

    /**
     * Tính toán lương từ ca làm dựa trên working_hours
     */
    public void calculateEarnings() {
        if (hourlyWage != null && workingHours != null) {
            // Lương cơ bản dựa trên thời gian thực tế làm việc
            BigDecimal actualWorkHours = workingHours;
            BigDecimal plannedWorkHours = plannedHours != null ? plannedHours : BigDecimal.valueOf(8.0);
            
            // Chỉ tính lương cho thời gian thực tế làm việc
            BigDecimal regularHours = actualWorkHours;
            BigDecimal overtimeWorkHours = BigDecimal.ZERO;
            
            // Phân biệt giờ thường và overtime
            if (actualWorkHours.compareTo(plannedWorkHours) > 0) {
                regularHours = plannedWorkHours; // Giờ thường = planned hours
                overtimeWorkHours = actualWorkHours.subtract(plannedWorkHours); // Overtime = thực tế - kế hoạch
            }
            
            // Tính lương giờ thường
            totalEarnings = regularHours.multiply(hourlyWage);
            
            // Tính lương overtime (x1.5)
            if (overtimeWorkHours.compareTo(BigDecimal.ZERO) > 0) {
                overtimeEarnings = overtimeWorkHours.multiply(hourlyWage).multiply(BigDecimal.valueOf(1.5));
                totalEarnings = totalEarnings.add(overtimeEarnings);
            } else {
                overtimeEarnings = BigDecimal.ZERO;
            }
            
            System.out.println("Earnings calculation:");
            System.out.println("- Working hours: " + actualWorkHours);
            System.out.println("- Planned hours: " + plannedWorkHours);  
            System.out.println("- Regular hours: " + regularHours);
            System.out.println("- Overtime hours: " + overtimeWorkHours);
            System.out.println("- Hourly wage: " + hourlyWage);
            System.out.println("- Total earnings: " + totalEarnings);
            
            finalEarnings = totalEarnings;
        }
    }
    
    /**
     * Kiểm tra nhân viên có thể bắt đầu ca không
     */
    public boolean canCheckIn() {
        return "SCHEDULED".equals(workStatus) && loginTime == null;
    }
    
    /**
     * Kiểm tra nhân viên có thể chốt ca không
     */
    public boolean canCheckOut() {
        return "IN_PROGRESS".equals(workStatus) && loginTime != null && logoutTime == null;
    }
    
    /**
     * Kiểm tra ca làm có thể chốt ca không
     */
    public boolean canCloseShift() {
        return "IN_PROGRESS".equals(workStatus);
    }

    /**
     * Kiểm tra ca làm có đang diễn ra không
     */
    public boolean isActive() {
        return "IN_PROGRESS".equals(workStatus);
    }

    /**
     * Convert to table row data
     */
    public Object[] toTableRow() {
        return new Object[] {
            workingSessionId,
            employeeName != null ? employeeName : "N/A",
            date,
            shiftType != null ? shiftType.getDisplayName() : "",
            startTime,
            endTime,
            plannedHours,
            workingHours != null ? workingHours : "N/A",
            workStatus != null ? getStatusDisplayName() : ""
        };
    }
    
    /**
     * Convert to table row data for staff view
     */
    public Object[] toStaffTableRow() {
        return new Object[] {
            date,
            shiftType != null ? shiftType.getDisplayName() : "",
            startTime,
            endTime != null ? endTime : "Chưa kết thúc",
            workingHours != null ? workingHours : plannedHours,
            workStatus != null ? getStatusDisplayName() : ""
        };
    }
    
    /**
     * Lấy tên hiển thị của trạng thái
     */
    private String getStatusDisplayName() {
        switch (workStatus) {
            case "SCHEDULED":
                return "Đã lên lịch";
            case "IN_PROGRESS":
                return "Đang làm";
            case "COMPLETED":
                return "Hoàn thành";
            case "ABSENT":
                return "Vắng mặt";
            default:
                return workStatus;
        }
    }

    @Override
    public String toString() {
        return String.format("WorkShift{workingSessionId=%d, employeeId=%d, date=%s, type=%s, status=%s}", 
                workingSessionId, employeeId, date, shiftType, workStatus);
    }
} 