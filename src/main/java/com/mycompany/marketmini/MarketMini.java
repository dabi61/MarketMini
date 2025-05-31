/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.marketmini;

import com.raven.main.Main;
import java.net.URL;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author macbook
 */
public class MarketMini {

    public static void main(String[] args) {
        Main view = new Main();
//        new CRUDController(view);
//        try {
//            UIManager.setLookAndFeel(new FlatLightLaf());
//        } catch (UnsupportedLookAndFeelException ex) {
//            System.err.println("Không thể thiết lập FlatLaf: " + ex);
//        }
        // Gọi giao diện chính sau khi thiết lập LookAndFeel
        java.awt.EventQueue.invokeLater(() -> {
            view.setVisible(true); // Thay bằng JFrame chính của bạn
        });
        
    }
}
