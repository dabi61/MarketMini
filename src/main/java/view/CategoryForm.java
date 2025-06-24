package view;

import controller.CategoryController;
import model.Category;
import model.Session;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Form quản lý Categories
 * @author Admin
 */
public class CategoryForm extends JPanel {
    
    private int userRole;
    private CategoryController controller;
    
    // Components
    private JTable categoryTable;
    private JTextField searchField;
    private JButton btnAdd, btnEdit, btnDelete, btnSearch, btnRefresh;
    private JLabel lblTotal;
    
    public CategoryForm() {
        // Lấy role từ Session
        this.userRole = Session.getInstance().getRole();
        
        try {
            this.controller = new CategoryController(userRole);
            this.controller.setCategoryForm(this);
            initComponents();
            loadData();
        } catch (Exception ex) {
            Logger.getLogger(CategoryForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo form: " + ex.getMessage());
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));
        
        // Initial setup - sẽ được load lại trong loadData()
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createLoadingPanel(), BorderLayout.CENTER);
    }
    
    private JPanel createLoadingPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("🔄 Đang tải dữ liệu..."));
        return panel;
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        JLabel title = new JLabel("📂 QUẢN LÝ DANH MỤC SẢN PHẨM", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(0, 102, 204));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        panel.add(title);
        return panel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("🔍 Tìm Kiếm"));
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        btnSearch = new JButton("🔍 Tìm kiếm");
        btnSearch.setActionCommand("search");
        
        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        
        panel.add(searchPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        // Action buttons
        JPanel actionPanel = createActionPanel();
        panel.add(actionPanel, BorderLayout.NORTH);
        
        // Table will be created in loadData(), just create scroll pane
        if (categoryTable != null) {
            JScrollPane scrollPane = new JScrollPane(categoryTable);
            scrollPane.setPreferredSize(new Dimension(1150, 450));
            panel.add(scrollPane, BorderLayout.CENTER);
        } else {
            JLabel loadingLabel = new JLabel("🔄 Đang tải dữ liệu bảng...", JLabel.CENTER);
            panel.add(loadingLabel, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("⚡ Thao Tác"));
        
        // Create buttons with icons and colors
        btnAdd = createStyledButton("➕ Thêm", new Color(40, 167, 69));
        btnEdit = createStyledButton("✏️ Sửa", new Color(0, 123, 255));
        btnDelete = createStyledButton("🗑️ Xóa", new Color(220, 53, 69));
        btnRefresh = createStyledButton("🔄 Làm Mới", new Color(108, 117, 125));
        
        // Set action commands
        btnAdd.setActionCommand("add");
        btnEdit.setActionCommand("edit");
        btnDelete.setActionCommand("delete");
        btnRefresh.setActionCommand("refresh");
        
        // Add buttons to panel
        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnRefresh);
        
        // Role-based button visibility
        if (userRole != 1) { // Not admin
            btnDelete.setEnabled(false);
        }
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEtchedBorder());
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void createCategoryTable(List<Category> categories) {
        String[] columnNames = {
            "ID", "Mã DM", "Tên Danh Mục", "Mô Tả", "Ngày Tạo"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        categoryTable = new JTable(model);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.setRowHeight(25);
        categoryTable.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        categoryTable.getTableHeader().setBackground(new Color(240, 240, 240));
        
        // Enable table sorting
        categoryTable.setAutoCreateRowSorter(true);
        
        // Set column widths
        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Mã DM
        categoryTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Tên
        categoryTable.getColumnModel().getColumn(3).setPreferredWidth(300); // Mô tả
        categoryTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Ngày tạo
        
        // Add row selection listener for double-click to edit
        categoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    btnEdit.doClick(); // Double-click to edit
                }
            }
        });
        
        // Populate table with data and sort by created_at DESC (newest first)
        if (controller != null) {
            controller.updateCategoryTable(categories);
            // Sort by ngày tạo (column 4) descending
            categoryTable.getRowSorter().toggleSortOrder(4);
            categoryTable.getRowSorter().toggleSortOrder(4); // Toggle twice for DESC
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("❌ Đóng");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.addActionListener(e -> {
            cleanup();
            // Close parent window if needed
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
        
        JButton refreshButton = new JButton("🔄 Làm Mới");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.addActionListener(e -> loadData());
        
        return buttonPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Info Panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblTotal = new JLabel("Tổng: 0 danh mục");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(lblTotal);
        
        
        return panel;
    }
    
    private void setupEventListeners() {
        // Add action listeners
        btnAdd.addActionListener(controller);
        btnEdit.addActionListener(controller);
        btnDelete.addActionListener(controller);
        btnRefresh.addActionListener(controller);
        btnSearch.addActionListener(controller);
        
        // Enter key for search
        searchField.addActionListener(controller);
        searchField.setActionCommand("search");
        
        // ESC key to close
        registerKeyboardAction(
            e -> {
                cleanup();
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }
            },
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void loadData() {
        try {
            if (controller == null) return;
            
            // Get categories data
            List<Category> categories = controller.getAllCategories();
            
            // Create table
            createCategoryTable(categories);
            
            // Create content panels
            JPanel headerPanel = createHeaderPanel();
            JPanel centerPanel = createCenterPanel();
            JPanel footerPanel = createFooterPanel();
            
            // Setup event listeners after buttons are created
            setupEventListeners();
            
            // Update total label
            updateTotalLabel(categories.size());
            
            // Remove previous content and add new
            removeAll();
            add(createTitlePanel(), BorderLayout.NORTH);
            
            // Main content panel
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(centerPanel, BorderLayout.CENTER);
            mainPanel.add(footerPanel, BorderLayout.SOUTH);
            
            add(mainPanel, BorderLayout.CENTER);
            add(createButtonPanel(), BorderLayout.SOUTH);
            
            revalidate();
            repaint();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    public void updateTotalLabel() {
        if (categoryTable != null) {
            int rowCount = categoryTable.getRowCount();
            lblTotal.setText("Tổng: " + rowCount + " danh mục");
        }
    }
    
    public void updateTotalLabel(int count) {
        if (lblTotal != null) {
            lblTotal.setText("Tổng: " + count + " danh mục");
        }
    }
    
    // Getters for controller access
    public JTable getCategoryTable() {
        return categoryTable;
    }
    
    public JTextField getSearchField() {
        return searchField;
    }
    
    public void cleanup() {
        if (controller != null) {
            controller.closeConnection();
        }
    }
} 