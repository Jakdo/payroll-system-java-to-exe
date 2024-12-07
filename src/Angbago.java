
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class Angbago extends JFrame {
    private List<Employee> employees = new ArrayList<>();
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> positionComboBox;

    public Angbago() {
        setTitle("Payroll Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        loadEmployeesFromDatabase();
    }

    // Database Connection Method
    public static Connection connect() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Update this with your database details
            String url = "jdbc:mysql://localhost:3306/payroll_db?zeroDateTimeBehavior=CONVERT_TO_NULL";
            String user = "root";
            String password = "";

            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    Angbago(Connection conn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(52, 152, 219),
                        getWidth(), getHeight(), new Color(44, 62, 80)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{
                "Employee ID", "Name", "Position",
                "Basic Salary", "SSS", "PhilHealth",
                "Pagibig", "Tax", "Net Salary"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setForeground(Color.DARK_GRAY);
        employeeTable.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.SOUTH);

        JPanel actionPanel = createActionPanel();
        mainPanel.add(actionPanel, BorderLayout.EAST);

        add(mainPanel);
        setLocationRelativeTo(null);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel titleLabel = new JLabel("Payroll Management System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setOpaque(false);

        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search by ID");
        searchButton.addActionListener(e -> searchEmployeeById());

        searchPanel.add(new JLabel("Search Employee ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private void searchEmployeeById() {
        String searchId = searchField.getText().trim();
        List<Employee> filteredEmployees = employees.stream()
                .filter(emp -> emp.getId().equalsIgnoreCase(searchId))
                .collect(Collectors.toList());

        updateEmployeeTable(filteredEmployees);
    }
    

 //   private JPanel createActionPanel() {
//        JPanel actionPanel = new JPanel();
//        actionPanel.setOpaque(false);
//        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
//
//        JButton addEmployeeBtn = new JButton("Add Employaaee");
//        JButton calcEmployeeBtn = new JButton("Calculate Salary");
//        JButton deleteEmployeeBtn = new JButton("Delete Employee");
//        JButton resetTableBtn = new JButton("Reset Table");
//
//        addEmployeeBtn.addActionListener(e -> addEmployee());
//        calcEmployeeBtn.addActionListener(e-> calcEmployee());
//        deleteEmployeeBtn.addActionListener(e -> deleteEmployee());
//        resetTableBtn.addActionListener(e -> updateEmployeeTable(employees));
//
//        actionPanel.add(addEmployeeBtn);
//        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
//        actionPanel.add(deleteEmployeeBtn);
//        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
//        actionPanel.add(resetTableBtn);
//
//        return actionPanel;
//    }
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        
        JButton addEmployeeBtn = new JButton("Add Employee");
        JButton deleteEmployeeBtn = new JButton("Delete Employee");
        JButton calculatePayrollBtn = new JButton("Calculate Selected Payroll");
        JButton resetTableBtn = new JButton("Reset Table");
        JButton generateReportBtn = new JButton("Generate Report");
        
        addEmployeeBtn.addActionListener(e -> addEmployee());
        deleteEmployeeBtn.addActionListener(e -> deleteEmployee());
        calculatePayrollBtn.addActionListener(e -> calculateSelectedPayroll());
        resetTableBtn.addActionListener(e -> updateEmployeeTable(employees));
        generateReportBtn.addActionListener(e -> generateReport());
        
        actionPanel.add(addEmployeeBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionPanel.add(deleteEmployeeBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionPanel.add(calculatePayrollBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionPanel.add(resetTableBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        actionPanel.add(generateReportBtn);
        
        return actionPanel;
    }

    private void calculateSelectedPayroll() {
       //calculate payroll
       
       
       int selectedRow = employeeTable.getSelectedRow();
    
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
            this, 
            "Please select an employee to calculate payroll.", 
            "Error", 
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }
    
    String employeeId = (String) employeeTable.getValueAt(selectedRow, 0);
    Employee selectedEmployee = employees.stream()
        .filter(emp -> emp.getId().equals(employeeId))
        .findFirst()
        .orElse(null);
    
    if (selectedEmployee != null) {
        JTextField daysWorkedField = new JTextField(5);
        JTextField hoursWorkedField = new JTextField(5);
        JLabel hoursWorkedLabel = new JLabel("Hours Worked: 0");
        JCheckBox overtimeCheckBox = new JCheckBox("Include Overtime");
        
        // Add ActionListener to days worked field
        daysWorkedField.addActionListener(e -> {
            try {
                int daysWorked = Integer.parseInt(daysWorkedField.getText().trim());
                int hoursWorked = daysWorked * 8; // Assume 8 hours per day by default
                
                // If overtime is included
                if (overtimeCheckBox.isSelected()) {
                    int overtimeHours = daysWorked * 2; // Adjust this based on rules (2 hours per day for example)
                    hoursWorked += overtimeHours;
                }
                
                // Update the Hours Worked label
                hoursWorkedLabel.setText("Hours Worked: " + hoursWorked);
                // Also update the hours worked field
                hoursWorkedField.setText(String.valueOf(hoursWorked));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Please enter a valid number for Days Worked.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        
        // Existing pay frequency options
        String[] payFrequencyOptions = {"Semi-Monthly (15 days)", "Monthly (30 days)"};
        JComboBox<String> payFrequencyComboBox = new JComboBox<>(payFrequencyOptions);
        
        // Modify the input panel to include the hours worked label
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Pay Frequency:"));
        inputPanel.add(payFrequencyComboBox);
        inputPanel.add(new JLabel("Days Worked:"));
        inputPanel.add(daysWorkedField);
        inputPanel.add(hoursWorkedLabel);  // Add the hours worked label
        inputPanel.add(hoursWorkedField);
        inputPanel.add(new JLabel("Overtime:"));
        inputPanel.add(overtimeCheckBox);
        
        int result = JOptionPane.showConfirmDialog(
            this, inputPanel, 
            "Enter Work Details", 
            JOptionPane.OK_CANCEL_OPTION
        );
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int daysWorked = Integer.parseInt(daysWorkedField.getText().trim());
                int hoursWorked = Integer.parseInt(hoursWorkedField.getText().trim());
                boolean includeOvertime = overtimeCheckBox.isSelected();

                Employee.PayFrequency selectedFrequency = 
                    payFrequencyComboBox.getSelectedIndex() == 0 ? 
                    Employee.PayFrequency.SEMI_MONTHLY : 
                    Employee.PayFrequency.MONTHLY;

                // Existing validation checks remain the same
                if (selectedFrequency == Employee.PayFrequency.SEMI_MONTHLY && daysWorked > 15) {
                    throw new IllegalArgumentException("Days worked cannot exceed 15 for Semi-Monthly pay frequency.");
                }
                
                if (selectedFrequency == Employee.PayFrequency.MONTHLY && daysWorked > 30) {
                    throw new IllegalArgumentException("Days worked cannot exceed 30 for Monthly pay frequency.");
                }

                selectedEmployee.setPayFrequency(selectedFrequency);
                selectedEmployee.calculateContributions(daysWorked, hoursWorked, includeOvertime);
                updateEmployeeTable(employees);
                
                JOptionPane.showMessageDialog(
                    this, 
                    "Payroll Calculation Complete for " + selectedEmployee.getName(),
                    "Payroll", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Invalid input for days or hours worked. Please enter numeric values.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                    this, 
                    ex.getMessage(), 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }
    }
    
    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String employeeId = (String) employeeTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = connect()) {
                String sql = "DELETE FROM employees WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, employeeId);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                loadEmployeesFromDatabase(); // Refresh the table
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to delete employee!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addEmployee() {
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        String[] positions = {"Junior Staff", "Senior Staff", "Manager", "Executive"};
        positionComboBox = new JComboBox<>(positions);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Employee ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Position:"));
        inputPanel.add(positionComboBox);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add New Employee", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = connect()) {
                String sql = "INSERT INTO employees (id, name, position) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, idField.getText().trim());
                pstmt.setString(2, nameField.getText().trim());
                pstmt.setString(3, (String) positionComboBox.getSelectedItem());
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Employee added successfully!");
                loadEmployeesFromDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add employee!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadEmployeesFromDatabase() {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM employees");
             ResultSet rs = stmt.executeQuery()) {

            employees.clear();
            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("position"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("sss"),
                        rs.getDouble("philhealth"),
                        rs.getDouble("pagibig"),
                        rs.getDouble("tax"),
                        rs.getDouble("net_salary")
                );
                employees.add(emp);
            }
            updateEmployeeTable(employees);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load employees!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployeeTable(List<Employee> employeeList) {
        tableModel.setRowCount(0);
        for (Employee emp : employeeList) {
            tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getName(),
                    emp.getPosition(),
                    String.format("₱%.2f", emp.getBaseSalary()),
                    String.format("₱%.2f", emp.getSssContribution()),
                    String.format("₱%.2f", emp.getPhilHealthContribution()),
                    String.format("₱%.2f", emp.getPagibigContribution()),
                    String.format("₱%.2f", emp.getIncomeTax()),
                    String.format("₱%.2f", emp.getNetSalary())
                    
            });
        }
    }
    private void generateReport() {
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, 
                "No employees found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

    StringBuilder report = new StringBuilder();
    report.append("Comprehensive Payroll Report\n");
    report.append("Generated on: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");
    
    double totalBaseSalary = 0, totalSSS = 0, totalPhilHealth = 0, 
           totalPagibig = 0, totalTax = 0, totalNetSalary = 0;
    
    for (Employee emp : employees) {
        report.append(emp.getPayrollDetails());
        
        totalBaseSalary += emp.getMonthlyBasePay(); 
        totalSSS += emp.getSssContribution();
        totalPhilHealth += emp.getPhilHealthContribution();
        totalPagibig += emp.getPagibigContribution();
        totalTax += emp.getIncomeTax();
        totalNetSalary += emp.getNetSalary();
    }
    
    report.append("\nPayroll Totals:\n");
    report.append(String.format("Total Base Salary: ₱%.2f\n", totalBaseSalary));
    report.append(String.format("Total SSS Contributions: ₱%.2f\n", totalSSS));
    report.append(String.format("Total PhilHealth Contributions: ₱%.2f\n", totalPhilHealth));
    report.append(String.format("Total Pagibig Contributions: ₱%.2f\n", totalPagibig));
    report.append(String.format("Total Income Tax: ₱%.2f\n", totalTax));
    report.append(String.format("Total Net Salary: ₱%.2f\n", totalNetSalary));
    
    JTextArea reportArea = new JTextArea(report.toString());
    JScrollPane scrollPane = new JScrollPane(reportArea);
    scrollPane.setPreferredSize(new Dimension(400, 300));
    
    JOptionPane.showMessageDialog(
        this, 
        scrollPane, 
        "Payroll Report", 
        JOptionPane.INFORMATION_MESSAGE
    );
}
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Angbago().setVisible(true));
    }
    
}

// Updated Employee Class
class Employee {
    private String id;
    private String name;
    private String position;
    private double sss;
    private double philhealth;
    private double pagibig;
    private double tax;
    private double netSalary;
    
    private PayFrequency payFrequency;
    private double monthlyBasePay;
    private double baseSalary;
    private double sssContribution;
    private double philHealthContribution;
    private double pagibigContribution;
    private double incomeTax;
    private double overtimePay;
    private int daysWorked;
    private int hoursWorked;
    private static final double BASE_HOURLY_RATE = 72.0; // Fixed hourly rate
    private final double BaseSalary;
    
    public enum PayFrequency {
        SEMI_MONTHLY(15), // 15 days
        MONTHLY(30);       // 30 days

        private final int maxDays;

        PayFrequency(int maxDays) {
            this.maxDays = maxDays;
        }

        public int getMaxDays() {
            return maxDays;
        }
    }

    public Employee(String id, String name, String position, double BaseSalary, double sssContribution, double philHealthContribution, double pagibigContribution, double incomeTax, double netSalary) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.BaseSalary = BaseSalary;

        this.pagibig = pagibigContribution;
        this.tax = incomeTax;
        this.monthlyBasePay = calculateMonthlyBasePay();
        this.payFrequency = PayFrequency.SEMI_MONTHLY; // Default to semi-monthly pay
    }

    
    private double calculateMonthlyBasePay() {
        // Base monthly pay for different positions
        switch (position) {
            case "Junior Staff": return 15000;
            case "Senior Staff": return 18000;
            case "Manager": return 22000;
            case "Executive": return 35000;
            default: return 15000;
        }
    }

    // Method to set pay frequency
    public void setPayFrequency(PayFrequency frequency) {
        this.payFrequency = frequency;
    }
    
    // Method to calculate base salary based on days worked
    private void calculateBaseSalary(int daysWorked) {
        // Limit days worked to pay frequency max days
        daysWorked = Math.min(daysWorked, payFrequency.getMaxDays());
        
        // Calculate base salary proportionally
        this.baseSalary = (monthlyBasePay / payFrequency.getMaxDays()) * daysWorked;
    }
    
    public void calculateContributions(int daysWorked, int hoursWorked, boolean includeOvertime) {
        // Calculate base salary first
        calculateBaseSalary(daysWorked);
        
        // Limit days worked to pay frequency max days
        daysWorked = Math.min(daysWorked, payFrequency.getMaxDays());
        
        // Calculate hours worked and overtime
        int standardWorkHours = daysWorked * 8;
        int actualHoursWorked = Math.min(hoursWorked, standardWorkHours + (includeOvertime ? 8 : 0));
        this.hoursWorked = actualHoursWorked;

        // Overtime calculation
        overtimePay = 0;
        if (includeOvertime && actualHoursWorked > standardWorkHours) {
            int overtimeHours = actualHoursWorked - standardWorkHours;
            overtimePay = calculateOvertimePay(overtimeHours);
        }

        // Total salary including overtime
        double totalSalary = baseSalary + overtimePay;

        // Calculate contributions
        sssContribution = calculateSssContribution(totalSalary);
        philHealthContribution = calculatePhilHealthContribution(totalSalary);
        pagibigContribution = calculatePagibigContribution(totalSalary);
        incomeTax = calculateIncomeTax(totalSalary);

        // Net salary calculation
        netSalary = totalSalary - (sssContribution + philHealthContribution + pagibigContribution + incomeTax);
    }
    
    private double calculateOvertimePay(double overtimeHours) {
        if (overtimeHours <= 0) return 0;

        // Consistent overtime rate of 1.25x for all positions
        double overtimeRate = BASE_HOURLY_RATE * 1.25;

        return overtimeHours * overtimeRate;
    }
    
    private double calculateIncomeTax(double salary) {
        // 2024 Philippine Income Tax Calculation
        double annualSalary = salary * 12;
        
        // Tax calculation based on 2024 BIR tax table
        if (annualSalary <= 250000) return 0;
        
        if (annualSalary <= 400000) 
            return ((annualSalary - 250000) * 0.15) / 12;
        
        if (annualSalary <= 800000)
            return ((22500 + (annualSalary - 400000) * 0.20)) / 12;
        
        if (annualSalary <= 2000000)
            return ((102500 + (annualSalary - 800000) * 0.25)) / 12;
        
        if (annualSalary <= 8000000)
            return ((402500 + (annualSalary - 2000000) * 0.30)) / 12;
        
        return ((2202500 + (annualSalary - 8000000) * 0.35)) / 12;
    }

    private double calculateSssContribution(double salary) {
        // 2024 SSS Contribution Table (Employer-Employee Contribution)
        double[][] sssContributionTable = {
            {4250, 180},
            {4750, 202.5},
            {5250, 225},
            {5750, 247.5},
            {6250, 270},
            {6750, 292.5},
            {7250, 315},
            {7750, 337.5},
            {8250, 360},
        };
        
        for (double[] bracket : sssContributionTable) {
            if (salary <= bracket[0]) {
                return bracket[1];
            }
        }
        return 360; // Maximum contribution
    }

    private double calculatePhilHealthContribution(double salary) {
        // 2024 PhilHealth Contribution 
        // 4% total (2% employee, 2% employer)
        return Math.min(salary * 0.02, 450);
    }

    private double calculatePagibigContribution(double salary) {
        // 2024 Pagibig Contribution 
        // 2% employee contribution
        return Math.min(salary * 0.02, 100);
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public double getSss() {
        return sss;
    }

    public double getPhilhealth() {
        return philhealth;
    }

    public double getPagibig() {
        return pagibig;
    }

    public double getTax() {
        return tax;
    }

    public double getNetSalary() {
        return netSalary;
    }
    
    public double getMonthlyBasePay() { return monthlyBasePay; }
    
    public double getBaseSalary() { 
        return baseSalary;
    }
    
    public double getOvertimePay() { return overtimePay; }
    public double getSssContribution() { return sssContribution; }
    public double getPhilHealthContribution() { return philHealthContribution; }
    public double getPagibigContribution() { return pagibigContribution; }
    public double getIncomeTax() { return incomeTax; }
    
    public String getPayrollDetails() {
        return String.format(
            "Employee: %s (ID: %s)\n"+
            "Position: %s\n" +
            "Pay Frequency: %s\n" +
            "Days Worked: %d\n" +
            "Hours Worked: %d\n" +
            "Monthly Base Pay: ₱%.2f\n" +
            "Proportional Base Salary: ₱%.2f\n" +
            "Overtime Pay: ₱%.2f\n" +
            "SSS Contribution: ₱%.2f\n" +
            "PhilHealth Contribution: ₱%.2f\n" +
            "Pagibig Contribution: ₱%.2f\n" +
            "Income Tax: ₱%.2f\n" +
            "Net Salary: ₱%.2f\n\n",
            name, id, position, payFrequency, 
            daysWorked, hoursWorked, 
            monthlyBasePay, baseSalary, overtimePay,
            sssContribution, philHealthContribution, 
            pagibigContribution, incomeTax, netSalary
        );
    }
}