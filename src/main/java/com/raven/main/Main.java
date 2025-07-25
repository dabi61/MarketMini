package com.raven.main;

import com.raven.component.MenuLayout;
import com.raven.event.EventMenuSelected;
import view.ThongKeView;
import view.Form_2;
import view.MainForm;
import com.raven.swing.WindowSnapshots;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import model.Session;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import view.Category.CategorysForm;
import view.DisplayForm;
import view.Employee.EmployeeForm;
import view.Expense;
import view.LoginForm;
import view.Promotion;
import view.Salary.SalaryForm;
import view.SalesForm;
import view.StoreForm;
import view.Supplier.SupplierForm;
import view.WorkShiftForm;

public class Main extends javax.swing.JFrame {

    private final MigLayout layout;
    private final MainForm main;
    private final MenuLayout menu;
    private final Animator animator;
    private final WindowSnapshots windowSnapshots;

    public Main() {
        initComponents();
        displayEmployeeName();
        layout = new MigLayout("fill", "0[fill]0", "0[fill]0");
        main = new MainForm();
        menu = new MenuLayout();
        windowSnapshots = new WindowSnapshots(Main.this);
        menu.getMenu().initMoving(Main.this);
        main.initMoving(Main.this);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(main);
        JPanel glassPanel = new JPanel(layout);
        glassPanel.setOpaque(false);
        glassPanel.add(menu, "pos -215 0 100% 100%");
        setGlassPane(glassPanel);
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                float x = (fraction * 215);
                float alpha;
                if (menu.isShow()) {
                    x = -x;
                    alpha = 0.5f - (fraction / 2);
                } else {
                    x -= 215;
                    alpha = fraction / 2;
                }
                layout.setComponentConstraints(menu, "pos " + (int) x + " 0 100% 100%");
                if (alpha < 0) {
                    alpha = 0;
                }
                menu.setAlpha(alpha);
                menu.revalidate();
            }

            @Override
            public void begin() {
                getGlassPane().setVisible(true);
                windowSnapshots.createSnapshot();
                getContentPane().setVisible(false);
            }

            @Override
            public void end() {
                menu.setShow(!menu.isShow());
                if (!menu.isShow()) {
                    menu.setVisible(false);
                }
                windowSnapshots.removeSnapshot();
                getContentPane().setVisible(true);
            }

        };
        animator = new Animator(350, target);
        animator.setDeceleration(0.5f);
        animator.setAcceleration(0.5f);
        animator.setResolution(1);
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    if (!animator.isRunning()) {
                        if (menu.isShow()) {
                            animator.start();
                        }
                    }
                }
            }
        });
        main.addEventMenu(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) {
                    if (!menu.isShow()) {
                        menu.setVisible(true);
                        animator.start();
                    }
                }
            }
        });
        menu.getMenu().addEventMenuSelected(new EventMenuSelected() {
            @Override
            public void selected(int index) {
                // Lấy role
                int role = Session.getInstance().getRole();

                if (index == 0) { // Thống kê
                    main.show(new ThongKeView());
                } else if (index == 1) { // bán hàng
                    main.show(new SalesForm());
                } else if (index == 2) { // Kho, nhập hàng
                    main.show(new StoreForm());
                } else if (index == 3) { // Nhà cung cấp
                    if (role == 1) {
                        main.show(new SupplierForm());
                    } else {
                        JOptionPane.showMessageDialog(null, "Bạn không có quyền truy cập vào chức năng này.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                } else if (index == 4) { // Danh mục sản phẩm
                    if (role == 1) {
                        main.show(new CategorysForm());
                    } else {
                        JOptionPane.showMessageDialog(null, "Bạn không có quyền truy cập vào chức năng này.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                } else if (index == 5) { // trưng bày sản phẩm
                    if (role == 1) {
                        main.show(new DisplayForm());
                    } else {
                        JOptionPane.showMessageDialog(null, "Bạn không có quyền truy cập vào chức năng này.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                } else if (index == 6) { // Khuyến mãi
                    if (role == 1) {
                        main.show(new Promotion());
                    } else {
                        JOptionPane.showMessageDialog(null, "Bạn không có quyền truy cập vào chức năng này.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                } else if (index == 7) { // Nhân viên
                    if (role == 1) {
                        main.show(new EmployeeForm());
                    } else {
                        JOptionPane.showMessageDialog(null, "Bạn không có quyền truy cập vào chức năng này.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                } else if (index == 8) { // Lương
                    if (role == 1) {
                        main.show(new SalaryForm());
                    } else {
                        JOptionPane.showMessageDialog(null, "Bạn không có quyền truy cập vào chức năng này.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                } else if (index == 9) { // Ca Làm
                    if (role == 1) {
                        main.show(new WorkShiftForm());
                    } else {
                        JOptionPane.showMessageDialog(null, "Bạn không có quyền truy cập vào chức năng này.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                } else if (index == 10) { // Chi phí Tháng
                    if (role == 1) {
                        main.show(new Expense());
                    } else {
                        JOptionPane.showMessageDialog(null, "Bạn không có quyền truy cập vào chức năng này.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                } else if (index == 11) { // Đăng xuất
                    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        Session.getInstance().logout(); // Xoá nhân viên đang đăng nhập

                        // Đóng cửa sổ chính
                        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(menu); // hoặc main nếu nó là component
                        topFrame.dispose();

                        // Mở lại LoginForm
                        LoginForm loginForm = new LoginForm();
                        loginForm.setVisible(true);
                    }
                }
            }
        });
    }

    private void displayEmployeeName() {
        String employeeName = Session.getInstance().getEmployeeName();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        mainPanel.setBackground(new java.awt.Color(250, 250, 250));
        mainPanel.setOpaque(true);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1100, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane mainPanel;
    // End of variables declaration//GEN-END:variables
}
