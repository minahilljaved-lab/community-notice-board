import javax.swing.*;
import java.io.*;
import java.util.*;

public class UserStorage {
    private static final String FILE_NAME =
        System.getProperty("user.home") + File.separator +
        "community_board" + File.separator + "users.txt";
    private static final String DESKTOP_FILE_NAME =
        System.getProperty("user.home") + File.separator + "Desktop" + File.separator +
        "community_board" + File.separator + "users.txt";
    private static final String DEFAULT_OWNER_USERNAME = "owner";
    private static final String DEFAULT_OWNER_PASSWORD = "Owner@2026!";
    private static final String DEFAULT_OWNER_EMAIL = "owner@community.com";
    private static final String DEFAULT_OWNER_PHONE = "0000000000";

    private static void ensureFileExists() {
        File file = new File(FILE_NAME);
        File dir = file.getParentFile();
        if (!dir.exists()) dir.mkdirs();
        try {
            if (!file.exists()) {
                file.createNewFile();
                List<User> users = new ArrayList<>();
                users.add(new User(DEFAULT_OWNER_USERNAME, DEFAULT_OWNER_PASSWORD, "owner", false, true, "", "", "", 0, DEFAULT_OWNER_EMAIL, DEFAULT_OWNER_PHONE));
                writeUsers(users);
            }
            mirrorToDesktop();
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    private static void ensureOwnerAccountExists() {
        ensureFileExists();
        List<User> users = loadUsers();
        boolean ownerExists = false;
        for (User user : users) {
            if (user.isOwner()) {
                ownerExists = true;
                break;
            }
        }
        if (!ownerExists) {
            users.add(new User(DEFAULT_OWNER_USERNAME, DEFAULT_OWNER_PASSWORD, "owner", false, true, "", "", "", 0, DEFAULT_OWNER_EMAIL, DEFAULT_OWNER_PHONE));
            writeUsers(users);
        }
    }

    public static void saveUser(User user) {
        ensureFileExists();
        List<User> users = loadUsers();
        boolean replaced = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                users.set(i, user);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            users.add(user);
        }
        writeUsers(users);
    }

    public static void updateUser(User user) {
        saveUser(user);
    }

    public static boolean validateLogin(String username, String password) {
        ensureOwnerAccountExists();
        User user = findUser(username);
        if (user != null) {
            if (!user.getPassword().equals(password) || user.isBanned()) {
                return false;
            }
            return hasRequiredContactInfo(user);
        }
        return DEFAULT_OWNER_USERNAME.equalsIgnoreCase(username)
            && DEFAULT_OWNER_PASSWORD.equals(password);
    }

    public static boolean changePassword(String username, String currentPassword, String newPassword) {
        ensureOwnerAccountExists();
        User user = findUser(username);
        if (user == null || !user.getPassword().equals(currentPassword) || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }
        user.setPassword(newPassword.trim());
        saveUser(user);
        return true;
    }

    public static boolean hasRequiredContactInfo(User user) {
        if (user == null) return false;
        if (!user.isAdmin() && !user.isOwner()) {
            return true;
        }
        return user.getEmail() != null && !user.getEmail().trim().isEmpty()
            && user.getPhone() != null && !user.getPhone().trim().isEmpty();
    }

    public static boolean promptForAdminContactInfo(JFrame parent, User user, String title) {
        if (user == null || (!user.isAdmin() && !user.isOwner())) {
            return true;
        }
        if (hasRequiredContactInfo(user)) {
            return true;
        }

        JOptionPane.showMessageDialog(parent,
            "Email and phone are required to keep admin powers active. Please provide them now.",
            "Admin Access Required", JOptionPane.WARNING_MESSAGE);

        JTextField emailField = new JTextField(user.getEmail() == null ? "" : user.getEmail());
        JTextField phoneField = new JTextField(user.getPhone() == null ? "" : user.getPhone());

        Object[] message = {
            "Email (Gmail only):", emailField,
            "Phone number:", phoneField
        };

        int option = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            user.setRole("member");
            user.setAdminCommunityIds("");
            user.setPendingAdminCommunityIds("");
            user.setAnonymousAdmin(true);
            updateUser(user);
            for (Community community : CommunityStorage.loadAllCommunities()) {
                community.removeAdmin(user.getUsername());
                CommunityStorage.saveCommunity(community);
            }
            JOptionPane.showMessageDialog(parent,
                "Admin access was not activated because contact details were not provided.",
                "Access Blocked", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        if (!isValidGmail(email) || !isValidPhone(phone)) {
            JOptionPane.showMessageDialog(parent,
                "Please enter a valid Gmail address and phone number.",
                "Invalid Details", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        user.setEmail(email);
        user.setPhone(phone);
        updateUser(user);
        return true;
    }

    public static boolean isValidGmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\d{10,15}$");
    }

    public static boolean usernameExists(String username) {
        return findUser(username) != null;
    }

    public static boolean hasOwnerAccount() {
        ensureOwnerAccountExists();
        for (User user : loadUsers()) {
            if (user.isOwner()) {
                return true;
            }
        }
        return false;
    }

    public static boolean emailOrPhoneExists(String email, String phone) {
        ensureFileExists();
        for (User user : loadUsers()) {
            boolean sameEmail = email != null && !email.isEmpty() && email.equalsIgnoreCase(user.getEmail());
            boolean samePhone = phone != null && !phone.isEmpty() && phone.equals(user.getPhone());
            if (sameEmail || samePhone) return true;
        }
        return false;
    }

    public static User findUser(String username) {
        ensureFileExists();
        for (User user : loadUsers()) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }

    public static List<User> loadUsers() {
        ensureFileExists();
        List<User> users = new ArrayList<>();
        try {
            Scanner input = new Scanner(new File(FILE_NAME));
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    User user = new User(parts[0], parts[1]);
                    if (parts.length >= 3 && !parts[2].isEmpty()) user.setRole(parts[2]);
                    if (parts.length >= 4) user.setBanned(Boolean.parseBoolean(parts[3]));
                    if (parts.length >= 5) user.setAnonymousAdmin(Boolean.parseBoolean(parts[4]));
                    if (parts.length >= 6) user.setCommunityIds(parts[5]);
                    if (parts.length >= 7) user.setAdminCommunityIds(parts[6]);
                    if (parts.length >= 8) user.setPendingAdminCommunityIds(parts[7]);
                    if (parts.length >= 9) user.setViolationCount(Integer.parseInt(parts[8]));
                    if (parts.length >= 11) {
                        user.setEmail(parts[9]);
                        user.setPhone(parts[10]);
                    }
                    users.add(user);
                }
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Error reading users: " + e.getMessage());
        }
        return users;
    }

    private static void writeUsers(List<User> users) {
        try {
            PrintWriter output = new PrintWriter(new FileWriter(FILE_NAME, false));
            for (User user : users) {
                output.println(user.getUsername() + "," + user.getPassword() + "," +
                    user.getRole() + "," + user.isBanned() + "," + user.isAnonymousAdmin() + "," +
                    user.getCommunityIds() + "," + user.getAdminCommunityIds() + "," +
                    user.getPendingAdminCommunityIds() + "," + user.getViolationCount() + "," +
                    user.getEmail() + "," + user.getPhone());
            }
            output.close();
            mirrorToDesktop();
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    private static void mirrorToDesktop() throws IOException {
        File desktopFile = new File(DESKTOP_FILE_NAME);
        File desktopDir = desktopFile.getParentFile();
        if (!desktopDir.exists()) desktopDir.mkdirs();
        copyFile(new File(FILE_NAME), desktopFile);
    }

    private static void copyFile(File source, File target) throws IOException {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
}