import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
 
public class CreatePostFrame extends JFrame {
    private final String currentUser;
    private final DashboardFrame dashboard;
 
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;
    private JComboBox<Community> communityCombo;
    private JTextField eventDateField;
    private JLabel imageLabel;
    private String selectedImagePath = "";
 
    public CreatePostFrame(String currentUser, DashboardFrame dashboard) {
        this.currentUser = currentUser;
        this.dashboard = dashboard;
 
        setTitle("Create New Post");
        setSize(500, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
 
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        formPanel.setBackground(Color.WHITE);
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
 
        JLabel heading = new JLabel("Create New Post", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setForeground(new Color(46, 64, 87));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(heading, gbc);
 
        JLabel titleLbl = new JLabel("Post Title");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 2, 0);
        formPanel.add(titleLbl, gbc);
 
        titleField = new JTextField();
        titleField.setFont(new Font("Arial", Font.PLAIN, 13));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(titleField, gbc);
 
        JLabel catLbl = new JLabel("Category");
        catLbl.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 0, 2, 0);
        formPanel.add(catLbl, gbc);
 
        String[] categories = {"Announcement", "Event", "Lost & Found", "Buy/Sell", "General"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(categoryCombo, gbc);
 
        JLabel communityLbl = new JLabel("Community");
        communityLbl.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 5;
        gbc.insets = new Insets(4, 0, 2, 0);
        formPanel.add(communityLbl, gbc);
 
        CommunityStorage.ensureDefaultCommunities();
        List<Community> communities = CommunityStorage.loadAllCommunities();
        communityCombo = new JComboBox<>();
        for (Community community : communities) {
            if (community.isApproved()) communityCombo.addItem(community);
        }
        communityCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(communityCombo, gbc);
 
        JLabel descLbl = new JLabel("Description (links supported)");
        descLbl.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 7;
        gbc.insets = new Insets(4, 0, 2, 0);
        formPanel.add(descLbl, gbc);
 
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane scrollDesc = new JScrollPane(descriptionArea);
        scrollDesc.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(scrollDesc, gbc);
 
        JLabel eventLbl = new JLabel("Event Date (optional — format: yyyy-MM-dd)");
        eventLbl.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 9;
        gbc.insets = new Insets(4, 0, 2, 0);
        formPanel.add(eventLbl, gbc);
 
        eventDateField = new JTextField();
        eventDateField.setFont(new Font("Arial", Font.PLAIN, 13));
        eventDateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 10, 0);
        formPanel.add(eventDateField, gbc);
 
        JLabel imgLbl = new JLabel("Attach Image (optional)");
        imgLbl.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 11;
        gbc.insets = new Insets(4, 0, 2, 0);
        formPanel.add(imgLbl, gbc);
 
        JButton imageButton = new JButton("Browse Image...");
        imageButton.setFocusPainted(false);
        imageButton.addActionListener(e -> selectImage());
        gbc.gridy = 12;
        gbc.insets = new Insets(0, 0, 4, 0);
        formPanel.add(imageButton, gbc);
 
        imageLabel = new JLabel("No image selected");
        imageLabel.setForeground(Color.GRAY);
        imageLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        gbc.gridy = 13;
        gbc.insets = new Insets(0, 0, 16, 0);
        formPanel.add(imageLabel, gbc);
 
        JButton postButton = new JButton("Post");
        postButton.setBackground(new Color(46, 64, 87));
        postButton.setForeground(Color.WHITE);
        postButton.setFocusPainted(false);
        postButton.setFont(new Font("Arial", Font.BOLD, 14));
        postButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        postButton.addActionListener(e -> handlePost());
        gbc.gridy = 14;
        gbc.insets = new Insets(0, 0, 8, 0);
        formPanel.add(postButton, gbc);
 
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelButton.addActionListener(e -> dispose());
        gbc.gridy = 15;
        gbc.insets = new Insets(0, 0, 0, 0);
        formPanel.add(cancelButton, gbc);
 
        JScrollPane outerScroll = new JScrollPane(formPanel);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);
        setVisible(true);
    }
 
    private void selectImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            selectedImagePath = file.getAbsolutePath();
            imageLabel.setText(file.getName());
            imageLabel.setForeground(new Color(46, 64, 87));
        }
    }
 
    private void handlePost() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String eventDate = eventDateField.getText().trim();
        Community community = (Community) communityCombo.getSelectedItem();
 
        if (title.isEmpty() || description.isEmpty() || community == null) {
            JOptionPane.showMessageDialog(this, "Title, description, and community are required!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        User current = UserStorage.findUser(currentUser);
        if (current == null) {
            JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (current.isBanned()) {
            JOptionPane.showMessageDialog(this, "You are blocked from posting.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if ("Announcement".equalsIgnoreCase(category) && !current.isOwner() && !current.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Only owner and admins can create announcements.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!community.isMember(currentUser) && !current.isOwner() && !current.isAdmin()) {
            JOptionPane.showMessageDialog(this, "You must join the community first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (community.isBlocked(currentUser)) {
            JOptionPane.showMessageDialog(this, "You are blocked from this community.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        String id = UUID.randomUUID().toString().substring(0, 8);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String displayAuthor = current.isOwner() ? "Anonymous Owner" : (current.isAdmin() && current.isAnonymousAdmin() ? "Anonymous Admin" : currentUser);
 
        Post post = new Post(id, title, description, category,
                             currentUser, displayAuthor, date, selectedImagePath, eventDate, community.getId(), null);
        PostStorage.savePost(post);
        CommunityStorage.markCommunityActive(community.getId());
 
        JOptionPane.showMessageDialog(this, "Post created successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
 
        dispose();
        dashboard.refreshPosts();
        dashboard.revalidate();
        dashboard.repaint();
    }
}
 