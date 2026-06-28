import java.time.*;
import java.time.format.*;
import java.util.*;
import javax.swing.*;

public class NotificationService {
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void notifyAllUsers(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void notifyUser(String username, String title, String message) {
        User user = UserStorage.findUser(username);
        if (user == null) return;
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void checkEventReminders(List<Post> posts) {
        LocalDate today = LocalDate.now();
        List<String> reminders = new ArrayList<>();

        for (Post post : posts) {
            String eventDate = post.getEventDate();
            if (eventDate == null || eventDate.isEmpty()) continue;

            try {
                LocalDate date = LocalDate.parse(eventDate, FORMATTER);
                long daysUntil = today.until(date, java.time.temporal.ChronoUnit.DAYS);

                if (daysUntil == 0) {
                    reminders.add("TODAY: " + post.getTitle());
                } else if (daysUntil == 1) {
                    reminders.add("TOMORROW: " + post.getTitle());
                } else if (daysUntil > 1 && daysUntil <= 3) {
                    reminders.add("In " + daysUntil + " days: " + post.getTitle());
                }
            } catch (Exception e) {
                // skip invalid dates
            }
        }

        if (!reminders.isEmpty()) {
            StringBuilder message = new StringBuilder("Upcoming Events:\n\n");
            for (String r : reminders) {
                message.append("• ").append(r).append("\n");
            }
            JOptionPane.showMessageDialog(null, message.toString(),
                "Event Reminders", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}