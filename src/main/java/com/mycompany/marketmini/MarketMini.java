/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.marketmini;

import com.formdev.flatlaf.FlatLightLaf;
import com.raven.main.Main;
import controller.LoginController;
import java.net.URL;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import view.LoginForm;

/**
 *
 * @author macbook
 */
public class MarketMini {
    private LoginController loginController;

    public static void main(String[] args) {
        LoginForm view = new LoginForm();
//        new CRUDController(view);
        System.setProperty("flatlaf.uiScale", "1.0");
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            System.err.println("Không thể thiết lập FlatLaf: " + ex);
        }
//         Gọi giao diện chính sau khi thiết lập LookAndFeel
        java.awt.EventQueue.invokeLater(() -> {
            view.setVisible(true); // Thay bằng JFrame chính của bạn
        });
        
    }
}
