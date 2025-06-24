/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;

/**
 *
 * @author Admin
 */
public class ProductDisplay {
    private int display_id;
    private int product_id;
    private String row;
    private String floor;
    private LocalDate start_date;
    private LocalDate end_date;

    public ProductDisplay() {
    }

    public ProductDisplay(int display_id, int product_id, String row, String floor, LocalDate start_date, LocalDate end_date) {
        this.display_id = display_id;
        this.product_id = product_id;
        this.row = row;
        this.floor = floor;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public int getDisplay_id() {
        return display_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getRow() {
        return row;
    }

    public String getFloor() {
        return floor;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setDisplay_id(int display_id) {
        this.display_id = display_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }
    
}
