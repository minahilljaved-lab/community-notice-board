import java.util.ArrayList;
import java.util.List;

public class Post {
    private String id;
    private String title;
    private String description;
    private String category;
    private String author;
    private String displayAuthor;
    private String date;
    private String imagePath;
    private String eventDate;
    private String communityId;
    private List<String> replies;
    private boolean repliesEnabled;

    public Post(String id, String title, String description,
                String category, String author, String date,
                String imagePath, String eventDate) {
        this(id, title, description, category, author, author, date, imagePath, eventDate, "default", new ArrayList<>(), true);
    }

    public Post(String id, String title, String description,
                String category, String author, String displayAuthor, String date,
                String imagePath, String eventDate, String communityId, List<String> replies) {
        this(id, title, description, category, author, displayAuthor, date, imagePath, eventDate, communityId, replies, true);
    }

    public Post(String id, String title, String description,
                String category, String author, String displayAuthor, String date,
                String imagePath, String eventDate, String communityId, List<String> replies, boolean repliesEnabled) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.author = author;
        this.displayAuthor = displayAuthor == null || displayAuthor.isEmpty() ? author : displayAuthor;
        this.date = date;
        this.imagePath = imagePath;
        this.eventDate = eventDate;
        this.communityId = communityId == null || communityId.isEmpty() ? "default" : communityId;
        this.replies = replies == null ? new ArrayList<>() : new ArrayList<>(replies);
        this.repliesEnabled = repliesEnabled;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getAuthor() { return author; }
    public String getDisplayAuthor() { return displayAuthor; }
    public String getDate() { return date; }
    public String getImagePath() { return imagePath; }
    public String getEventDate() { return eventDate; }
    public String getCommunityId() { return communityId; }
    public List<String> getReplies() { return replies; }
    public boolean isRepliesEnabled() { return repliesEnabled; }
    public void setRepliesEnabled(boolean repliesEnabled) { this.repliesEnabled = repliesEnabled; }

    public void addReply(String reply) {
        if (reply != null && !reply.trim().isEmpty()) {
            replies.add(reply.trim());
        }
    }
}