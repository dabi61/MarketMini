/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import dao.OrderDAO;
import model.Order;

import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author THIS PC
 */
public class OrderController {
    private OrderDAO orderDAO;
    
    public OrderController() {
        try {
            orderDAO = new OrderDAO();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    public boolean addOrder(Order order) {
        return orderDAO.insert(order);
    }

    public boolean updateOrder(Order order) {
        return orderDAO.update(order);
    }

    public boolean deleteOrder(int orderId) {
        return orderDAO.delete(orderId);
    }

    public Order getOrderById(int orderId) {
        return orderDAO.getOrderById(orderId);
    }
}
