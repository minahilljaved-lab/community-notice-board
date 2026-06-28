import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private String role;
    private boolean banned;
    private boolean anonymousAdmin;
    private String communityIds;
    private String adminCommunityIds;
    private String pendingAdminCommunityIds;
    private int violationCount;
    private String email;
    private String phone;

    public User(String username, String password) {
        this(username, password, "member", false, true, "", "", "", 0, "", "");
    }

    public User(String username, String password, String role, boolean banned,
                boolean anonymousAdmin, String communityIds, String adminCommunityIds,
                String pendingAdminCommunityIds, int violationCount) {
        this(username, password, role, banned, anonymousAdmin, communityIds, adminCommunityIds,
            pendingAdminCommunityIds, violationCount, "", "");
    }

    public User(String username, String password, String role, boolean banned,
                boolean anonymousAdmin, String communityIds, String adminCommunityIds,
                String pendingAdminCommunityIds, int violationCount, String email, String phone) {
        this.username = username;
        this.password = password;
        this.role = role == null || role.isEmpty() ? "member" : role;
        this.banned = banned;
        this.anonymousAdmin = anonymousAdmin;
        this.communityIds = communityIds == null ? "" : communityIds;
        this.adminCommunityIds = adminCommunityIds == null ? "" : adminCommunityIds;
        this.pendingAdminCommunityIds = pendingAdminCommunityIds == null ? "" : pendingAdminCommunityIds;
        this.violationCount = violationCount;
        this.email = email == null ? "" : email;
        this.phone = phone == null ? "" : phone;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password == null ? "" : password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isOwner() { return "owner".equalsIgnoreCase(role); }
    public boolean isAdmin() { return "admin".equalsIgnoreCase(role); }
    public boolean isBanned() { return banned; }
    public void setBanned(boolean banned) { this.banned = banned; }
    public boolean isAnonymousAdmin() { return anonymousAdmin; }
    public void setAnonymousAdmin(boolean anonymousAdmin) { this.anonymousAdmin = anonymousAdmin; }
    public String getCommunityIds() { return communityIds; }
    public void setCommunityIds(String communityIds) { this.communityIds = communityIds; }
    public String getAdminCommunityIds() { return adminCommunityIds; }
    public void setAdminCommunityIds(String adminCommunityIds) { this.adminCommunityIds = adminCommunityIds; }
    public String getPendingAdminCommunityIds() { return pendingAdminCommunityIds; }
    public void setPendingAdminCommunityIds(String pendingAdminCommunityIds) { this.pendingAdminCommunityIds = pendingAdminCommunityIds; }
    public int getViolationCount() { return violationCount; }
    public void setViolationCount(int violationCount) { this.violationCount = violationCount; }
    public void incrementViolationCount() { this.violationCount++; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? "" : email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone == null ? "" : phone; }

    public boolean isMemberOfCommunity(String communityId) {
        return containsId(communityIds, communityId);
    }

    public boolean isAdminOfCommunity(String communityId) {
        return containsId(adminCommunityIds, communityId);
    }

    public int getAdminCommunityCount() {
        return splitIds(adminCommunityIds).size();
    }

    public void addCommunity(String communityId) {
        if (communityId == null || communityId.isEmpty()) return;
        List<String> ids = splitIds(communityIds);
        if (!ids.contains(communityId)) ids.add(communityId);
        communityIds = joinIds(ids);
    }

    public void addAdminCommunity(String communityId) {
        if (communityId == null || communityId.isEmpty()) return;
        addCommunity(communityId);
        List<String> ids = splitIds(adminCommunityIds);
        if (!ids.contains(communityId)) ids.add(communityId);
        adminCommunityIds = joinIds(ids);
    }

    public void addPendingAdminCommunity(String communityId) {
        if (communityId == null || communityId.isEmpty()) return;
        List<String> ids = splitIds(pendingAdminCommunityIds);
        if (!ids.contains(communityId)) ids.add(communityId);
        pendingAdminCommunityIds = joinIds(ids);
    }

    public void removeCommunity(String communityId) {
        if (communityId == null || communityId.isEmpty()) return;
        List<String> ids = splitIds(communityIds);
        ids.remove(communityId);
        communityIds = joinIds(ids);
    }

    public void removeAdminCommunity(String communityId) {
        if (communityId == null || communityId.isEmpty()) return;
        List<String> ids = splitIds(adminCommunityIds);
        ids.remove(communityId);
        adminCommunityIds = joinIds(ids);
    }

    private boolean containsId(String csv, String target) {
        if (target == null || target.isEmpty()) return false;
        for (String id : splitIds(csv)) {
            if (target.equals(id)) return true;
        }
        return false;
    }

    private List<String> splitIds(String csv) {
        List<String> ids = new ArrayList<>();
        if (csv == null || csv.isEmpty()) return ids;
        for (String part : csv.split(",")) {
            if (!part.trim().isEmpty()) ids.add(part.trim());
        }
        return ids;
    }

    private String joinIds(List<String> ids) {
        List<String> clean = new ArrayList<>();
        for (String id : ids) {
            if (!id.trim().isEmpty() && !clean.contains(id.trim())) clean.add(id.trim());
        }
        return String.join(",", clean);
    }
}