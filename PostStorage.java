import java.io.*;
import java.util.*;
 
public class PostStorage {
    private static final String FILE_NAME =
        System.getProperty("user.home") + File.separator +
        "community_board" + File.separator + "posts.txt";
 
    private static void ensureFileExists() {
        File file = new File(FILE_NAME);
        File dir = file.getParentFile();
        if (!dir.exists()) dir.mkdirs();
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }
 
    public static void savePost(Post post) {
        ensureFileExists();
        try {
            PrintWriter output = new PrintWriter(new FileWriter(FILE_NAME, true));
            writePost(output, post);
            output.close();
        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "SAVE ERROR: " + e.getMessage(), "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void updatePost(Post post) {
        List<Post> posts = loadAllPosts();
        List<Post> updated = new ArrayList<>();
        boolean replaced = false;
        for (Post current : posts) {
            if (current.getId().equals(post.getId())) {
                updated.add(post);
                replaced = true;
            } else {
                updated.add(current);
            }
        }
        if (!replaced) {
            updated.add(post);
        }

        try {
            PrintWriter output = new PrintWriter(new FileWriter(FILE_NAME, false));
            for (Post item : updated) {
                writePost(output, item);
            }
            output.close();
        } catch (IOException e) {
            System.out.println("Error updating post: " + e.getMessage());
        }
    }
 
    public static List<Post> loadAllPosts() {
        ensureFileExists();
        List<Post> posts = new ArrayList<>();
        try {
            Scanner input = new Scanner(new File(FILE_NAME));
            String id = "", title = "", desc = "", cat = "",
                   author = "", displayAuthor = "", date = "", img = "", event = "", communityId = "default";
            List<String> replies = new ArrayList<>();
            boolean repliesEnabled = true;
 
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (line.startsWith("ID:"))     id     = line.substring(3);
                else if (line.startsWith("TITLE:"))  title  = line.substring(6);
                else if (line.startsWith("DESC:"))   desc   = line.substring(5);
                else if (line.startsWith("CAT:"))    cat    = line.substring(4);
                else if (line.startsWith("AUTHOR:")) author = line.substring(7);
                else if (line.startsWith("DISPLAY:")) displayAuthor = line.substring(8);
                else if (line.startsWith("DATE:"))   date   = line.substring(5);
                else if (line.startsWith("IMG:"))    img    = line.substring(4);
                else if (line.startsWith("EVENT:"))  event  = line.substring(6);
                else if (line.startsWith("COMMUNITY:")) communityId = line.substring(10);
                else if (line.startsWith("REPLIES:")) replies = parseReplies(line.substring(8));
                else if (line.startsWith("REPLIES_ENABLED:")) repliesEnabled = Boolean.parseBoolean(line.substring(16));
                else if (line.equals("---")) {
                    if (!id.isEmpty() && !title.isEmpty()) {
                        posts.add(new Post(id, title, desc, cat, author, displayAuthor, date, img, event, communityId, replies, repliesEnabled));
                    }
                    id = title = desc = cat = author = displayAuthor = date = img = event = communityId = "";
                    communityId = "default";
                    replies = new ArrayList<>();
                    repliesEnabled = true;
                }
            }
            input.close();
        } catch (IOException e) {
            System.out.println("Error loading posts: " + e.getMessage());
        }
        return posts;
    }
 
    public static void deletePost(String postId) {
        List<Post> posts = loadAllPosts();
        try {
            PrintWriter output = new PrintWriter(new FileWriter(FILE_NAME, false));
            for (Post post : posts) {
                if (!post.getId().equals(postId)) {
                    writePost(output, post);
                }
            }
            output.close();
        } catch (IOException e) {
            System.out.println("Error deleting post: " + e.getMessage());
        }
    }

    public static void deletePostsByCommunity(String communityId) {
        List<Post> posts = loadAllPosts();
        try {
            PrintWriter output = new PrintWriter(new FileWriter(FILE_NAME, false));
            for (Post post : posts) {
                if (!post.getCommunityId().equals(communityId)) {
                    writePost(output, post);
                }
            }
            output.close();
        } catch (IOException e) {
            System.out.println("Error deleting community posts: " + e.getMessage());
        }
    }

    private static void writePost(PrintWriter output, Post post) {
        output.println("ID:" + post.getId());
        output.println("TITLE:" + post.getTitle());
        output.println("DESC:" + post.getDescription());
        output.println("CAT:" + post.getCategory());
        output.println("AUTHOR:" + post.getAuthor());
        output.println("DISPLAY:" + post.getDisplayAuthor());
        output.println("DATE:" + post.getDate());
        output.println("IMG:" + post.getImagePath());
        output.println("EVENT:" + post.getEventDate());
        output.println("COMMUNITY:" + post.getCommunityId());
        output.println("REPLIES:" + String.join("||", post.getReplies()));
        output.println("REPLIES_ENABLED:" + post.isRepliesEnabled());
        output.println("---");
    }

    private static List<String> parseReplies(String value) {
        List<String> replies = new ArrayList<>();
        if (value == null || value.isEmpty()) return replies;
        for (String part : value.split("\\|\\|")) {
            if (!part.trim().isEmpty()) replies.add(part.trim());
        }
        return replies;
    }
}
 