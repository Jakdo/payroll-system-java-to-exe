import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class lagiiiiinn extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public lagiiiiinn() {
        // Set up the frame
        setTitle("Login System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Custom background
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(52, 152, 219), 
                    getWidth(), getHeight(), new Color(44, 62, 80)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        });
        
        // Use BorderLayout
        setLayout(new BorderLayout());
        
        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title Label
        JLabel titleLabel = new JLabel("LOGIN SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Username Label and Field
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 35));
        mainPanel.add(usernameField, gbc);
        
        // Password Label and Field
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 35));
        mainPanel.add(passwordField, gbc);
        
        // Login Button
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setBackground(new Color(41, 128, 185));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> performLogin());
        mainPanel.add(loginButton, gbc);
        
        // Add hover and click effects
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setBackground(new Color(52, 152, 219));
            }
            public void mouseExited(MouseEvent evt) {
                loginButton.setBackground(new Color(41, 128, 185));
            }
        });
        
        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, 
                "Please enter both username and password.", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try (Connection conn = connect()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Failed to connect to the database.", 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Successful login - Open Angbago frame
                        openDashboard(conn);
                    } else {
                        JOptionPane.showMessageDialog(
                            this, 
                            "Invalid username or password.", 
                            "Login Failed", 
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, 
                "Database error: " + e.getMessage(), 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

private void openDashboard(Connection conn) {
    try {
        // Close login window
        this.dispose(); 
        
        // Open Angbago frame
        SwingUtilities.invokeLater(() -> {
            Angbago dashboard = new Angbago();
            
            // Set the frame to fullscreen
            dashboard.setExtendedState(JFrame.MAXIMIZED_BOTH);
            
            // Optional: Make the dashboard maximize to full screen
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
            
            if (defaultScreen.isFullScreenSupported()) {
                defaultScreen.setFullScreenWindow(dashboard);
            }
            
            dashboard.setVisible(true);
        });
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(
            this, 
            "Error opening dashboard: " + e.getMessage(), 
            "Dashboard Error", 
            JOptionPane.ERROR_MESSAGE
        );
    }
}

    private Connection connect() {
        String url = "jdbc:mysql://localhost:3306/login_db?zeroDateTimeBehavior=CONVERT_TO_NULL";
        String user = "root";
        String password = ""; // Usually blank in XAMPP

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection successful!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            JOptionPane.showMessageDialog(
                this, 
                "MySQL JDBC Driver not found:\n" + e.getMessage(), 
                "Driver Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        } catch (SQLException e) {
            System.err.println("Connection Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this, 
                "Database Connection Failed:\n" + e.getMessage(), 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    public static void main(String[] args) {
        // Set the look and feel to system default for a native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ensure the login window is created and displayed on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new lagiiiiinn().setVisible(true);
        });
    }
}