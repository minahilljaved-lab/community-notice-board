import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardFrame extends JFrame {
    private final String currentUser;
    private JPanel postsPanel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JComboBox<Community> communityCombo;
    private JLabel communityStatusLabel;
    private List<Post> allPosts;
    private Community selectedCommunity;

    public DashboardFrame(String currentUser) {
        this.currentUser = currentUser;
        CommunityStorage.ensureDefaultCommunities();
        CommunityStorage.pruneInactiveCommunities();

        setTitle("Community Board - Welcome, " + currentUser);
        setSize(920, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBorder(new EmptyBorder(10, 15, 10, 15));
        topBar.setBackground(new Color(46, 64, 87));

        JLabel appTitle = new JLabel("Community Board");
        appTitle.setFont(new Font("Arial", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Logged in as: " + currentUser);
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JButton newPostButton = new JButton("+ New Post");
        newPostButton.setBackground(new Color(255, 255, 255));
        newPostButton.setForeground(new Color(46, 64, 87));
        newPostButton.setFocusPainted(false);
        newPostButton.setFont(new Font("Arial", Font.BOLD, 12));
        newPostButton.addActionListener(e -> new CreatePostFrame(currentUser, this));

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.addActionListener(e -> changePassword());

        JButton sharePageButton = new JButton("🔗 Share");
        sharePageButton.setFocusPainted(false);
        sharePageButton.addActionListener(e -> shareCurrentPage());

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setBackground(new Color(46, 64, 87));
        topRight.add(userLabel);
        topRight.add(newPostButton);
        topRight.add(changePasswordButton);
        topRight.add(sharePageButton);
        User currentUserData = UserStorage.findUser(currentUser);
        boolean isModerator = currentUserData != null && (currentUserData.isOwner() || currentUserData.isAdmin());
        boolean isOwner = currentUserData != null && currentUserData.isOwner();
        if (isOwner) {
            JButton ownerPanelButton = new JButton("Owner Panel");
            ownerPanelButton.setFocusPainted(false);
            ownerPanelButton.addActionListener(e -> new OwnerPanelFrame(currentUser, this));
            topRight.add(ownerPanelButton);
        }

        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(topRight, BorderLayout.EAST);

        JPanel controlBar = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 8));
        controlBar.setBackground(new Color(240, 244, 248));

        communityCombo = new JComboBox<>();
        communityCombo.addActionListener(e -> {
            selectedCommunity = (Community) communityCombo.getSelectedItem();
            if (selectedCommunity != null) {
                CommunityStorage.markCommunityActive(selectedCommunity.getId());
            }
            refreshPosts();
        });

        JButton createCommunityButton = new JButton("Create Community");
        createCommunityButton.setFocusPainted(false);
        createCommunityButton.addActionListener(e -> createCommunity());

        JButton joinCommunityButton = new JButton("Join Selected");
        joinCommunityButton.setFocusPainted(false);
        joinCommunityButton.addActionListener(e -> joinSelectedCommunity());

        JButton browseCommunityButton = new JButton("Browse Communities");
        browseCommunityButton.setFocusPainted(false);
        browseCommunityButton.addActionListener(e -> browseCommunities());

        JButton requestAdminButton = new JButton("Request Admin");
        requestAdminButton.setFocusPainted(false);
        requestAdminButton.addActionListener(e -> requestAdminAccess());

        JButton addMemberButton = new JButton("Add Member");
        addMemberButton.setFocusPainted(false);
        addMemberButton.addActionListener(e -> addMember());

        JButton removeMemberButton = new JButton("Remove Member");
        removeMemberButton.setFocusPainted(false);
        removeMemberButton.addActionListener(e -> removeMember());

        JButton approveAdminButton = new JButton("Approve Admin");
        approveAdminButton.setFocusPainted(false);
        approveAdminButton.addActionListener(e -> approveAdminRequest());

        JButton deleteCommunityButton = new JButton("Delete Community");
        deleteCommunityButton.setFocusPainted(false);
        deleteCommunityButton.addActionListener(e -> deleteSelectedCommunity());

        JButton adminPanelButton = new JButton("Admin Panel");
        adminPanelButton.setFocusPainted(false);
        adminPanelButton.addActionListener(e -> openAdminPanel());

        communityStatusLabel = new JLabel("Community: none");
        communityStatusLabel.setForeground(new Color(46, 64, 87));
        communityStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));

        searchField = new JTextField(20);
        searchField.setToolTipText("Search posts...");

        String[] categories = {"All", "Announcement", "Event", "Lost & Found", "Buy/Sell", "General"};
        categoryFilter = new JComboBox<>(categories);

        JButton searchButton = new JButton("Search");
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> applyFilter());

        JButton clearButton = new JButton("Clear");
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> {
            searchField.setText("");
            categoryFilter.setSelectedIndex(0);
            applyFilter();
        });

        controlBar.add(new JLabel("Community:"));
        controlBar.add(communityCombo);
        controlBar.add(createCommunityButton);
        controlBar.add(joinCommunityButton);
        controlBar.add(browseCommunityButton);
        controlBar.add(requestAdminButton);
        if (isModerator) {
            controlBar.add(addMemberButton);
            controlBar.add(removeMemberButton);
        }
        if (isOwner) {
            controlBar.add(approveAdminButton);
        }
        if (isModerator) {
            controlBar.add(deleteCommunityButton);
            controlBar.add(adminPanelButton);
        }
        controlBar.add(searchField);
        controlBar.add(new JLabel("Category:"));
        controlBar.add(categoryFilter);
        controlBar.add(searchButton);
        controlBar.add(clearButton);
        controlBar.add(communityStatusLabel);

        postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));
        postsPanel.setBackground(Color.WHITE);
        postsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(postsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(topBar, BorderLayout.NORTH);
        topSection.add(controlBar, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        refreshCommunities();
        refreshPosts();
        setVisible(true);

        NotificationService.checkEventReminders(PostStorage.loadAllPosts());
    }

    public void refreshPosts() {
        if (selectedCommunity == null) {
            allPosts = new ArrayList<>();
        } else {
            allPosts = PostStorage.loadAllPosts().stream()
                .filter(post -> post.getCommunityId().equals(selectedCommunity.getId()))
                .collect(Collectors.toList());
        }
        applyFilter();
    }

    private void refreshCommunities() {
        List<Community> communities = CommunityStorage.loadAllCommunities().stream()
            .filter(Community::isApproved)
            .collect(Collectors.toList());
        communityCombo.removeAllItems();
        for (Community community : communities) {
            communityCombo.addItem(community);
        }
        if (!communities.isEmpty()) {
            if (selectedCommunity == null) {
                selectedCommunity = communities.get(0);
            }
            for (int i = 0; i < communityCombo.getItemCount(); i++) {
                if (communityCombo.getItemAt(i).getId().equals(selectedCommunity.getId())) {
                    communityCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        updateCommunityStatus();
    }

    private void changePassword() {
        JPasswordField currentPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        Object[] message = {
            "Current Password:", currentPasswordField,
            "New Password:", newPasswordField,
            "Confirm New Password:", confirmPasswordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All password fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!isStrongPassword(newPassword)) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters and include uppercase, lowercase, number, and symbol.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!UserStorage.changePassword(currentUser, currentPassword, newPassword)) {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8
            && password.matches(".*[A-Z].*")
            && password.matches(".*[a-z].*")
            && password.matches(".*\\d.*")
            && password.matches(".*[^A-Za-z0-9].*");
    }

    private void applyFilter() {
        String keyword = searchField.getText().trim().toLowerCase();
        String category = (String) categoryFilter.getSelectedItem();

        List<Post> filtered = allPosts.stream()
            .filter(p -> {
                boolean matchesKeyword = keyword.isEmpty() ||
                    p.getTitle().toLowerCase().contains(keyword) ||
                    p.getDescription().toLowerCase().contains(keyword);
                boolean matchesCategory = category.equals("All") ||
                    p.getCategory().equals(category);
                return matchesKeyword && matchesCategory;
            })
            .collect(Collectors.toList());

        renderPosts(filtered);
    }

    private void renderPosts(List<Post> posts) {
        postsPanel.removeAll();

        if (posts.isEmpty()) {
            JLabel emptyLabel = new JLabel("No posts yet. Click '+ New Post' to get started!", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setBorder(new EmptyBorder(60, 0, 0, 0));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            postsPanel.add(emptyLabel);
        }

        for (Post post : posts) {
            postsPanel.add(buildPostCard(post));
            postsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        postsPanel.revalidate();
        postsPanel.repaint();
    }

    private JPanel buildPostCard(Post post) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(12, 15, 12, 15)
        ));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(post.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel categoryBadge = new JLabel(post.getCategory());
        categoryBadge.setFont(new Font("Arial", Font.PLAIN, 11));
        categoryBadge.setForeground(Color.WHITE);
        categoryBadge.setBackground(new Color(58, 124, 165));
        categoryBadge.setOpaque(true);
        categoryBadge.setBorder(new EmptyBorder(2, 6, 2, 6));

        titleRow.add(titleLabel);
        titleRow.add(categoryBadge);

        JEditorPane descLabel = buildHtmlPane(post.getDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        String meta = "By " + post.getDisplayAuthor() + "  •  " + post.getDate();
        if (post.getEventDate() != null && !post.getEventDate().isEmpty()) {
            meta += "  •  Event: " + post.getEventDate();
        }
        JLabel metaLabel = new JLabel(meta);
        metaLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        metaLabel.setForeground(Color.GRAY);

        if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(new ImageIcon(post.getImagePath())
                    .getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
                JLabel imgLabel = new JLabel(icon);
                imgLabel.setBorder(new EmptyBorder(0, 0, 0, 5));
                card.add(imgLabel, BorderLayout.EAST);
            } catch (Exception ignored) {}
        }

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actionRow.setBackground(Color.WHITE);

        JButton openButton = new JButton("Open");
        openButton.setFocusPainted(false);
        openButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
            post.getTitle() + "\n\n" + post.getDescription(), "Post Details", JOptionPane.INFORMATION_MESSAGE));
        actionRow.add(openButton);

        JButton sharePostButton = new JButton("🔗");
        sharePostButton.setToolTipText("Copy share link");
        sharePostButton.setFocusPainted(false);
        sharePostButton.addActionListener(e -> sharePost(post));
        actionRow.add(sharePostButton);

        JButton replyButton = new JButton("Reply");
        replyButton.setFocusPainted(false);
        replyButton.setEnabled(post.isRepliesEnabled());
        replyButton.addActionListener(e -> addReply(post));
        actionRow.add(replyButton);

        JButton dmButton = new JButton("Message");
        dmButton.setFocusPainted(false);
        dmButton.addActionListener(e -> sendDirectMessage(post));
        actionRow.add(dmButton);

        User currentUserData = UserStorage.findUser(currentUser);
        if (currentUserData != null && (currentUserData.isOwner() || currentUserData.isAdmin())) {
            JButton flagButton = new JButton("Flag User");
            flagButton.setFocusPainted(false);
            flagButton.addActionListener(e -> flagUser(post));
            actionRow.add(flagButton);

            if (post.getCategory().equalsIgnoreCase("Announcement")) {
                JButton manageRepliesButton = new JButton("Manage Replies");
                manageRepliesButton.setFocusPainted(false);
                manageRepliesButton.addActionListener(e -> manageAnnouncementReplies(post));
                actionRow.add(manageRepliesButton);
            }
        }

        if (post.getAuthor().equals(currentUser)) {
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            deleteBtn.setForeground(new Color(180, 50, 50));
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setBackground(Color.WHITE);
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Delete this post?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    PostStorage.deletePost(post.getId());
                    refreshPosts();
                }
            });
            actionRow.add(deleteBtn);
        }

        JPanel leftPanel = new JPanel(new BorderLayout(0, 6));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(titleRow, BorderLayout.NORTH);
        leftPanel.add(descLabel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(metaLabel, BorderLayout.WEST);
        southPanel.add(actionRow, BorderLayout.EAST);

        leftPanel.add(southPanel, BorderLayout.SOUTH);
        card.add(leftPanel, BorderLayout.CENTER);

        if (!post.getReplies().isEmpty()) {
            JPanel repliesPanel = new JPanel();
            repliesPanel.setLayout(new BoxLayout(repliesPanel, BoxLayout.Y_AXIS));
            repliesPanel.setBackground(new Color(248, 250, 252));
            repliesPanel.setBorder(new EmptyBorder(6, 8, 6, 8));
            JLabel repliesTitle = new JLabel("Replies:");
            repliesTitle.setFont(new Font("Arial", Font.BOLD, 11));
            repliesPanel.add(repliesTitle);
            if (!post.isRepliesEnabled()) {
                JEditorPane disabledPane = buildHtmlPane("Replies are currently disabled by an admin. ❌");
                disabledPane.setFont(new Font("Arial", Font.ITALIC, 11));
                repliesPanel.add(disabledPane);
            }
            for (String reply : post.getReplies()) {
                JEditorPane replyLabel = buildHtmlPane("• " + reply);
                replyLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                repliesPanel.add(replyLabel);
            }
            card.add(repliesPanel, BorderLayout.SOUTH);
        }
        return card;
    }

    private void shareCurrentPage() {
        String link = selectedCommunity == null
            ? "https://community-board.local/community/default"
            : "https://community-board.local/community/" + selectedCommunity.getId() + "?user=" + currentUser;
        copyTextToClipboard(link);
        JOptionPane.showMessageDialog(this, "Share link copied:\n" + link, "Share Link", JOptionPane.INFORMATION_MESSAGE);
    }

    private void sharePost(Post post) {
        if (post == null) return;
        String link = "https://community-board.local/post/" + post.getId() + "?community=" + (selectedCommunity != null ? selectedCommunity.getId() : "default");
        copyTextToClipboard(link);
        JOptionPane.showMessageDialog(this, "Share link copied:\n" + link, "Share Link", JOptionPane.INFORMATION_MESSAGE);
    }

    private void copyTextToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private void sendDirectMessage(Post post) {
        User current = UserStorage.findUser(currentUser);
        if (current == null) return;
        if (current.isBanned()) {
            JOptionPane.showMessageDialog(this, "You are blocked from sending messages.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<String> recipients = new ArrayList<>();
        if (current.isOwner()) {
            recipients.addAll(selectedCommunity != null ? selectedCommunity.getMemberUsernames() : new ArrayList<>());
            recipients.remove(currentUser);
        } else if (current.isAdmin()) {
            recipients.add(selectedCommunity != null ? selectedCommunity.getOwnerUsername() : "");
            recipients.addAll(MessageService.getUsersWhoMessaged(currentUser));
            recipients.removeIf(r -> r == null || r.isEmpty() || r.equals(currentUser));
        } else {
            if (selectedCommunity != null) {
                recipients.addAll(selectedCommunity.getAdminUsernames());
                recipients.add(selectedCommunity.getOwnerUsername());
            }
            recipients.removeIf(r -> r == null || r.isEmpty() || r.equals(currentUser));
        }
        if (recipients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No eligible recipient found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] options = recipients.toArray(new String[0]);
        String chosen = (String) JOptionPane.showInputDialog(this, "Choose who to message:", "Direct Message", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (chosen == null || chosen.isEmpty()) return;
        String content = JOptionPane.showInputDialog(this, "Write your message:");
        if (content == null || content.trim().isEmpty()) return;
        MessageService.sendMessage(currentUser, chosen, selectedCommunity != null ? selectedCommunity.getId() : "default", content);
        JOptionPane.showMessageDialog(this, "Message sent.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addReply(Post post) {
        if (!post.isRepliesEnabled()) {
            JOptionPane.showMessageDialog(this, "Replies are disabled for this announcement.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String prompt = post.getCategory().equalsIgnoreCase("Announcement")
            ? "Reply to this announcement 🎉✨ (links okay)"
            : "Reply to this post (links okay):";
        String reply = JOptionPane.showInputDialog(this, prompt);
        if (reply != null && !reply.trim().isEmpty()) {
            User current = UserStorage.findUser(currentUser);
            if (current != null && current.isBanned()) {
                JOptionPane.showMessageDialog(this, "You are blocked from posting replies.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (containsInappropriate(reply) && current != null && (current.isOwner() || current.isAdmin())) {
                JOptionPane.showMessageDialog(this, "Admins cannot use inappropriate language.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (containsInappropriate(reply)) {
                JOptionPane.showMessageDialog(this, "Please keep replies respectful.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User currentUserData = UserStorage.findUser(currentUser);
            String replyAuthor = currentUserData != null && currentUserData.isOwner() ? "Anonymous Owner" : currentUser;
            String finalReply = reply.trim();
            if (post.getCategory().equalsIgnoreCase("Announcement")) {
                String[] flair = {"🎉", "✨", "📢", "💬"};
                String emoji = flair[(int) (Math.random() * flair.length)];
                finalReply = emoji + " " + finalReply + " 🎬[GIF-style reaction]";
            }
            post.addReply(replyAuthor + ": " + finalReply);
            PostStorage.updatePost(post);
            refreshPosts();
        }
    }

    private void createCommunity() {
        String name = JOptionPane.showInputDialog(this, "Enter community name:");
        if (name == null || name.trim().isEmpty()) return;
        Community existing = CommunityStorage.findByName(name.trim());
        if (existing != null) {
            JOptionPane.showMessageDialog(this, "That community already exists.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = "comm-" + System.currentTimeMillis();
        Community community = new Community(id, name.trim(), currentUser, true);
        community.addMember(currentUser);
        CommunityStorage.saveCommunity(community);
        refreshCommunities();
        String rulesMessage = "A new community, " + community.getName() + ", has been created.\n\nCommunity rules for users:\n- Be respectful and kind\n- No spam or harassment\n- Keep posts relevant\n- Follow admin guidance and report issues";
        NotificationService.notifyAllUsers("New Community Rules", rulesMessage);
        JOptionPane.showMessageDialog(this, "Community created and approved automatically.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void joinSelectedCommunity() {
        if (selectedCommunity == null) return;
        User current = UserStorage.findUser(currentUser);
        if (current == null) return;
        if (selectedCommunity.isBlocked(currentUser)) {
            JOptionPane.showMessageDialog(this, "You are blocked from this community.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!selectedCommunity.isMember(currentUser)) {
            selectedCommunity.addMember(currentUser);
            current.addCommunity(selectedCommunity.getId());
            UserStorage.updateUser(current);
            CommunityStorage.saveCommunity(selectedCommunity);
        }
        String welcomeMessage = "Welcome to " + selectedCommunity.getName() + "!\n\nCommunity rules:\n- Be respectful\n- No spam or harassment\n- Keep posts relevant\n- Report issues to admins";
        JOptionPane.showMessageDialog(this, welcomeMessage, "Welcome", JOptionPane.INFORMATION_MESSAGE);
        refreshCommunities();
        refreshPosts();
    }

    private void browseCommunities() {
        List<Community> communities = CommunityStorage.loadAllCommunities().stream()
            .filter(Community::isApproved)
            .collect(Collectors.toList());
        if (communities.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No approved communities are available yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] options = communities.stream().map(Community::getName).toArray(String[]::new);
        String chosen = (String) JOptionPane.showInputDialog(this, "Select a community to view:", "Browse Communities", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (chosen == null) return;
        for (Community community : communities) {
            if (community.getName().equals(chosen)) {
                selectedCommunity = community;
                refreshCommunities();
                refreshPosts();
                JOptionPane.showMessageDialog(this, "Now viewing: " + community.getName(), "Community Selected", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    private void requestAdminAccess() {
        if (selectedCommunity == null) return;
        User current = UserStorage.findUser(currentUser);
        if (current == null) return;
        if (current.isOwner() || current.isAdmin()) {
            JOptionPane.showMessageDialog(this, "You already have admin access.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (current.getAdminCommunityCount() >= 2) {
            JOptionPane.showMessageDialog(this, "Admins can only hold up to 2 community admin roles.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        selectedCommunity.addPendingAdmin(currentUser);
        current.addPendingAdminCommunity(selectedCommunity.getId());
        UserStorage.updateUser(current);
        CommunityStorage.saveCommunity(selectedCommunity);
        JOptionPane.showMessageDialog(this, "Admin request sent. Owner approval is required.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void approveAdminRequest() {
        if (selectedCommunity == null) return;
        User current = UserStorage.findUser(currentUser);
        if (current == null || !current.isOwner()) {
            JOptionPane.showMessageDialog(this, "Only the owner can approve admin requests.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedCommunity.getPendingAdminUsernames().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No pending admin requests.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] options = selectedCommunity.getPendingAdminUsernames().toArray(new String[0]);
        String chosen = (String) JOptionPane.showInputDialog(this, "Approve which admin request?", "Owner Approval", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (chosen == null) return;
        User target = UserStorage.findUser(chosen);
        if (target == null) {
            JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (target.getAdminCommunityCount() >= 2) {
            JOptionPane.showMessageDialog(this, "That user already holds 2 admin roles.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        selectedCommunity.approvePendingAdmin(chosen);
        target.setRole("admin");
        target.addAdminCommunity(selectedCommunity.getId());
        target.addCommunity(selectedCommunity.getId());
        UserStorage.updateUser(target);
        CommunityStorage.saveCommunity(selectedCommunity);
        CommunityStorage.markCommunityActive(selectedCommunity.getId());
        String adminMessage = "You have been appointed as an admin for " + selectedCommunity.getName() + " by the owner.\n\nYour powers:\n- Enforce community rules\n- Help manage members\n- Remove disruptive users when needed\n- Keep discussions respectful and safe\n\nImportant: misuse of these powers can lead to removal from the admin role or a community ban.";
        NotificationService.notifyUser(chosen, "Admin Powers", adminMessage);
        UserStorage.promptForAdminContactInfo(this, target, "Complete Admin Contact Details");
        JOptionPane.showMessageDialog(this, chosen + " is now an admin.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addMember() {
        if (selectedCommunity == null) return;
        User current = UserStorage.findUser(currentUser);
        if (current == null || (!current.isOwner() && !current.isAdmin())) {
            JOptionPane.showMessageDialog(this, "Only owner/admin can add members.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String username = JOptionPane.showInputDialog(this, "Enter username to add:");
        if (username == null || username.trim().isEmpty()) return;
        User target = UserStorage.findUser(username.trim());
        if (target == null) {
            JOptionPane.showMessageDialog(this, "That user does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        selectedCommunity.addMember(username.trim());
        target.addCommunity(selectedCommunity.getId());
        UserStorage.updateUser(target);
        CommunityStorage.saveCommunity(selectedCommunity);
        CommunityStorage.markCommunityActive(selectedCommunity.getId());
        JOptionPane.showMessageDialog(this, "Member added.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void removeMember() {
        if (selectedCommunity == null) return;
        User current = UserStorage.findUser(currentUser);
        if (current == null || (!current.isOwner() && !current.isAdmin())) {
            JOptionPane.showMessageDialog(this, "Only owner/admin can remove members.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String username = JOptionPane.showInputDialog(this, "Enter username to remove:");
        if (username == null || username.trim().isEmpty()) return;
        User target = UserStorage.findUser(username.trim());
        if (target != null) {
            target.removeCommunity(selectedCommunity.getId());
            target.removeAdminCommunity(selectedCommunity.getId());
            UserStorage.updateUser(target);
        }
        selectedCommunity.removeMember(username.trim());
        CommunityStorage.saveCommunity(selectedCommunity);
        CommunityStorage.markCommunityActive(selectedCommunity.getId());
        JOptionPane.showMessageDialog(this, "Member removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void flagUser(Post post) {
        if (selectedCommunity == null) return;
        User current = UserStorage.findUser(currentUser);
        if (current == null || (!current.isOwner() && !current.isAdmin())) {
            return;
        }
        User target = UserStorage.findUser(post.getAuthor());
        if (target == null) return;
        target.setBanned(true);
        target.incrementViolationCount();
        UserStorage.updateUser(target);
        selectedCommunity.addBlocked(post.getAuthor());
        CommunityStorage.saveCommunity(selectedCommunity);
        CommunityStorage.markCommunityActive(selectedCommunity.getId());
        JOptionPane.showMessageDialog(this, post.getAuthor() + " has been flagged and blocked from this community.", "Moderation", JOptionPane.WARNING_MESSAGE);
    }

    private void openAdminPanel() {
        if (selectedCommunity == null) return;
        String[] actions = {"Flag & Remove User", "Remove User Only"};
        int choice = JOptionPane.showOptionDialog(this, "Choose a moderation action for the selected community:", "Community Admin Panel",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, actions, actions[0]);
        if (choice < 0) return;

        List<String> members = new ArrayList<>();
        for (String username : selectedCommunity.getMemberUsernames()) {
            if (!username.equals(selectedCommunity.getOwnerUsername())) members.add(username);
        }
        if (members.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No members available to moderate.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String selectedUser = (String) JOptionPane.showInputDialog(this, "Choose a user from this community:", "Community Admin Panel",
            JOptionPane.QUESTION_MESSAGE, null, members.toArray(new String[0]), members.get(0));
        if (selectedUser == null || selectedUser.isEmpty()) return;

        User target = UserStorage.findUser(selectedUser);
        if (target == null) return;

        if (choice == 0) {
            target.setBanned(true);
            target.incrementViolationCount();
            selectedCommunity.addBlocked(selectedUser);
        }

        target.removeCommunity(selectedCommunity.getId());
        target.removeAdminCommunity(selectedCommunity.getId());
        UserStorage.updateUser(target);
        selectedCommunity.removeMember(selectedUser);
        CommunityStorage.saveCommunity(selectedCommunity);
        CommunityStorage.markCommunityActive(selectedCommunity.getId());
        JOptionPane.showMessageDialog(this, selectedUser + " was removed from the community." + (choice == 0 ? " They were also flagged." : ""),
            "Moderation", JOptionPane.WARNING_MESSAGE);
    }

    private void manageAnnouncementReplies(Post post) {
        if (post == null) return;
        String[] actions = {"Delete a reply", "Enable/Disable replies"};
        int choice = JOptionPane.showOptionDialog(this, "Manage replies for this announcement:", "Announcement Moderation",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, actions, actions[0]);
        if (choice < 0) return;

        if (choice == 0) {
            if (post.getReplies().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No replies to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String selectedReply = (String) JOptionPane.showInputDialog(this, "Choose a reply to delete:", "Delete Reply",
                JOptionPane.QUESTION_MESSAGE, null, post.getReplies().toArray(new String[0]), post.getReplies().get(0));
            if (selectedReply == null) return;
            post.getReplies().remove(selectedReply);
            PostStorage.updatePost(post);
            JOptionPane.showMessageDialog(this, "Reply removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshPosts();
        } else {
            String action = post.isRepliesEnabled() ? "disable" : "enable";
            int confirm = JOptionPane.showConfirmDialog(this, "Do you want to " + action + " replies for this announcement?", "Toggle Replies", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                post.setRepliesEnabled(!post.isRepliesEnabled());
                PostStorage.updatePost(post);
                JOptionPane.showMessageDialog(this, "Replies are now " + (post.isRepliesEnabled() ? "enabled" : "disabled") + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshPosts();
            }
        }
    }

    private JEditorPane buildHtmlPane(String text) {
        String escaped = text == null ? "" : text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
        escaped = escaped.replace("\n", "<br>");
        escaped = escaped.replaceAll("(https?://[^\\s<]+)", "<a href=\"$1\">$1</a>");
        escaped = escaped.replaceAll("(www\\.[^\\s<]+)", "<a href=\"http://$1\">$1</a>");
        JEditorPane pane = new JEditorPane("text/html", "<html><body style='width:560px'>" + escaped + "</body></html>");
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.setBorder(null);
        pane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (Exception ignored) {
                    }
                }
            }
        });
        return pane;
    }

    private static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                Dimension dim = new Dimension(0, 0);
                int nmembers = target.getComponentCount();
                boolean firstVisible = true;
                int rowHeight = 0;
                int rowWidth = 0;

                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (!m.isVisible()) continue;
                    Dimension d = m.getPreferredSize();
                    if (firstVisible) {
                        firstVisible = false;
                    } else {
                        rowWidth += getHgap();
                    }
                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                    if (rowWidth > target.getWidth() - getHgap() * 2 && !firstVisible) {
                        dim.width = Math.max(dim.width, rowWidth - d.width - getHgap());
                        dim.height += rowHeight + getVgap();
                        rowWidth = d.width;
                        rowHeight = d.height;
                        firstVisible = true;
                    }
                }
                if (!firstVisible) {
                    dim.width = Math.max(dim.width, rowWidth);
                    dim.height += rowHeight;
                }
                Insets insets = target.getInsets();
                dim.width += insets.left + insets.right + getHgap() * 2;
                dim.height += insets.top + insets.bottom + getVgap() * 2;
                return dim;
            }
        }
    }

    private void updateCommunityStatus() {
        if (selectedCommunity != null) {
            communityStatusLabel.setText("Community: " + selectedCommunity.getName());
        } else {
            communityStatusLabel.setText("Community: none");
        }
    }

    private void deleteSelectedCommunity() {
        if (selectedCommunity == null) return;
        User current = UserStorage.findUser(currentUser);
        if (current == null || (!current.isOwner() && !current.isAdmin())) {
            JOptionPane.showMessageDialog(this, "Only the owner or an admin can delete a community.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this community and all its posts?", "Delete Community", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        CommunityStorage.deleteCommunity(selectedCommunity.getId());
        PostStorage.deletePostsByCommunity(selectedCommunity.getId());
        selectedCommunity = null;
        refreshCommunities();
        refreshPosts();
        JOptionPane.showMessageDialog(this, "Community deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean containsInappropriate(String message) {
        String lower = message.toLowerCase();
        String[] banned = {"curse", "damn", "idiot", "stupid", "harassment", "bully", "nude", "sex"};
        for (String word : banned) {
            if (lower.contains(word)) return true;
        }
        return false;
    }
}