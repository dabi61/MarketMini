package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Model cho quản lý ca làm việc
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
    
    private int shiftId;
    private int employeeId;
    private String employeeName; // Thêm để hiển thị
    private Date shiftDate;
    private ShiftType shiftType;
    private Time startTime;
    private Time endTime;
    private BigDecimal plannedHours;
    private BigDecimal actualHours;
    private int breakMinutes;
    private BigDecimal overtimeHours;
    private ShiftStatus status;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Enhanced fields for penalty system
    private Time checkInTime;
    private Time checkOutTime;
    private int lateMinutes;
    private int earlyLeaveMinutes;
    private boolean isScheduled;
    private BigDecimal penaltyAmount;
    private String penaltyReason;
    private BigDecimal salaryAdjustmentPercent;
    private String adjustmentReason;
    private boolean autoCheckoutPenalty;
    
    // Calculated fields
    private BigDecimal hourlyWage;
    private BigDecimal totalEarnings;
    private BigDecimal overtimeEarnings;
    private BigDecimal finalEarnings;

    // Constructors
    public WorkShift() {
        this.plannedHours = BigDecimal.valueOf(8.0);
        this.breakMinutes = 60;
        this.overtimeHours = BigDecimal.ZERO;
        this.status = ShiftStatus.SCHEDULED;
        this.shiftType = ShiftType.FULL;
        
        // Initialize penalty system fields
        this.lateMinutes = 0;
        this.earlyLeaveMinutes = 0;
        this.isScheduled = true;
        this.penaltyAmount = BigDecimal.ZERO;
        this.salaryAdjustmentPercent = BigDecimal.valueOf(100.0);
        this.autoCheckoutPenalty = false;
    }

    public WorkShift(int employeeId, Date shiftDate, ShiftType shiftType, Time startTime) {
        this();
        this.employeeId = employeeId;
        this.shiftDate = shiftDate;
        this.shiftType = shiftType;
        this.startTime = startTime;
        setPlannedHoursByShiftType();
    }

    // Getters and Setters
    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
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

    public Date getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(Date shiftDate) {
        this.shiftDate = shiftDate;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
        setPlannedHoursByShiftType();
    }

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

    public int getBreakMinutes() {
        return breakMinutes;
    }

    public void setBreakMinutes(int breakMinutes) {
        this.breakMinutes = breakMinutes;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

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

    // Enhanced penalty system getters and setters
    public Time getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Time checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Time getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(Time checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public int getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(int lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public int getEarlyLeaveMinutes() {
        return earlyLeaveMinutes;
    }

    public void setEarlyLeaveMinutes(int earlyLeaveMinutes) {
        this.earlyLeaveMinutes = earlyLeaveMinutes;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public String getPenaltyReason() {
        return penaltyReason;
    }

    public void setPenaltyReason(String penaltyReason) {
        this.penaltyReason = penaltyReason;
    }

    public BigDecimal getSalaryAdjustmentPercent() {
        return salaryAdjustmentPercent;
    }

    public void setSalaryAdjustmentPercent(BigDecimal salaryAdjustmentPercent) {
        this.salaryAdjustmentPercent = salaryAdjustmentPercent;
    }

    public String getAdjustmentReason() {
        return adjustmentReason;
    }

    public void setAdjustmentReason(String adjustmentReason) {
        this.adjustmentReason = adjustmentReason;
    }

    public boolean isAutoCheckoutPenalty() {
        return autoCheckoutPenalty;
    }

    public void setAutoCheckoutPenalty(boolean autoCheckoutPenalty) {
        this.autoCheckoutPenalty = autoCheckoutPenalty;
    }

    public BigDecimal getFinalEarnings() {
        return finalEarnings;
    }

    public void setFinalEarnings(BigDecimal finalEarnings) {
        this.finalEarnings = finalEarnings;
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
     * Tính toán lương từ ca làm với penalty system - dựa trên thời gian thực tế
     */
    public void calculateEarnings() {
        if (hourlyWage != null) {
            // Lương cơ bản dựa trên thời gian thực tế làm việc
            BigDecimal actualWorkHours = actualHours != null ? actualHours : BigDecimal.ZERO;
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
            System.out.println("- Actual hours: " + actualWorkHours);
            System.out.println("- Planned hours: " + plannedWorkHours);  
            System.out.println("- Regular hours: " + regularHours);
            System.out.println("- Overtime hours: " + overtimeWorkHours);
            System.out.println("- Hourly wage: " + hourlyWage);
            System.out.println("- Total before adjustments: " + totalEarnings);
            
            // Áp dụng điều chỉnh lương (%)
            if (salaryAdjustmentPercent != null) {
                totalEarnings = totalEarnings.multiply(salaryAdjustmentPercent).divide(BigDecimal.valueOf(100));
                System.out.println("- After salary adjustment (" + salaryAdjustmentPercent + "%): " + totalEarnings);
            }
            
            // Trừ penalty
            if (penaltyAmount != null) {
                finalEarnings = totalEarnings.subtract(penaltyAmount);
                System.out.println("- After penalty (-" + penaltyAmount + "): " + finalEarnings);
            } else {
                finalEarnings = totalEarnings;
            }
            
            // Đảm bảo lương cuối không âm
            if (finalEarnings.compareTo(BigDecimal.ZERO) < 0) {
                finalEarnings = BigDecimal.ZERO;
            }
            
            System.out.println("- Final earnings: " + finalEarnings);
        }
    }
    
    /**
     * Kiểm tra nhân viên có thể bắt đầu ca không
     */
    public boolean canCheckIn() {
        return status == ShiftStatus.SCHEDULED && checkInTime == null;
    }
    
    /**
     * Kiểm tra nhân viên có thể chốt ca không
     */
    public boolean canCheckOut() {
        return status == ShiftStatus.IN_PROGRESS && checkInTime != null && checkOutTime == null;
    }
    
    /**
     * Tính số phút đến muộn
     */
    public void calculateLateMinutes() {
        if (checkInTime != null && startTime != null) {
            long diffMs = checkInTime.getTime() - startTime.getTime();
            if (diffMs > 0) {
                lateMinutes = (int) (diffMs / (1000 * 60)); // Convert to minutes
            } else {
                lateMinutes = 0;
            }
        }
    }
    
    /**
     * Tính số phút về sớm (nếu có)
     */
    public void calculateEarlyLeaveMinutes() {
        if (checkOutTime != null && endTime != null) {
            long diffMs = endTime.getTime() - checkOutTime.getTime();
            if (diffMs > 0) {
                earlyLeaveMinutes = (int) (diffMs / (1000 * 60));
            } else {
                earlyLeaveMinutes = 0;
            }
        }
    }
    
    /**
     * Áp dụng penalty cho đến muộn
     */
    public void applyLatePenalty() {
        if (lateMinutes > 0) {
            // Giảm 25% lương cho mỗi giờ muộn (làm tròn lên)
            int lateHours = (int) Math.ceil(lateMinutes / 60.0);
            BigDecimal penaltyPercent = BigDecimal.valueOf(25 * lateHours);
            
            // Áp dụng penalty percent (giảm từ 100%)
            BigDecimal newPercent = salaryAdjustmentPercent.subtract(penaltyPercent);
            if (newPercent.compareTo(BigDecimal.ZERO) < 0) {
                newPercent = BigDecimal.ZERO;
            }
            
            setSalaryAdjustmentPercent(newPercent);
            setAdjustmentReason("Đến muộn " + lateMinutes + " phút - giảm " + penaltyPercent + "% lương");
        }
    }
    
    /**
     * Lấy trạng thái chấm công
     */
    public String getAttendanceStatus() {
        if (lateMinutes > 0) {
            return "Muộn " + lateMinutes + " phút";
        } else if (earlyLeaveMinutes > 0) {
            return "Về sớm " + earlyLeaveMinutes + " phút";
        } else {
            return "Đúng giờ";
        }
    }

    /**
     * Kiểm tra ca làm có thể chốt ca không
     */
    public boolean canCloseShift() {
        return status == ShiftStatus.IN_PROGRESS;
    }

    /**
     * Kiểm tra ca làm có đang diễn ra không
     */
    public boolean isActive() {
        return status == ShiftStatus.IN_PROGRESS;
    }

    /**
     * Convert to table row data
     */
    public Object[] toTableRow() {
        return new Object[] {
            shiftId,
            employeeName != null ? employeeName : "N/A",
            shiftDate,
            shiftType != null ? shiftType.getDisplayName() : "",
            startTime,
            endTime,
            plannedHours,
            actualHours,
            status != null ? status.getDisplayName() : ""
        };
    }
    
    /**
     * Convert to table row data for staff view
     */
    public Object[] toStaffTableRow() {
        return new Object[] {
            shiftDate,
            shiftType != null ? shiftType.getDisplayName() : "",
            startTime,
            endTime != null ? endTime : "Chưa kết thúc",
            actualHours != null ? actualHours : plannedHours,
            status != null ? status.getDisplayName() : ""
        };
    }

    @Override
    public String toString() {
        return String.format("WorkShift{shiftId=%d, employeeId=%d, shiftDate=%s, type=%s, status=%s}", 
                shiftId, employeeId, shiftDate, shiftType, status);
    }
} 