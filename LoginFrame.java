import javax.swing.*;
import java.awt.*;
 
public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
 
    public LoginFrame() {
        setTitle("Community Board - Login");
        setSize(420, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
 
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(Color.WHITE);
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
 
        JLabel titleLabel = new JLabel("Community Board", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(46, 64, 87));
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);
 
        JLabel subtitleLabel = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(subtitleLabel, gbc);
 
        gbc.insets = new Insets(4, 0, 2, 0);
 
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 2;
        mainPanel.add(userLabel, gbc);
 
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 13));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(usernameField, gbc);
 
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 4;
        gbc.insets = new Insets(4, 0, 2, 0);
        mainPanel.add(passLabel, gbc);
 
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(passwordField, gbc);
 
        JButton loginButton = new JButton("Sign In");
        loginButton.setBackground(new Color(46, 64, 87));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 8, 0);
        mainPanel.add(loginButton, gbc);
 
        JButton registerButton = new JButton("New here? Register");
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("Arial", Font.PLAIN, 13));
        registerButton.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        registerButton.addActionListener(e -> openRegister());
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(registerButton, gbc);
 
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
 
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
 
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        if (UserStorage.validateLogin(username, password)) {
            dispose();
            new DashboardFrame(username);
        } else {
            User user = UserStorage.findUser(username);
            if (user != null && (user.isOwner() || user.isAdmin()) && !UserStorage.hasRequiredContactInfo(user)) {
                boolean completed = UserStorage.promptForAdminContactInfo(this, user, "Complete Admin Contact Details");
                if (completed && UserStorage.validateLogin(username, password)) {
                    dispose();
                    new DashboardFrame(username);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid username or password or missing admin contact details.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void openRegister() {
        new RegisterFrame();
    }
}