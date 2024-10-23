package hub.troubleshooters.soundlink.core.auth;

import hub.troubleshooters.soundlink.data.models.Community;
import hub.troubleshooters.soundlink.data.models.CommunityMember;
import hub.troubleshooters.soundlink.data.models.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the context of the current user. This context includes the {@link User} model and the current
 * permissions (scopes) of the user across different communities. It also tracks the community memberships
 * the user belongs to.
 */
public class UserContext {
    private User user;

    /**
     * A list of all community memberships this user belongs to
     */
    private final List<CommunityMember> communityMembers;

    /**
     * Constructs a {@code UserContext} with the given user and list of community memberships.
     *
     * @param user The {@link User} representing the current user.
     * @param communityUsers A list of {@link CommunityMember} objects representing the communities the user belongs to.
     */
    public UserContext(User user, List<CommunityMember> communityUsers) {
        this.user = user;
        this.communityMembers = communityUsers;
    }

    /**
     * Retrieves the current set of permissions (scopes) the user has for a specific community.
     *
     * @param community The {@link Community} for which to retrieve the user's scopes.
     * @return A set of {@link Scope} values representing the user's permissions for the specified community.
     *         If the user is not a member of the community, an empty set is returned.
     */
    public Set<Scope> getCurrentScopes(Community community) {
        var communityUser = communityMembers.stream().filter(c -> c.getCommunityId() == community.getId()).findFirst();
        return communityUser.map(communityUserModel -> ScopeUtils.deconstructScopes(communityUserModel.getPermission())).orElseGet(HashSet::new);
    }

    /**
     * Retrieves the list of all community memberships the user belongs to.
     *
     * @return A list of {@link CommunityMember} objects representing the communities the user is a member of.
     */
    public List<CommunityMember> getCommunityMembers() {
        return communityMembers;
    }

    /**
     * Retrieves the current {@link User} associated with this context.
     *
     * @return The {@link User} object representing the current user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the current {@link User} for this context.
     *
     * @param user The {@link User} to set as the current user.
     */
    public void setUser(User user) {
        this.user = user;
    }
}