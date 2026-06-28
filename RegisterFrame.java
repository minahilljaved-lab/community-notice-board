import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
 
public class RegisterFrame extends JFrame {
    private final JTextField usernameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;
    private final JLabel passwordStrengthLabel;
 
    public RegisterFrame() {
        setTitle("Community Board - Register");
        setSize(460, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
 
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        mainPanel.setBackground(Color.WHITE);
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
 
        int row = 0;
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(46, 64, 87));
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 6, 0);
        mainPanel.add(titleLabel, gbc);
 
        JLabel subtitleLabel = new JLabel("Fill in the details below", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 16, 0);
        mainPanel.add(subtitleLabel, gbc);
 
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 0, 2, 0);
        mainPanel.add(userLabel, gbc);
 
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 13));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(usernameField, gbc);

        JLabel emailLabel = new JLabel("Gmail");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 0, 2, 0);
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 13));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(emailField, gbc);

        JLabel phoneLabel = new JLabel("Phone Number");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 0, 2, 0);
        mainPanel.add(phoneLabel, gbc);

        phoneField = new JTextField();
        phoneField.setFont(new Font("Arial", Font.PLAIN, 13));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(phoneField, gbc);
 
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 0, 2, 0);
        mainPanel.add(passLabel, gbc);
 
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        mainPanel.add(passwordField, gbc);

        passwordStrengthLabel = new JLabel("Password strength: weak");
        passwordStrengthLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        passwordStrengthLabel.setForeground(Color.GRAY);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(passwordStrengthLabel, gbc);
 
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 0, 2, 0);
        mainPanel.add(confirmLabel, gbc);
 
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 13));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 0, 8, 0);
        mainPanel.add(confirmPasswordField, gbc);
 
        JButton registerButton = new JButton("Create Account");
        registerButton.setBackground(new Color(46, 64, 87));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        registerButton.addActionListener(e -> handleRegister());
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 8, 0);
        mainPanel.add(registerButton, gbc);
 
        JButton backButton = new JButton("Back to Login");
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Arial", Font.PLAIN, 13));
        backButton.addActionListener(e -> dispose());
        gbc.gridy = row;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(backButton, gbc);

        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updatePasswordStrength(); }
            public void removeUpdate(DocumentEvent e) { updatePasswordStrength(); }
            public void changedUpdate(DocumentEvent e) { updatePasswordStrength(); }
        });
        confirmPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updatePasswordStrength(); }
            public void removeUpdate(DocumentEvent e) { updatePasswordStrength(); }
            public void changedUpdate(DocumentEvent e) { updatePasswordStrength(); }
        });
 
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
 
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
 
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in the username and password fields!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.equalsIgnoreCase("owner")) {
            JOptionPane.showMessageDialog(this, "The owner username is reserved.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!UserStorage.hasOwnerAccount()) {
            JOptionPane.showMessageDialog(this, "Only the owner can create the first account.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Gmail address.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!phone.isEmpty() && !phone.matches("\\d{7,15}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isStrongPassword(password)) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters and include uppercase, lowercase, number, and symbol.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        if (UserStorage.usernameExists(username) || UserStorage.emailOrPhoneExists(email, phone)) {
            JOptionPane.showMessageDialog(this, "Username, Gmail, or phone number already used!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        User newUser = new User(username, password, "member", false, true, "", "", "", 0, email, phone);
        UserStorage.saveUser(newUser);
        AuditLog.logUserRegistration(newUser);
        JOptionPane.showMessageDialog(this, "Account created! You can now log in.",
            "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void updatePasswordStrength() {
        String password = new String(passwordField.getPassword());
        if (password.isEmpty()) {
            passwordStrengthLabel.setText("Password strength: weak");
            passwordStrengthLabel.setForeground(Color.GRAY);
            return;
        }
        if (isStrongPassword(password)) {
            passwordStrengthLabel.setText("Password strength: strong");
            passwordStrengthLabel.setForeground(new Color(0, 128, 0));
        } else {
            passwordStrengthLabel.setText("Password strength: weak");
            passwordStrengthLabel.setForeground(new Color(180, 50, 50));
        }
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8
            && password.matches(".*[A-Z].*")
            && password.matches(".*[a-z].*")
            && password.matches(".*\\d.*")
            && password.matches(".*[^A-Za-z0-9].*");
    }
}
 