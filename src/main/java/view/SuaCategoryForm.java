package view;

import controller.CategoryController;
import model.Category;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Form thêm/sửa Category
 * @author Admin
 */
public class SuaCategoryForm extends JDialog {
    
    private CategoryController controller;
    private Category category;
    private int userRole;
    private boolean isEdit;
    
    // Components
    private JTextField txtCategoryName;
    private JTextArea txtDescription;
    private JTextField txtCategoryCode;
    private JButton btnSave, btnCancel;
    
    public SuaCategoryForm(JFrame parent, boolean modal, Category category, int userRole) {
        super(parent, modal);
        this.category = category;
        this.userRole = userRole;
        this.isEdit = (category != null);
        
        initComponents();
        setupEventListeners();
        loadData();
        
        if (isEdit) {
            fillFormData();
        }
    }
    
    private void initComponents() {
        setTitle(isEdit ? "✏️ Sửa Danh Mục" : "➕ Thêm Danh Mục");
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel(isEdit ? "✏️ SỬA DANH MỤC SẢN PHẨM" : "➕ THÊM DANH MỤC SẢN PHẨM", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(isEdit ? new Color(0, 123, 255) : new Color(40, 167, 69));
        panel.add(title);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📝 Thông Tin Danh Mục"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Tên danh mục
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("* Tên danh mục:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCategoryName = new JTextField(20);
        txtCategoryName.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(txtCategoryName, gbc);
        
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCategoryCode = new JTextField(20);
        txtCategoryCode.setFont(new Font("Arial", Font.PLAIN, 12));
        txtCategoryCode.setEditable(false);
        txtCategoryCode.setBackground(new Color(240, 240, 240));
        txtCategoryCode.setText("Tự động tạo");
        panel.add(txtCategoryCode, gbc);
        
        // Mô tả
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Mô tả:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtDescription = new JTextArea(5, 20);
        txtDescription.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDescription = new JScrollPane(txtDescription);
        scrollDescription.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollDescription, gbc);
        
        // Required field note
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.weighty = 0; gbc.anchor = GridBagConstraints.WEST;
        JLabel noteLabel = new JLabel("* Trường bắt buộc");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        noteLabel.setForeground(Color.RED);
        panel.add(noteLabel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnSave = new JButton(isEdit ? "💾 Cập Nhật" : "💾 Thêm Mới");
        btnSave.setFont(new Font("Arial", Font.BOLD, 12));
        btnSave.setBackground(isEdit ? new Color(0, 123, 255) : new Color(40, 167, 69));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setPreferredSize(new Dimension(120, 35));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCancel = new JButton("❌ Hủy");
        btnCancel.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panel.add(btnCancel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(btnSave);
        
        return panel;
    }
    
    private void setupEventListeners() {
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCategory();
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // ESC key to cancel
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Enter key to save
        getRootPane().registerKeyboardAction(
            e -> saveCategory(),
            KeyStroke.getKeyStroke("ENTER"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Set default button
        getRootPane().setDefaultButton(btnSave);
    }
    
    private void loadData() {
        // No additional data to load for simplified form
    }
    
    private void fillFormData() {
        if (category != null) {
            txtCategoryName.setText(category.getCategory_name());
            if (category.getCategory_code() != null && !category.getCategory_code().isEmpty()) {
                txtCategoryCode.setText(category.getCategory_code());
            }
            txtDescription.setText(category.getDescription());
        }
    }
    
    private void saveCategory() {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        try {
            // Create or update category object
            Category cat = isEdit ? category : new Category();
            
            cat.setCategory_name(txtCategoryName.getText().trim());
            cat.setDescription(txtDescription.getText().trim().isEmpty() ? null : txtDescription.getText().trim());
            
            // Auto generate category code if new category
            if (!isEdit) {
                cat.setCategory_code(null); // Will be auto-generated in DAO
                cat.setParent_category_id(null); // No parent categories
                cat.setDisplay_order(0); // Default order
                cat.setIs_active(true); // Always active
            }
            
            // Save via controller
            boolean success;
            if (isEdit) {
                success = controller.updateCategory(cat);
            } else {
                success = controller.addCategory(cat);
            }
            
            if (success) {
                dispose();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        // Check required fields
        if (txtCategoryName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên danh mục!");
            txtCategoryName.requestFocus();
            return false;
        }
        
        if (txtCategoryName.getText().trim().length() > 255) {
            JOptionPane.showMessageDialog(this, "Tên danh mục không được vượt quá 255 ký tự!");
            txtCategoryName.requestFocus();
            return false;
        }
        
        return true;
    }
    
    public void setController(CategoryController controller) {
        this.controller = controller;
    }
} 