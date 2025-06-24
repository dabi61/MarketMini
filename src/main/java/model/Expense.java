package model;

import java.util.Date;

public class Expense {
    private int expenseId;
    private Date monthYear;
    private int electricityCost;
    private int rentCost;
    private int waterCost;
    private int repairCost;

    // Getters and Setters
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public Date getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(Date monthYear) {
        this.monthYear = monthYear;
    }

    public int getElectricityCost() {
        return electricityCost;
    }

    public void setElectricityCost(int electricityCost) {
        this.electricityCost = electricityCost;
    }

    public int getRentCost() {
        return rentCost;
    }

    public void setRentCost(int rentCost) {
        this.rentCost = rentCost;
    }

    public int getWaterCost() {
        return waterCost;
    }

    public void setWaterCost(int waterCost) {
        this.waterCost = waterCost;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(int repairCost) {
        this.repairCost = repairCost;
    }
}
