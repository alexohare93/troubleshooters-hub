package hub.troubleshooters.soundlink.core.auth;

import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.CommunityMember;
import hub.troubleshooters.soundlink.data.models.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the context of the current user. This includes the user model and the current permissions of the user.
 */
public class UserContext {
    private User user;

    /**
     * A list of all community memberships this user belongs to
     */
    private final List<CommunityMember> communityUsers;

    public UserContext(User user, List<CommunityMember> communityUsers) {
        this.user = user;
        this.communityUsers = communityUsers;
    }

    public Set<Scope> getCurrentScopes(Community community) {
        var communityUser = communityUsers.stream().filter(c -> c.getCommunityId() == community.getId()).findFirst();
        return communityUser.map(communityUserModel -> ScopeUtils.deconstructScopes(communityUserModel.getPermission())).orElseGet(HashSet::new);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}